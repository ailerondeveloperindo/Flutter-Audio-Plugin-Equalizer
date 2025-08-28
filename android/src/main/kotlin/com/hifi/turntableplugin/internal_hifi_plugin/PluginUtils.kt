package com.hifi.turntableplugin.internal_hifi_plugin

import android.util.Base64
import androidx.media3.common.MediaMetadata
import com.hifi.turntableplugin.internal_hifi_plugin.models.SongMetadataModel

class PluginUtils {
    object MetaDataUtils {
        fun createMetaDataModels(mediaMetadata: MediaMetadata) : SongMetadataModel
        {
            val metadataModel = SongMetadataModel()
            metadataModel.title = mediaMetadata.title?.toString()
            metadataModel.artist = mediaMetadata.artist?.toString()
            metadataModel.album = mediaMetadata.albumTitle?.toString()
            metadataModel.recordingYear = mediaMetadata.recordingYear
            metadataModel.recordingMonth = mediaMetadata.recordingMonth
            metadataModel.recordingDay = mediaMetadata.recordingDay
            metadataModel.releaseYear = mediaMetadata.releaseYear
            metadataModel.releaseMonth = mediaMetadata.releaseMonth
            metadataModel.releaseDay = mediaMetadata.releaseDay
            metadataModel.writer = mediaMetadata.writer?.toString()
            metadataModel.discNumber = mediaMetadata.discNumber
            metadataModel.totalDiscCount = mediaMetadata.totalDiscCount
            metadataModel.genre = mediaMetadata.genre?.toString()
            metadataModel.compilation = mediaMetadata.compilation?.toString()
            metadataModel.station = mediaMetadata.station?.toString()
            metadataModel.durationMs = mediaMetadata.durationMs ?: 0
            metadataModel.composer = mediaMetadata.composer?.toString()
            metadataModel.conductor = mediaMetadata.conductor?.toString()
            metadataModel.albumArtworkBase64 = if (mediaMetadata.artworkData != null) Base64.encodeToString(mediaMetadata.artworkData, Base64.DEFAULT) else null
            metadataModel.artworkDataType = mediaMetadata.artworkDataType
            metadataModel.artworkUri = mediaMetadata.artworkUri?.toString()
            metadataModel.trackNumber = mediaMetadata.trackNumber
            metadataModel.totalTrackCount = mediaMetadata.totalTrackCount
            return metadataModel
        }
    }
}