package cn.xihan.age.ui.player

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.os.Build
import android.os.Parcelable
import android.util.Rational
import android.view.View
import android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
import android.view.Window
import androidx.annotation.Keep
import androidx.annotation.RawRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.common.Player.STATE_BUFFERING
import androidx.media3.common.Player.STATE_ENDED
import androidx.media3.common.Player.STATE_IDLE
import androidx.media3.common.Player.STATE_READY
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FILL
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_HEIGHT
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_FIXED_WIDTH
import androidx.media3.ui.AspectRatioFrameLayout.RESIZE_MODE_ZOOM
import androidx.media3.ui.PlayerView
import cn.xihan.age.R
import cn.xihan.age.component.LoadingDotsVariant
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.basicWhite
import cn.xihan.age.ui.theme.pink
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Settings
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.logDebug
import cn.xihan.age.util.mainThread
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.util.rememberSavableMutableStateOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.parcelize.Parcelize
import okhttp3.OkHttpClient
import java.io.File
import java.util.Objects
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.time.Duration.Companion.seconds

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/23 14:55
 * @介绍 :
 */
@UnstableApi
@Composable
fun AgeAnimePlayer(
    title: String,
    controller: VideoPlayerController,
    isFullscreen: Boolean,
    currentEpisode: Int,
    totalEpisodes: Int,
    modifier: Modifier = Modifier,
    handleLifecycle: Boolean = true,
    enablePip: Boolean = false,
    backgroundColor: Color = Color.Black,
    onEpisodeChange: (Int) -> Unit,
    onFullScreenChange: (Boolean) -> Unit,
    onBack: () -> Unit,
) {
    require(controller is DefaultVideoPlayerController) {
        "Use [rememberVideoPlayerController] to create an instance of [VideoPlayerController]"
    }

    CompositionLocalProvider(
        LocalContentColor provides Color.White,
        LocalVideoPlayerController provides controller
    ) {
        val context = LocalContext.current
//        val aspectRatio by controller.collect { videoSize.first / videoSize.second }
        var isLock by rememberSavableMutableStateOf(value = false)
        var lockVisible by rememberMutableStateOf(key1 = isFullscreen, value = true)
        val controlsVisible by controller.collect { controlsVisible }
        val isSeeking by controller.collect { isSeeking }
        var isEpisodesDrawerVisible by rememberMutableStateOf(key1 = isFullscreen, value = false)
        val coroutineScope = rememberCoroutineScope()
        val state by controller.collect()

        SideEffect {
            controller.enableControls(!isLock)
            controller.enableGestures(!isLock)
        }

        if (isLock) {
            LaunchedEffect(Unit) {
                controller.hideControls()
                delay(5.seconds)
                lockVisible = false
            }
        }

        if (lockVisible) {
            LaunchedEffect(Unit) {
                delay(5.seconds)
                lockVisible = false
            }
        }

        if (controlsVisible && !isSeeking) {
            LaunchedEffect(Unit) {
                delay(5.seconds)
                controller.hideControls()
            }
        }

        Box(
            modifier = modifier
        ) {

            if (state.secondaryProgress < 1L) {
                LoadingDotsVariant(
                    Modifier
                        .zIndex(1f)
                        .align(Alignment.Center)
                )
            }

            VideoPlayerSurface(
                modifier = Modifier
                    .clickable {
                        if (isLock) {
                            lockVisible = !lockVisible
                        }
                    }
                    .align(Alignment.Center),
                usePlayerController = false,
                enablePip = enablePip,
                handleLifecycle = handleLifecycle,
                onPipEntered = {

                }
            )

            MediaControlGestures(modifier = Modifier.matchParentSize())

            AnimatedVisibility(
                modifier = Modifier
                    .align(Alignment.TopCenter),
                visible = controlsVisible,
                enter = fadeIn(spring(stiffness = 4_000f)),
                exit = fadeOut(spring(stiffness = 1_000f))
            ) {
                TopController(
                    modifier = Modifier.background(
                        Brush.verticalGradient(listOf(Color.Black.copy(0.5f), Color.Transparent))
                    ),
                    title = title,
                    isFullscreen = isFullscreen,
                    onBack = onBack
                )
            }

            AnimatedVisibility(
                modifier = Modifier
                    .zIndex(1f)
                    .align(Alignment.CenterStart),
                visible = if (isLock) lockVisible && isFullscreen else lockVisible && isFullscreen && controlsVisible,
                enter = fadeIn(spring(stiffness = 4_000f)),
                exit = fadeOut(spring(stiffness = 1_000f))
            ) {
                Icon(
                    modifier = Modifier
                        .clickable {
                            isLock = !isLock
                        },
                    painter = painterResource(AgeAnimeIcons.Player.lock),
                    tint = if (isLock) pink else basicWhite,
                    contentDescription = null
                )
            }

            AnimatedVisibility(
                modifier = Modifier.align(Alignment.BottomCenter),
                visible = controlsVisible,
                enter = fadeIn(spring(stiffness = 4_000f)),
                exit = fadeOut(spring(stiffness = 1_000f))
            ) {
                BottomController(
                    modifier = Modifier.background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.5f)))
                    ),
                    isPaused = state.isPlaying.not(),
                    currentEpisode = currentEpisode,
                    totalEpisodes = totalEpisodes,
                    isFullscreen = isFullscreen,
                    onPlay = controller::play,
                    onPause = controller::pause,
                    onFullScreen = { onFullScreenChange(true) },
                    onEpisodeChange = onEpisodeChange,
                    onDisplayModeChange = {
                        val displayModel = PlayDisplayMode.of(Settings.playAspectRatio).value
                        val displayModelIndex =
                            if (displayModel + 1 == PlayDisplayMode.entries.size) 0 else displayModel + 1
                        controller.setDisplayMode(PlayDisplayMode.of(displayModelIndex))
                    },
                    onSpeedChange = {
                        val speed = PlaySpeed.indexOf(PlaySpeed.of(Settings.playSpeed))
                        val speedIndex = if (speed + 1 == PlaySpeed.entries.size) 0 else speed + 1
                        controller.setSpeed(PlaySpeed.of(speedIndex))
                    },
                    showEpisodesDrawer = {
                        controller.hideControls()
                        isEpisodesDrawerVisible = true
                    }
                )
            }

            AnimatedVisibility(
                visible = isEpisodesDrawerVisible,
                enter = slideInHorizontally { it / 2 },
                exit = slideOutHorizontally { it / 2 }
            ) {
                EpisodesDrawer(
                    currentEpisode = currentEpisode,
                    totalEpisodes = totalEpisodes,
                    onEpisodeChange = {
                        onEpisodeChange(it)
                        isEpisodesDrawerVisible = false
                    },
                    hideDrawer = {
                        controller.hideControls()
                        isEpisodesDrawerVisible = false
                    }
                )
            }

        }
    }

}

