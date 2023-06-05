package cn.xihan.age.ui.player

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.xihan.age.R
import cn.xihan.age.component.AnimePlayTab
import cn.xihan.age.component.CardIconButton
import cn.xihan.age.component.IconSwitchSetting
import cn.xihan.age.component.IconTextSetting
import cn.xihan.age.component.MyModalNavigationDrawer
import cn.xihan.age.component.MyRightModalDrawerSheet
import cn.xihan.age.component.player.VideoPlayer
import cn.xihan.age.component.player.VideoPlayerSource
import cn.xihan.age.component.player.rememberVideoPlayerController
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.network.SPSettings
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.Utils.getEpNameByIndex
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.rememberSavableMutableStateOf
import com.kongzue.dialogx.dialogs.PopTip
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/27 9:46
 * @介绍 :
 */
@SuppressLint("CoroutineCreationDuringComposition")
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel = hiltViewModel(),
    appState: MainAppState,
    animeId: String?,
    episodeId: Int?,
    episodeName: String?,
) {
    var selectedVideoState by rememberSavableMutableStateOf<AnimeDetailModel.AniInfoModel.PlayModel?>(
        null
    )
    val state by viewModel.collectAsState()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val showLeftDrawer = rememberSavableMutableStateOf(false)
    var playSkipTime by rememberSavableMutableStateOf(SPSettings.playSkipTime)
    var playHideBottomProgress = rememberSavableMutableStateOf(SPSettings.hideBottomProgress)
    var autoNextEpisode = rememberSavableMutableStateOf(SPSettings.autoNextEpisode)
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
//    val player = rememberPlayer(VideoPlayerSource.Network(selectedVideoState?.playVid ?: ""), VideoPlayerFactory)
//    val controller = rememberVideoPlayerController(player)
    val videoPlayerController = rememberVideoPlayerController()
    val videoPlayerUiState by videoPlayerController.state.collectAsState()
    var currentPosition by rememberSavableMutableStateOf(0L)
    var currentDuration by rememberSavableMutableStateOf(0L)
    // 生命周期监控
    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {

            override fun onCreate(owner: LifecycleOwner) {
                if (playHideBottomProgress.value) {
                    videoPlayerController.state.value.hideBottomProgress = true
                }
            }

            override fun onPause(owner: LifecycleOwner) {
                if (videoPlayerUiState.playReady || videoPlayerUiState.isPlaying || videoPlayerUiState.playEnded) {
                    viewModel.saveVideoPlayRecord(
                        videoPlayerUiState.currentPosition,
                        videoPlayerUiState.duration
                    )
                }
            }

            override fun onResume(owner: LifecycleOwner) {

            }

            override fun onStart(owner: LifecycleOwner) {

            }

            override fun onStop(owner: LifecycleOwner) {
                logDebug("onStop")

            }

            override fun onDestroy(owner: LifecycleOwner) {
                logDebug("onDestroy")
                videoPlayerController.release()

            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            logDebug("onDispose")
            if (videoPlayerUiState.playReady || videoPlayerUiState.isPlaying || videoPlayerUiState.playEnded) {
                viewModel.saveVideoPlayRecord(
                    videoPlayerUiState.currentPosition,
                    videoPlayerUiState.duration
                )
            }
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    LaunchedEffect(selectedVideoState) {
        val selectedVideo = selectedVideoState
        if (selectedVideo != null) {
            videoPlayerController.setSource(VideoPlayerSource.Network(selectedVideo.playVid))
            logDebug("切换视频源: ${selectedVideo.playVid}")
        }
    }

    LaunchedEffect(drawerState.currentValue, showLeftDrawer.value) {
        if (showLeftDrawer.value) {
            when {
                drawerState.isOpen -> {
                    videoPlayerController.pause()
                }

                drawerState.isClosed -> {
                    videoPlayerController.play()
                }
            }
        }
    }

    viewModel.collectSideEffect {
        when (it) {
            is PlayerIntent.SetSelectedVideoModel -> {
                selectedVideoState = it.selectedVideoModel
//                    .copy(
//                        playVid = "http://192.168.43.110/Videos/%E5%A6%84%E6%83%B3%E5%AD%A6%E7%94%9F%E4%BC%9A/[AGE-SumiSora][100015][01][BDRIP][AVC_AAC][720P][CHS]%20AVC.mp4"
//                    )
            }
        }
    }

    if (videoPlayerUiState.playEnded) {
        if (autoNextEpisode.value) {
            viewModel.setSelectedVideoPosition(isNext = true)
        } else {
            coroutineScope.launch {
                drawerState.open()
            }
        }
    }

    if (videoPlayerUiState.playReady && videoPlayerUiState.secondaryProgress > 0 && state.hasPosition && state.currentPosition > 0) {
        PopTip.show(
            "已定位到上次观看位置 " + Utils.stringForTime(state.currentPosition),
            "从头开始播放"
        ).setButton { _, _ ->
            videoPlayerController.seekTo(0)
            false
        }
        videoPlayerController.seekTo(state.currentPosition)
        state.hasPosition = false
    }

    BackHandler(true) {
        if (drawerState.isOpen) {
            coroutineScope.launch {
                drawerState.close()
            }
        } else {
            appState.popBackStack()
        }
    }

    MyModalNavigationDrawer(
        modifier = Modifier.fillMaxSize(),
        drawerState = drawerState,
        leftDrawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = Color.DarkGray.copy(alpha = 0.5f),
            ) {
                Column {
                    IconTextSetting(title = stringResource(id = R.string.set_seek_duration),
                        subtitle = "${playSkipTime}s",
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.Refresh,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        textColor = Color.White,
                        onClick = {
                            Utils.showPlaySkipTimeDialog(onSkipTimeChangeListener = {
                                val time = it.removeSuffix("s").toInt()
                                SPSettings.playSkipTime = time
                                playSkipTime = time
                            })
                        })

                    IconSwitchSetting(
                        title = stringResource(id = R.string.hide_bottom_progress),
                        checked = playHideBottomProgress,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        onCheckedChange = {
                            playHideBottomProgress.value = it
                            SPSettings.hideBottomProgress = it

                        },
                        onClick = {
                            playHideBottomProgress.value = !playHideBottomProgress.value
                            SPSettings.hideBottomProgress = playHideBottomProgress.value

                        },
                        textColor = Color.White,
                    )

                    IconSwitchSetting(
                        title = stringResource(id = R.string.auto_play_next_episode),
                        checked = autoNextEpisode,
                        icon = {
                            Icon(
                                imageVector = Icons.Filled.PlayArrow,
                                contentDescription = null,
                                tint = Color.White
                            )
                        },
                        onCheckedChange = {
                            autoNextEpisode.value = it
                            SPSettings.autoNextEpisode = it
                        },
                        onClick = {
                            autoNextEpisode.value = !autoNextEpisode.value
                            SPSettings.autoNextEpisode = autoNextEpisode.value
                        },
                        textColor = Color.White,
                    )
                }
            }
        },
        rightDrawerContent = {
            MyRightModalDrawerSheet(
                drawerContainerColor = Color.DarkGray.copy(alpha = 0.5f)
            ) {
                AnimePlayTab(playList = state.playerList, onPlayClick = {
                    viewModel.setSelectedVideoPosition(playModel = it)
                })
            }
        },
        showLeftDrawer = showLeftDrawer.value,
        content = {
            VideoPlayer(
                modifier = Modifier.fillMaxSize(),
                videoPlayerController = videoPlayerController,
                backgroundColor = Color.Black,
                title = "${viewModel.animeTitle}-${selectedVideoState?.title}",
                controlsEnabled = true,
                onBackClick = {
                    appState.popBackStack()
                },
                onSettingClick = {
                    showLeftDrawer.value = true
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                selectEpisode = {
                    showLeftDrawer.value = false
                    coroutineScope.launch {
                        drawerState.open()
                    }
                },
                previousEpisode = {
                    if (state.currentPlayerPosition != 0) {
                        state.playerList.getEpNameByIndex(state.currentPlayerPosition)
                            .whatIfNotNullOrEmpty {
                                CardIconButton(onClick = {
                                    viewModel.setSelectedVideoPosition()
                                }, text = {
                                    Text(
                                        String.format(
                                            stringResource(id = R.string.previous_episode), it
                                        ), color = Color.White
                                    )
                                }, icon = {
                                    Icon(
                                        Icons.Filled.KeyboardArrowLeft,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                })

                            }
                    }
                },
                nextEpisode = {
                    if (state.currentPlayerPosition != state.playerList.size - 1) {
                        state.playerList.getEpNameByIndex(state.currentPlayerPosition, true)
                            .whatIfNotNullOrEmpty {
                                CardIconButton(onClick = {
                                    viewModel.setSelectedVideoPosition(
                                        isNext = true
                                    )
                                }, text = {
                                    Text(
                                        String.format(
                                            stringResource(id = R.string.next_episode), it
                                        ), color = Color.White
                                    )
                                }, icon = {
                                    Icon(
                                        Icons.Filled.KeyboardArrowRight,
                                        contentDescription = null,
                                        tint = Color.White
                                    )
                                })
                            }
                    }
                }
            )
        }
    )
}
