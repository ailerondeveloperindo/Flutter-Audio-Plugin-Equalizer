package com.hifi.turntableplugin.internal_hifi_plugin

//import androidx.media3.common.Player
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioManager
import android.media.MediaMetadataRetriever
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.DefaultLoadControl
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.LoadControl
import androidx.media3.exoplayer.MetadataRetriever
import androidx.media3.exoplayer.upstream.DefaultAllocator
import com.hifi.turntableplugin.internal_hifi_plugin.models.BandLevels
import com.hifi.turntableplugin.internal_hifi_plugin.models.DeviceStateModel
import io.flutter.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.FlutterPlugin.FlutterPluginBinding
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.guava.await
import kotlinx.serialization.json.Json
import java.io.IOException


@OptIn(UnstableApi::class)
class InternalHifiPlugin : FlutterPlugin, MethodCallHandler, ActivityAware, BroadcastReceiver() {
    private lateinit var positionTrackingChannel: EventChannel
    private lateinit var playStateChannel: EventChannel
    private lateinit var metaDataChannel: EventChannel
    private lateinit var deviceChannel: EventChannel
    protected var players: List<ExoPlayer> = emptyList() //TODO: Generate Player object through factory
    private var player: ExoPlayer? = null
    private lateinit var lifecycle: Lifecycle


    private lateinit var context: Context
    private var eventsPositionTracking: EventChannel.EventSink? = null
    private var eventsPlayerState: EventChannel.EventSink? = null
    private var eventsMetadata: EventChannel.EventSink? = null
    private var deviceState: EventChannel.EventSink? = null
    private lateinit var channel: MethodChannel
    private lateinit var audioProcessor: AudioProcessor
    private lateinit var binding: FlutterPluginBinding


    private fun getAudioVolume(): Float {
        val audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val currentVolume: Int = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val desiredValue = currentVolume / maxVolume.toFloat()
        return desiredValue
    }

    private fun initializePlayer(loadControl: LoadControl, binding: FlutterPluginBinding): ExoPlayer {
        var player: ExoPlayer = ExoPlayer.Builder(binding.applicationContext).setLoadControl(loadControl).build()
        player.addListener(
            PluginPlayerListener(
                player = player,
                lifecycle = lifecycle,
                onListenVolumeChange = { deviceStateJson -> deviceState?.success(deviceStateJson) },
                onListenPositionChange = { positionStateJson -> eventsPositionTracking?.success(positionStateJson) },
                onListenMediaMetadataChange = { mediaMetadata -> eventsMetadata?.success(mediaMetadata)})
        )
        return player;
    }