@UnstableApi
@Composable
fun MediaControlGestures(
    modifier: Modifier = Modifier
) {

    val controller = LocalVideoPlayerController.current

    val controlsEnabled by controller.collect { controlsEnabled }
    val gesturesEnabled by controller.collect { gesturesEnabled }
    val controlsVisible by controller.collect { controlsVisible }
    val quickSeekDirection by controller.collect { quickSeekAction.direction }
    val draggingProgress by controller.collect { draggingProgress }

    if (controlsEnabled && gesturesEnabled) {
        Box(
            modifier = modifier
                .draggingProgressOverlay(draggingProgress)
                .quickSeekAnimation(quickSeekDirection) {
                    controller.setQuickSeekAction(QuickSeekAction.none())
                }
        )
        {
            GestureBox()
        }
    }

}

@UnstableApi
@Composable
fun GestureBox(modifier: Modifier = Modifier) {
    val controller = LocalVideoPlayerController.current
    val controlsVisible by controller.collect { controlsVisible }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(controller) {
                var wasPlaying = true
                var totalOffset = Offset.Zero
                var diffTime = -1f

                var duration: Long = 0
                var currentPosition: Long = 0


                var seekJob: Job? = null

                fun resetState() {
                    totalOffset = Offset.Zero
                    controller.setDraggingProgress(null)
                }

                detectMediaPlayerGesture(
                    onDoubleTap = { doubleTapPosition ->
                        when {
                            doubleTapPosition.x < size.width * 0.4f -> {
                                controller.quickSeekRewind()
                            }

                            doubleTapPosition.x > size.width * 0.6f -> {
                                controller.quickSeekForward()
                            }

                            else -> {
                                controller.playPauseToggle()
                            }
                        }
                    },
                    onTap = {
                        if (controlsVisible) controller.hideControls() else controller.showControls()
                    },
                    onHorizontalDragStart = { offset ->
                        wasPlaying = controller.currentState { it.isPlaying }
                        controller.pause()

                        currentPosition = controller.currentState { it.position }
                        duration = controller.currentState { it.duration }

                        resetState()
                    },
                    onHorizontalDragEnd = {
                        if (wasPlaying) controller.play()
                        resetState()
                    },
                    onHorizontalDrag = { dragAmount: Float ->
                        seekJob?.cancel()

                        totalOffset += Offset(x = dragAmount, y = 0f)

                        val diff = totalOffset.x

                        diffTime = if (duration <= 60_000) {
                            duration.toFloat() * diff / size.width.toFloat()
                        } else {
                            60_000.toFloat() * diff / size.width.toFloat()
                        }

                        var finalTime = currentPosition + diffTime
                        if (finalTime < 0) {
                            finalTime = 0f
                        } else if (finalTime > duration) {
                            finalTime = duration.toFloat()
                        }
                        diffTime = finalTime - currentPosition

                        controller.setDraggingProgress(
                            DraggingProgress(
                                finalTime = finalTime,
                                diffTime = diffTime
                            )
                        )

                        seekJob = coroutineScope.launch {
                            delay(200)
                            controller.seekTo(finalTime.toLong())
                        }
                    },
                    onVerticalDragStart = { offset ->

                    },
                    onVerticalDragEnd = {
                    },
                    onVerticalDrag = { dragAmount: Float ->

                    }
                )
            }
            .then(modifier))
}

@Composable
fun TopController(
    title: String,
    isFullscreen: Boolean,
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .displayCutoutPadding(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier
                    .clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Button,
                        onClick = onBack
                    )
                    .padding(top = 15.dp, start = 15.dp, bottom = 15.dp),
                painter = painterResource(AgeAnimeIcons.Player.back),
                contentDescription = "back",
            )
            if (isFullscreen)
                Text(
                    text = title,
                    maxLines = 1,
                    style = MaterialTheme.typography.bodyMedium
                )
        }
        Icon(
            modifier = Modifier
                .clickable(
                    interactionSource = rememberMutableInteractionSource(),
                    indication = null,
                    role = Role.Button,
                    onClick = { /* TODO: menu */ }
                )
                .padding(top = 15.dp, end = 15.dp, bottom = 15.dp),
            painter = painterResource(AgeAnimeIcons.Player.more),
            contentDescription = "menu",
        )
    }
}

