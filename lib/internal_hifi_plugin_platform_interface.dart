import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'band_level_model.dart';
import 'internal_hifi_plugin_method_channel.dart';

abstract class InternalHifiPluginPlatform extends PlatformInterface {
  /// Constructs a InternalHifiPluginPlatform.
  InternalHifiPluginPlatform() : super(token: _token);

  static final Object _token = Object();

  static InternalHifiPluginPlatform _instance = MethodChannelInternalHifiPlugin();

  /// The default instance of [InternalHifiPluginPlatform] to use.
  ///
  /// Defaults to [MethodChannelInternalHifiPlugin].
  static InternalHifiPluginPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [InternalHifiPluginPlatform] when
  /// they register themselves.
  static set instance(InternalHifiPluginPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }


  Future<String?> addToPlaylist(String uri) {
    throw UnimplementedError('addToPlaylist(String uri) has not been implemented.');
  }

  Future<String?> playPlaylist() {
    throw UnimplementedError('playPlaylist has not been implemented.');
  }

  Future<void> stopPlaylist() {
    throw UnimplementedError('stopPlaylist has not been implemented.');
  }

  Future<void> pausePlaylist() {
    throw UnimplementedError('pausePlaylist has not been implemented.');
  }

  Future<void> nextTrack() {
    throw UnimplementedError('nextTrack has not been implemented.');
  }

  Future<void> previousTrack() {
    throw UnimplementedError('previousTrack has not been implemented.');
  }

  Future<void> forwardTrack(int durationMs) {
    throw UnimplementedError('forwardTrack has not been implemented.');
  }

  Future<void> reverseTrack(int durationMs) {
    throw UnimplementedError('reverseTrack has not been implemented.');
  }

  Future<BandLevels> getBandLevels() {
    throw UnimplementedError('getBandLevels has not been implemented.');
  }

  Future<void> setBandLevels(BandLevels bandLevels) {
    throw UnimplementedError('setBandLevels has not been implemented.');
}

}