    @kotlin.OptIn(ExperimentalCoroutinesApi::class)
    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "internal_hifi_plugin")
        channel.setMethodCallHandler(this)
        context = flutterPluginBinding.applicationContext
        binding = flutterPluginBinding
        positionTrackingChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.posTrackEventChannel
        )
        playStateChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.playStateEventChannel
        )

        metaDataChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.metadataEventChannel
        )

        deviceChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            PluginConstants.deviceStateEventChannel
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

        deviceChannel.setStreamHandler(object : EventChannel.StreamHandler {
            override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
                Log.d("deviceChannel", "Setting Up EventChannel")
                if (player != null) {
                    deviceState = events
                    player!!.volume = getAudioVolume()
                    val deviceStates = DeviceStateModel()
                    deviceStates.volume = player!!.volume
                    deviceStates.isMuted = player!!.isDeviceMuted
                    deviceState?.success(Json.encodeToString(deviceStates))
                    Log.d("deviceState model", deviceStates.toString())
                }
            }

            override fun onCancel(arguments: Any?) {
                Log.d("deviceChannel", "Cancelling EventChannel")
                deviceState = null
            }

        })

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onMethodCall(call: MethodCall, result: Result) {
        try {
            when (call.method) {
                "init" -> {
                    var loadControl: LoadControl = DefaultLoadControl.Builder()
                        .setAllocator(DefaultAllocator(true, 16))
                        .setBufferDurationsMs(
                            1_000, // minBufferMs
                            20_000, // maxBufferMs
                            500,  // bufferForPlaybackMs
                            1_000   // bufferForPlaybackAfterRebufferMs
                        )
                        .setTargetBufferBytes(-1) // Default
                        .setPrioritizeTimeOverSizeThresholds(true)
                        .build()
                    player = initializePlayer(loadControl, binding);
                }

                "addSongToPlaylist" -> {
                    addSongToPlaylist((call.arguments as ArrayList<*>)[0].toString())
                    result.success("Song added to playlist")
                }

                "playPlaylist" -> {
                    playPlaylist()
                    result.success("Play Track")
                }

                "stopPlaylist" -> {
                    stopPlaylist()
                    result.success("Stop Track")
                }

                "pausePlaylist" -> {
                    pausePlaylist()
                    result.success("Pause Track")
                }

                "nextTrack" -> {
                    nextTrack()
                    result.success("Next Track")
                }

                "previousTrack" -> {
                    previousTrack()
                    result.success("Previous Track")
                }

                "seekTo" -> {
                    // expect argument in milliseconds
                    val duration = (call.arguments as ArrayList<*>)[0] as Int ?: 5000
                    seekTo(duration)
                    result.success("Seeked To by $duration ms")
                }


                "setBandLevel" -> {
                    Log.d("setBandLevel methodCall", (call.arguments as ArrayList<*>)[0].toString())
                    audioProcessor.setBandDetails(
                        Json.decodeFromString(
                            BandLevels.serializer(),
                            (call.arguments as ArrayList<*>)[0].toString()
                        )
                    )
                }

                "getBandLevel" -> {
                    Log.d("getBandLevel methodCall", Json.encodeToString(audioProcessor.getBandDetails()))
                    result.success(Json.encodeToString(audioProcessor.getBandDetails()))
                }

                "setRepeatMode" -> {
                    setRepeatMode((call.arguments as ArrayList<*>)[0] as Int)
                }

                else -> result.notImplemented()
            }
        } catch (e: Exception) {
            Log.e("HifiPluginPlayer Error", e.message.toString())
            result.error("c", e.message, "error")
        }

    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
    }


    fun addSongToPlaylist(uri: String) {
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
        player!!.addMediaItem(mediaItem)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun playPlaylist() {
        if (!player!!.isPlaying) {
//            eq = Equalizer(0, player.audioSessionId)
//            eq.usePreset(2)
//            Log.d("Equalizer ---", eq.numberOfPresets.toString())
//            Log.d("Equalizer Current Preset ---", eq.currentPreset.toString())
            player!!.prepare()
            player!!.play()
        }
    }


    private fun stopPlaylist() {
        try {
            if (player!!.isPlaying) {
                player!!.stop()
            }
        } catch (e: Exception) {
            Log.e("HifiPluginPlayer Error", e.message.toString())
        }

    }

    private fun pausePlaylist() {
        if (player!!.isPlaying) {
            player!!.stop()
        }
    }

    private fun nextTrack() {
        player!!.seekToNextMediaItem()
    }

    private fun previousTrack() {
        player!!.seekToPreviousMediaItem()
    }


    private fun seekTo(duration: Int) {
        player!!.seekTo(duration.toLong())
    }

    fun setVolume(volume: Float) {
        TODO("Not yet implemented")
    }

    private fun setRepeatMode(repeatMode: Int) {
        if (player!!.isCommandAvailable(Player.COMMAND_SET_REPEAT_MODE)) {
            if (repeatMode == Player.REPEAT_MODE_OFF || repeatMode == Player.REPEAT_MODE_ONE || repeatMode == Player.REPEAT_MODE_ALL) {
                player!!.repeatMode = repeatMode
            } else {
                // Throw Error
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(UnstableApi::class)
    suspend fun getMetadataWithoutPlayback(uri: String) {
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
        lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(binding)
        lifecycle.addObserver(LifecycleEventObserver { x, _ ->
            if (x.lifecycle.currentState == Lifecycle.State.CREATED) {
                val filter: IntentFilter = IntentFilter("android.media.VOLUME_CHANGED_ACTION")
                context.registerReceiver(this, filter)
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
        // TODO: unregister observers and broadcastReceivers
        context.unregisterReceiver(this)
        TODO("Not yet implemented")
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            if (intent.action == "android.media.VOLUME_CHANGED_ACTION") {
                player!!.volume = getAudioVolume()
            }
        }
    }
}