@UnstableApi
@Composable
private fun BottomController(
    modifier: Modifier = Modifier,
    isPaused: Boolean,
    currentEpisode: Int,
    totalEpisodes: Int,
    isFullscreen: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onFullScreen: () -> Unit,
    onDisplayModeChange: () -> Unit,
    onSpeedChange: () -> Unit,
    onEpisodeChange: (Int) -> Unit,
    showEpisodesDrawer: () -> Unit,
) {

    val controller = LocalVideoPlayerController.current
    val videoPlayerUiState by controller.collect()
    val totalDuration = formatMilliseconds(videoPlayerUiState.duration)
    val currentPos = formatMilliseconds(videoPlayerUiState.position, totalDuration.length > 5)
    if (isFullscreen) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .displayCutoutPadding()
                .padding(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currentPos,
                    color = basicWhite,
                    style = MaterialTheme.typography.labelSmall
                )
                with(videoPlayerUiState) {
                    MySeekBar(
                        progress = position,
                        max = duration,
                        enabled = controlsVisible && controlsEnabled,
                        modifier = Modifier.weight(1f),
                        onSeek = {
                            controller.previewSeekTo(it)
                        },
                        onSeekStopped = {
                            controller.seekTo(it)
                        },
                        secondaryProgress = secondaryProgress,
                        seekerPopup = {
                            AndroidView(
                                factory = { context ->
                                    PlayerView(context).apply {
                                        useController = false
                                        controller.previewPlayerViewAvailable(this)
                                    }
                                },
                                modifier = Modifier
                                    .height(96.dp)
                                    .width(96.dp * videoSize.first / videoSize.second)
                                    .background(Color.DarkGray)
                            )
                        }
                    )
                }
                Text(
                    text = totalDuration,
                    color = basicWhite,
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                    AnimatedPlayPauseButton(
                        isPaused = isPaused,
                        onPlay = onPlay,
                        onPause = onPause
                    )
                    Icon(
                        modifier = Modifier.clickable(
                            enabled = currentEpisode < totalEpisodes,
                            interactionSource = rememberMutableInteractionSource(),
                            indication = null,
                            role = Role.Button,
                            onClick = { onEpisodeChange(currentEpisode + 1) }
                        ),
                        painter = painterResource(AgeAnimeIcons.Player.playNext),
                        contentDescription = "play next",
                        tint = if (currentEpisode < totalEpisodes) basicWhite
                        else basicWhite.copy(0.6f)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.spacedBy(28.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayModeText = when (PlayDisplayMode.of(Settings.playAspectRatio)) {
                        PlayDisplayMode.FIT -> "自适应"
                        PlayDisplayMode.FIXED_WIDTH -> "固定宽"
                        PlayDisplayMode.FIXED_HEIGHT -> "固定高"
                        PlayDisplayMode.FILL -> "填充"
                        PlayDisplayMode.ZOOM -> "缩放"
                    }
                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = rememberMutableInteractionSource(),
                            indication = null,
                            role = Role.DropdownList,
                            onClick = onDisplayModeChange
                        ),
                        text = displayModeText,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    val playSpeedText = PlaySpeed.entries[Settings.playSpeed].value
                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = rememberMutableInteractionSource(),
                            indication = null,
                            role = Role.DropdownList,
                            onClick = onSpeedChange
                        ),
                        text = "${playSpeedText}x",
                        style = MaterialTheme.typography.bodyMedium
                    )

                    Text(
                        modifier = Modifier.clickable(
                            interactionSource = rememberMutableInteractionSource(),
                            indication = null,
                            role = Role.DropdownList,
                            onClick = showEpisodesDrawer
                        ),
                        text = "选集",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    } else {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(12.dp, 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AnimatedPlayPauseButton(
                isPaused = isPaused,
                onPlay = onPlay,
                onPause = onPause
            )
            with(videoPlayerUiState) {
                MySeekBar(
                    progress = position,
                    max = duration,
                    enabled = controlsVisible && controlsEnabled,
                    modifier = Modifier.weight(1f),
                    onSeek = {
                        controller.previewSeekTo(it)
                    },
                    onSeekStopped = {
                        controller.seekTo(it)
                    },
                    secondaryProgress = secondaryProgress,
                    seekerPopup = {
                        AndroidView(
                            factory = { context ->
                                PlayerView(context).apply {
                                    useController = false
                                    controller.previewPlayerViewAvailable(this)
                                }
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .width(48.dp * videoSize.first / videoSize.second)
                                .background(Color.DarkGray)
                        )
                    }
                )
            }

            Text(
                text = "$currentPos / $totalDuration",
                color = basicWhite,
                style = MaterialTheme.typography.labelSmall
            )

            Icon(
                modifier = Modifier.clickable(
                    interactionSource = rememberMutableInteractionSource(),
                    indication = null,
                    role = Role.Button,
                    onClick = onFullScreen
                ),
                painter = painterResource(AgeAnimeIcons.Player.expand),
                contentDescription = "full-screen",
                tint = basicWhite
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedVisibilityScope.EpisodesDrawer(
    currentEpisode: Int,
    totalEpisodes: Int,
    onEpisodeChange: (Int) -> Unit,
    hideDrawer: () -> Unit,
) {
    val isTablet = isTablet()

    Box(
        modifier = Modifier.displayCutoutPadding(),
        contentAlignment = Alignment.CenterEnd
    ) {
        Box(
            modifier = Modifier
                .animateEnterExit(enter = fadeIn(), exit = fadeOut())
                .fillMaxSize()
                .clickable(
                    interactionSource = rememberMutableInteractionSource(),
                    indication = null,
                    onClick = hideDrawer
                )
                .background(
                    Brush.horizontalGradient(
                        0f to Color.Black.copy(0f),
                        1f to Color.Black.copy(0.9f)
                    )
                ),
        )
        Column(
            modifier = Modifier
                .width(min(360.dp, LocalConfiguration.current.screenWidthDp.dp * 4 / 5))
                .padding(start = 15.dp, end = 15.dp, top = 15.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            val cellSize = if (isTablet) 64.dp else 50.dp
            Text(
                text = "选集",
                color = basicWhite.copy(0.65f),
                style = MaterialTheme.typography.bodyLarge,
            )
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Adaptive(cellSize),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                repeat(totalEpisodes) {
                    val episode = it + 1
                    item(episode) {
                        if (episode == currentEpisode) {
                            Box(
                                modifier = Modifier
                                    .requiredSize(cellSize)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.LightGray.copy(0.35f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = episode.toString(),
                                    color = basicWhite,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .requiredSize(cellSize)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.LightGray.copy(0.1f))
                                    .clickable(
                                        interactionSource = rememberMutableInteractionSource(),
                                        indication = null,
                                        role = Role.Button,
                                        onClick = { onEpisodeChange(episode) }
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = episode.toString(),
                                    color = basicWhite,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun AnimatedPlayPauseButton(
    isPaused: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
) {
    AnimatedContent(
        targetState = isPaused,
        transitionSpec = {
            (scaleIn(
                tween(delayMillis = 100),
                0.8f
            ) + fadeIn(tween(delayMillis = 100))).togetherWith(
                scaleOut(targetScale = 0.7f) + fadeOut()
            )
        }, label = ""
    ) { paused ->
        val rotation by transition.animateFloat(
            transitionSpec = { tween(400) },
            label = ""
        ) {
            when (it) {
                EnterExitState.PreEnter -> -55f
                EnterExitState.Visible -> 0f
                EnterExitState.PostExit -> 30f
            }
        }
        if (paused) {
            Icon(
                modifier = Modifier
                    .rotate(rotation)
                    .clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Switch,
                        onClick = onPlay
                    ),
                painter = painterResource(AgeAnimeIcons.Player.play),
                contentDescription = "play",
                tint = basicWhite
            )
        } else {
            Icon(
                modifier = Modifier
                    .rotate(rotation)
                    .clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Switch,
                        onClick = onPause
                    ),
                painter = painterResource(AgeAnimeIcons.Player.pause),
                contentDescription = "pause",
                tint = basicWhite
            )
        }
    }
}

@UnstableApi
@SuppressLint("ComposableLambdaParameterPosition", "ComposableLambdaParameterNaming")
@Composable
fun MySeekBar(
    progress: Long,
    max: Long,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    secondaryProgress: Long? = null,
    onSeek: (progress: Long) -> Unit = {},
    onSeekStarted: (startedProgress: Long) -> Unit = {},
    onSeekStopped: (stoppedProgress: Long) -> Unit = {},
    seekerPopup: @Composable () -> Unit = {},
    showSeekerDuration: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    secondaryColor: Color = Color.White.copy(alpha = 0.6f)
) {
    // if there is an ongoing drag, only dragging progress is evaluated.
    // when dragging finishes, given [progress] continues to be used.
    var onGoingDrag by rememberMutableStateOf(value = false)
    val indicatorSize = if (onGoingDrag) 24.dp else 16.dp
    val controller = LocalVideoPlayerController.current
    val controlsVisible by controller.collect { controlsVisible }
    val hideBottomProgress by controller.collect { hideBottomProgress }

    LaunchedEffect(onGoingDrag) {
        controller.setIsSeeking(onGoingDrag)
    }

    BoxWithConstraints(modifier = modifier.offset(y = indicatorSize / 2)) {
        if (progress >= max) return@BoxWithConstraints

        val boxWidth = constraints.maxWidth.toFloat()

        val percentage = remember(progress, max) {
            progress.coerceAtMost(max).toFloat() / max.toFloat()
        }

        val indicatorOffsetByPercentage = remember(percentage) {
            Offset(percentage * boxWidth, 0f)
        }

        // Indicator should be at "percentage" but dragging can change that.
        // This state keeps track of current dragging position.
        var indicatorOffsetByDragState by rememberMutableStateOf(value = Offset.Zero)

        val finalIndicatorOffset = remember(
            indicatorOffsetByDragState,
            indicatorOffsetByPercentage,
            onGoingDrag
        ) {
            val finalIndicatorPosition = if (onGoingDrag) {
                indicatorOffsetByDragState
            } else {
                indicatorOffsetByPercentage
            }
            finalIndicatorPosition.copy(x = finalIndicatorPosition.x.coerceIn(0f, boxWidth))
        }

        Column {

            // SEEK POPUP
            if (onGoingDrag) {
                var popupSize by rememberMutableStateOf(value = IntSize(0, 0))

                // popup seeker must center the actual seeker position. Therefore, we offset
                // it negatively to the left.
                val popupSeekerOffsetXDp = with(LocalDensity.current) {
                    (finalIndicatorOffset.x - popupSize.width / 2)
                        .coerceIn(0f, (boxWidth - popupSize.width))
                        .toDp()
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier
                        .offset(x = popupSeekerOffsetXDp)
                        .alpha(if (popupSize == IntSize.Zero) 0f else 1f)
                        .onGloballyPositioned {
                            if (popupSize != it.size) {
                                popupSize = it.size
                            }
                        }
                ) {
                    val indicatorProgressDurationString = formatMilliseconds(
                        ((finalIndicatorOffset.x / boxWidth) * max).toLong(),
                        false
                    )

                    Box(modifier = Modifier.shadow(4.dp)) {
                        seekerPopup()
                    }

                    if (showSeekerDuration) {
                        Text(
                            text = indicatorProgressDurationString,
                            style = TextStyle(
                                shadow = Shadow(
                                    blurRadius = 8f,
                                    offset = Offset(2f, 2f)
                                )
                            )
                        )
                    }
                }
            }

            if (hideBottomProgress) {
                if (controlsVisible) {
                    Box(modifier = Modifier.height(indicatorSize)) {
                        // SECONDARY PROGRESS
                        if (secondaryProgress != null) {
                            LinearProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.Center),
                                progress = secondaryProgress.coerceAtMost(max)
                                    .toFloat() / max.coerceAtLeast(1L).toFloat(),
                                color = secondaryColor
                            )
                        }

                        // SEEK INDICATOR
                        if (enabled) {
                            val (offsetDpX, offsetDpY) = with(LocalDensity.current) {
                                (finalIndicatorOffset.x).toDp() - indicatorSize / 2 to (finalIndicatorOffset.y).toDp()
                            }

                            val draggableState = rememberDraggableState(onDelta = { dx ->
                                indicatorOffsetByDragState = Offset(
                                    x = (indicatorOffsetByDragState.x + dx),
                                    y = indicatorOffsetByDragState.y
                                )

                                val currentProgress =
                                    (indicatorOffsetByDragState.x / boxWidth) * max
                                onSeek(currentProgress.toLong())
                            })

                            Row(
                                modifier = Modifier
                                    .matchParentSize()
                                    .draggable(
                                        state = draggableState,
                                        orientation = Orientation.Horizontal,
                                        startDragImmediately = true,
                                        onDragStarted = { downPosition ->
                                            onGoingDrag = true
                                            indicatorOffsetByDragState =
                                                indicatorOffsetByDragState.copy(x = downPosition.x)
                                            val newProgress =
                                                (indicatorOffsetByDragState.x / boxWidth) * max
                                            onSeekStarted(newProgress.toLong())
                                        },
                                        onDragStopped = {
                                            val newProgress =
                                                (indicatorOffsetByDragState.x / boxWidth) * max
                                            onSeekStopped(newProgress.toLong())
                                            indicatorOffsetByDragState = Offset.Zero
                                            onGoingDrag = false
                                        }
                                    )
                            ) {

                                Indicator(
                                    modifier = Modifier
                                        .offset(x = offsetDpX, y = offsetDpY)
                                        .size(indicatorSize)
                                )
                            }
                        }

                        // MAIN PROGRESS
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            progress = percentage,
                            color = color
                        )
                    }
                }
            } else {

                Box(modifier = Modifier.height(indicatorSize)) {
                    // SECONDARY PROGRESS
                    if (secondaryProgress != null) {
                        LinearProgressIndicator(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            progress = secondaryProgress.coerceAtMost(max)
                                .toFloat() / max.coerceAtLeast(1L).toFloat(),
                            color = secondaryColor
                        )
                    }

                    // SEEK INDICATOR
                    if (enabled) {
                        val (offsetDpX, offsetDpY) = with(LocalDensity.current) {
                            (finalIndicatorOffset.x).toDp() - indicatorSize / 2 to (finalIndicatorOffset.y).toDp()
                        }

                        val draggableState = rememberDraggableState(onDelta = { dx ->
                            indicatorOffsetByDragState = Offset(
                                x = (indicatorOffsetByDragState.x + dx),
                                y = indicatorOffsetByDragState.y
                            )

                            val currentProgress =
                                (indicatorOffsetByDragState.x / boxWidth) * max
                            onSeek(currentProgress.toLong())
                        })

                        Row(
                            modifier = Modifier
                                .matchParentSize()
                                .zIndex(1f)
                                .draggable(
                                    state = draggableState,
                                    orientation = Orientation.Horizontal,
                                    startDragImmediately = true,
                                    onDragStarted = { downPosition ->
                                        onGoingDrag = true
                                        indicatorOffsetByDragState =
                                            indicatorOffsetByDragState.copy(x = downPosition.x)
                                        val newProgress =
                                            (indicatorOffsetByDragState.x / boxWidth) * max
                                        onSeekStarted(newProgress.toLong())
                                    },
                                    onDragStopped = {
                                        val newProgress =
                                            (indicatorOffsetByDragState.x / boxWidth) * max
                                        onSeekStopped(newProgress.toLong())
                                        indicatorOffsetByDragState = Offset.Zero
                                        onGoingDrag = false
                                    }
                                )
                        ) {
                            Indicator(
                                modifier = Modifier
                                    .offset(x = offsetDpX, y = offsetDpY)
                                    .size(indicatorSize)
                            )
                        }
                    }

                    // MAIN PROGRESS
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        progress = percentage,
                        color = color
                    )
                }
            }

        }
    }
}

@Composable
fun Indicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
) {
//    Canvas(modifier = modifier) {
//        val radius = size.height / 2
//        drawCircle(color, radius)
//    }
    Image(
        modifier = modifier,
        painter = painterResource(AgeAnimeIcons.Player.thumb),
        contentDescription = "thumb"
    )
}

private fun formatMilliseconds(millis: Long, includeHour: Boolean? = null): String {
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)

    val useHours = includeHour ?: (hours > 0)

    return if (useHours) {
        String.format("%d:%02d:%02d", hours, minutes - hours * 60, seconds - minutes * 60)
    } else {
        String.format("%02d:%02d", minutes, seconds - minutes * 60)
    }
}

@UnstableApi
@Composable
internal fun VideoPlayerSurface(
    modifier: Modifier = Modifier,
    usePlayerController: Boolean,
    enablePip: Boolean,
    handleLifecycle: Boolean = true,
    onPipEntered: () -> Unit = {},
    autoDispose: Boolean = true,
) {

    val lifecycleOwner = rememberUpdatedState(LocalLifecycleOwner.current)
    val context = LocalContext.current
    val defaultPlayerView = remember {
        PlayerView(context)
    }
    var isPendingPipMode by rememberSavableMutableStateOf(value = false)
    val videoPlayerController = LocalVideoPlayerController.current
    val state by videoPlayerController.collect()
    DisposableEffect(
        AndroidView(
            factory = { context ->
                defaultPlayerView.apply {
                    useController = usePlayerController
                    videoPlayerController.playerViewAvailable(this)
                }
            },
            modifier = modifier
        )
    ) {

        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                if (handleLifecycle) {
                    videoPlayerController.pause()
                }
                if (enablePip && state.playReady) {
                    isPendingPipMode = true

                    mainThread {
                        enterPIPMode(context, defaultPlayerView)
                        onPipEntered()
                        mainThread(500) {
                            isPendingPipMode = false
                        }
                    }
                }


            }

            override fun onResume(owner: LifecycleOwner) {
                if (handleLifecycle) {
                    videoPlayerController.play()
                }

                if (enablePip && state.playReady) {
                    defaultPlayerView.useController = usePlayerController
                }
            }

            override fun onStop(owner: LifecycleOwner) {
                val isPipMode = context.isActivityStatePipMode()
                if (handleLifecycle || (enablePip && isPipMode && !isPendingPipMode)) {
                    videoPlayerController.pause()
                }
            }

        }

        val lifecycle = lifecycleOwner.value.lifecycle
        lifecycle.addObserver(observer)
        onDispose {
            if (autoDispose) {
                lifecycle.removeObserver(observer)
                videoPlayerController.release()
            }
        }
    }

}

internal val LocalVideoPlayerController =
    compositionLocalOf<DefaultVideoPlayerController> { error("VideoPlayerController is not initialized") }

@UnstableApi
@Composable
fun rememberVideoPlayerController(
    source: VideoPlayerSource? = null
): VideoPlayerController {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    return rememberSaveable(
        context, coroutineScope,
        saver = object : Saver<DefaultVideoPlayerController, VideoPlayerState> {
            override fun restore(value: VideoPlayerState): DefaultVideoPlayerController {
                return DefaultVideoPlayerController(
                    context = context,
                    initialState = value,
                    coroutineScope = coroutineScope
                )
            }

            override fun SaverScope.save(value: DefaultVideoPlayerController): VideoPlayerState {
                return value.currentState { it }
            }
        },
        init = {
            DefaultVideoPlayerController(
                context = context,
                initialState = VideoPlayerState(),
                coroutineScope = coroutineScope
            ).apply {
                source?.let { setSource(it) }
            }
        }
    )
}

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

    private var initialStateRunner: (() -> Unit)? = {
        exoPlayer.seekTo(initialState.position)
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

    var videoPlayerBackgroundColor: Int = Color.Black.value.toInt()
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
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .build()
        )
    }

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

    private val waitPlayerViewToPrepare = AtomicBoolean(false)

    override fun play() {
        if (exoPlayer.playbackState == STATE_ENDED) {
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
            (exoPlayer.currentPosition + (Settings.playSkipTime * 1000)).coerceAtMost(exoPlayer.duration)
        exoPlayer.seekTo(target)
        updateDurationAndPosition()
        _state.set { copy(quickSeekAction = QuickSeekAction.forward()) }
    }

    override fun quickSeekRewind() {
        if (_state.value.quickSeekAction.direction != QuickSeekDirection.None) {
            // Currently animating
            return
        }
        val target = (exoPlayer.currentPosition - (Settings.playSkipTime * 1000)).coerceAtLeast(0)
        exoPlayer.seekTo(target)
        updateDurationAndPosition()
        _state.set { copy(quickSeekAction = QuickSeekAction.rewind()) }
    }

    override fun seekTo(position: Long) {
        exoPlayer.seekTo(position)
        updateDurationAndPosition()
    }

    override fun setVolume(volume: Float) {
        exoPlayer.volume = volume
        _state.set { copy(volume = volume) }
    }

    override fun setSpeed(speed: Float) {
        exoPlayer.playbackParameters = PlaybackParameters(speed, 1.0f)
        Settings.playSpeed = PlaySpeed.indexOf(speed)
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

    fun setIsSeeking(isSeeking: Boolean) {
        _state.set { copy(isSeeking = isSeeking) }
    }

    fun setPlayEnded(playEnded: Boolean) {
        _state.set { copy(playEnded = playEnded) }
    }

    private fun updateDurationAndPosition() {
        _state.set {
            copy(
                duration = exoPlayer.duration.coerceAtLeast(0),
                position = exoPlayer.currentPosition.coerceAtLeast(0),
                secondaryProgress = exoPlayer.bufferedPosition.coerceAtLeast(0)
            )
        }
    }

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
//                    if (source.videoUrl.contains("hls") || source.videoUrl.contains("m3u8")) {
//                        HlsMediaSource.Factory(httpDataSourceFactory)
//                            .createMediaSource(MediaItem.fromUri(source.videoUrl))
//                    } else {
//                        ProgressiveMediaSource.Factory(httpDataSourceFactory)
//                            .createMediaSource(MediaItem.fromUri(source.videoUrl))
//                    }
                    DefaultMediaSourceFactory(httpDataSourceFactory).createMediaSource(
                        MediaItem.fromUri(
                            source.videoUrl
                        )
                    )
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
        setDisplayMode(PlayDisplayMode.of(Settings.playAspectRatio))

        if (waitPlayerViewToPrepare.compareAndSet(true, false)) {
            prepare()
        }
    }

    fun previewPlayerViewAvailable(playerView: PlayerView) {
        playerView.player = previewExoPlayer
    }

    fun previewSeekTo(position: Long) {
        val seconds = position.toInt() / 1000
        val nearestEven = (seconds - seconds.rem(2)).toLong()
        coroutineScope.launch {
            previewSeekDebouncer.put(nearestEven * 1000)
        }
    }

    fun setDisplayMode(playDisplayMode: PlayDisplayMode) {
        playerView?.resizeMode = when (playDisplayMode) {
            PlayDisplayMode.FIT -> RESIZE_MODE_FIT
            PlayDisplayMode.FIXED_WIDTH -> RESIZE_MODE_FIXED_WIDTH
            PlayDisplayMode.FIXED_HEIGHT -> RESIZE_MODE_FIXED_HEIGHT
            PlayDisplayMode.FILL -> RESIZE_MODE_FILL
            PlayDisplayMode.ZOOM -> RESIZE_MODE_ZOOM
        }
        Settings.playAspectRatio = playDisplayMode.value
        _state.set { copy(playDisplayMode = playDisplayMode) }
    }

}

interface VideoPlayerController {

    fun setSource(source: VideoPlayerSource)

    fun play()

    fun pause()

    fun playPauseToggle()

    fun quickSeekForward()

    fun quickSeekRewind()

    fun seekTo(position: Long)

    fun setVolume(volume: Float)

    fun setSpeed(speed: Float)

    fun release()

    fun reset()

    val state: StateFlow<VideoPlayerState>
}

sealed class VideoPlayerSource {
    data class Raw(@RawRes val resId: Int) : VideoPlayerSource()
    data class Network(val videoUrl: String) : VideoPlayerSource()
}

@UnstableApi
@Parcelize
@Keep
data class VideoPlayerState(
    val isLoading: Boolean = true,
    val isPlaying: Boolean = true,
    var controlsVisible: Boolean = true,
    var controlsEnabled: Boolean = true,
    val gesturesEnabled: Boolean = true,
    var hideBottomProgress: Boolean = false,
    val isSeeking: Boolean = false,
    val playReady: Boolean = false,
    val playEnded: Boolean = false,
    val duration: Long = 1L,
    var position: Long = 1L,
    val secondaryProgress: Long = 1L,
    val speed: Float = 1f,
    val volume: Float = 1f,
    val luminance: Float = 1f,
    val videoSize: Pair<Float, Float> = 1920f to 1080f,
    val draggingProgress: DraggingProgress? = null,
    val playbackState: PlaybackState = PlaybackState.IDLE,
    val playDisplayMode: PlayDisplayMode = PlayDisplayMode.FILL,
    val quickSeekAction: QuickSeekAction = QuickSeekAction.none(),
    val seekDirection: VideoSeekDirection = VideoSeekDirection.NONE,
    var error: AgeException? = null
) : Parcelable

@Parcelize
data class DraggingProgress(
    val finalTime: Float,
    val diffTime: Float
) : Parcelable {
    val progressText: String
        get() = "${formatMilliseconds(finalTime.toLong(), false)} " +
                "[${if (diffTime < 0) "-" else "+"}${
                    formatMilliseconds(
                        kotlin.math.abs(diffTime.toLong()),
                        false
                    )
                }]"
}

enum class PlaybackState(val value: Int) {

    IDLE(STATE_IDLE), BUFFERING(STATE_BUFFERING), READY(STATE_READY), ENDED(STATE_ENDED);

    companion object {
        fun of(value: Int): PlaybackState {
            return entries.first { it.value == value }
        }
    }
}

@UnstableApi
enum class PlayDisplayMode(val value: Int) {

    FIT(RESIZE_MODE_FIT),
    FIXED_WIDTH(RESIZE_MODE_FIXED_WIDTH),
    FIXED_HEIGHT(RESIZE_MODE_FIXED_HEIGHT),
    FILL(RESIZE_MODE_FILL),
    ZOOM(RESIZE_MODE_ZOOM);

    companion object {
        fun of(value: Int): PlayDisplayMode {
            return entries.first { it.value == value }
        }
    }
}

enum class PlaySpeed(val value: Float) {
    SPEED_0_5(0.5f),
    SPEED_1(1f),
    SPEED_1_5(1.5f),
    SPEED_2(2.0f),
    SPEED_2_5(2.5f),
    SPEED_3(3.0f);


    companion object {
        fun of(value: Float): PlaySpeed {
            return entries.first { it.value == value }
        }

        fun of(index: Int): Float {
            return entries[index].value
        }

        fun indexOf(value: Float): Int {
            return entries.toTypedArray().indexOfFirst { it.value == value }
        }
    }
}

@Parcelize
data class QuickSeekAction(
    val direction: QuickSeekDirection
) : Parcelable {
    // Each action is unique
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return Objects.hash(direction)
    }

    companion object {
        fun none() = QuickSeekAction(QuickSeekDirection.None)
        fun forward() = QuickSeekAction(QuickSeekDirection.Forward)
        fun rewind() = QuickSeekAction(QuickSeekDirection.Rewind)
    }
}

