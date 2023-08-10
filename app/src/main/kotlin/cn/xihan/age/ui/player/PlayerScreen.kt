package cn.xihan.age.ui.player

import android.annotation.SuppressLint
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import cn.xihan.age.R
import cn.xihan.age.component.AccompanistWebViewClient
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimePlayListExpandAll
import cn.xihan.age.component.CardIconButton
import cn.xihan.age.component.IconSwitchSetting
import cn.xihan.age.component.IconTextSetting
import cn.xihan.age.component.MyModalNavigationDrawer
import cn.xihan.age.component.MyRightModalDrawerSheet
import cn.xihan.age.component.WebView
import cn.xihan.age.component.player.VideoPlayer
import cn.xihan.age.component.player.VideoPlayerSource
import cn.xihan.age.component.player.rememberVideoPlayerController
import cn.xihan.age.component.rememberWebViewNavigator
import cn.xihan.age.component.rememberWebViewState
import cn.xihan.age.network.SPSettings
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.Utils.getEpisodeTitleByIndex
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.isNotNightMode
import cn.xihan.age.util.rememberSavableMutableStateOf
import com.kongzue.dialogx.dialogs.PopTip
import com.skydoves.whatif.whatIfNotNull
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/8/5 20:21
 * @介绍 :
 */
@SuppressLint("CoroutineCreationDuringComposition")
@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PlayerScreen(
    appState: MainAppState,
    animeId: Int?,
    episodeType: String?,
    episodeTitle: String?,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    var playerUrl by rememberSavableMutableStateOf<String?>(null)
    val state by viewModel.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val showLeftDrawer = rememberSavableMutableStateOf(false)
    val coroutineScope = rememberCoroutineScope()
    val videoPlayerController = rememberVideoPlayerController()
    val videoPlayerUiState by videoPlayerController.state.collectAsState()
    var playSkipTime by rememberSavableMutableStateOf(SPSettings.playSkipTime)
    val playHideBottomProgress = rememberSavableMutableStateOf(SPSettings.hideBottomProgress)
    val autoNextEpisode = rememberSavableMutableStateOf(SPSettings.autoNextEpisode)

    val webState = rememberWebViewState(url = "")
    val navigator = rememberWebViewNavigator()
    val webClient = remember {
        object : AccompanistWebViewClient() {
            override fun onLoadResource(webView: WebView, url: String) {
                val list = listOf(
                    "cqkeb.com",
                    "aiqingyu1314.com",
                    "hm.baidu.com",
                    "bdxiguaimg.com",
                    "yzcdn.cn",
                    "sp-flv.com"
                )
                if (url.lastIndexOf(".") < url.length - 5 && !list.any { it in url }) {
                    logDebug("onLoadResource: $url")
                    viewModel.getContent(url)
                }
            }
        }


    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {

            override fun onCreate(owner: LifecycleOwner) {

            }

            override fun onPause(owner: LifecycleOwner) {
                if (videoPlayerUiState.playReady || videoPlayerUiState.isPlaying || videoPlayerUiState.playEnded) {
                    viewModel.saveHistoryProgress(
                        currentPosition = videoPlayerUiState.currentPosition,
                        currentDuration = videoPlayerUiState.duration
                    )
                }
            }

            override fun onResume(owner: LifecycleOwner) {
            }

            override fun onStart(owner: LifecycleOwner) {

            }

            override fun onStop(owner: LifecycleOwner) {

            }

            override fun onDestroy(owner: LifecycleOwner) {
                viewModel.saveHistoryProgress(
                    currentPosition = videoPlayerUiState.currentPosition,
                    currentDuration = videoPlayerUiState.duration
                )
                videoPlayerController.release()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }

    }

    LaunchedEffect(playerUrl) {
        if (playerUrl != null) {
            videoPlayerController.setSource(VideoPlayerSource.Network(playerUrl!!))
            logDebug("title: ${state.currentEpisodeTitle} 切换视频源: $playerUrl")
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
            is PlayerIntent.SetPlayerUrl -> {
                playerUrl = it.url
                viewModel.hideLoading()
            }

            is PlayerIntent.SetAnimeOriginPlayerUrl -> {
                logDebug("SetAnimeOriginPlayerUrl: ${it.url}")
                navigator.loadUrl(it.url)
            }
        }
    }

    if (videoPlayerUiState.playEnded) {
        if (autoNextEpisode.value) {
            if (state.hasNextVideo) {
                viewModel.changePlayerIndex(state.currentPlayerIndex.plus(1))
            } else {
                PopTip.show("已经是最后一集了")
            }
        } else {
            coroutineScope.launch {
                drawerState.open()
            }
        }
    }

    if (videoPlayerUiState.playReady && videoPlayerUiState.secondaryProgress > 0 && state.hasPosition && state.currentPosition > 0) {
        PopTip.show(
            "已定位到上次观看位置 " + Utils.stringForTime(state.currentPosition), "从头开始播放"
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

    if (videoPlayerUiState.error != null) {
        viewModel.showError(videoPlayerUiState.error!!)
    }

    AgeScaffold(
        state = state,
        snackbarHostState = appState.snackbarHost,
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissErrorDialog = {
            if (videoPlayerUiState.error != null) {
                videoPlayerUiState.error = null
            }
            viewModel.hideError()
        }
    ) { _, _ ->
        WebView(
            modifier = Modifier.size(0.dp),
            state = webState,
            onCreated = {
                it.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    builtInZoomControls = true
                    defaultTextEncodingName = "GBK"
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    textZoom = 100
                    loadsImagesAutomatically = false
                    blockNetworkImage = true
                    setSupportZoom(true)
                    setGeolocationEnabled(false)
                    setSupportMultipleWindows(true)
                }
                it.settingsExtension?.apply {
                    setDisplayCutoutEnable(true)
                    setDayOrNight(isNotNightMode)
                    setPicModel(IX5WebSettingsExtension.PicModel_NoPic)
                }
            },
            onDispose = {
                it.destroy()
            },
            navigator = navigator,
            client = webClient,
        )

        MyModalNavigationDrawer(
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
                    AnimePlayListExpandAll(playList = state.currentOriginPlayerList,
                        onDismissRequest = {
                            coroutineScope.launch {
                                drawerState.close()
                            }
                        },
                        onPlayClick = { index, title ->
                            viewModel.changePlayerIndex(index, videoPlayerController)
                        }
                    )
                }
            },
            showLeftDrawer = showLeftDrawer.value,
            content = {
                VideoPlayer(
//                    modifier = Modifier.fillMaxSize(),
                    videoPlayerController = videoPlayerController,
                    backgroundColor = Color.Black,
                    title = "${state.currentAnimeDetailModel?.name}-${state.currentEpisodeTitle}",
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
                        if (state.hasPreVideo) {
                            state.currentAnimePlayerList.getEpisodeTitleByIndex(state.currentPlayerIndex)
                                .whatIfNotNull {
                                    CardIconButton(onClick = {
                                        viewModel.changePlayerIndex(
                                            state.currentPlayerIndex.minus(1),
                                            videoPlayerController
                                        )
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
                        if (state.hasNextVideo) {
                            state.currentAnimePlayerList.getEpisodeTitleByIndex(
                                state.currentPlayerIndex, true
                            ).whatIfNotNull {
                                CardIconButton(onClick = {
                                    viewModel.changePlayerIndex(
                                        state.currentPlayerIndex.plus(1),
                                        videoPlayerController
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
}