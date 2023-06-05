package cn.xihan.age.ui.desc

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimeCoverItem
import cn.xihan.age.component.AnimeDiskResource
import cn.xihan.age.component.AnimeGuessLike
import cn.xihan.age.component.AnimeHotDiscussCollectItem
import cn.xihan.age.component.AnimeIntroductionItem
import cn.xihan.age.component.AnimePlayListTab
import cn.xihan.age.component.AnimeRelated
import cn.xihan.age.component.AnywhereDropdown
import cn.xihan.age.component.MyDropdownMenuItem
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.BlurTransformation
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.isNotNightMode
import cn.xihan.age.util.lastVisibleItemIndex
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.util.url
import coil.ImageLoader
import coil.request.ImageRequest
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.whatif.whatIfNotNull
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/25 22:18
 * @介绍 :
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DescScreen(
    viewModel: DescViewModel = hiltViewModel(),
    animeId: String? = "",
    appState: MainAppState
) {
    val state by viewModel.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val topAppBarExpanded = rememberMutableStateOf(value = false)
    val refreshState = rememberPullRefreshState(state.refreshing, onRefresh = {
        viewModel.getAnimeDetailModel()
    })
    val listState = rememberLazyListState()

    AgeScaffold(
        modifier = Modifier
            .fillMaxSize(),
        state = state,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                modifier = Modifier
                    .fillMaxWidth(),
                actions = {
                    AnywhereDropdown(
                        expanded = topAppBarExpanded.value,
                        onDismissRequest = { topAppBarExpanded.value = false },
                        onClick = { topAppBarExpanded.value = true },
                        surface = {
                            IconButton(onClick = {
                                topAppBarExpanded.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null
                                )
                            }
                        }
                    ) {
                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.scroll_to_top)) },
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(0)
                                }
                            }

                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.scroll_to_bottom)) },
                            onClick = {
                                coroutineScope.launch {
                                    listState.animateScrollToItem(listState.lastVisibleItemIndex ?: 100)
                                }
                            }
                        )


                    }

                },
                navigationIcon = {
                    IconButton(onClick = {
                        appState.popBackStack()
                    }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, null)
                    }
                },
                scrollBehavior = null
            )
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        }
    ) { _, _ ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .pullRefresh(refreshState)
        ) {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                state.currentAnimeDetailModel?.aniInfo.whatIfNotNull { animeInfoModel ->
                    item {
                        Box {
                            if (isNotNightMode) {
                                CoilImage(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    imageRequest = {
                                        ImageRequest.Builder(context)
                                            .data(animeInfoModel.r封面图.url)
                                            .crossfade(true)
                                            .lifecycle(lifecycleOwner)
                                            .transformations(BlurTransformation(context, 25f, 10f))
                                            .build()
                                    },
                                    imageLoader = {
                                        ImageLoader.Builder(context)
                                            .crossfade(true)
                                            .build()
                                    },
                                    imageOptions = ImageOptions(
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.Center
                                    )
                                )
                            }

                            Column {
                                AnimeCoverItem(
                                    animeId = animeInfoModel.aID,
                                    animeTitle = animeInfoModel.r动画名称,
                                    animeSubTitle = animeInfoModel.r原版名称,
                                    animeCover = animeInfoModel.r封面图,
                                    animeRegion = animeInfoModel.r地区,
                                    animeType = animeInfoModel.r动画种类,
                                    animeOriginalWork = animeInfoModel.r原作,
                                    animePremiereDate = animeInfoModel.r首播时间,
                                    animePlotType = animeInfoModel.r剧情类型,
                                    animePlotTypeList = animeInfoModel.r剧情类型2,
                                    appState = appState
                                )

                                AnimeHotDiscussCollectItem(
                                    animeHot = animeInfoModel.rankCnt,
                                    animeCollect = animeInfoModel.commentCnt,
                                    animeDiscuss = animeInfoModel.collectCnt,
                                    isFavorite = state.favorite,
                                    onCollectClick = {
                                        viewModel.updateAnimeFavorites(it)
                                        appState.showSnackbar(
                                            message = if (it) application.getString(R.string.join_ok) else application.getString(
                                                R.string.join_error
                                            )
                                        )
                                    }
                                )

                                Surface(
                                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
                                    shadowElevation = 10.dp,
                                    border = BorderStroke(1.dp, Color.Gray),
                                ) {
                                    Column {

                                        AnimeIntroductionItem(animeInfoModel.r简介)

                                        AnimePlayListTab(
                                            playList = animeInfoModel.r在线播放All
                                        ) { playListIndex, episodeText ->
                                            appState.navigateToPlayer(
                                                animeId = animeInfoModel.aID,
                                                episodeId = playListIndex,
                                                episodeName = episodeText
                                            )
                                        }

                                        AnimeDiskResource(
                                            diskResourceList = animeInfoModel.r网盘资源2
                                        )

                                        AnimeRelated(
                                            relatedList = state.currentAnimeDetailModel?.aniPreRel,
                                            appState = appState
                                        )

                                        AnimeGuessLike(
                                            guessLikeList = state.currentAnimeDetailModel?.aniPreSim,
                                            appState = appState
                                        )

                                    }

                                }

                            }

                        }
                    }

                }
            }

            PullRefreshIndicator(
                refreshing = state.refreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter),
            )

        }
    }

}