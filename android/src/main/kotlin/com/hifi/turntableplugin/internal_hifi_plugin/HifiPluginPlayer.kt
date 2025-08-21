package com.hifi.turntableplugin.internal_hifi_plugin

import io.flutter.plugin.common.MethodChannel

interface HifiPluginPlayer {
    fun addSongToPlaylist(uri: String)
    fun playPlaylist()
    fun stopPlaylist(result: MethodChannel.Result)
    suspend fun getMetadataWithoutPlayback(uri: String)

}