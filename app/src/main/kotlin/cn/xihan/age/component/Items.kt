package cn.xihan.age.component

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import androidx.compose.animation.animateColor
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.model.BannerListItemModel
import cn.xihan.age.model.HistoryModel
import cn.xihan.age.model.HomeListModel
import cn.xihan.age.model.RankModel
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.Utils
import cn.xihan.age.util.VerticalSpace
import cn.xihan.age.util.currentWeek
import cn.xihan.age.util.extension.copyToClipboard
import cn.xihan.age.util.openUrl
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.util.url
import com.skydoves.landscapist.ImageOptions
import com.skydoves.landscapist.coil.CoilImage
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/23 20:43
 * @介绍 :
 */
/**
 * 测量compose帧数
 */
@Composable
fun FpsMonitor(modifier: Modifier) {
    var fpsCount by rememberMutableStateOf(0)
    var fps by rememberMutableStateOf(0)
    var lastUpdate by rememberMutableStateOf(0L)
    Text(
        text = "Fps: $fps", modifier = modifier
            .size(60.dp, 30.dp), color = Color.Red, style = MaterialTheme.typography.bodySmall
    )

    LaunchedEffect(Unit) {
        while (true) {
            withFrameMillis { ms ->
                fpsCount++
                if (fpsCount == 5) {
                    fps = (5000 / (ms - lastUpdate)).toInt()
                    lastUpdate = ms
                    fpsCount = 0
                }
            }
        }
    }
}

/**
 * 错误模型
 * @param errorMessage 错误信息
 * @param onRetryClick 点击事件
 */
@Composable
fun ErrorItem(
    modifier: Modifier = Modifier,
    errorMessage: String?, onRetryClick: () -> Unit = {},
) {
    Box(modifier = modifier
        .padding(16.dp)
        .clickable {
            onRetryClick()
        }) {
        CoilImage(
            imageModel = { R.drawable.parsing_error_bg }, imageOptions = ImageOptions(
                contentScale = ContentScale.Fit, alignment = Alignment.Center
            )
        )
        Text(
            text = errorMessage ?: stringResource(id = R.string.error_unknown),
            modifier = Modifier
                .padding(top = 8.dp)
                .align(Alignment.Center),
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )
    }
}

inline fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline span: (LazyGridItemSpanScope.(item: T?) -> GridItemSpan)? = null,
    noinline contentType: (item: T?) -> Any? = { null },
    crossinline itemContent: @Composable LazyGridItemScope.(item: T?) -> Unit
) {
    items(
        count = items.itemCount,
        key = if (key == null) null else { index ->
            val item = items.peek(index)
            if (item == null) {
                MyPagingPlaceholderKey(index)
            } else {
                key(item)
            }
        },
        span = if (span != null) {
            { span(items[it]) }
        } else null,
        contentType = { index: Int -> contentType(items[index]) }
    ) { index ->
        itemContent(items[index])
    }
}

@SuppressLint("BanParcelableUsage")
data class MyPagingPlaceholderKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<MyPagingPlaceholderKey> =
            object : Parcelable.Creator<MyPagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    MyPagingPlaceholderKey(parcel.readInt())

                override fun newArray(size: Int) =
                    arrayOfNulls<MyPagingPlaceholderKey?>(size)
            }
    }
}

