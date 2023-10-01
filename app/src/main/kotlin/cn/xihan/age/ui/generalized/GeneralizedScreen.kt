package cn.xihan.age.ui.generalized

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimeGrid
import cn.xihan.age.component.AnywhereDropdown
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.ExpandedAnimeCard
import cn.xihan.age.component.MyDropdownMenuItem
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.model.HistoryModel
import cn.xihan.age.ui.theme.pink
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Utils
import cn.xihan.age.util.VerticalSpace
import cn.xihan.age.util.getAspectRadio
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.items
import cn.xihan.age.util.logDebug
import cn.xihan.age.util.rememberMutableStateOf
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/28 22:45
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralizedScreen(
    key: String,
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: GeneralizedViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val aspectRatio = getAspectRadio()
    val isTablet = isTablet()
    val lazyGridState = rememberLazyGridState()
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val topAppBarExpanded = rememberMutableStateOf(value = false)
    logDebug("key: $key")

    AgeScaffold(
        state = state,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = key)
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            when (key) {
                                "最近更新", "本地收藏", "历史记录" -> lazyGridState.animateScrollToItem(
                                    0
                                )

                                "每日推荐", "网络收藏" -> lazyListState.animateScrollToItem(0)
                                else -> viewModel.showError(AgeException.SnackBarException("key错误"))
                            }
                        }
                    }) {
                        Icon(Icons.Filled.KeyboardArrowUp, null)
                    }

                    if ("本地收藏" == key) {
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
                            },
                            content = {
                                if (cn.xihan.age.BuildConfig.DEBUG) {
                                    MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                                        text = { Text("设置全部收藏") },
                                        onClick = {
                                            viewModel.changeAllFavoriteState(true)
                                        })
                                }
                                MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(id = R.string.clear_collect)) },
                                    onClick = {
                                        viewModel.changeAllFavoriteState(false)
                                    })

                                MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(R.string.default_sort)) },
                                    onClick = {
                                        viewModel.changeLocalFavoriteType(0)
                                    })

                                MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(R.string.reverse_sort)) },
                                    onClick = {
                                        viewModel.changeLocalFavoriteType(1)
                                    })

                            }
                        )
                    }

                    if ("历史记录" == key) {
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
                            },
                            content = {
                                MyDropdownMenuItem(
                                    topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(R.string.clear_history)) },
                                    onClick = {
                                        viewModel.deleteHistoryByAnimeId(0, true)
                                    }
                                )

                                MyDropdownMenuItem(
                                    topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(R.string.watch_time_sort)) },
                                    onClick = {
                                        viewModel.changeHistorySortState(0)
                                    }
                                )

                                MyDropdownMenuItem(
                                    topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(R.string.update_time_sort)) },
                                    onClick = {
                                        viewModel.changeHistorySortState(1)
                                    }
                                )

                                MyDropdownMenuItem(
                                    topAppBarExpanded = topAppBarExpanded,
                                    text = { Text(stringResource(R.string.default_sort)) },
                                    onClick = {
                                        viewModel.changeHistorySortState(2)
                                    }
                                )
                            }
                        )

                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, null)
                    }
                }, scrollBehavior = null
            )
        },
        onShowSnackbar = onShowSnackbar,
        onDismissErrorDialog = viewModel::hideError
    ) { padding, _ ->

        when (key) {
            "最近更新" -> Generzlized(
                padding = padding,
                lazyGridState = lazyGridState,
                isTablet = isTablet,
                aspectRatio = aspectRatio,
                pagingDataFlow = state.recentUpdates,
                onRetryClick = viewModel::getRecentUpdates,
                onAnimeClick = onAnimeClick
            )

            "每日推荐" -> Generalized(
                padding = padding,
                lazyListState = lazyListState,
                animeList = state.recommendList,
                onRetryClick = viewModel::getRecommend,
                onAnimeClick = onAnimeClick
            )

            "网络收藏" -> Generalized(
                padding = padding,
                lazyListState = lazyListState,
                animeList = state.collectList,
                onRetryClick = viewModel::getRecommend,
                onAnimeClick = onAnimeClick
            )

            "本地收藏" -> FavoriteGenerzlized(
                padding = padding,
                lazyGridState = lazyGridState,
                isTablet = isTablet,
                aspectRatio = aspectRatio,
                pagingDataFlow = state.localFavorites,
                onRetryClick = viewModel::getRecentUpdates,
                onAnimeClick = onAnimeClick
            )

            "历史记录" -> HistoryGenerzlized(
                padding = padding,
                lazyGridState = lazyGridState,
                pagingDataFlow = state.historys,
                onRetryClick = viewModel::getRecentUpdates,
                onRemoveClick = viewModel::deleteHistoryByAnimeId,
                onAnimeClick = onAnimeClick
            )

            else -> viewModel.showError(AgeException.SnackBarException("key错误"))
        }

    }


}

