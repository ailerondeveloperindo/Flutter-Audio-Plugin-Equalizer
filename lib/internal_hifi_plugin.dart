
import 'package:flutter/services.dart';

import 'plugin_constants.dart';

import 'band_level_model.dart';
import 'models/device_state_model.dart';
import 'models/metadata_model.dart';
import 'internal_hifi_plugin_platform_interface.dart';

class InternalHifiPlugin {

  static PluginConstants constants = PluginConstants();
  EventChannel deviceStateChannel = EventChannel(
    constants.deviceStateEventChannel,
  );
  

  EventChannel metaDataChannel = EventChannel(
    InternalHifiPlugin.constants.metadataEventChannel,
  );
  
  EventChannel trackingPositionChannel = EventChannel(
    InternalHifiPlugin.constants.posTrackEventChannel,
  );


  Future<String?> addToPlaylist(String uri) {
    return InternalHifiPluginPlatform.instance.addToPlaylist(uri);
  }

  Future<String?> playPlaylist() {
    return InternalHifiPluginPlatform.instance.playPlaylist();
  }

  Future<void> stopPlaylist() {
    return InternalHifiPluginPlatform.instance.stopPlaylist();
  }

    Future<void> pausePlaylist() {
    return InternalHifiPluginPlatform.instance.pausePlaylist();
  }

  Future<void> nextTrack() {
    return InternalHifiPluginPlatform.instance.nextTrack();
  }

  Future<void> previousTrack() {
    return InternalHifiPluginPlatform.instance.previousTrack();
  }

  Future<void> forwardTrack(int durationMs) {
    return InternalHifiPluginPlatform.instance.forwardTrack(durationMs);
  }

  Future<void> reverseTrack(int durationMs) {
    return InternalHifiPluginPlatform.instance.reverseTrack(durationMs);
  }

  Future<BandLevels> getBandLevels() {
    return InternalHifiPluginPlatform.instance.getBandLevels();
  }

  Future<void> setBandLevels(BandLevels bandLevels) {
    return InternalHifiPluginPlatform.instance.setBandLevels(bandLevels);
  }
}
