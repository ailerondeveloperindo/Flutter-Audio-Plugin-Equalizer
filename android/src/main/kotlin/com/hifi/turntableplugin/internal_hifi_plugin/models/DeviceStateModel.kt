package com.hifi.turntableplugin.internal_hifi_plugin.models

import kotlinx.serialization.Serializable

@Serializable
class DeviceStateModel {
    var volume : Float = 0f
    var isMuted: Boolean = false
}