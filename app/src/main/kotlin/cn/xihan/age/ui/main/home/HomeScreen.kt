package cn.xihan.age.ui.main.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimeGrid
import cn.xihan.age.component.PlaceholderHighlight
import cn.xihan.age.component.placeholder3
import cn.xihan.age.component.rememberSearchBarState
import cn.xihan.age.component.shimmer
import cn.xihan.age.component.shimmerBrush
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.Banner
import cn.xihan.age.model.WeekItem
import cn.xihan.age.ui.search.SearchScreen
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.basicWhite
import cn.xihan.age.ui.theme.brightNeutral05
import cn.xihan.age.ui.theme.brightNeutral06
import cn.xihan.age.ui.theme.pink50
import cn.xihan.age.util.Utils
import cn.xihan.age.util.getAspectRadio
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.rememberDerivedStateOf
import cn.xihan.age.util.rememberFocusRequester
import cn.xihan.age.util.rememberMutableInteractionSource
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.time.Duration.Companion.seconds


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 0:24
 * @介绍 :
 */
@Composable
fun HomeScreen(
    padding: PaddingValues,
    viewModel: HomeViewModel = hiltViewModel(),
    onCategoryClick: () -> Unit,
    onAnimeClick: (Int) -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    onNavigationClick: (String) -> Unit,
) {

    val state by viewModel.collectAsState()
    val aspectRatio = getAspectRadio()
    val isTablet = isTablet()
    val focusRequester = rememberFocusRequester()
    val lazyListState = rememberLazyListState()
    val scrollProgress by rememberDerivedStateOf {
        if (aspectRatio > 1.8 && lazyListState.firstVisibleItemIndex == 0) (lazyListState.firstVisibleItemScrollOffset / 300f).coerceAtMost(
            1f
        )
        else 1f
    }
    val searchBarState = rememberSearchBarState(state.isSearching, focusRequester, scrollProgress)

    SearchScreen(
        modifier = Modifier
            .zIndex(1f),
        uiState = searchBarState,
        onEnterExit = viewModel::updateSearchState,
        onCategoryClick = onCategoryClick,
        onHistoryClick = { onNavigationClick("历史记录") },
        onAnimeClick = onAnimeClick
    )

    AgeScaffold(
        state = state,
        modifier = Modifier
//            .fillMaxSize()
            .padding(padding),
        onShowSnackbar = onShowSnackbar,
        onErrorPositiveAction = {
            viewModel.getHomePage()
        },
        onDismissErrorDialog = viewModel::hideError,
        onRefresh = viewModel::getHomePage
    ) { _, _ ->

        LazyColumn(
            state = lazyListState,
            contentPadding = PaddingValues(top = if (aspectRatio > 1.8) 0.dp else 86.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            item("Carousel") {
                Carousel(
                    bannerList = state.bannerList,
                    onSlideClick = { it?.let { onAnimeClick(it) } })
            }

            item("Weekly Delivery List") {
                WeeklyDeliveryList(
                    weekItems = state.weekItems,
                    onAnimeClick = { it?.let { onAnimeClick(it) } })
            }

            item("Recent Updates") {
                AnimeGridWithHead(
                    headline = "最近更新",
                    animeList = state.latest,
                    useExpandCardStyle = true,
                    onMoreDetails = onNavigationClick,
                    onAnimeClick = onAnimeClick
                )
            }

            item("Daily Recommend") {
                AnimeGridWithHead(
                    headline = "每日推荐",
                    animeList = state.recommend,
                    useExpandCardStyle = true,
                    onMoreDetails = onNavigationClick,
                    onAnimeClick = onAnimeClick
                )
            }

        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Carousel(
    bannerList: List<Banner.BannerItemModel?>, onSlideClick: (Int?) -> Unit
) {
    val pagerState =
        rememberPagerState(0, pageCount = bannerList::size)

    LaunchedEffect(Unit) {
        while (true) {
            val before = pagerState.currentPage
            delay(5.seconds)
            if (pagerState.currentPage == before) {
                val target =
                    if (pagerState.currentPage == bannerList.size - 1) 0 else pagerState.currentPage + 1
                pagerState.animateScrollToPage(target)
            }
        }
    }

    Box {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
            state = pagerState,
            contentPadding = PaddingValues(0.dp),
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 0,
            pageSpacing = 0.dp,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val index = it % bannerList.size
            Box(
                Modifier
                    .clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Button,
                        onClick = { onSlideClick(bannerList[index]?.aID) })
                    .placeholder3(
                        visible = bannerList[index] == null,
                        highlight = PlaceholderHighlight.shimmer(),
                    )
            ) {
                CoilImage(
                    modifier = Modifier
                        .height(350.dp)
                        .fillMaxWidth(),
                    imageModel = { bannerList[index]?.picUrl },
                    imageOptions = ImageOptions(
                        contentScale = ContentScale.Crop, alignment = Alignment.Center
                    )
                )
                Box(
                    Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent, Color.Black.copy(0.6f)
                                )
                            )
                        )
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 12.dp, end = 12.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    TextWithShadow(
                        text = bannerList[index]?.title ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                    )
                }
            }
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 15.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            repeat(bannerList.size) {
                Box(
                    Modifier
                        .size(8.dp)
                        .padding(if (pagerState.currentPage % bannerList.size == it) 0.dp else 1.dp)
                        .background(
                            color = if (pagerState.currentPage % bannerList.size == it) basicWhite.copy(
                                0.9f
                            )
                            else basicWhite.copy(0.4f), shape = CircleShape
                        )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WeeklyDeliveryList(
    weekItems: Map<Int, List<WeekItem?>>, onAnimeClick: (Int?) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = Utils.getWeekOfDate().minus(1), pageCount = weekItems::size
    )
    val weekTitleList by lazy {
        arrayListOf(
            "周一", "周二", "周三", "周四", "周五", "周六", "周日"
        )
    }
    val coroutineScope = rememberCoroutineScope()
    Column {

        TabRow(
            contentColor = MaterialTheme.colorScheme.secondary,
            selectedTabIndex = pagerState.currentPage
        ) {
            weekTitleList.forEachIndexed { i, s ->
                val index = i % weekItems.size
                Tab(selected = pagerState.currentPage == index, onClick = {
                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                }, text = {
                    Text(
                        modifier = Modifier.padding(vertical = 16.dp), text = s
                    )
                })
            }
        }

        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp)),
            state = pagerState,
            contentPadding = PaddingValues(0.dp),
            pageSize = PageSize.Fill,
            beyondViewportPageCount = 0,
            pageSpacing = 0.dp,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val list = if (it == 6) weekItems[0] else weekItems[it + 1]
            Column {
                repeat(list!!.size) { index ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable {
                                onAnimeClick(list[index]?.id)
                            },
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .weight(1f)
                                .placeholder3(
                                    visible = list[index]?.name.isNullOrBlank(),
                                    highlight = PlaceholderHighlight.shimmer(),
                                ),
                            text = "${list[index]?.name}",
                            style = MaterialTheme.typography.bodyMedium,
                            maxLines = 1
                        )
                        if (list[index]?.isnew == 1) {
                            Image(
                                painter = painterResource(id = R.drawable.qmui_icon_tip_new),
                                null,
                                alignment = Alignment.Center,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(20.dp)
                                    .padding(4.dp)
                            )
                        }
                        Text(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(0.5f)
                                .placeholder3(
                                    visible = list[index]?.namefornew.isNullOrBlank(),
                                    highlight = PlaceholderHighlight.shimmer(),
                                ),
                            text = "${list[index]?.namefornew}",
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.End
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AnimeGridWithHead(
    headline: String?,
    animeList: List<AnimeModel?>,
    useExpandCardStyle: Boolean,
    onMoreDetails: (genre: String) -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (headline == null) {
                Box(
                    modifier = Modifier
                        .size(128.dp, 20.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(shimmerBrush(x = 128.dp, y = 20.dp, brightNeutral06))
                )
                Box(
                    modifier = Modifier
                        .size(64.dp, 16.dp)
                        .clip(MaterialTheme.shapes.small)
                        .background(shimmerBrush(x = 128.dp, y = 16.dp, brightNeutral05))
                )
            } else {
                StylizedHead(text = headline)
                MoreInfo { onMoreDetails(headline) }
            }
        }
        AnimeGrid(
            modifier = Modifier.padding(horizontal = 12.dp),
            useExpandCardStyle = useExpandCardStyle,
            animeList = animeList,
            onAnimeClick = onAnimeClick
        )
    }
}

@Composable
private fun MoreInfo(
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.clickable(
            interactionSource = rememberMutableInteractionSource(),
            indication = null,
            role = Role.Button,
            onClick = onClick
        ), verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.more), style = MaterialTheme.typography.bodySmall
        )
        Icon(
            modifier = Modifier.size(16.dp),
            painter = painterResource(AgeAnimeIcons.arrowRight),
            contentDescription = null
        )
    }
}

@Composable
private fun StylizedHead(
    text: String,
) {
    Box(Modifier.width(IntrinsicSize.Max)) {
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth(if (text.length < 3) 1f else 0.7f)
                .height(7.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        listOf(
                            pink50, pink50.copy(alpha = 0f)
                        )
                    ), shape = CircleShape
                )
        )
        Text(
            modifier = Modifier.padding(bottom = 1.dp),
            text = text,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
private fun TextWithShadow(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
) {
    Box {
        Text(
            modifier = modifier
                .offset(2.dp, 2.dp)
                .alpha(0.5f),
            text = text,
//            fontFamily = fontFamily,
            style = style
        )
        Text(
            modifier = modifier,
            text = text,
//            fontFamily = fontFamily,
            style = style
        )
    }
}