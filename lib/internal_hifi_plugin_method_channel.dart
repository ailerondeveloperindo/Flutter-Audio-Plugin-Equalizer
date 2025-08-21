import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'internal_hifi_plugin_platform_interface.dart';

/// An implementation of [InternalHifiPluginPlatform] that uses method channels.
class MethodChannelInternalHifiPlugin extends InternalHifiPluginPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('internal_hifi_plugin');

  @override
  Future<String?> getPlatformVersion() async {
    final version = await methodChannel.invokeMethod<String>('addSongToPlaylist');
    return version;
  }

  @override
  Future<String?> addToPlaylist(String uri) async {
    // TODO: implement addToPlaylist
    return await methodChannel.invokeMethod<String>('addSongToPlaylist');
  }

  @override
  Future<String?> playPlaylist() async{
    // TODO: implement playPlaylist
    return await methodChannel.invokeMethod<String>('playPlaylist');
  }
}
