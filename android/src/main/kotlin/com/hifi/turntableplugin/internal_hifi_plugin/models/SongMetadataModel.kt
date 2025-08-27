package com.hifi.turntableplugin.internal_hifi_plugin.models

import android.net.Uri
import androidx.media3.common.MediaMetadata.PictureType
import kotlinx.serialization.Serializable

@Serializable
class AlbumArtworkModel {
    var songAlbumCoverBase64: String? = null
    var artworkDataType: String? = null
}

@Serializable
class SongMetadataModel {
    var title: String? = null
    var artist: String? = null
    var album: String? = null
    var recordingYear: Int? = null
    var recordingMonth: Int? = null
    var recordingDay: Int? = null
    var releaseYear: Int? = null
    var releaseMonth: Int? = null
    var releaseDay: Int? = null
    var writer: String? = null
    var discNumber: Int? = null
    var totalDiscCount: Int? = null
    var genre: String? = null
    var compilation: String? = null
    var station: String? = null
    var durationMs: Long = 0
    var size: String? = null
    var author: String? = null
    var composer: String? = null
    var userRating: Int? = null
    var overallRating: Int? = null
    var conductor: String? = null
    var albumArtworkBase64: String? = null
    var artworkDataType: Int? = null
    var artworkUri: String? = null
    var trackNumber: Int? = null
    var totalTrackCount: Int? = null
}