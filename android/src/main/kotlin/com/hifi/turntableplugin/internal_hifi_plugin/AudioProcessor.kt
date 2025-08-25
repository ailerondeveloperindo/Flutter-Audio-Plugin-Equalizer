package com.hifi.turntableplugin.internal_hifi_plugin

import android.media.audiofx.Equalizer
import androidx.media3.common.util.Log
import com.hifi.turntableplugin.internal_hifi_plugin.models.BandLevelModel
import com.hifi.turntableplugin.internal_hifi_plugin.models.BandLevels

class AudioProcessor(var eq: Equalizer)  {
    //TODO: Custom Native Implementation with Oboe
    fun getBandDetails() : BandLevels {
        var bandLevelList = mutableListOf<BandLevelModel>()
        for (i in 0 until eq.numberOfBands)
        {
            var bandLevelModel = BandLevelModel()
            bandLevelModel.bandLevel = eq.getBandLevel(i.toShort()).toInt()
            bandLevelModel.bandFrequency = eq.getBandFreqRange(i.toShort())
            bandLevelList.add(bandLevelModel)
        }
        return BandLevels(bandLevels = bandLevelList)
    }

    fun setBandDetails(bandDetails: BandLevels) {
        if(bandDetails.bandLevels != null)
        {
            bandDetails.bandLevels!!.forEach { bandLevelModel ->
                eq.setBandLevel(bandLevelModel.bandLevelId,bandLevelModel.bandLevel.toShort())
            }
        }
        else
        {
            //TODO: return error
        }

    }
}