enum class QuickSeekDirection {
    None,
    Rewind,
    Forward
}

enum class VideoSeekDirection {
    NONE,
    Rewind,
    Forward;

    val isSeeking: Boolean
        get() = this != NONE
}

@UnstableApi
object VideoPlayerCacheManager {

    private lateinit var cacheInstance: Cache

    /**
     * Set the cache for video player.
     * It can only be set once in the app, and it is shared and used by multiple video players.
     *
     * @param context Current activity context.
     * @param maxCacheBytes Sets the maximum cache capacity in bytes. If the cache builds up as much as the set capacity, it is deleted from the oldest cache.
     */
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun initialize(context: Context, maxCacheBytes: Long) {
        if (VideoPlayerCacheManager::cacheInstance.isInitialized) {
            return
        }

        cacheInstance = SimpleCache(
            File(context.cacheDir, "video"),
            LeastRecentlyUsedCacheEvictor(maxCacheBytes),
            StandaloneDatabaseProvider(context),
        )
    }

    /**
     * Gets the ExoPlayer cache instance. If null, the cache to be disabled.
     */
    internal fun getCache(): Cache? =
        if (VideoPlayerCacheManager::cacheInstance.isInitialized) {
            cacheInstance
        } else {
            null
        }
}

/**
 * Enables PIP mode for the current activity.
 *
 * @param context Activity context.
 * @param defaultPlayerView Current video player controller.
 */
