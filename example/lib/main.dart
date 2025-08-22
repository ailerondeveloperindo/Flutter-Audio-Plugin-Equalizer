import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:internal_hifi_plugin/internal_hifi_plugin.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  String currentPosition = '0';
  final EventChannel trackingPositionChannel = EventChannel(
    "internal_hifi_plugin_baseEventChannel/posTrackEventChannel",
  );
  final _internalHifiPlugin = InternalHifiPlugin();

  String formatPositionMStoMinuteFormat(String t) {
    var dt = Duration(milliseconds: int.parse(t));
    String negativeSign = dt.isNegative ? '-' : '';
    String twoDigits(int n) => n.toString().padLeft(2, "0");
    String twoDigitMinutes = twoDigits(dt.inMinutes.remainder(60).abs());
    String twoDigitSeconds = twoDigits(dt.inSeconds.remainder(60).abs());
    return "${twoDigitMinutes} : ${twoDigitSeconds}";
  }

  @override
  void initState() {
    super.initState();
    trackingPositionChannel.receiveBroadcastStream().listen((dynamic event) {
      print("CurrentPosition -> " + event.toString());
      setState(() {
        currentPosition = formatPositionMStoMinuteFormat(event.toString());
      });
    });
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      await _internalHifiPlugin.addToPlaylist("sdasd");
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Plugin example app')),
        body: Column(
          children: [
            Text('Running on: $currentPosition\n'),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.playPlaylist();
              },
              child: Text("Play Song"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.stopPlaylist();
              },
              child: Text("Stop Song"),
            ),
          ],
        ),
      ),
    );
  }
}
