package com.hifi.turntableplugin.internal_hifi_plugin

import android.media.audiofx.Equalizer

class AudioProcessor(private var eq: Equalizer)  {

    fun getPresets()
    {

    }

    fun getAvailableEQBands() : Short
    {
        return eq.numberOfBands
    }
}