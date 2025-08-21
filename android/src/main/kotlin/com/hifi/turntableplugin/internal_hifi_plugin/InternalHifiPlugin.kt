package com.hifi.turntableplugin.internal_hifi_plugin

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.audiofx.Equalizer
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.MetadataRetriever
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter
import io.flutter.embedding.engine.plugins.lifecycle.HiddenLifecycleReference
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException


/** InternalHifiPlugin */
@OptIn(UnstableApi::class)
class InternalHifiPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, HifiPluginPlayer {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var playStatusEventChannel: EventChannel
    private lateinit var positionTrackingChannel: EventChannel
    private fun positionTrackingFlow(): Flow<Long> = flow {
        while (true) {
            if(player.isPlaying)
            {
                emit(player.currentPosition)

            }
            kotlinx.coroutines.delay(1000)

        }

    }

    private lateinit var context: Context
    private var eventsStatus: EventChannel.EventSink? = null
    private var eventsPositionTracking: EventChannel.EventSink? = null
    private lateinit var player: ExoPlayer
    private lateinit var channel: MethodChannel
    private lateinit var eq: Equalizer

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        //TODO: Create channel for tracking currentPosition and currentTrack
        //TODO: Refractor to several methods, to ease development
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "internal_hifi_plugin")

        player = ExoPlayer.Builder(flutterPluginBinding.applicationContext).build()
        context = flutterPluginBinding.applicationContext
        player.addListener(object : Player.Listener {
//            override fun onEvents(player: Player, events: Player.Events) {
//                super.onEvents(player, events)
//            }

            override fun onPlayerErrorChanged(error: PlaybackException?) {
                super.onPlayerErrorChanged(error)
            }

            override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
                super.onPlaylistMetadataChanged(mediaMetadata)
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                super.onIsPlayingChanged(isPlaying)
            }

            override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
                //TODO: Get Mediaitem MetaData
                super.onMediaItemTransition(mediaItem, reason)
                eventsStatus?.success("a")
            }

            override fun onPlayerError(error: PlaybackException) {
                // TODO: Send error to flutter
                super.onPlayerError(error)
                eventsStatus?.error("b", error.message, "error")
            }

            override fun onMetadata(metadata: Metadata) {
                // TODO: Retrieve album cover, title, etc
                super.onMetadata(metadata)
            }
        })
        playStatusEventChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            "internal_hifi_plugin/play_status_events"
        )
        positionTrackingChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            "internal_hifi_plugin/positionTracking"
        )
        // https://blog.stackademic.com/understanding-event-channel-and-method-channel-in-flutter-and-dart-9134d6a8ceba
        playStatusEventChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                this@InternalHifiPlugin.eventsStatus = events
            }

            override fun onCancel(arguments: Any?) {
                this@InternalHifiPlugin.eventsStatus = null
            }
        })
        positionTrackingChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                this@InternalHifiPlugin.eventsPositionTracking = events
            }

            override fun onCancel(arguments: Any?) {
                this@InternalHifiPlugin.eventsPositionTracking = null
            }
        })

        /* TODO: Will this run on the main thread? Player should be on separate thread
            Currently, if the event position value is being tracked, it blocks the main UI thread preventing app to show the flutter UI
        */
        channel.setMethodCallHandler(this)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMethodCall(call: MethodCall, result: Result) {
        try {
            if (call.method == "addSongToPlaylist") {
                addSongToPlaylist(
                    "file://" + Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOWNLOADS
                    ) + "/04 Joy Spring.m4a"
                )
                result.success("hifiPluginPlayer initialized")
            } else if (call.method == "playPlaylist") {
                playPlaylist()
                result.success("Play Song")
            } else {
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
        val mediaItem =
            MediaItem.fromUri("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/04 Joy Spring.m4a")
        val mediaItem2 =
            MediaItem.fromUri("file://" + Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/04 Joy Spring.flac")
        player.addMediaItem(mediaItem)
        player.addMediaItem(mediaItem2)
        if (!player.isPlaying) {
            eq = Equalizer(0, player.audioSessionId)
            eq.usePreset(2)
            Log.d("Equalizer ---", eq.numberOfPresets.toString())
            Log.d("Equalizer Current Preset ---", eq.currentPreset.toString())
            player.prepare()
            player.play()
        }
    }

    override fun stopPlaylist(result: Result) {
        TODO("Not yet implemented")
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(UnstableApi::class)
    override suspend fun getMetadataWithoutPlayback(uri: String) {
        @OptIn(UnstableApi::class)
        fun handleMetadata(trackGroupArray: String) {
            println("tes123" + trackGroupArray)
        }

        // TODO: Call from separate thread
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
        lifecycle.addObserver(LifecycleEventObserver { x, event ->
            lifecycle.coroutineScope.launch {
                x.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    positionTrackingFlow().collect {
                        Log.i("HifiPlugin", "Current Position $it")
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
