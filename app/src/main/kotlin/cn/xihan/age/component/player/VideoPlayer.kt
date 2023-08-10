@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
package cn.xihan.age.component.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

internal val LocalVideoPlayerController =
    compositionLocalOf<DefaultVideoPlayerController> { error("VideoPlayerController is not initialized") }

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

@Composable
fun VideoPlayer(
    title: String,
    videoPlayerController: VideoPlayerController,
    modifier: Modifier = Modifier,
    controlsEnabled: Boolean = true,
    gesturesEnabled: Boolean = true,
    enablePip: Boolean = false,
    enablePipWhenBackPressed: Boolean = false,
    backgroundColor: Color = Color.Black,
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    selectEpisode: () -> Unit = {},
    previousEpisode: @Composable () -> Unit = {},
    nextEpisode: @Composable () -> Unit = {}
) {
    require(videoPlayerController is DefaultVideoPlayerController) {
        "Use [rememberVideoPlayerController] to create an instance of [VideoPlayerController]"
    }

    SideEffect {
        videoPlayerController.videoPlayerBackgroundColor = backgroundColor.value.toInt()
        videoPlayerController.enableControls(controlsEnabled)
        videoPlayerController.enableGestures(gesturesEnabled)
    }

    CompositionLocalProvider(
        LocalContentColor provides Color.White,
        LocalVideoPlayerController provides videoPlayerController
    ) {
        val aspectRatio by videoPlayerController.collect { videoSize.first / videoSize.second }

        Box(
            modifier = Modifier
                .background(color = backgroundColor)
//                .aspectRatio(aspectRatio)
                .then(modifier)
        ) {
            PlayerSurface(
                modifier = Modifier
                    .align(Alignment.Center)

            ) {
                videoPlayerController.playerViewAvailable(it)
            }

            MediaControlGestures(modifier = Modifier.matchParentSize())

            MediaControlButtons(
                title = title,
                modifier = Modifier.matchParentSize(),
                onBackClick = onBackClick,
                onSettingClick = onSettingClick,
                selectEpisode = selectEpisode,
                previousEpisode = previousEpisode,
                nextEpisode = nextEpisode
            )

            ProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .align(Alignment.BottomCenter)
            )

        }
    }
}