#ifndef FLUTTER_PLUGIN_INTERNAL_HIFI_PLUGIN_H_
#define FLUTTER_PLUGIN_INTERNAL_HIFI_PLUGIN_H_

#include <flutter/method_channel.h>
#include <flutter/plugin_registrar_windows.h>

#include <memory>

namespace internal_hifi_plugin {

class InternalHifiPlugin : public flutter::Plugin {
 public:
  static void RegisterWithRegistrar(flutter::PluginRegistrarWindows *registrar);

  InternalHifiPlugin();

  virtual ~InternalHifiPlugin();

  // Disallow copy and assign.
  InternalHifiPlugin(const InternalHifiPlugin&) = delete;
  InternalHifiPlugin& operator=(const InternalHifiPlugin&) = delete;

  // Called when a method is called on this plugin's channel from Dart.
  void HandleMethodCall(
      const flutter::MethodCall<flutter::EncodableValue> &method_call,
      std::unique_ptr<flutter::MethodResult<flutter::EncodableValue>> result);
};

}  // namespace internal_hifi_plugin

#endif  // FLUTTER_PLUGIN_INTERNAL_HIFI_PLUGIN_H_
