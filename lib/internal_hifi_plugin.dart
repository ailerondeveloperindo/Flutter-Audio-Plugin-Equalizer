
import 'internal_hifi_plugin_platform_interface.dart';

class InternalHifiPlugin {

  Future<String?> addToPlaylist(String uri) {
    return InternalHifiPluginPlatform.instance.addToPlaylist(uri);
  }

  Future<String?> playPlaylist() {
    return InternalHifiPluginPlatform.instance.playPlaylist();
  }

    Future<void> stopPlaylist() {
    return InternalHifiPluginPlatform.instance.stopPlaylist();
  }
}
