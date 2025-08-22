package com.hifi.turntableplugin.internal_hifi_plugin

import io.flutter.plugin.common.MethodChannel

interface HifiPluginPlayer {
    fun addSongToPlaylist(uri: String)
    fun playPlaylist()
    fun stopPlaylist()
    fun pausePlaylist()
    fun nextTrack()
    fun previousTrack()
    fun forwardTrack(duration : Int)
    fun reverseTrack(duration : Int)
    suspend fun getMetadataWithoutPlayback(uri: String)

}