/**
 * 图片轮播图
 * @param list 图片列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyBanner(
    list: List<BannerListItemModel>,
    appState: MainAppState
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        list.size
    }
    HorizontalPager(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
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
        pageContent = { index ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .clickable {
                        appState.navigateToDesc(list[index].aID)
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CoilImage(
                        modifier = Modifier.fillMaxWidth(),
                        imageModel = {
                            list[index].picUrl.url
                        }, failure = {
                            Image(
                                modifier = Modifier.matchParentSize(),
                                painter = painterResource(id = R.drawable.error),
                                contentDescription = null
                            )
                        },
                        imageOptions = ImageOptions(
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.Center
                        )
                    )
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomEnd),
                        shape = MaterialTheme.shapes.small,
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0X66000000)
                        )
                    ) {
                        Text(
                            modifier = Modifier.padding(10.dp),
                            text = list[index].title,
                            color = Color.White
                        )
                    }

                }

            }

        }
    )
}

/**
 * 星期选项卡
 * @param [scope] 范围
 * @param [xinFanDataList] 新番数据列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun WeekTab(
    scope: CoroutineScope = rememberCoroutineScope(),
    xinFanDataList: List<ArrayList<HomeListModel.XinfansInfoModel>>,
    appState: MainAppState
) {
    val pagerState = rememberPagerState(
        initialPage = Utils.getWeekOfDate(),
        initialPageOffsetFraction = 0f
    ) { xinFanDataList.size }
    val list = remember { Utils.weekList() }
    Column {
        Card {
            TabRow(
                contentColor = MaterialTheme.colorScheme.secondary,
                selectedTabIndex = pagerState.currentPage
            ) {
                list.forEachIndexed { index, s ->
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
            pageContent = { index ->
                val currentWeek = pagerState.currentPage.currentWeek
                Column {
                    repeat(xinFanDataList[currentWeek].size) { index ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(4.dp)
                                .clickable {
                                    appState.navigateToDesc(xinFanDataList[currentWeek][index].id)
                                }
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth()
                                    .weight(1f),
                                text = xinFanDataList[currentWeek][index].name,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 1
                            )
                            if (xinFanDataList[currentWeek][index].isnew) {
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
                                    .weight(0.5f),
                                text = xinFanDataList[currentWeek][index].namefornew,
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

/**
 * 垂直网格动画卡项目
 * @param [modifier] 修饰符
 * @param [animeId] 动漫id
 * @param [title] 标题
 * @param [subtitle] 副标题
 * @param [cover] 封面
 * @param [onLongClick] 长按事件
 * @param [onDoubleClick] 双击事件
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun VerticalAnimeGridCardItem(
    modifier: Modifier = Modifier,
    animeId: String,
    title: String,
    subtitle: String,
    cover: String,
    onLongClick: () -> Unit = {},
    onDoubleClick: () -> Unit = {},
    appState: MainAppState
) {
    Card(
        modifier = modifier.combinedClickable(onLongClick = {
            onLongClick()
        }, onDoubleClick = {
            onDoubleClick()
        }, onClick = {
            appState.navigateToDesc(animeId)
        }),
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.CenterHorizontally)
            //.padding(8.dp)
            , horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                // 图片
                CoilImage(
                    modifier = Modifier.fillMaxSize(),
                    imageModel = { cover.url },
                    loading = {
                        CoilImage(
                            modifier = Modifier.fillMaxSize(), imageModel = { R.drawable.loading }
                        )
                    },
                    failure = {
                        CoilImage(
                            modifier = Modifier.fillMaxSize(), imageModel = { R.drawable.error }
                        )
                    }
                )
                Card(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    shape = RoundedCornerShape(0),
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = colorResource(id = R.color.colorAccent)
                    )
                ) {
                    Text(
                        text = subtitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }
            Text(
                text = title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

/**
 * 番剧封面以及信息模型
// * @param modifier Modifier
 * @param animeTitle 番剧标题
 * @param animeSubTitle 番剧副标题
 * @param animeCover 番剧封面
 * @param animeRegion 番剧地区
 * @param animeType 番剧类型
 * @param animeOriginalWork 番剧原作
 * @param animePremiereDate 番剧首播时间
 * @param animePlotType 番剧剧情类型
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeCoverItem(
    animeId: String,
    animeTitle: String,
    animeSubTitle: String,
    animeCover: String,
    animeRegion: String,
    animeType: String,
    animeOriginalWork: String,
    animePremiereDate: String,
    animePlotType: String,
    animePlotTypeList: List<String>,
    appState: MainAppState,
    modifier: Modifier = Modifier
) {
    Row {
        Card(
            modifier = modifier
                .width(128.dp)
                .height(182.dp)
                .padding(8.dp)
        ) {
            CoilImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { animeCover.url },
                imageOptions = ImageOptions(
                    contentScale = ContentScale.Crop, alignment = Alignment.Center
                )
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = animeTitle,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$animeRegion · $animeType · $animeOriginalWork",
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = animeSubTitle,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall
            )
            Text(
                text = animePremiereDate,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall
            )

            /*
            Text(
                text = animePlotType,
                maxLines = 1,
                overflow = TextOverflow.Visible,
                modifier = Modifier.padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleSmall
            )

             */

            FlowRow {
                animePlotTypeList.forEach {
                    ElevatedAssistChip(
                        onClick = {
                            appState.navigateToCatalog(type = it, animeId = animeId)
                        },
                        label = {
                            Text(text = it)
                        },
                    )
                }
            }

        }
    }

}

