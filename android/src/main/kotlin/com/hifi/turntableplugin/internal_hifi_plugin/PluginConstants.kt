package com.hifi.turntableplugin.internal_hifi_plugin

object PluginConstants {
    private var baseEventChannelName: String = "internal_hifi_plugin_baseEventChannel"
    var posTrackEventChannel : String = this.baseEventChannelName + "/posTrackEventChannel"
    var playStateEventChannel : String = this.baseEventChannelName + "/playStateEventChannel"
    var deviceStateEventChannel : String = this.baseEventChannelName + "/deviceStateEventChannel"
    var metadataEventChannel : String = this.baseEventChannelName + "/metadataEventChannel"
    var errorEventChannel : String = this.baseEventChannelName + "/errorEventChannel"
}