@Suppress("DEPRECATION")
internal fun enterPIPMode(context: Context, defaultPlayerView: PlayerView) {
    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
        defaultPlayerView.useController = false
        val params = PictureInPictureParams.Builder()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            params.setTitle("Video Player").setAspectRatio(Rational(16, 9))
                .setSeamlessResizeEnabled(true)
        }

        context.findActivity().enterPictureInPictureMode(params.build())
    }
}

/**
 * Check that the current activity is in PIP mode.
 *
 * @return `true` if the activity is in pip mode. (PIP mode is not supported in the version below Android N, so `false` is returned unconditionally.)
 */
internal fun Context.isActivityStatePipMode(): Boolean = findActivity().isInPictureInPictureMode

/**
 * Bring the activity to the full screen.
 */
internal fun Activity.setFullScreen(fullscreen: Boolean) {
    window.setFullScreen(fullscreen)
}

/**
 * Bring the window to full screen. (Remove the status bar and navigation bar.)
 */
@Suppress("Deprecation")
internal fun Window.setFullScreen(fullscreen: Boolean) {
    if (fullscreen) {
        decorView.systemUiVisibility =
            (SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
    } else {
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
    }
}

/**
 * The environment in which Compose is hosted may not be an activity unconditionally.
 * Gets the current activity that is open from various kinds of contexts such as Fragment, Dialog, etc.
 */
internal fun Context.findActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("Activity not found. Unknown error.")
}