/**
 * 番剧热度、讨论、收藏模型
 * @param animeHot 番剧热度
 * @param animeDiscuss 番剧讨论
 * @param animeCollect 番剧收藏
 * @param isFavorite 是否收藏
 * @param onCollectClick 收藏点击事件
 */
@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun AnimeHotDiscussCollectItem(
    animeHot: Int,
    animeDiscuss: Int,
    animeCollect: Int,
    isFavorite: Boolean,
    onCollectClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    var favorite by rememberMutableStateOf(value = isFavorite)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentWidth(Alignment.CenterHorizontally)
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = R.drawable.local_fire_department),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Red)
        )

        Text(
            text = Utils.intChange2Str(animeHot),
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.size(8.dp))

        Image(
            modifier = Modifier.padding(start = 8.dp),
            painter = painterResource(id = R.drawable.sms),
            contentDescription = null,
            colorFilter = ColorFilter.tint(Color.Red)
        )

        Text(
            text = "$animeDiscuss",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.size(8.dp))

        IconToggleButton(checked = favorite, onCheckedChange = {
            favorite = it
            onCollectClick(it)
        }) {
            val transition = updateTransition(favorite, label = "")
            val tint by transition.animateColor(label = "iconColor") { isChecked ->
                if (isChecked) Color.Red else MaterialTheme.colorScheme.onSurface
            }
            val size by transition.animateDp(
                transitionSpec = {
                    if (false isTransitioningTo true) {
                        keyframes {
                            durationMillis = 250
                            30.dp at 0 with LinearOutSlowInEasing
                            durationMillis = 250
                            30.dp at 0 with LinearOutSlowInEasing
                            35.dp at 15 with FastOutLinearInEasing
                            40.dp at 75
                            35.dp at 150
                        }
                    } else {
                        spring(stiffness = Spring.StiffnessVeryLow)
                    }
                }, label = "Size"
            ) { 24.dp }

            Icon(
                imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(size)
            )
        }

        Text(
            text = "$animeCollect",
            modifier = Modifier.padding(8.dp),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )

    }

}

/**
 * 番剧简介模型
 */
