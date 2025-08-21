#include "include/internal_hifi_plugin/internal_hifi_plugin_c_api.h"

#include <flutter/plugin_registrar_windows.h>

#include "internal_hifi_plugin.h"

void InternalHifiPluginCApiRegisterWithRegistrar(
    FlutterDesktopPluginRegistrarRef registrar) {
  internal_hifi_plugin::InternalHifiPlugin::RegisterWithRegistrar(
      flutter::PluginRegistrarManager::GetInstance()
          ->GetRegistrar<flutter::PluginRegistrarWindows>(registrar));
}