fun <T> MutableStateFlow<T>.set(block: T.() -> T) {
    this.value = this.value.block()
}

class FlowDebouncer<T>(timeoutMillis: Long) : Flow<T> {

    private val sourceChannel: Channel<T> = Channel(capacity = 1)

    @OptIn(FlowPreview::class)
    private val flow: Flow<T> = sourceChannel.consumeAsFlow().debounce(timeoutMillis)

    suspend fun put(item: T) {
        sourceChannel.send(item)
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        flow.collect(collector)
    }

}

suspend fun PointerInputScope.detectMediaPlayerGesture(
    onTap: (Offset) -> Unit,
    onDoubleTap: (Offset) -> Unit,
    onHorizontalDragStart: (Offset) -> Unit,
    onHorizontalDragEnd: () -> Unit,
    onHorizontalDrag: (Float) -> Unit,
    onVerticalDragStart: (Offset) -> Unit,
    onVerticalDragEnd: () -> Unit,
    onVerticalDrag: (Float) -> Unit,
) {
    coroutineScope {
        launch {
            detectHorizontalDragGestures(
                onDragStart = onHorizontalDragStart,
                onDragEnd = onHorizontalDragEnd,
                onHorizontalDrag = { change, dragAmount ->
                    onHorizontalDrag(dragAmount)
                    if (change.positionChange() != Offset.Zero) change.consume()
                },
            )
        }

        launch {
            detectVerticalDragGestures(
                onDragStart = onVerticalDragStart,
                onDragEnd = onVerticalDragEnd,
                onVerticalDrag = { change, dragAmount ->
                    onVerticalDrag(dragAmount)
                    if (change.positionChange() != Offset.Zero) change.consume()
                },
            )
        }

        launch {
            detectTapGestures(
                onTap = onTap,
                onDoubleTap = onDoubleTap
            )
        }
    }
}