@Composable
fun AnimeIntroductionItem(
    animeIntroduction: String
) {
    var expander by rememberMutableStateOf(value = false)

    Text(
        modifier = Modifier.padding(8.dp),
        text = "简介",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                expander = !expander
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .animateContentSize()
                .fillMaxWidth()
                .padding(8.dp),
            text = animeIntroduction,
            maxLines = if (!expander) 3 else Int.MAX_VALUE,
            overflow = TextOverflow.Visible,
            style = MaterialTheme.typography.bodyMedium
        )

        IconButton(
            onClick = { expander = !expander }) {
            Icon(
                imageVector = if (expander) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
    }

}

/**
 * 番剧播放列表Tab 选项卡
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimePlayListTab(
    playList: List<List<AnimeDetailModel.AniInfoModel.PlayModel>>,
    onPlayClick: (selectedOptionIndex: Int, title: String) -> Unit,
) {
    /**
     * 播放列表标题
     */
    val titleItems: List<String> = Utils.getPlayListTitle(playList)
    val playList by rememberMutableStateOf(playList)
    if (playList.isNotEmpty() && titleItems.isNotEmpty()) {

        /**
         * 正序倒序
         */
        var isAsc by rememberMutableStateOf(true)
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            titleItems.size
        }
        val scope = rememberCoroutineScope()

        /**
         * 展开全部状态
         */
        var expandedAll by rememberMutableStateOf(false)
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f),
                    text = "播放列表",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleLarge,
                )

                /*
                IconButton(onClick = { isAsc = !isAsc }) {
                    Icon(
                        if (isAsc) Icons.Outlined.KeyboardArrowUp else Icons.Outlined.ArrowDropDown,
                        contentDescription = null
                    )
                }

                 */


                TextButton(
                    onClick = { isAsc = !isAsc }) {
                    Text(text = if (isAsc) "正序" else "倒序")
                }


            }

            TabRow(
                contentColor = MaterialTheme.colorScheme.secondary,
                selectedTabIndex = pagerState.currentPage
            ) {
                titleItems.forEachIndexed { index, s ->
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

                    val index = Utils.getPlayListIndex(titleItems[page])
                    var playListItem = playList[index].toMutableList()
                    if (playListItem.isNotEmpty()) {
                        if (playListItem.size > 24) {
                            playListItem = if (isAsc) {
                                playListItem.subList(0, 23)
                            } else {
                                playListItem.subList(
                                    playListItem.size - 23,
                                    playListItem.size
                                )
                            }
                            playListItem += AnimeDetailModel.AniInfoModel.PlayModel(
                                title = stringResource(id = R.string.expand_all),
                                epName = stringResource(id = R.string.expand_all)
                            )
                        }



                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(if (playListItem.size < 4) 100.dp else ((playListItem.size / 4) * 60).dp)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
//                        state = rememberLazyListState()
                        ) {
                            items(playListItem.size) { item ->
                                if (isAsc) {
                                    AnimePlayListGrid(title = playListItem[item].title) {
                                        if ("展开全部" == playListItem[item].epName) {
                                            expandedAll = true
                                        } else {
                                            onPlayClick(
                                                index, playListItem[item].epName
                                            )
                                        }
                                    }
                                } else {
                                    AnimePlayListGrid(title = playListItem.reversed()[item].title) {
                                        if ("展开全部" == playListItem.reversed()[item].epName) {
                                            expandedAll = true
                                        } else {
                                            onPlayClick(
                                                index, playListItem.reversed()[item].epName
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        if (expandedAll) {
                            BottomSheetDialog(
                                onDismissRequest = {
                                    expandedAll = false
                                }, properties = BottomSheetDialogProperties(
                                    dismissOnBackPress = true, dismissOnClickOutside = true
                                )
                            ) {
                                AnimePlayListExpandAll(
                                    playList = playList[index],
                                    onDismissRequest = { expandedAll = false },
                                    onPlayClick = { i ->
                                        val las = titleItems[pagerState.currentPage].substring(
                                            titleItems[pagerState.currentPage].length - 1,
                                            titleItems[pagerState.currentPage].length
                                        ).toInt() - 1
                                        onPlayClick(
                                            las,
                                            playList[index][i].title
                                        )
                                    }
                                )

                            }
                        }

                    } else {
                        ErrorItem(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(8.dp),
                            errorMessage = stringResource(id = R.string.error_empty)
                        )
                    }

                }
            )

        }


    } else {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp), errorMessage = stringResource(id = R.string.error_empty)
        )
    }

}

/**
 * 网格集数列表
 * @param title 集数标题
 */
@Composable
fun AnimePlayListGrid(
    title: String,
    isSelected: Boolean = false,
    isDarkColor: Boolean = false,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 2.dp, end = 2.dp)
            .clickable {
                onClick()
            },
        colors =
        CardDefaults.cardColors(
            containerColor = if (isDarkColor) Color.DarkGray else MaterialTheme.colorScheme.surface,
            contentColor = if (isDarkColor) Color.DarkGray else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(5.dp),
    ) {
        Text(
            text = title,
            modifier = Modifier
                .wrapContentSize()
                .padding(15.dp)
                .align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.bodySmall,
            color = if (isSelected) colorResource(id = R.color.colorAccent) else MaterialTheme.colorScheme.onSurface
        )
    }
}

