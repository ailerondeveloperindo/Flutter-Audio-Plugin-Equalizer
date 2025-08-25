import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:internal_hifi_plugin/band_level_model.dart';
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
  late String currentPosition;
  late BandLevels? bandLevels = null;
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
    currentPosition = formatPositionMStoMinuteFormat('0');
    trackingPositionChannel.receiveBroadcastStream().listen((dynamic event) {
      setState(() {
        currentPosition = formatPositionMStoMinuteFormat(event.toString());
      });
    });
  }

  Widget generateBandLevelSliders() {
    if (bandLevels?.bandLevels == null) {
      return const Text("No Band Levels");
    }
    List<Widget> sliders = [];
    for (var band in bandLevels!.bandLevels!) {
      sliders.add(Column(
        children: [
          Text("Frequency: ${band.bandFrequency![1]/100} Hz"),
          Slider(
            value: band.bandLevel.toDouble()/100,
            min: -12,
            max: 12,
            divisions: 24,
            label: band.bandLevel.toString(),
            onChanged: (double value) {
              setState(() {
                band.bandLevel = value.toInt();
              });
            },
          ),
        ],
      ));
    }
    return Column(children: sliders);
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
                FilePickerResult? result = await FilePicker.platform.pickFiles(allowMultiple: true);
                if(result != null)
                {
                    List<File> trackList = result.paths.map((path) => File(path!)).toList();
                    await _internalHifiPlugin.addToPlaylist(trackList[0].path);
                }
              },
              child: Text("Add Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.playPlaylist();
              },
              child: Text("Play Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.stopPlaylist();
              },
              child: Text("Stop Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.stopPlaylist();
              },
              child: Text("Pause Playlist"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.nextTrack();
              },
              child: Text("Next Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.previousTrack();
              },
              child: Text("Previous Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.forwardTrack(15000);
              },
              child: Text("Forward 10s"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _internalHifiPlugin.reverseTrack(15000);
              },
              child: Text("Rewind 10s"),
            ),
            ElevatedButton(
              onPressed: () async {
                BandLevels bands = await _internalHifiPlugin.getBandLevels();
                setState(() {
                  bandLevels = bands;
                });
                await _internalHifiPlugin.setBandLevels(bands);
              },
              child: Text("Set Band Levels"),
            ),
            generateBandLevelSliders()
          ],
        ),
      ),
    );
  }
}