fun Modifier.quickSeekAnimation(
    quickSeekDirection: QuickSeekDirection,
    onAnimationEnd: () -> Unit
) = composed {
    val alphaRewind = remember { Animatable(0f) }
    val alphaForward = remember { Animatable(0f) }

    LaunchedEffect(quickSeekDirection) {
        when (quickSeekDirection) {
            QuickSeekDirection.Rewind -> alphaRewind
            QuickSeekDirection.Forward -> alphaForward
            else -> null
        }?.let { animatable ->
            animatable.animateTo(1f)
            animatable.animateTo(0f)
            onAnimationEnd()
        }
    }

    Row(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            ShadowedIcon(
                ImageVector.vectorResource(R.drawable.fast_rewind),
                modifier = Modifier
                    .alpha(alphaRewind.value)
                    .align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            ShadowedIcon(
                ImageVector.vectorResource(R.drawable.fast_forward),
                modifier = Modifier
                    .alpha(alphaForward.value)
                    .align(Alignment.Center)
            )
        }
    }

    this
}

fun Modifier.draggingProgressOverlay(draggingProgress: DraggingProgress?) = composed {
    if (draggingProgress != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                draggingProgress.progressText,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                style = TextStyle(
                    shadow = Shadow(
                        blurRadius = 8f,
                        offset = Offset(2f, 2f)
                    )
                ),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
    this
}

@Composable
fun ShadowedIcon(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    iconSize: Dp = 48.dp,
) {
    Box(modifier = modifier) {
        Icon(
            imageVector = icon,
            tint = Color.Black.copy(alpha = 0.3f),
            modifier = Modifier
                .size(iconSize)
                .offset(2.dp, 2.dp)
                .then(modifier),
            contentDescription = null
        )
        Icon(
            imageVector = icon,
            modifier = Modifier.size(iconSize),
            contentDescription = null
        )
    }
}