/**
 * 展开全部播放列表
 * @param playList 播放列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimePlayListExpandAll(
    playList: List<AnimeDetailModel.AniInfoModel.PlayModel>,
    onDismissRequest: () -> Unit,
    onPlayClick: (item: Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.7f)
            .clip(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = "选集",
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
            )

            IconButton(onClick = {
                onDismissRequest()
            }) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "关闭",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        if (playList.size > 100) {
            val scope = rememberCoroutineScope()
            val groupTitleList = remember {
                Utils.getGroupList(playList)
            }
            val pagerState = rememberPagerState(
                initialPage = 0,
                initialPageOffsetFraction = 0f
            ) {
                groupTitleList.size
            }

//            val groupPosition = Utils.getGroupListPosition(playList)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
            ) {
                ScrollableTabRow(
                    selectedTabIndex = pagerState.currentPage,
                    edgePadding = 0.dp
                ) {
                    groupTitleList.forEachIndexed { index, s ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
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
                modifier = Modifier
                    .padding(top = 4.dp),
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
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(Utils.getGroupList(playList, page).size) { index ->
                            val title = Utils.getGroupList(playList, page)[index].title
                            AnimePlayListGrid(title = title) {
                                onPlayClick(playList.indexOfFirst { it.title == title })
                            }
                        }

                    }
                }
            )


        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(playList.size) { item ->
                    AnimePlayListGrid(title = playList[item].title) {
                        onPlayClick(item)
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimePlayTab(
    playList: List<AnimeDetailModel.AniInfoModel.PlayModel>,
    onPlayClick: (item: AnimeDetailModel.AniInfoModel.PlayModel) -> Unit,
) {
    if (playList.size > 100) {
        val scope = rememberCoroutineScope()
        val groupTitleList = remember { Utils.getGroupList(playList) }
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            groupTitleList.size
        }

//            val groupPosition = Utils.getGroupListPosition(playList)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.DarkGray,
                edgePadding = 0.dp
            ) {
                groupTitleList.forEachIndexed { index, s ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
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
            modifier = Modifier
                .padding(top = 4.dp),
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
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(Utils.getGroupList(playList, page).size) { index ->
                        val title = Utils.getGroupList(playList, page)[index].title
                        val isSelected =
                            playList[playList.indexOfFirst { it.title == title }].playing
                        AnimePlayListGrid(
                            title = title,
                            isSelected = isSelected,
                            isDarkColor = true
                        ) {
                            onPlayClick(playList[playList.indexOfFirst { it.title == title }])
                        }
                    }

                }

            }
        )


    } else {
        LazyVerticalGrid(
            columns = GridCells.Fixed(4),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(playList.size) { item ->
                AnimePlayListGrid(
                    title = playList[item].title,
                    isSelected = playList[item].playing,
                    isDarkColor = true
                ) {
                    onPlayClick(playList[item])
                }
            }
        }
    }

}

/**
 * 网盘资源模型
 * @param diskResourceList 网盘资源列表
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AnimeDiskResource(
    diskResourceList: List<AnimeDetailModel.AniInfoModel.CloudDiskModel>,
) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = "网盘资源",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
    )

    diskResourceList.whatIfNotNullOrEmpty(whatIf = { list ->
        /**
         * 展开状态
         */
        var expanded by remember { mutableStateOf(false) }

        /**
         * 选中文本
         */
        var selectedOptionText by remember { mutableStateOf("${diskResourceList[0].title} 提取码: ${diskResourceList[0].exCode}") }
        Column {
            ExposedDropdownMenuBox(
                expanded = expanded, onExpandedChange = {
                    expanded = !expanded
                }, modifier = Modifier.padding(5.dp)
            ) {
                TextField(readOnly = true, value = selectedOptionText, onValueChange = {
                    selectedOptionText = it
                }, modifier = Modifier.fillMaxSize(), trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                }, colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = {
                        expanded = false
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = MaterialTheme.colorScheme.surface)
                ) {
                    list.forEach { aniInfoModel ->
                        DropdownMenuItem(text = {
                            Text(
                                text = "${aniInfoModel.title} 提取码: ${aniInfoModel.exCode}",
                                modifier = Modifier.padding(5.dp)
                            )
                        }, onClick = {
                            selectedOptionText =
                                "${aniInfoModel.title} 提取码: ${aniInfoModel.exCode}"

                            (aniInfoModel.link).whatIfNotNullOrEmpty {
                                it.openUrl()
                            }
                            (aniInfoModel.exCode).whatIfNotNullOrEmpty {
                                it.copyToClipboard()
                            }
                            expanded = false
                        }, modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }, whatIfNot = {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp), errorMessage = stringResource(id = R.string.error_empty)
        ) {}
    })

}

/**
 * 相关动画模型
 * @param relatedList 相关动画列表
 */
@Composable
fun AnimeRelated(
    relatedList: List<AnimeDetailModel.AniPreRelModel>?,
    appState: MainAppState
) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = "相关动画",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
    )

    relatedList.whatIfNotNullOrEmpty(whatIf = { list ->
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            items(list.size) { item ->
                VerticalAnimeGridCardItem(
                    modifier = Modifier
                        .fillParentMaxWidth(.5f)
                        .fillParentMaxHeight(.7f)
                        .padding(8.dp),
                    animeId = list[item].aID,
                    title = list[item].title,
                    subtitle = list[item].newTitle,
                    cover = list[item].picSmall,
                    appState = appState
                )
            }

        }
    }, whatIfNot = {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp), errorMessage = stringResource(id = R.string.error_empty)
        ) {}
    })
}