@Composable
fun Generzlized(
    padding: PaddingValues,
    lazyGridState: LazyGridState,
    isTablet: Boolean,
    aspectRatio: Float,
    pagingDataFlow: Flow<PagingData<AnimeModel>>,
    onRetryClick: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    val animeList = pagingDataFlow.collectAsLazyPagingItems()

    if (animeList.itemCount == 0) {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(padding)
                .padding(top = 36.dp),
            errorMessage = stringResource(id = R.string.error_empty),
            onRetryClick = onRetryClick
        )
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(if (isTablet) 4 else if (aspectRatio < 0.56) 6 else 3),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 36.dp,
                bottom = 12.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = animeList,
                key = { item -> item.aID },
                span = { GridItemSpan(1) }
            ) { item ->
                item?.let {
                    ExpandedAnimeCard(
                        anime = item, onClick = onAnimeClick
                    )
                }
            }
        }
    }

}

@Composable
fun FavoriteGenerzlized(
    padding: PaddingValues,
    lazyGridState: LazyGridState,
    isTablet: Boolean,
    aspectRatio: Float,
    pagingDataFlow: Flow<PagingData<FavoriteModel>>,
    onRetryClick: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    val animeList = pagingDataFlow.collectAsLazyPagingItems()

    if (animeList.itemCount == 0) {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(padding)
                .padding(top = 36.dp),
            errorMessage = stringResource(id = R.string.error_empty),
            onRetryClick = onRetryClick
        )
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(if (isTablet) 4 else if (aspectRatio < 0.56) 6 else 3),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 36.dp,
                bottom = 12.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = animeList,
                key = { item -> item.animeId },
                span = { GridItemSpan(1) }
            ) { item ->
                item?.let {
                    ExpandedAnimeCard(
                        anime = AnimeModel(
                            aID = item.animeId,
                            newTitle = item.animeSubtitle,
                            picSmall = item.animeCover,
                            title = item.animeName
                        ), onClick = onAnimeClick
                    )
                }
            }
        }
    }

}

@Composable
fun HistoryGenerzlized(
    padding: PaddingValues,
    lazyGridState: LazyGridState,
    pagingDataFlow: Flow<PagingData<HistoryModel>>,
    onRetryClick: () -> Unit,
    onRemoveClick: (Int) -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    val animeList = pagingDataFlow.collectAsLazyPagingItems()

    if (animeList.itemCount == 0) {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(padding)
                .padding(top = 36.dp),
            errorMessage = stringResource(id = R.string.error_empty),
            onRetryClick = onRetryClick
        )
    } else {
        LazyVerticalGrid(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = lazyGridState,
            columns = GridCells.Fixed(1),
            contentPadding = PaddingValues(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = animeList,
                key = { item -> item.animeId },
                span = { GridItemSpan(1) }
            ) { item ->
                item?.let {
                    HistoryItem(
                        historyModel = item,
                        onAnimeClick = onAnimeClick,
                        onRemoveClick = onRemoveClick
                    )
                }
            }
        }
    }
}

@Composable
fun Generalized(
    padding: PaddingValues,
    lazyListState: LazyListState,
    animeList: List<AnimeModel?>,
    onRetryClick: () -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    if (animeList.isEmpty()) {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(padding)
                .padding(top = 36.dp),
            errorMessage = stringResource(id = R.string.error_empty),
            onRetryClick = onRetryClick
        )
    } else {
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            state = lazyListState
        ) {
            item("Recommend") {
                AnimeGrid(
                    animeList = animeList,
                    useExpandCardStyle = true,
                    onAnimeClick = onAnimeClick
                )
            }
        }

    }
}


/**
 * 历史记录模型
 */
@Composable
private fun HistoryItem(
    historyModel: HistoryModel,
    modifier: Modifier = Modifier,
    onAnimeClick: (Int) -> Unit = {},
    onRemoveClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clickable { onAnimeClick(historyModel.animeId) },
    ) {
        Row(
            modifier = modifier.padding(8.dp),
        ) {
            Card(
                modifier = Modifier
                    .width(128.dp)
                    .height(182.dp)
                    .padding(8.dp)
            ) {
                CoilImage(modifier = Modifier.fillMaxSize(),
                    imageModel = { historyModel.animeCover },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop, alignment = Alignment.Center
                    ),
                    loading = {
                        Image(
                            modifier = Modifier.matchParentSize(),
                            painter = painterResource(id = R.drawable.loading),
                            contentDescription = null
                        )
                    },
                    failure = {
                        Image(
                            modifier = Modifier.matchParentSize(),
                            painter = painterResource(id = R.drawable.error),
                            contentDescription = null
                        )
                    })
            }

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = historyModel.animeName,
                    fontWeight = FontWeight.Bold,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )

                VerticalSpace(dp = 58.dp)

                Text(
                    text = String.format(
                        stringResource(id = R.string.last_watch_time),
                        historyModel.animeLastPlayingTime
                    ),
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall
                )

                VerticalSpace(dp = 8.dp)

                Text(
                    text = "${historyModel.animeLastPlayTitle} ${
                        Utils.stringForTime(
                            historyModel.animeLastPlayProgress
                        )
                    }/${
                        Utils.stringForTime(
                            historyModel.animeLastPlayDuration
                        )
                    }",
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall
                )

                VerticalSpace(dp = 8.dp)

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.weight(1f))

                    IconButton(onClick = { onRemoveClick(historyModel.animeId) }) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = pink
                        )
                    }
                }
            }
        }
    }
}