package com.hifi.turntableplugin.internal_hifi_plugin

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Environment
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.Timeline
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.source.TrackGroupArray
import io.flutter.Log
import io.flutter.plugin.common.MethodChannel
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.runBlocking
import java.io.IOException

// TODO: Internal HifiPlugin should extends HifiPluginPLayerImpl
class HifiPluginPlayerImpl
    (private var context: Context) : HifiPluginPlayer{

    private lateinit var player: ExoPlayer

    override fun addSongToPlaylist(uri: String)
    {
        val mediaItem = MediaItem.fromUri(uri)
        // TODO: Check for duplicate songs
//        if(player.mediaItemCount > 0)
//        {
//            for(i in 0 until player.mediaItemCount)
//            {
//                if(player.getMediaItemAt(i) == mediaItem)
//                {
//                    return
//                }
//            }
//        }
        player.addMediaItem(mediaItem)
    }

    override fun playPlaylist()
    {
        if(!player.isPlaying)
        {
//            if(player.mediaItemCount != 0)
//            {
                player.play()
//            }
//            else
//            {
//                Log.e("HifiPluginPlayer Error", "No songs in playlist")
//            }

        }
    }

    override fun stopPlaylist()
    {
        if(player.isPlaying)
        {
            player.stop()
        }
        else
        {
            Log.e("HifiPluginPlayer Error", "Player has already been stopped")
        }
    }

    @OptIn(UnstableApi::class)
    override suspend fun getMetadataWithoutPlayback(uri: String)
    {
        @OptIn(UnstableApi::class)
        fun handleMetadata(trackGroupArray: String)
        {
            println("tes123" + trackGroupArray)
        }

        // TODO: Call from separate thread
        try {
            val mediaItem = MediaItem.fromUri(uri)
            MetadataRetriever.Builder(context, mediaItem).build().use { metadataRetriever ->
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(uri)
                val artist = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val title = metadataRetriever.retrieveDurationUs().await()
                if (artist != null) {
                    return handleMetadata(artist)
                }
            }
        }
        catch (e : IOException)
        {
            Log.e("HifiPluginPlayer Error", e.message.toString())
        }
    }


}