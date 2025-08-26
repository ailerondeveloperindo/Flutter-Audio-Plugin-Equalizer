package com.hifi.turntableplugin.internal_hifi_plugin.models

import kotlinx.serialization.Serializable

@Serializable
class DeviceStateModel {
    var volume : Int = 0
    var isMuted: Boolean = false
}