package com.hifi.turntableplugin.internal_hifi_plugin.models

import kotlinx.serialization.Serializable

class AlbumArtworkModel {
    var songAlbumCoverBase64: String? = null
    var artworkDataType: String? = null
}

@Serializable
class SongMetadataModel {
    var songTitle: String? = null
    var songArtist: String? = null
    var songAlbum: String? = null
    var songDurationMs: String? = null
    var songSize: String? = null
    var songAuthor: String? = null
    var songAlbumCoverBase64: String? = null
}