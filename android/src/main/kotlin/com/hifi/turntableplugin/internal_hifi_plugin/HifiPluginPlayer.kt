package com.hifi.turntableplugin.internal_hifi_plugin

import io.flutter.plugin.common.MethodChannel

interface HifiPluginPlayer {
    fun addSongToPlaylist(uri: String)
    fun playPlaylist()
    fun stopPlaylist()
    suspend fun getMetadataWithoutPlayback(uri: String)

}