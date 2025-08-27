class PluginConstants {
  static const String _baseEventChannelName = 'internal_hifi_plugin_baseEventChannel';
  String posTrackEventChannel = '$_baseEventChannelName/posTrackEventChannel';
  String playStateEventChannel = '$_baseEventChannelName/playStateEventChannel';
  String deviceStateEventChannel = '$_baseEventChannelName/deviceStateEventChannel';
  String errorEventChannel = '$_baseEventChannelName/errorEventChannel';
  String metadataEventChannel = '$_baseEventChannelName/metadataEventChannel';
  // Player state constants
  int STATE_IDLE = 1;
  int STATE_BUFFERING = 2;
  int STATE_READY = 3;
  int STATE_ENDED = 4;
}