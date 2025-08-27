import 'dart:convert';
import 'dart:io';

import 'package:file_picker/file_picker.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:internal_hifi_plugin/band_level_model.dart';
import 'package:internal_hifi_plugin/internal_hifi_plugin.dart';
import 'package:internal_hifi_plugin/models/device_state_model.dart';
import 'package:internal_hifi_plugin/models/metadata_model.dart';

void main() {
  runApp(const MyApp());
}

extension Double on double {
  double toHz() {
    return (this / 1000);
  }

  double toKhz() {
    return (this / 1000000);
  }

  double toDbFromMilli() {
    return (this / 1000);
  }
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
  late DeviceStateModel? deviceState = null;
  late SongMetadataModel? currentlyPlayingMetaData = null;

  final _hifiInstance = InternalHifiPlugin();




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
    //TODO: Need even less EventChannels. Probably we can wrap several values in one channel.
    _hifiInstance.trackingPositionChannel.receiveBroadcastStream().listen((dynamic event) {
      setState(() {
        currentPosition = formatPositionMStoMinuteFormat(event.toString());
      });
    });
    _hifiInstance.deviceStateChannel.receiveBroadcastStream().listen((dynamic event) {
      setState(() {
        deviceState = DeviceStateModel.fromJson(jsonDecode(event) as Map<String, dynamic>);
      });
    });
    _hifiInstance.metaDataChannel.receiveBroadcastStream().listen((dynamic event) {
      print("Metadata event: $event");
      // setState(() {
      //   // Handle metadata updates if needed
      // });
    });
  }

  Widget generateBandLevelSliders() {
    if (bandLevels?.bandLevels == null) {
      return const Text("No Band Levels");
    }
    List<Widget> sliders = [];
    for (var band in bandLevels!.bandLevels!) {
      sliders.add(
        Row(
          children: [
            Text("${band.bandFrequency![1].toDouble().toKhz()} kHz"),
            Slider(
              value: band.bandLevel!.toDouble().toDbFromMilli(),
              min: -12,
              max: 12,
              divisions: 4,
              label: band.bandLevel.toString(),
              onChanged: (double value) {
                setState(() {
                  band.bandLevel = value.toInt();
                });
              },
            ),
          ],
        ),
      );
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
                FilePickerResult? result = await FilePicker.platform.pickFiles(
                  allowMultiple: true,
                );
                if (result != null) {
                  List<File> trackList = result.paths
                      .map((path) => File(path!))
                      .toList();
                  await _hifiInstance.addToPlaylist(trackList[0].path);
                }
              },
              child: Text("Add Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.playPlaylist();
              },
              child: Text("Play Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.stopPlaylist();
              },
              child: Text("Stop Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.stopPlaylist();
              },
              child: Text("Pause Playlist"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.nextTrack();
              },
              child: Text("Next Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.previousTrack();
              },
              child: Text("Previous Track"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.forwardTrack(15000);
              },
              child: Text("Forward 10s"),
            ),
            ElevatedButton(
              onPressed: () async {
                await _hifiInstance.reverseTrack(15000);
              },
              child: Text("Rewind 10s"),
            ),
            ElevatedButton(
              onPressed: () async {
                BandLevels bands = await _hifiInstance.getBandLevels();
                setState(() {
                  bandLevels = bands;
                });
                await _hifiInstance.setBandLevels(bands);
              },
              child: Text("Set Band Levels"),
            ),
            Container(
              width: double.infinity,
              child: Center(child: generateBandLevelSliders()),
            ),
            Text("Volume: ${deviceState != null ? deviceState?.volume.toString() : "Volume not available"}"),
          ],
        ),
      ),
    );
  }
}
