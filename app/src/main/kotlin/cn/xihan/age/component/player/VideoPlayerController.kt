package cn.xihan.age.component.player

import kotlinx.coroutines.flow.StateFlow

interface VideoPlayerController {

    fun setSource(source: VideoPlayerSource)

    fun play()

    fun pause()

    fun playPauseToggle()

    fun quickSeekForward()

    fun quickSeekRewind()

    fun seekTo(position: Long)

    fun setVolume(leftVolume: Float, rightVolume: Float)

    fun setSpeed(speed: Float)

    fun release()

    fun reset()

    val state: StateFlow<VideoPlayerState>
}
