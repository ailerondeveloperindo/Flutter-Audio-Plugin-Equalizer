import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'band_level_model.dart';
import 'internal_hifi_plugin_platform_interface.dart';

/// An implementation of [InternalHifiPluginPlatform] that uses method channels.
class MethodChannelInternalHifiPlugin extends InternalHifiPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('internal_hifi_plugin');


  @override
  Future<String?> addToPlaylist(String uri) async {
    // TODO: implement addToPlaylist
    return await methodChannel.invokeMethod<String>('addSongToPlaylist',[uri]);
  }

  @override
  Future<String?> playPlaylist() async{
    // TODO: implement playPlaylist
    return await methodChannel.invokeMethod<String>('playPlaylist');
  }

  @override
  Future<void> stopPlaylist() async {
    // TODO: implement stopPlaylist
    await methodChannel.invokeMethod<String>('stopPlaylist');
  }

    @override
  Future<void> pausePlaylist() async {
    await methodChannel.invokeMethod<void>('pausePlaylist');
  }

  @override
  Future<void> nextTrack() async {
    await methodChannel.invokeMethod<void>('nextTrack');
  }

  @override
  Future<void> previousTrack() async {
    await methodChannel.invokeMethod<void>('previousTrack');
  }

  @override
  Future<void> seekTo(int durationMs) async {
    // Pass duration inside array
    await methodChannel.invokeMethod<void>('seekTo', [durationMs]);
  }

  @override
  Future<void> init() async {
        await methodChannel.invokeMethod<void>('init');
  }
  
  @override
  Future<BandLevels> getBandLevels() async {
    // TODO: implement getBandLevels
    return BandLevels.fromJson(jsonDecode(await methodChannel.invokeMethod<String>('getBandLevel') ?? "") as Map<String, dynamic>);
  }

  @override
  Future<void> setBandLevels(BandLevels bandLevels) async {
    // TODO: implement setBandLevels
    print("bandLevelsJson: ${bandLevels.toJson()}");
    await methodChannel.invokeMethod<void>('setBandLevel', [jsonEncode( bandLevels.toJson())]);
  }
  
}
