package com.hifi.turntableplugin.internal_hifi_plugin

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.coroutineScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Metadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.hifi.turntableplugin.internal_hifi_plugin.models.DeviceStateModel
import com.hifi.turntableplugin.internal_hifi_plugin.models.ErrorModel
import com.hifi.turntableplugin.internal_hifi_plugin.models.PositionStateModel
import io.flutter.Log
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

/** InternalHifiPlugin */
class PluginPlayerListener(
    var player: ExoPlayer,
    var lifecycle: Lifecycle,
    private var onListenVolumeChange: (deviceStateJson: String) -> Unit,
    private var onListenMediaMetadataChange: (mediaMetadataJson: String) -> Unit,
    private var onListenPositionChange: (positionStateJson: String) -> Unit,
    private var onError: (errorJson: String) -> Unit,
) : Player.Listener {

    private fun positionTrackingFlow(): Flow<PositionStateModel?> = flow {
        while (true) {
            if (player.isPlaying) {

                emit(
                    PositionStateModel(
                        position = player.currentPosition.toInt(),
                        durationMs = PluginUtils.MetaDataUtils.getTrackDuration(player)
                    )
                )

            }
            delay(500)

        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        // Player.STATE_IDLE, Player.STATE_BUFFERING, Player.STATE_READY, Player.STATE_ENDED
        // Executes during playback state change
        Log.d("onPlaylistMetadataChanged", playbackState.toString())
        if (playbackState == Player.STATE_READY) {
            Log.d("PlaybackState", "Ended, No Mediaitem on playlist")
        } else if (playbackState == Player.STATE_ENDED) {

            if (player.repeatMode == Player.REPEAT_MODE_OFF) {
                player.playWhenReady = false
                player.seekTo(0, 0)
            }

        }
    }

    override fun onPlayerErrorChanged(error: PlaybackException?) {
        Log.e("onPlayerErrorChanged", error.toString())

    }

    override fun onAudioSessionIdChanged(audioSessionId: Int) {
//                audioProcessor = AudioProcessor(Equalizer(0, audioSessionId))

    }

    override fun onVolumeChanged(volume: Float) {
        Log.d("onDeviceVolumeChanged", volume.toString())
        val deviceStates = DeviceStateModel()
        deviceStates.volume = volume
        deviceStates.isMuted = player.isDeviceMuted
        onListenVolumeChange(Json.encodeToString(deviceStates))
    }


    override fun onPlaylistMetadataChanged(mediaMetadata: MediaMetadata) {
        Log.d("onPlaylistMetadataChanged", mediaMetadata.toString())
        //TODO: Extract this into separate method


    }

    /* Called when track is either in play or stop state */
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        Log.d("onIsPlayingChanged", isPlaying.toString())

    }

    override fun onRepeatModeChanged(repeatMode: Int) {
        super.onRepeatModeChanged(repeatMode)
    }

    override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
        //TODO: Fetch all metaDatas from MediaItems
        // This will be fired on mediaitem addition
        Log.d("onMetaItemTransition", mediaItem.toString())


    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        Log.d("onMediaMetadataChanged", mediaMetadata.toString())
        lifecycle.addObserver(LifecycleEventObserver { x, _ ->
            lifecycle.coroutineScope.launch {
                x.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    positionTrackingFlow().collect {
                        //TODO: Store Metadata and merge metadata tracking into position
                        //TODO: Add Player State
                        onListenPositionChange(Json.encodeToString(it))
                    }
                }

            }
        })
        // This will be fired on mediaitem change
        onListenMediaMetadataChange(Json.encodeToString(PluginUtils.MetaDataUtils.createMetaDataModels(mediaMetadata)))
    }

    override fun onPlayerError(error: PlaybackException) {
        // TODO: Send error to flutter
        Log.e("onPlayerError", error.toString())
        ErrorModel(message = error.message ?: "Unknown error", errorCode = error.errorCode, timestamp = error.timestampMs)
        onError(Json.encodeToString(error))
    }

    override fun onMetadata(metadata: Metadata) {
        // TODO: Retrieve album cover, title, etc
        Log.d("onMetadata", metadata.toString())

    }

    override fun onPositionDiscontinuity(
        oldPosition: Player.PositionInfo,
        newPosition: Player.PositionInfo,
        reason: Int
    ) {
        if (reason == Player.DISCONTINUITY_REASON_SEEK) {
            if (!player.isPlaying) {
                var currentPositionModel =
                    PositionStateModel(
                        position = player.currentPosition.toInt(),
                        durationMs = PluginUtils.MetaDataUtils.getTrackDuration(player)
                    )
                onListenPositionChange(Json.encodeToString(currentPositionModel))
            }
        }
    }
}