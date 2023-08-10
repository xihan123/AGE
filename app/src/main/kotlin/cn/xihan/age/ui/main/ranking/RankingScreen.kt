package cn.xihan.age.ui.main.ranking

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.BottomSheetDialog
import cn.xihan.age.component.BottomSheetDialogProperties
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.VerticalItems
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.Utils.rankTitleList
import cn.xihan.age.util.rememberMutableStateOf
import com.skydoves.whatif.whatIfNotNullOrEmpty
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
    ExperimentalFoundationApi::class, ExperimentalMaterialApi::class
)
@Composable
fun RankingScreen(
    appState: MainAppState,
    year: String? = "",
    viewModel: RankingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val refreshState = rememberPullRefreshState(state.refreshing, onRefresh = {
        viewModel.getRankingModel()
    })
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { rankTitleList.size }
    val context = LocalContext.current

    val listState = rememberLazyListState()
    val listState2 = rememberLazyListState()
    val listState3 = rememberLazyListState()
    var expanded by rememberMutableStateOf(value = false)
    val firstPlayYear = rememberMutableStateOf(value = context.getString(R.string.all))

    LaunchedEffect(state.year) {
        firstPlayYear.value = if (state.year == "all") {
            context.getString(R.string.all)
        } else {
            state.year
        }
    }


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
                    IconButton(
                        onClick = {
                            scope.launch {
                                when (pagerState.currentPage) {
                                    0 -> listState
                                    1 -> listState2
                                    2 -> listState3
                                    else -> {
                                        listState
                                    }
                                }.animateScrollToItem(0)
                            }
                        }
                    ) {
                        Icon(Icons.Filled.KeyboardArrowUp, null)
                    }

                    IconButton(onClick = {
                        expanded = !expanded
                    }) {
                        Icon(Icons.Filled.List, null)
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

        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(color = Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = firstPlayYear.value,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = buildAnnotatedString {
                        append("各榜前")
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xffff5f00),
                                fontWeight = FontWeight.W500,
                            )
                        ) {
                            append("50")
                        }
                        append("部")
                    },
                    textAlign = TextAlign.End,
                )

            }

            Card {
                TabRow(
                    contentColor = MaterialTheme.colorScheme.secondary,
                    selectedTabIndex = pagerState.currentPage
                ) {
                    rankTitleList.forEachIndexed { index, s ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch { pagerState.animateScrollToPage(index) }
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

            Box(modifier = Modifier.pullRefresh(refreshState)) {
                if (state.error != null) {
                    ErrorItem(
                        errorMessage = state.error?.message
                            ?: stringResource(id = R.string.error_unknown)
                    ) {
                        viewModel.getRankingModel()
                    }
                } else {
                    state.currentRankingListData.whatIfNotNullOrEmpty {

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
                            pageContent = { page ->
                                LazyColumn(
                                    state = when (page) {
                                        0 -> listState
                                        1 -> listState2
                                        2 -> listState3
                                        else -> {
                                            listState
                                        }
                                    }
                                ) {
                                    items(
                                        state.currentRankingListData[page].size,
                                        key = { state.currentRankingListData[page][it].id }) { index ->
                                        val rankingItemModel =
                                            state.currentRankingListData[page][index]
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(4.dp)
                                                .clickable {
                                                    appState.navigateToDesc(rankingItemModel.id)
                                                },
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Card(
                                                modifier = Modifier.align(Alignment.CenterVertically),
                                                shape = MaterialTheme.shapes.small,
                                                border = BorderStroke(
                                                    1.dp,
                                                    color = if (rankingItemModel.nO <= 10) Color.Red else MaterialTheme.colorScheme.onSurface
                                                ),
                                                colors = CardDefaults.cardColors(
                                                    containerColor = Color.Transparent
                                                ),
                                            ) {
                                                Text(
                                                    modifier = Modifier.padding(8.dp),
                                                    text = "${rankingItemModel.nO}",
                                                    color = if (rankingItemModel.nO <= 10) Color.Red else MaterialTheme.colorScheme.onSurface,
                                                    style = MaterialTheme.typography.titleLarge,
                                                )
                                            }

                                            Text(
                                                modifier = Modifier
                                                    .padding(8.dp)
                                                    .align(Alignment.CenterVertically),
                                                text = rankingItemModel.title,
                                                color = MaterialTheme.colorScheme.onSurface,
                                                maxLines = 1,
                                                style = MaterialTheme.typography.labelLarge,
                                            )

                                            Text(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .weight(0.5f),
                                                text = rankingItemModel.cCnt,
                                                style = MaterialTheme.typography.bodyMedium,
                                                textAlign = TextAlign.End
                                            )

                                        }

                                    }
                                }

                            }
                        )

                    }
                }

                PullRefreshIndicator(
                    refreshing = state.refreshing,
                    state = refreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
        if (expanded) {
            BottomSheetDialog(
                onDismissRequest = {
                    expanded = false
                },
                properties = BottomSheetDialogProperties(
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                )
            ) {

                VerticalItems(
                    modifier = Modifier
                        .height(250.dp)
                        .padding(20.dp),
                    data = Utils.analysisLabels(3)
                ) {
                    viewModel.changeYear(it)
                }
            }

        }


    }

}