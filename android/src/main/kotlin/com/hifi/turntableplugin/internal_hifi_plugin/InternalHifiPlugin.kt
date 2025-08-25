package com.hifi.turntableplugin.internal_hifi_plugin

import android.content.Context
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
//import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.MetadataRetriever
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import java.io.IOException


/** InternalHifiPlugin */
@OptIn(UnstableApi::class)
class InternalHifiPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, HifiPluginPlayer {
    private lateinit var positionTrackingChannel: EventChannel
    private lateinit var playStateChannel: EventChannel
    private lateinit var metaDataChannel: EventChannel
    protected lateinit var player: ExoPlayer
    private fun positionTrackingFlow(): Flow<Long> = flow {
        while (true) {
            if (player.isPlaying) {
                emit(player.currentPosition)

            }
            kotlinx.coroutines.delay(1000)

        }

    }

    private lateinit var context: Context
    private var eventsPositionTracking: EventChannel.EventSink? = null
    private var eventsPlayerState: EventChannel.EventSink? = null
    private var eventsMetadata: EventChannel.EventSink? = null
    private lateinit var channel: MethodChannel
    private lateinit var audioProcessor: AudioProcessor

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "internal_hifi_plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        player = ExoPlayer.Builder(flutterPluginBinding.applicationContext).build()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                // Player.STATE_IDLE, Player.STATE_BUFFERING, Player.STATE_READY, Player.STATE_ENDED
                // Executes during playback state change
                Log.d("onPlaylistMetadataChanged", playbackState.toString())
                if(Player.STATE_READY == playbackState)
                {
                    Log.d("PlaybackState", "Ended, No Mediaitem on playlist")
                }
            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                Log.e("onPlayerErrorChanged", error.toString())

            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                Log.d("onPlaylistMetadataChanged", mediaMetadata.toString())

            }

            /* Called when track is either in play or stop state */
            override fun onIsPlayingChanged(isPlaying: Boolean) {
                Log.d("onIsPlayingChanged", isPlaying.toString())

            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                //TODO: Get Mediaitem MetaData
                Log.d("onMetaItemTransition", mediaItem.toString())


            }

            override fun onPlayerError(error: PlaybackException) {
                // TODO: Send error to flutter
                Log.e("onPlayerError", error.toString())

            }

            override fun onMetadata(metadata: Metadata) {
                // TODO: Retrieve album cover, title, etc
                Log.d("onMetadata", metadata.toString())

            }
        })

        positionTrackingChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.posTrackEventChannel
        )
        playStateChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.playStateEventChannel
        )

        metaDataChannel =  EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.playStateEventChannel
        )

        positionTrackingChannel.setStreamHandler(object : EventChannel.StreamHandler {
            // Emitted when the flutter side starts listening to this EventChannel
            override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                Log.d("TrackingChannel", "Setting Up EventChannel")
                eventsPositionTracking = events
            }

            override fun onCancel(arguments: Any?) {
                eventsPositionTracking = null
            }
        })

        playStateChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onCancel(arguments: Any?) {
                Log.d("PlayStateChannel", "Cancelling EventChannel")
                eventsPlayerState = null
            }

            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                Log.d("playStateChannel", "Setting Up EventChannel")
                eventsPlayerState = events
            }
        })

        metaDataChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                Log.d("metaDataChannel", "Setting Up EventChannel")
                eventsMetadata = events
            }

            override fun onCancel(arguments: Any?) {
                Log.d("MetaDataChannel", "Cancelling EventChannel")
                eventsMetadata = null
            }

        })
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMethodCall(call: MethodCall, result: Result) {
        try {
            if (call.method == "addSongToPlaylist") {
                addSongToPlaylist((call.arguments as ArrayList<*>)[0].toString())
                result.success("hifiPluginPlayer initialized")
            } else if (call.method == "playPlaylist") {
                playPlaylist()
                result.success("Play Track")
            } else if (call.method == "stopPlaylist") {
                stopPlaylist()
                result.success("Stop Track")
            } else if(call.method == "skipSong") {

            } else if(call.method == "pausePlaylist") {

            }
            else if(call.method == "nextTrack"){

            }
            else if(call.method == "previousTrack") {

            } else if(call.method == "forwardTrack"){

            } else if(call.method == "reverseTrack")
            else {
                result.notImplemented()
            }
        } catch (e: Exception) {
            Log.e("HifiPluginPlayer Error", e.message.toString())
            result.error("c", e.message, "error")
        }

    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    override fun addSongToPlaylist(uri: String) {
        val mediaItem = MediaItem.fromUri(uri)
        // TODO: Check for duplicate songs
        // TODO: Check if url invalid
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

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun playPlaylist() {
        if (!player.isPlaying) {
//            eq = Equalizer(0, player.audioSessionId)
//            eq.usePreset(2)
//            Log.d("Equalizer ---", eq.numberOfPresets.toString())
//            Log.d("Equalizer Current Preset ---", eq.currentPreset.toString())
            if(player.isCommandAvailable(Player.COMMAND_PREPARE))
            {
                player.prepare()
            }
            player.play()
        }
    }


    override fun stopPlaylist() {
        try {
            if (player.isPlaying) {
                player.stop()
            }
        } catch (e: Exception) {
            Log.e("HifiPluginPlayer Error", e.message.toString())
        }

    }

    override fun pausePlaylist() {
        TODO("Not yet implemented")
    }

    override fun nextTrack() {
        TODO("Not yet implemented")
    }

    override fun previousTrack() {
        TODO("Not yet implemented")
    }

    override fun forwardTrack(duration: Int) {
        TODO("Not yet implemented")
    }

    override fun reverseTrack(duration: Int) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(UnstableApi::class)
    override suspend fun getMetadataWithoutPlayback(uri: String) {
        @OptIn(UnstableApi::class)
        fun handleMetadata(trackGroupArray: String) {
            println("tes123" + trackGroupArray)
        }

        try {
            val mediaItem = MediaItem.fromUri(uri)
            val cx = MediaStore.Downloads.EXTERNAL_CONTENT_URI
            MetadataRetriever.Builder(context, mediaItem).build().use { metadataRetriever ->
                val mediaMetadataRetriever = MediaMetadataRetriever()
                mediaMetadataRetriever.setDataSource(uri)
                val artist =
                    mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST)
                val title = metadataRetriever.retrieveDurationUs().await()
                if (artist != null) {
                    return handleMetadata(artist)
                }
            }
        } catch (e: IOException) {
            Log.e("HifiPluginPlayer Error", e.message.toString())
        }
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        var lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding)
        //TODO: HifiPlugin should derive from LifecycleObserver class
        lifecycle.addObserver(LifecycleEventObserver { x, _ ->
            lifecycle.coroutineScope.launch {
                x.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    positionTrackingFlow().collect {
                        Log.d("Current Position Tracked", it.toString())
                        eventsPositionTracking?.success(it)
                    }
                }

            }
        })

        lifecycle.addObserver(LifecycleEventObserver { x, _ ->
            lifecycle.coroutineScope.launch {
                x.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    if (!player.isPlaying) {
                        eventsPositionTracking?.success(player.currentPosition)

                    }
                }

            }
        })
    }

    override fun onDetachedFromActivityForConfigChanges() {
        TODO("Not yet implemented")
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        TODO("Not yet implemented")
    }

    override fun onDetachedFromActivity() {
        TODO("Not yet implemented")
    }
}
