package cn.xihan.age.ui.main.ranking

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimeRanking
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.items
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/10 6:27
 * @介绍 :
 */
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun RankingScreen(
    appState: MainAppState,
    year: String? = "",
    viewModel: RankingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val yearList = remember { Utils.analysisLabels(3) }
    val pagerState = rememberPagerState(
        initialPage = if (year.isNullOrBlank()) 0 else yearList.indexOf(year),
        initialPageOffsetFraction = 0f
    ) {
        yearList.size
    }
    val yearDataList = state.currentRankingListData.collectAsLazyPagingItems()
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    AgeScaffold(
        state = state,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.app_name))
                },
                modifier = Modifier
                    .fillMaxWidth(),
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            gridState.animateScrollToItem(0)
                        }
                    }) {
                        Icon(Icons.Filled.KeyboardArrowUp, null)
                    }
                },
                navigationIcon = {

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
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Card {
                ScrollableTabRow(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    selectedTabIndex = pagerState.currentPage
                ) {
                    yearList.forEachIndexed { index, s ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            },
                            text = {
                                Text(
                                    modifier = Modifier.padding(vertical = 16.dp),
                                    text = s
                                )
                            }
                        )
                    }
                }
            }

            HorizontalPager(
                modifier = Modifier,
                state = pagerState,
                pageSpacing = 0.dp,
                userScrollEnabled = true,
                reverseLayout = false,
                contentPadding = PaddingValues(0.dp),
                beyondBoundsPageCount = 0,
                pageSize = PageSize.Fill,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                key = null,
                pageNestedScrollConnection = PagerDefaults.pageNestedScrollConnection(
                    Orientation.Horizontal
                ),
                pageContent = { page ->
                    viewModel.changeYear(yearList[page])
                }
            )

            if (state.error != null) {
                ErrorItem(
                    errorMessage = state.error?.message
                        ?: stringResource(id = R.string.error_unknown)
                ) {
                    viewModel.getRankingModel()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    state = gridState,
                    contentPadding = PaddingValues(15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = yearDataList,
                        key = { item -> item.title },
                        span = { GridItemSpan(1) }
                    ) { item ->
                        item?.let {
                            AnimeRanking(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(4.dp),
                                rankingModel = item,
                                appState = appState
                            )
                        }
                    }
                }
            }

        }


    }

}