@file:androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)

package cn.xihan.age.component.player

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cn.xihan.age.R
import cn.xihan.age.component.AdaptiveIconButton
import cn.xihan.age.component.CardIconButton
import cn.xihan.age.component.FpsMonitor
import cn.xihan.age.util.Utils
import cn.xihan.age.util.Utils.getDurationString


@Composable
fun MediaControlButtons(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    selectEpisode: () -> Unit = {},
    previousEpisode: @Composable () -> Unit = {},
    nextEpisode: @Composable () -> Unit = {}
) {
    val controller = LocalVideoPlayerController.current

    val controlsEnabled by controller.collect { controlsEnabled }

    // Dictates the direction of appear animation.
    // If controlsVisible is true, appear animation needs to be triggered.
    val controlsVisible by controller.collect { controlsVisible }

    // When controls are not visible anymore we should remove them from UI tree
    // Controls by default should always be on screen.
    // Only when disappear animation finishes, controls can be freely cleared from the tree.
    val (controlsExistOnUITree, setControlsExistOnUITree) = remember(controlsVisible) {
        mutableStateOf(true)
    }

    val appearAlpha = remember { Animatable(0f) }

    LaunchedEffect(controlsVisible) {
        appearAlpha.animateTo(
            targetValue = if (controlsVisible) 1f else 0f,
            animationSpec = tween(
                durationMillis = 250,
                easing = LinearEasing
            )
        )
        setControlsExistOnUITree(controlsVisible)
    }

    if (controlsEnabled && controlsExistOnUITree) {
        MediaControlButtonsContent(
            modifier = Modifier
                .alpha(appearAlpha.value)
                .background(Color.Black.copy(alpha = appearAlpha.value * 0.6f))
                .then(modifier),
            title = title,
            onBackClick = onBackClick,
            onSettingClick = onSettingClick,
            selectEpisode = selectEpisode,
            previousEpisode = previousEpisode,
            nextEpisode = nextEpisode
        )
    }
}

@Preview
@Composable
private fun MediaControlButtonsContent(
    title: String = "",
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {},
    selectEpisode: () -> Unit = {},
    previousEpisode: @Composable () -> Unit = {},
    nextEpisode: @Composable () -> Unit = {}
) {
    val controller = LocalVideoPlayerController.current

    Box(modifier = modifier) {

        Box(modifier = Modifier
            .fillMaxSize()
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                controller.hideControls()
            }
        )

        PlayerTitle(
            title = title,
            onBackClick = onBackClick,
            onSettingClick = onSettingClick,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()

            ) {
                ShadowedIcon(
                    ImageVector.vectorResource(R.drawable.fast_rewind),
                    modifier = Modifier
                        .clickable {
                            controller.quickSeekRewind()
                        }
                        .align(Alignment.Center)
                )
            }

            PlayPauseButton(modifier = Modifier)

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()

            ) {
                ShadowedIcon(
                    ImageVector.vectorResource(R.drawable.fast_forward),
                    modifier = Modifier
                        .clickable {
                            controller.quickSeekForward()
                        }
                        .align(Alignment.Center)
                )
            }
        }

        PositionAndDurationNumbers(
            modifier = Modifier.align(Alignment.BottomCenter),
            selectEpisode = selectEpisode,
            previousEpisode = previousEpisode,
            nextEpisode = nextEpisode
        )

    }
}

@Composable
fun PlayerTitle(
    title: String,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onSettingClick: () -> Unit = {}
) {
    Column {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    Icons.Filled.ArrowBack,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            Text(
                text = title,
                modifier = Modifier.weight(1f),
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            AdaptiveIconButton(
                modifier = Modifier.size(SmallIconButtonSize),
                onClick = onSettingClick
            ) {
                Icon(
                    imageVector = Icons.Rounded.Settings,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }

        FpsMonitor(Modifier.wrapContentSize())
    }

}

@Composable
fun PositionAndDurationNumbers(
    modifier: Modifier = Modifier,
    selectEpisode: () -> Unit = {},
    previousEpisode: @Composable () -> Unit = {},
    nextEpisode: @Composable () -> Unit = {}
) {
    val controller = LocalVideoPlayerController.current

    val positionText by controller.collect {
        getDurationString(currentPosition, false)
    }
    val remainingDurationText by controller.collect {
        getDurationString(duration - currentPosition, false)
    }

    /**
     * 当前显示模式
     */
    val currentDisplayMode by controller.collect { playDisplayMode }
    val currentDisplayModeText by controller.collect {
        when (currentDisplayMode) {
            PlayDisplayMode.FIT -> "自适应"
            PlayDisplayMode.FIXED_WIDTH -> "固定宽度"
            PlayDisplayMode.FIXED_HEIGHT -> "固定高度"
            PlayDisplayMode.FILL -> "全屏"
            PlayDisplayMode.ZOOM -> "原始比例"
        }
    }

    /**
     * 当前播放速度
     */
    val currentSpeed by controller.collect { speed }
    val currentSpeedText by controller.collect {
        "倍数X${PlaySpeed.of(currentSpeed)}"
    }


    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            previousEpisode()
            Spacer(modifier = Modifier.width(3.dp))
            nextEpisode()
            Box(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier
                        .padding(4.dp)
                        .align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CardIconButton(
                        onClick = {
                            controller.setDisplayMode(PlayDisplayMode.of(if (currentDisplayMode.value == 4) 0 else currentDisplayMode.value + 1))
                        },
                        text = {
                            Text(
                                text = currentDisplayModeText
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    CardIconButton(
                        onClick = {
                            Utils.showPlaySpeedDialog(
                                onSpeedChangeListener = {
                                    controller.setSpeed(PlaySpeed.of(it))
                                },
                                onShowSpeedChangeListener ={
                                    controller.pause()
                                },
                                onDismissSpeedChangeListener= {
                                    controller.play()
                                }
                            )
                        },
                        text = {
                            Text(
                                text = currentSpeedText//stringResource(id = R.string.speed)
                            )
                        }
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    CardIconButton(
                        onClick = selectEpisode,
                        text = {
                            Text(
                                text = stringResource(id = R.string.select_episode)
                            )
                        }
                    )

                }
            }
        }

        Row {
            Text(
                positionText,
                style = TextStyle(
                    shadow = Shadow(
                        blurRadius = 8f,
                        offset = Offset(2f, 2f)
                    )
                )
            )
            Box(modifier = Modifier.weight(1f))
            Text(
                remainingDurationText,
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

@Composable
fun PlayPauseButton(modifier: Modifier = Modifier) {
    val controller = LocalVideoPlayerController.current

    val isPlaying by controller.collect { isPlaying }
    val playbackState by controller.collect { playbackState }

    IconButton(
        onClick = { controller.playPauseToggle() },
        modifier = modifier
    ) {
        if (isPlaying) {
            ShadowedIcon(icon = ImageVector.vectorResource(R.drawable.pause))
        } else {
            when (playbackState) {
                PlaybackState.ENDED -> {
                    ShadowedIcon(icon = ImageVector.vectorResource(R.drawable.restore))
                }

                PlaybackState.BUFFERING -> {
                    CircularProgressIndicator()
                }

                else -> {
                    ShadowedIcon(icon = Icons.Filled.PlayArrow)
                }
            }
        }
    }
}

val BigIconButtonSize = 52.dp
val SmallIconButtonSize = 32.dp