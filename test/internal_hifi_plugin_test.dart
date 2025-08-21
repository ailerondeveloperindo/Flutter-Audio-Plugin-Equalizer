// import 'package:flutter_test/flutter_test.dart';
// import 'package:internal_hifi_plugin/internal_hifi_plugin.dart';
// import 'package:internal_hifi_plugin/internal_hifi_plugin_platform_interface.dart';
// import 'package:internal_hifi_plugin/internal_hifi_plugin_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockInternalHifiPluginPlatform
//     with MockPlatformInterfaceMixin
//     implements InternalHifiPluginPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final InternalHifiPluginPlatform initialPlatform = InternalHifiPluginPlatform.instance;

//   test('$MethodChannelInternalHifiPlugin is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelInternalHifiPlugin>());
//   });

//   test('getPlatformVersion', () async {
//     InternalHifiPlugin internalHifiPlugin = InternalHifiPlugin();
//     MockInternalHifiPluginPlatform fakePlatform = MockInternalHifiPluginPlatform();
//     InternalHifiPluginPlatform.instance = fakePlatform;

//     expect(await internalHifiPlugin.getPlatformVersion(), '42');
//   });
// }
