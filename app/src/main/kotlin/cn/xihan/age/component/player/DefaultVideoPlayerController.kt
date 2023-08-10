package cn.xihan.age.component.player

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.media3.common.*
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.hls.HlsMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import cn.xihan.age.network.SPSettings
import cn.xihan.age.util.OkHttpDns
import cn.xihan.age.util.VideoPlayerCacheManager
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.FlowDebouncer
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.extension.logError
import cn.xihan.age.util.extension.set
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

@UnstableApi
@OptIn(InternalCoroutinesApi::class)
internal class DefaultVideoPlayerController(
    context: Context,
    private val initialState: VideoPlayerState,
    private val coroutineScope: CoroutineScope
) : VideoPlayerController, Player.Listener {

    private val _state = MutableStateFlow(initialState)
    override val state: StateFlow<VideoPlayerState>
        get() = _state.asStateFlow()

    /**
     * Some properties in initial state are not applicable until player is ready.
     * These are kept in this container. Once the player is ready for the first time,
     * they are applied and removed.
     */
    private var initialStateRunner: (() -> Unit)? = {
        exoPlayer.seekTo(initialState.currentPosition)
    }

    fun <T> currentState(filter: (VideoPlayerState) -> T): T {
        return filter(_state.value)
    }

    @Composable
    fun collect(): State<VideoPlayerState> {
        return _state.collectAsState()
    }

    @SuppressLint("StateFlowValueCalledInComposition")
    @Composable
    fun <T> collect(filter: VideoPlayerState.() -> T): State<T> {
        return remember(filter) {
            _state.map { it.filter() }
        }.collectAsState(
            initial = _state.value.filter()
        )
    }

    var videoPlayerBackgroundColor: Int = DefaultVideoPlayerBackgroundColor.value.toInt()
        set(value) {
            field = value
            playerView?.setBackgroundColor(value)
        }

    private lateinit var source: VideoPlayerSource
    private var playerView: PlayerView? = null

    private var updateDurationAndPositionJob: Job? = null

    private val httpDataSourceFactory by lazy {
        OkHttpDataSource.Factory(
            OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
//                .dns(OkHttpDns())
                .build()
        )
    }

    /**
     * Internal exoPlayer instance
     */
    private val exoPlayer = ExoPlayer.Builder(context)
        .apply {
            val cache = VideoPlayerCacheManager.getCache()
            if (cache != null) {
                val cacheDataSourceFactory = CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(
                        DefaultDataSource.Factory(
                            context,
                            httpDataSourceFactory
                        )
                    )
                setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            }
        }
        .build().apply {
            playWhenReady = initialState.isPlaying
//        addAnalyticsListener(EventLogger())
        }

    /**
     * Not so efficient way of showing preview in video slider.
     */
    private val previewExoPlayer = ExoPlayer.Builder(context)
        .apply {
            val cache = VideoPlayerCacheManager.getCache()
            if (cache != null) {
                val cacheDataSourceFactory = CacheDataSource.Factory()
                    .setCache(cache)
                    .setUpstreamDataSourceFactory(
                        DefaultDataSource.Factory(
                            context,
                            httpDataSourceFactory
                        )
                    )
                setMediaSourceFactory(DefaultMediaSourceFactory(cacheDataSourceFactory))
            }
        }.build().apply {
            playWhenReady = false
        }

    private val previewSeekDebouncer = FlowDebouncer<Long>(200L)

    init {
        exoPlayer.playWhenReady = initialState.isPlaying

        coroutineScope.launch {
            previewSeekDebouncer.collect { position ->
//                logDebug("Seeking to $position")
                previewExoPlayer.seekTo(position)
            }
        }
    }

    /**
     * A flag to indicate whether source is already set and waiting for
     * playerView to become available.
     */
    private val waitPlayerViewToPrepare = AtomicBoolean(false)

    override fun play() {
        if (exoPlayer.playbackState == Player.STATE_ENDED) {
            exoPlayer.seekTo(0)
        }
        exoPlayer.playWhenReady = true
    }

    override fun pause() {
        exoPlayer.playWhenReady = false
    }

    override fun playPauseToggle() {
        if (exoPlayer.isPlaying) pause() else play()
    }

    override fun quickSeekForward() {
        if (_state.value.quickSeekAction.direction != QuickSeekDirection.None) {
            // Currently animating
            return
        }
        val target =
            (exoPlayer.currentPosition + (SPSettings.playSkipTime * 1000)).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(target)
        updateDurationAndPosition()
        _state.set { copy(quickSeekAction = QuickSeekAction.forward()) }
    }

    override fun quickSeekRewind() {
        if (_state.value.quickSeekAction.direction != QuickSeekDirection.None) {
            // Currently animating
            return
        }
        val target = (exoPlayer.currentPosition - (SPSettings.playSkipTime * 1000)).coerceAtLeast(0)
        exoPlayer.seekTo(target)
        updateDurationAndPosition()
        _state.set { copy(quickSeekAction = QuickSeekAction.rewind()) }
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        updateDurationAndPosition()
    }

    override fun setVolume(leftVolume: Float, rightVolume: Float) {
        exoPlayer.volume = leftVolume
        exoPlayer.volume = rightVolume
        _state.set { copy(volume = leftVolume) }
    }

    override fun setSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed, 1.0f)
        _state.set { copy(speed = speed) }
    }

    override fun release() {
        exoPlayer.release()
        previewExoPlayer.release()
    }

    override fun setSource(source: VideoPlayerSource) {
        this.source = source
        if (playerView == null) {
            waitPlayerViewToPrepare.set(true)
        } else {
            prepare()
        }
    }

    override fun reset() {
        exoPlayer.stop()
        previewExoPlayer.stop()
    }

    override fun onPlayWhenReadyChanged(playWhenReady: Boolean, playbackState: Int) {
        _state.set {
            copy(
                isPlaying = playWhenReady,
                playbackState = PlaybackState.of(playbackState),
                playEnded = PlaybackState.of(playbackState) == PlaybackState.ENDED,
                playReady = PlaybackState.of(playbackState) == PlaybackState.READY
            )
        }
    }

    override fun onVideoSizeChanged(videoSize: VideoSize) {
        super.onVideoSizeChanged(videoSize)
        logDebug("width: ${videoSize.width}, height: ${videoSize.height}")
        _state.set {
            copy(videoSize = videoSize.width.toFloat() to videoSize.height.toFloat())
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (PlaybackState.of(playbackState) == PlaybackState.READY) {
            initialStateRunner = initialStateRunner?.let {
                it.invoke()
                null
            }

            updateDurationAndPositionJob?.cancel()
            updateDurationAndPositionJob = coroutineScope.launch {
                while (this.isActive) {
                    updateDurationAndPosition()
                    delay(250)
                }
            }
        }

        _state.set {
            copy(
                isPlaying = exoPlayer.isPlaying,
                playbackState = PlaybackState.of(playbackState),
                playEnded = PlaybackState.of(playbackState) == PlaybackState.ENDED,
                playReady = PlaybackState.of(playbackState) == PlaybackState.READY
            )
        }
    }

    override fun onPlayerError(error: PlaybackException) {
//        TipDialog.show(error.message, WaitDialog.TYPE.ERROR)
        _state.set { copy(error = AgeException.SnackBarException("${error.message}")) }
//        logError("Player error: ${error.message}")
    }

    fun enableGestures(isEnabled: Boolean) {
        _state.set { copy(gesturesEnabled = isEnabled) }
    }

    fun enableControls(enabled: Boolean) {
        _state.set { copy(controlsEnabled = enabled) }
    }

    fun showControls() {
        _state.set { copy(controlsVisible = true) }
    }

    fun hideControls() {
        _state.set { copy(controlsVisible = false) }
    }

    fun setDraggingProgress(draggingProgress: DraggingProgress?) {
        _state.set { copy(draggingProgress = draggingProgress) }
    }

    fun setQuickSeekAction(quickSeekAction: QuickSeekAction) {
        _state.set { copy(quickSeekAction = quickSeekAction) }
    }

    fun setHideBottomProgress(hideBottomProgress: Boolean) {
        _state.set { copy(hideBottomProgress = hideBottomProgress) }
    }

    fun setPlayEnded(playEnded: Boolean) {
        _state.set { copy(playEnded = playEnded) }
    }

    private fun updateDurationAndPosition() {
        _state.set {
            copy(
                duration = exoPlayer.duration.coerceAtLeast(0),
                currentPosition = exoPlayer.currentPosition.coerceAtLeast(0),
                secondaryProgress = exoPlayer.bufferedPosition.coerceAtLeast(0)
            )
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun prepare() {
        fun createVideoSource(): MediaSource {
            return when (val source = source) {
                is VideoPlayerSource.Raw -> {
                    ProgressiveMediaSource.Factory(httpDataSourceFactory).createMediaSource(
                        MediaItem.fromUri(
                            RawResourceDataSource.buildRawResourceUri(
                                source.resId
                            )
                        )
                    )
                }

                is VideoPlayerSource.Network -> {
                    if (source.url.contains("hls") || source.url.contains("m3u8")) {
                        HlsMediaSource.Factory(httpDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(source.url))
                    } else {
                        ProgressiveMediaSource.Factory(httpDataSourceFactory)
                            .createMediaSource(MediaItem.fromUri(source.url))
                    }
                }
            }
        }

        exoPlayer.setMediaSource(createVideoSource())
        exoPlayer.addListener(this)
        previewExoPlayer.setMediaSource(createVideoSource())

        exoPlayer.prepare()
        previewExoPlayer.prepare()
    }

    fun playerViewAvailable(playerView: PlayerView) {
        this.playerView = playerView
        playerView.player = exoPlayer
        playerView.setBackgroundColor(videoPlayerBackgroundColor)
        /**
         * 设置默认播放比例
         */
        setDisplayMode(PlayDisplayMode.of(SPSettings.playAspectRatio))

        if (waitPlayerViewToPrepare.compareAndSet(true, false)) {
            prepare()
        }
    }

    fun previewPlayerViewAvailable(playerView: PlayerView) {
        playerView.player = previewExoPlayer
    }

    fun previewSeekTo(position: Long) {
        // position is very accurate. Thumbnail doesn't have to be.
        // Roll to the nearest "even" integer.
        val seconds = position.toInt() / 1000
        val nearestEven = (seconds - seconds.rem(2)).toLong()
        coroutineScope.launch {
            previewSeekDebouncer.put(nearestEven * 1000)
        }
    }

    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun setDisplayMode(playDisplayMode: PlayDisplayMode) {
        playerView?.resizeMode = when (playDisplayMode) {
            PlayDisplayMode.FIT -> AspectRatioFrameLayout.RESIZE_MODE_FIT
            PlayDisplayMode.FIXED_WIDTH -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
            PlayDisplayMode.FIXED_HEIGHT -> AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
            PlayDisplayMode.FILL -> AspectRatioFrameLayout.RESIZE_MODE_FILL
            PlayDisplayMode.ZOOM -> AspectRatioFrameLayout.RESIZE_MODE_ZOOM
        }
        SPSettings.playAspectRatio = playDisplayMode.value
        _state.set { copy(playDisplayMode = playDisplayMode) }
    }

}

val DefaultVideoPlayerBackgroundColor = Color.Black