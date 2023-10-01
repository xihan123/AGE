package cn.xihan.age.ui.rank

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
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
import cn.xihan.age.model.Category
import cn.xihan.age.model.RankModel
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberMutableStateOf
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 14:16
 * @介绍 :
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RankingScreen(
    onAnimeClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: RankingViewModel = hiltViewModel(),
) {
    val state by viewModel.collectAsState()
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(
        initialPage = 0, initialPageOffsetFraction = 0f
    ) { state.rankingList.size }
    val listState = rememberLazyListState()
    val listState2 = rememberLazyListState()
    val listState3 = rememberLazyListState()
    var expanded by rememberMutableStateOf(value = false)
    val interactionSource = rememberMutableInteractionSource()

    /**
     * 排行榜页面周榜、月榜、总榜的列表合集
     */
    val rankTitleList by lazy { arrayListOf("周榜", "月榜", "总榜") }
    AgeScaffold(
        state = state,
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = stringResource(id = R.string.title_ranking))
            }, modifier = Modifier.fillMaxWidth(), actions = {
                IconButton(onClick = {
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
                }) {
                    Icon(Icons.Filled.KeyboardArrowUp, null)
                }

                IconButton(onClick = {
                    expanded = !expanded
                }) {
                    Icon(painter = painterResource(AgeAnimeIcons.filter), null)
                }

                IconButton(onClick = onSearchClick) {
                    Icon(painter = painterResource(id = AgeAnimeIcons.search), null)
                }
            }, navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, null)
                }
            }, scrollBehavior = null
            )
        },
        onShowSnackbar = onShowSnackbar,
        onDismissErrorDialog = viewModel::hideError,
        onRefresh = viewModel::getRankingModel
    ) { padding, _ ->
        Column(
            modifier = Modifier.padding(padding),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .background(color = Color.Transparent),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.padding(start = 12.dp),
                    text = if ("all" == state.year || "全部" == state.year) "全部" else state.year,
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
                        Tab(selected = pagerState.currentPage == index,
                            onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                            content = {
                                Text(
                                    modifier = Modifier.padding(vertical = 16.dp), text = s
                                )
                            })
                    }
                }
            }

            if (state.error != null) {
                ErrorItem(
                    errorMessage = state.error?.message
                        ?: stringResource(id = R.string.error_unknown),
                    onRetryClick = viewModel::getRankingModel
                )
            } else {
                state.rankingList.whatIfNotNullOrEmpty {
                    HorizontalPager(modifier = Modifier,
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

                                items(state.rankingList[page].size,
                                    key = { state.rankingList[page][it].aID }) { index ->
                                    val item = state.rankingList[page][index]
                                    Ranking(rankModel = item, onAnimeClick = onAnimeClick)
                                }

                            }
                        })


                }

            }
            if (expanded) {
                BottomSheetDialog(
                    onDismissRequest = {
                        expanded = false
                    }, properties = BottomSheetDialogProperties(
                        dismissOnBackPress = true, dismissOnClickOutside = true
                    )
                ) {

                    VerticalItems(modifier = Modifier
                        .height(250.dp)
                        .padding(20.dp),
                        data = Category.Year.options.map { it.first }) {
                        viewModel.changeYear(it)
                    }
                }

            }
        }
    }

}

@Composable
fun Ranking(
    rankModel: RankModel, onAnimeClick: (Int) -> Unit
) {
    val interactionResource = rememberMutableInteractionSource()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .clickable(interactionSource = interactionResource,
                indication = null,
                role = Role.Button,
                onClick = { onAnimeClick(rankModel.aID) }),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.align(Alignment.CenterVertically),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(
                1.dp,
                color = if (rankModel.nO <= 10) Color.Red else MaterialTheme.colorScheme.onSurface
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "${rankModel.nO}",
                color = if (rankModel.nO <= 10) Color.Red else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            text = rankModel.title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            text = rankModel.cCnt,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )

    }
}

/**
 * 垂直列表
 */
@Composable
fun VerticalItems(
    data: List<String>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onClick: (String) -> Unit = {}
) {

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        items(data.size) {
            TextButton(onClick = { onClick.invoke(data[it]) }) {
                Text(
                    text = data[it],
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }
    }

}