/**
 * 猜你喜欢模型
 * @param guessLikeList 猜你喜欢列表
 */
@Composable
fun AnimeGuessLike(
    guessLikeList: List<AnimeDetailModel.AniPreSimModel>?,
    appState: MainAppState
) {
    Text(
        modifier = Modifier.padding(8.dp),
        text = "猜你喜欢",
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.titleLarge,
    )

    guessLikeList.whatIfNotNullOrEmpty(whatIf = { list ->
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {

            items(list.size) { item ->
                VerticalAnimeGridCardItem(
                    modifier = Modifier
                        .fillParentMaxWidth(.5f)
                        .padding(4.dp),
                    animeId = list[item].aID,
                    title = list[item].title,
                    subtitle = list[item].newTitle,
                    cover = list[item].picSmall,
                    appState = appState
                )
            }

        }
    }, whatIfNot = {
        ErrorItem(
            modifier = Modifier
                .wrapContentSize()
                .padding(8.dp), errorMessage = stringResource(id = R.string.error_empty)
        ) {}
    })
}

/**
 * 排行榜模型
 * @param [modifier] 修饰符
 * @param [rankingModel] 排名模型
 * @param [appState] 应用程序状态
 */
@Composable
fun AnimeRanking(
    modifier: Modifier = Modifier,
    rankingModel: RankModel.AniRankPreModel,
    appState: MainAppState
) {
    Row(
        modifier = modifier.clickable {
            appState.navigateToDesc(rankingModel.aID)
        }, verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.align(Alignment.CenterVertically),
            shape = MaterialTheme.shapes.small,
            border = BorderStroke(
                1.dp,
                color = if (rankingModel.nO <= 10) Color.Red else MaterialTheme.colorScheme.onSurface
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            ),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = "${rankingModel.nO}",
                color = if (rankingModel.nO <= 10) Color.Red else MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge,
            )
        }

        Text(
            modifier = Modifier
                .padding(8.dp)
                .align(Alignment.CenterVertically),
            text = rankingModel.title,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            style = MaterialTheme.typography.labelLarge,
        )

    }


}

/**
 * 历史记录模型
 */
@Composable
fun HistoryItem(
    historyModel: HistoryModel,
    modifier: Modifier = Modifier,
    onPlayClick: () -> Unit = {},
    onDescClick: () -> Unit = {},
    onRemoveClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
            .clickable { onPlayClick() },

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
                    imageModel = { historyModel.animeCover.url },
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
                    Text(
                        text = stringResource(id = R.string.detail),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Visible,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                onDescClick()
                            },
                        color = Color(0xFFFF6699),
                        style = MaterialTheme.typography.titleSmall,
                        textAlign = TextAlign.Start
                    )

                    IconButton(onClick = onRemoveClick) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = null,
                            tint = Color(0xFFFF6699)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AdaptiveIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .clickable(
                onClick = onClick,
                enabled = enabled,
                interactionSource = interactionSource,
                indication = LocalIndication.current
            ),
        contentAlignment = Alignment.Center
    ) {
        val contentAlpha = if (enabled) LocalContentAlpha.current else ContentAlpha.disabled
        CompositionLocalProvider(LocalContentAlpha provides contentAlpha, content = content)
    }
}

@Composable
fun CardIconButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    text: @Composable () -> Unit = {},
    icon: @Composable () -> Unit = {},
) {
    Card(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .then(modifier),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
        ) {
            text()
            icon()
        }

    }
}

@Composable
fun CardIconButton(
    text: String,
    icon: Painter,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .clickable {
                onClick()
            }
            .then(modifier),
        shape = RoundedCornerShape(10.dp),
//        elevation = CardDefaults.cardElevation(1.dp),
//        border = BorderStroke(1.dp, Color.White),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent,
            contentColor = Color.Transparent,
        )
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(30.dp),
                painter = icon,
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )

            Spacer(Modifier.width(12.dp))

            Text(
                modifier = Modifier,
                text = text,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold
            )

        }

    }
}

