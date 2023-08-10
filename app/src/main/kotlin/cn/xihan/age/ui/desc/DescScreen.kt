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
import cn.xihan.age.component.AnimeHotDiscussCollectItem
import cn.xihan.age.component.AnimeIntroductionItem
import cn.xihan.age.component.AnimePlayListTab
import cn.xihan.age.component.AnywhereDropdown
import cn.xihan.age.component.HorizontalBaseAnimeList
import cn.xihan.age.component.MyDropdownMenuItem
import cn.xihan.age.network.Api
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.BlurTransformation
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.isNotNightMode
import cn.xihan.age.util.lastVisibleItemIndex
import cn.xihan.age.util.rememberMutableStateOf
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
    appState: MainAppState,
    animeId: Int? = null,
//    playerAction: (page: Int, index: Int, title: String, type: String) -> Unit,
    viewModel: DescViewModel = hiltViewModel(),
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
                                    listState.animateScrollToItem(
                                        listState.lastVisibleItemIndex ?: 100
                                    )
                                }
                            }
                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(id = R.string.open_browser_player)) },
                            onClick = {
                                appState.navigateToWeb("${Api.PHONE_DETAIL_URL}$animeId")
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
                state.currentAnimeDetailModel?.whatIfNotNull { animeInfoModel ->
                    item {
                        Box {
                            if (isNotNightMode) {
                                CoilImage(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    imageRequest = {
                                        ImageRequest.Builder(context)
                                            .data(animeInfoModel.video.cover)
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
                                    animeId = animeInfoModel.video.id,
                                    animeTitle = animeInfoModel.video.name,
                                    animeSubTitle = animeInfoModel.video.nameOriginal,
                                    animeCover = animeInfoModel.video.cover,
                                    animeRegion = animeInfoModel.video.area,
                                    animeType = animeInfoModel.video.type,
                                    animeOriginalWork = animeInfoModel.video.writer,
                                    animePremiereDate = animeInfoModel.video.premiere,
                                    animePlotType = animeInfoModel.video.tags,
                                    animePlotTypeList = animeInfoModel.video.tagsArr,
                                    appState = appState
                                )

                                AnimeHotDiscussCollectItem(
                                    animeHot = animeInfoModel.video.rankCnt,
                                    animeCollect = animeInfoModel.video.commentCnt,
                                    animeDiscuss = animeInfoModel.video.collectCnt,
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
                                        AnimeIntroductionItem(animeInfoModel.video.intro)

                                        AnimePlayListTab(
                                            animeInfoModel.video.playlists,
                                            animeInfoModel.playerLabelArr,
                                            onPlayClick = { page, index, title, type ->
                                                appState.navigateToPlayer(
                                                    animeId = animeId
                                                        ?: animeInfoModel.video.id,
                                                    episodeType = type,
                                                    episodeTitle = title
                                                )
                                            }
                                        )

                                        HorizontalBaseAnimeList(
                                            stringResource(id = R.string.related_title),
                                            animeInfoModel.series,
                                            appState
                                        )

                                        HorizontalBaseAnimeList(
                                            stringResource(id = R.string.similar_title),
                                            animeInfoModel.similar,
                                            appState
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