package com.hifi.turntableplugin.internal_hifi_plugin.models

import kotlinx.serialization.Serializable

@Serializable
class BandLevels(var bandLevels : List<BandLevelModel>? = null){
}

@Serializable
class BandLevelModel {
    var bandFrequency: IntArray? = null
    var bandLevel: Int = -1
    var bandLevelId: Short = 0
}