@Composable
fun AbstractSetting(
    modifier: Modifier = Modifier,
    left: @Composable () -> Unit = {},
    right: @Composable () -> Unit = {},
    center: @Composable RowScope.() -> Unit = {},
    onClick: () -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        left()
        center()
        right()
    }
}

@Composable
fun IconTextSetting(
    modifier: Modifier = Modifier,
    title: String = "",
    subtitle: String = "",
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {},
    icon: @Composable () -> Unit = {},
) {
    AbstractSetting(
        modifier = modifier,
        left = icon,
        right = {
            if (subtitle.isNotBlank()) {
                Text(
                    text = subtitle,
                    overflow = TextOverflow.Visible,
                    modifier = Modifier.padding(8.dp),
                    color = textColor,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        },
        center = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
        },
        onClick = onClick
    )
}

@Composable
fun IconTextSetting(
    modifier: Modifier = Modifier,
    title: String = "",
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit = {},
    rightIcon: @Composable () -> Unit = {},
) {
    AbstractSetting(
        modifier = modifier,
        left = {  },
        right = rightIcon,
        center = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
        },
        onClick = onClick
    )
}

@Composable
fun TextNumberSetting(
    input: MutableState<Int>,
    modifier: Modifier = Modifier,
    title: String = "",
    message: String = "",
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    onOkClick: (Int) -> Unit = {},
) {
    val context = LocalContext.current
    AbstractSetting(
        modifier = modifier,
        right = {
            Text(
                text = "${input.value}",
                overflow = TextOverflow.Visible,
                modifier = Modifier.padding(8.dp),
                color = textColor,
                style = MaterialTheme.typography.titleSmall
            )
        },
        center = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
        },
        onClick = {
            Utils.showNumberInputDialog(
                context = context,
                title = title,
                message = message,
                input = input.value
            ) {
                input.value = it
                onOkClick(it)
            }
        }
    )

}

@Composable
fun IconSwitchSetting(
    checked: MutableState<Boolean>,
    modifier: Modifier = Modifier,
    title: String = "",
    textColor: Color = MaterialTheme.colorScheme.onSurface,
    enabled: Boolean = true,
    enabledAutoChange: Boolean = true,
    icon: @Composable () -> Unit = {},
    onCheckedChange: (Boolean) -> Unit = {},
    onClick: () -> Unit = {},
) {
    AbstractSetting(
        modifier = modifier,
        left = icon,
        right = {
            Switch(
                checked = checked.value,
                onCheckedChange = {
                    checked.value = it
                    onCheckedChange(it)
                },
                enabled = enabled
            )
        },
        center = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
        },
        onClick = {
            if (enabledAutoChange) {
                checked.value = !checked.value
            }
            onClick()
        }
    )
}

@Composable
fun EditTextSetting(
    title: String,
    text: MutableState<String>,
    modifier: Modifier = Modifier,
    subTitle: String = "",
    right: @Composable () -> Unit = {},
    onTextChange: ((String) -> Unit) = {},
) {
    AbstractSetting(
        modifier = modifier,
        left = {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Visible,
                modifier = Modifier
                    .padding(8.dp),
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Start
            )
        },
        right = right,
        center = {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
            ) {
                Text(
                    text = subTitle,
                    overflow = TextOverflow.Visible,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleSmall
                )
                TextField(
                    value = text.value,
                    onValueChange = {
                        text.value = it
                        onTextChange(it)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        errorIndicatorColor = Color.Transparent,
                    ),
                    textStyle = MaterialTheme.typography.titleMedium,
                    singleLine = true,
                    maxLines = 1,
                    trailingIcon = {
                        if (text.value.isNotEmpty()) {
                            IconButton(onClick = { text.value = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                )
            }
        }
    )

}

/**
 * 横向列表
 * @param data 数据
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HorizontalCatalogItems(
    data: List<String>,
    modifier: Modifier = Modifier,
    initialPage: Int = 0,
    onClick: (String) -> Unit = {},
) {
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) { data.size }
    val scope = rememberCoroutineScope()

    ScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
        edgePadding = 4.dp,
        modifier = modifier
    ) {
        data.forEachIndexed { index, s ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    onClick(s)
                },
                text = {
                    Text(
                        text = s,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            )
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
        pageContent = {

        }
    )


}