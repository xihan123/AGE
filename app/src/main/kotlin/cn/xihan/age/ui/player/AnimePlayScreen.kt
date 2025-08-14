package cn.xihan.age.ui.player

import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.hardware.SensorManager
import android.view.OrientationEventListener
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowInsetsControllerCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimatedFollowIcon
import cn.xihan.age.component.AnimeGrid
import cn.xihan.age.component.BottomSheetDialog
import cn.xihan.age.component.BottomSheetDialogProperties
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.PlaceholderHighlight
import cn.xihan.age.component.placeholder3
import cn.xihan.age.component.shimmer
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.CommentResponseModel
import cn.xihan.age.model.Video
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.neutral01
import cn.xihan.age.ui.theme.pink
import cn.xihan.age.ui.theme.pink10
import cn.xihan.age.ui.theme.pink30
import cn.xihan.age.util.Settings
import cn.xihan.age.util.Utils
import cn.xihan.age.util.isOrientationLocked
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.items
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.util.rememberSavableMutableStateOf
import cn.xihan.age.util.rememberSystemUiController
import cn.xihan.age.util.setScreenOrientation
import coil.compose.rememberAsyncImagePainter
import com.kongzue.dialogx.dialogs.PopTip
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/22 18:01
 * @介绍 :
 */
@OptIn(ExperimentalFoundationApi::class)
@UnstableApi
@Composable
fun AnimePlayScreen(
    animeId: Int,
    viewModel: AnimePlayViewModel = hiltViewModel(),
    onLabelClick: (String) -> Unit,
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit
) {

    val state by viewModel.collectAsState()
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val videoPlayerController =
        rememberVideoPlayerController(
//            VideoPlayerSource.Network("http://192.168.43.110:8888/down/BR2AfsDNU8tc.mp4")
//            VideoPlayerSource.Network("https://vip.lz-cdn14.com/20230707/26200_cb80acf2/2000k/hls/52699af9dfc000010.ts")
        )
    val followed by viewModel.queryFavoriteState().collectAsStateWithLifecycle(true)
    val videoState by videoPlayerController.state.collectAsStateWithLifecycle()
    var playerUrl by rememberSavableMutableStateOf<String?>(null)
    val coroutineScope = rememberCoroutineScope()
    val pageList by rememberMutableStateOf(value = listOf("简介", "评论"))
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = pageList::size
    )

    val systemUiController = rememberSystemUiController()
    var isFullscreen by rememberSavableMutableStateOf(value = false)
    var isAllowGetHistoryPosition by rememberSavableMutableStateOf(value = true)

    val onFullscreenChange: (Boolean) -> Unit = remember {
        { enable ->
            isFullscreen = enable
            context.setScreenOrientation(
                if (enable) ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            )
        }
    }

    val saveHistory: (Long, Long, Long) -> Unit = remember {
        { l1, l2, l3 ->
            if (l1 > 2000) {
                viewModel.saveHistoryProgress(
                    position = l2,
                    duration = l3
                )
            }
        }
    }

    LaunchedEffect(configuration.orientation) {
        when (configuration.orientation) {
            Configuration.ORIENTATION_LANDSCAPE -> {
                isFullscreen = true
            }

            else -> {}
        }
    }

    LaunchedEffect(isFullscreen) {
        systemUiController.run {
            setStatusBarColor(Color.Transparent, false)
            if (isFullscreen) {
                isSystemBarsVisible = false
                systemBarsBehavior =
                    WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            } else {
                isSystemBarsVisible = true
                systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
            }
        }
    }

    LaunchedEffect(playerUrl) {
        if (playerUrl != null) {
            videoPlayerController.setSource(VideoPlayerSource.Network(playerUrl!!))
//            logDebug("title: ${state.currentEpisodeTitle} 切换视频源: $playerUrl")
        }
    }

    viewModel.collectSideEffect {
        when (it) {
            is AnimePlayUiIntent.UpdateVideoUrl -> {
                playerUrl = it.videoUrl
                if (Settings.autoFullscreen) {
                    viewModel.queryAutoFullScreenState()
                }
            }

            is AnimePlayUiIntent.UpdateVideoProgress -> {
                if (it.position > 2000) {
                    PopTip.show(
                        "已定位到上次观看位置 " + Utils.stringForTime(it.position), "从头开始播放"
                    ).setButton { _, _ ->
                        videoPlayerController.seekTo(0)
                        false
                    }
                    videoPlayerController.seekTo(position = it.position)

                    isAllowGetHistoryPosition = false
                }
            }

            AnimePlayUiIntent.AutoFullscreen -> {
                onFullscreenChange(true)
            }
        }
    }

    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    DisposableEffect(Unit) {
        val backCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isFullscreen) {
                    onFullscreenChange(false)
                } else {
                    onBackClick()
                }
            }
        }
        backDispatcher?.addCallback(backCallback)

        val activity = context as? android.app.Activity
        activity?.window?.addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val orientationListener =
            object : OrientationEventListener(context, SensorManager.SENSOR_DELAY_NORMAL) {
                override fun onOrientationChanged(angle: Int) {
                    if (context.isOrientationLocked()) return

                    when (configuration.orientation) {
                        Configuration.ORIENTATION_PORTRAIT ->
                            if (angle <= 5 || angle >= 355) {
                                viewModel.unlockOrientation()
                            }

                        Configuration.ORIENTATION_LANDSCAPE ->
                            if (angle in 85..95 || angle in 265..275) {
                                viewModel.unlockOrientation()
                            }

                        else -> {}
                    }
                }

            }.apply { enable() }
        onDispose {
            backCallback.remove()
            orientationListener.disable()
            activity?.window?.clearFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    DisposableEffect(lifecycleOwner) {
        val observer = object : DefaultLifecycleObserver {
            override fun onPause(owner: LifecycleOwner) {
                saveHistory(videoState.secondaryProgress, videoState.position, videoState.duration)
            }

            override fun onStop(owner: LifecycleOwner) {
                saveHistory(videoState.secondaryProgress, videoState.position, videoState.duration)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(videoState.position) {
        if (isAllowGetHistoryPosition && videoState.secondaryProgress > 1L) {
            viewModel.getHistoryPosition()
            isAllowGetHistoryPosition = false
        } else {
            saveHistory(videoState.secondaryProgress, videoState.position, videoState.duration)
        }
    }

    val playerModifier = remember(isFullscreen) {
        if (isFullscreen) {
            Modifier
                .fillMaxSize()
                .background(Color.Black)
        } else {
            Modifier
                .wrapContentHeight(Alignment.Top)
                .fillMaxWidth()
                .background(Color.Black)
                .statusBarsPadding()
                .aspectRatio(16f / 9f)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Column {
            AgeAnimePlayer(
                title = state.video.name,
                controller = videoPlayerController,
                modifier = playerModifier,
                onEpisodeChange = {
                    viewModel.getVideoSource(
                        state.playIndex,
                        it,
                        state.playList[it] ?: state.episodeTitle,
                        state.playType
                    )
                },
                isFullscreen = isFullscreen,
                currentEpisode = state.episodeIndex,
                totalEpisodes = state.playList.size,
                onFullScreenChange = onFullscreenChange,
                enablePip = false,
                onBack = {
                    if (isFullscreen) onFullscreenChange(false)
                    else onBackClick()
                }
            )

            TabRow(
                contentColor = MaterialTheme.colorScheme.secondary,
                selectedTabIndex = pagerState.currentPage
            ) {
                pageList.forEachIndexed { index, s ->
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
                state = pagerState,
                pageSpacing = 0.dp,
                userScrollEnabled = true,
                reverseLayout = false,
                contentPadding = PaddingValues(0.dp),
                beyondViewportPageCount = 0,
                pageSize = PageSize.Fill,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                key = null,
                pageContent = { index ->
                    AgeScaffold(
                        state = state,
                        onShowSnackbar = onShowSnackbar,
                        onErrorPositiveAction = {
                            viewModel.refresh()
                        },
                        onRefresh = viewModel::refresh,
                        onDismissErrorDialog = viewModel::hideError,
                    ) { _, _ ->
                        when (index) {
                            0 -> Synopsis(
                                anime = state.video,
                                playList = state.video.playlists,
                                playLabelMap = state.playerLabelMap,
                                similar = state.similar,
                                series = state.series,
                                visible = state.video.id == 0,
                                isFollowed = followed,
                                episodeTitle = state.episodeTitle,
                                onUpdateFollowed = viewModel::updateAnimeFavorites,
                                onLabelClick = onLabelClick,
                                onPlayClick = viewModel::getVideoSource,
                                onAnimeClick = onAnimeClick
                            )

                            else -> Comments(
                                state.commentModelPagingData
                            )
                        }
                    }
                }
            )
        }
    }
}

@Composable
fun Synopsis(
    anime: Video,
    playList: Map<String, List<List<String>>>,
    playLabelMap: Map<String, String>,
    similar: List<AnimeModel?>,
    series: List<AnimeModel?>,
    visible: Boolean,
    isFollowed: Boolean,
    episodeTitle: String = "",
    onUpdateFollowed: (Boolean) -> Unit,
    onLabelClick: (String) -> Unit,
    onPlayClick: (page: Int, index: Int, title: String, type: String) -> Unit,
    onAnimeClick: (Int) -> Unit
) {
    Column(
        modifier = Modifier.navigationBarsPadding()
    ) {
        PlayerNeck(
            anime = anime,
            isFollowed = isFollowed,
            visible = visible,
            onUpdateFollowed = onUpdateFollowed
        )

        LazyColumn {
            item("Anime Detail") {
                AnimeDetail(
                    anime = anime,
                    visible = visible,
                    onLabelClick = onLabelClick
                )
            }

            item("Episodes List") {
                if (playList.isNotEmpty()) {
                    AnimePlayListTab(
                        episodeTitle = episodeTitle,
                        playLabelMap = playLabelMap,
                        playList = playList,
                        onPlayClick = onPlayClick
                    )
                }
            }

            if (series.isNotEmpty()) {
                item("Related Anime") {
                    ListAnimeGrid(
                        title = "相关动画",
                        animeList = series,
                        onAnimeClick = onAnimeClick
                    )
                }
            }

            item("For You Anime Grid") {
                ListAnimeGrid(
                    title = "猜你喜欢",
                    animeList = similar,
                    onAnimeClick = onAnimeClick
                )
//                ForYouAnimeGrid(
//                    animeList = similar,
//                    onAnimeClick = onAnimeClick
//                )
            }
        }
    }
}

@Composable
fun Comments(
    commentModelPagingData: Flow<PagingData<CommentResponseModel.DataModel.CommentModel>>
) {
    val lazyListState = rememberLazyGridState()
    val commentList = commentModelPagingData.collectAsLazyPagingItems()

    LazyVerticalGrid(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .fillMaxSize(),
        state = lazyListState,
        columns = GridCells.Fixed(1),
        contentPadding = PaddingValues(15.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        reverseLayout = false,
        userScrollEnabled = true,
    ) {
        items(
            items = commentList,
            key = { item -> item.floor },
            span = { GridItemSpan(1) }
        ) { item ->
            item?.let {
                CommentItem(
                    commentModel = item
                )
            }
        }

    }

}

@Composable
fun CommentItem(
    commentModel: CommentResponseModel.DataModel.CommentModel
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = commentModel.username,
                )

                Text(
                    text = commentModel.time,
                    fontSize = 12.sp
                )
            }

            Text(
                text = "#${commentModel.floor}"
            )
        }

        val imageList = mutableListOf<String>()
        val regex = Regex("<img.*?src=\"(.*?)\".*?>")
        regex.findAll(commentModel.content).forEach { match ->
            imageList.add(match.groupValues[1])
        }
        val (text, images) = commentModel.content to imageList
        val inlineContent = mutableMapOf<String, InlineTextContent>()
        images.forEachIndexed { index, url ->
            val id = "image_$index" // 生成图片的id，例如image_0, image_1等
            inlineContent[id] = InlineTextContent(
                placeholder = Placeholder(
                    width = 24.sp,
                    height = 24.sp,
                    placeholderVerticalAlign = PlaceholderVerticalAlign.TextCenter
                )

            ) {
                // 创建一个Image组件，使用rememberAsyncImagePainter加载url
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentDescription = null,
                    modifier = Modifier.width(24.dp)
                )
            }
        }
        Text(text = buildAnnotatedString {
            var cursor = 0 // 定义一个游标，用于记录当前处理的文本位置
            regex.findAll(text).forEach { match ->
                val url = match.groupValues[1] // 获取匹配到的图片url
                val imageIndex = images.indexOf(url) // 获取图片在列表中的索引
                if (imageIndex != -1) { // 如果找到了对应的图片
                    val id = "image_$imageIndex" // 获取图片的id
                    append(text.substring(cursor, match.range.first)) // 先追加<img>标签之前的文本内容
                    appendInlineContent(id) // 再追加对应的图片内容
                    cursor = match.range.last + 1 // 更新游标位置
                }
            }
            append(text.substring(cursor)) // 追加剩余的文本内容
        }, inlineContent = inlineContent)

    }

}

@Composable
private fun PlayerNeck(
    anime: Video,
    visible: Boolean,
    isFollowed: Boolean,
    onUpdateFollowed: (Boolean) -> Unit
) {
    val isTablet = isTablet()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 3.dp,
                ambientColor = pink10.copy(0.6f),
                spotColor = pink10.copy(0.6f)
            )
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp, 10.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(1.dp)
            ) {
                Text(
                    modifier = Modifier.placeholder3(
                        visible = visible,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = anime.name,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    modifier = Modifier.placeholder3(
                        visible = visible,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = "更新至第${
                        anime.playlists.takeUnless { it.isEmpty() }
                            ?.maxBy { it.value.size }?.value?.size
                    }话(${anime.status})",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    modifier = Modifier.placeholder3(
                        visible = visible,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = "♡ ${anime.collectCnt}收藏",
                    maxLines = 1,
                    style = MaterialTheme.typography.labelSmall
                )

            }
            Box(
                modifier = Modifier
                    .width(if (isTablet) 80.dp else 72.dp)
                    .clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Switch,
                        onClick = {
                            onUpdateFollowed(isFollowed.not())
                        }
                    ),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    AnimatedFollowIcon(
                        modifier = Modifier.scale(1.6f),
                        isFollowed = isFollowed
                    )
                    Text(
                        text = if (isFollowed) "已追番" else "追番 ",
                        color = pink30,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
private fun AnimeDetail(
    anime: Video,
    visible: Boolean,
    onLabelClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(AgeAnimeIcons.date),
                    contentDescription = "release"
                )
                Text(
                    modifier = Modifier.placeholder3(
                        visible = visible,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = "${anime.premiere}上映",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Row(
                modifier = Modifier.width(120.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(AgeAnimeIcons.tv),
                    contentDescription = "type"
                )
                Text(
                    modifier = Modifier.placeholder3(
                        visible = visible,
                        highlight = PlaceholderHighlight.shimmer(),
                    ),
                    text = anime.type,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(AgeAnimeIcons.tag),
                contentDescription = "genre"
            )
            if (anime.tagsArr.isNotEmpty()) {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp),
                    contentPadding = PaddingValues(end = 5.dp)
                ) {
                    for (label in anime.tagsArr) {
                        item(label) {
                            Row(
                                modifier = Modifier
                                    .background(neutral01, CircleShape)
                                    .clickable(
                                        interactionSource = rememberMutableInteractionSource(),
                                        indication = null,
                                        role = Role.Button,
                                        onClick = { onLabelClick(label) }
                                    )
                                    .padding(8.dp, 5.dp, 4.dp, 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelSmall
                                )
                                Icon(
                                    modifier = Modifier.size(12.dp),
                                    painter = painterResource(AgeAnimeIcons.arrowRight),
                                    contentDescription = "more"
                                )
                            }
                        }
                    }
                }
            }

        }
        AnimeDescription(
            visible = visible,
            description = anime.intro
        )
    }
}

@Composable
private fun AnimeDescription(
    visible: Boolean,
    description: String
) {
    var expandable by rememberMutableStateOf(value = false)
    var maxLines by rememberMutableStateOf(value = 4)
    var expanded by rememberMutableStateOf(value = false)

    Box(Modifier.padding(horizontal = 12.dp)) {
        Text(
            modifier = Modifier
                .placeholder3(
                    visible = visible,
                    highlight = PlaceholderHighlight.shimmer(),
                )
                .clickable(
                    interactionSource = rememberMutableInteractionSource(),
                    indication = null,
                    role = Role.Button,
                    onClick = {
                        if (expanded) {
                            maxLines = 4
                            expanded = false
                        } else {
                            maxLines = Int.MAX_VALUE
                            expanded = true
                        }
                    }
                ),
            text = buildAnnotatedString {
                append(description)
                if (expandable && expanded) {
                    pushStyle(SpanStyle(color = pink))
                    append(" 收起")
                }
                toAnnotatedString()
            },
            maxLines = maxLines,
            onTextLayout = { textLayoutResult ->
                if (textLayoutResult.hasVisualOverflow) expandable = true
            },
            style = MaterialTheme.typography.labelSmall,
        )
        if (expandable && !expanded) {
            Text(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .width(72.dp)
                    .background(
                        Brush.horizontalGradient(
                            0.0f to Color.Transparent,
                            0.3f to MaterialTheme.colorScheme.surface,
                        )
                    )
                    .clickable(
                        interactionSource = rememberMutableInteractionSource(),
                        indication = null,
                        role = Role.Button,
                        onClick = {
                            maxLines = Int.MAX_VALUE
                            expanded = true
                        }
                    ),
                text = buildAnnotatedString {
                    pushStyle(SpanStyle(color = pink))
                    append("      展开")
                    toAnnotatedString()
                },
                style = MaterialTheme.typography.labelSmall
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
    episodeTitle: String = "",
    playList: Map<String, List<List<String>>>,
    playLabelMap: Map<String, String>,
    onPlayClick: (page: Int, index: Int, title: String, type: String) -> Unit,
) {
    /**
     * 播放列表标题
     */
    val titleItems: List<String> = Utils.analysisPlayer(playLabelMap, playList.keys)
    val playLists by rememberMutableStateOf(playList)
    if (playList.isNotEmpty() && titleItems.isNotEmpty()) {
        /**
         * 正序倒序
         */
        var isAsc by rememberMutableStateOf(true)
        val pagerState = rememberPagerState(
            initialPage = 0, initialPageOffsetFraction = 0f
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
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    text = "播放列表",
                    style = MaterialTheme.typography.titleSmall,
                )

                TextButton(onClick = { isAsc = !isAsc }) {
                    Text(text = if (isAsc) "正序" else "倒序")
                }

            }

            ScrollableTabRow(
                contentColor = MaterialTheme.colorScheme.secondary,
                selectedTabIndex = pagerState.currentPage
            ) {
                titleItems.forEachIndexed { index, s ->
                    Tab(selected = pagerState.currentPage == index, onClick = {
                        scope.launch { pagerState.animateScrollToPage(index) }
                    }, text = {
                        Text(
                            text = s
                        )
                    })
                }
            }

            HorizontalPager(
                modifier = Modifier,
                state = pagerState,
                pageSpacing = 0.dp,
                userScrollEnabled = true,
                reverseLayout = false,
                contentPadding = PaddingValues(0.dp),
                beyondViewportPageCount = 0,
                pageSize = PageSize.Fill,
                flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                key = null,
                pageContent = { page ->
                    val currentTitle =
                        playLabelMap.filter { it.value == titleItems[page] }.keys.firstOrNull()
                    var playListItem = playLists[currentTitle]?.toMutableList()
                    if (playListItem.isNullOrEmpty()) {
                        ErrorItem(
                            modifier = Modifier
                                .wrapContentSize()
                                .padding(8.dp),
                            errorMessage = stringResource(id = R.string.error_empty)
                        )
                    } else {
                        if (playListItem.size > 24) {
                            playListItem = if (isAsc) {
                                playListItem.subList(0, 23)
                            } else {
                                playListItem.subList(
                                    playListItem.size - 23, playListItem.size
                                )
                            }
                            playListItem += listOf(
                                "展开全部",
                                "展开全部"
                            )
                        }

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(
                                    if (playListItem.size < 4) 100.dp else ((playListItem.size / 4).plus(
                                        1
                                    ) * 65).dp
                                )
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
//                        state = rememberLazyListState()
                        ) {
                            items(playListItem.size, key = { playListItem[it][0] }) { item ->
                                val title =
                                    if (isAsc) playListItem[item][0] else playListItem.reversed()[item][0]
                                AnimePlayListGrid(
                                    title = title,
                                    isSelected = title == episodeTitle
                                ) {
                                    if ("展开全部" == title) {
                                        expandedAll = true
                                    } else {
                                        onPlayClick(
                                            page,
                                            if (isAsc) item else playListItem.size - item - 1,
                                            title,
                                            currentTitle ?: ""
                                        )
                                    }
                                }
                                /*
                                if (isAsc) {
                                    val title = playListItem[item][0]
                                    AnimePlayListGrid(
                                        title = title,
                                        isSelected = title == episodeTitle
                                    ) {
                                        if ("展开全部" == title) {
                                            expandedAll = true
                                        } else {
                                            onPlayClick(
                                                page,
                                                item,
                                                title,
                                                currentTitle ?: ""
                                            )
                                        }
                                    }
                                } else {
                                    val title = playListItem.reversed()[item][0]
                                    AnimePlayListGrid(
                                        title = title,
                                        isSelected = title == episodeTitle
                                    ) {
                                        if ("展开全部" == title) {
                                            expandedAll = true
                                        } else {
                                            onPlayClick(
                                                page,
                                                playListItem.size - item - 1,
                                                title,
                                                currentTitle ?: ""
                                            )
                                        }
                                    }
                                }

                                 */
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
                                    playList = playLists[currentTitle],
                                    onDismissRequest = { expandedAll = false },
                                    onPlayClick = { i, s ->
                                        onPlayClick(page, i, s, currentTitle ?: "")
                                    }
                                )

                            }
                        }


                    }


                })

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
 * 展开全部播放列表
 * @param playList 播放列表
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AnimePlayListExpandAll(
    playList: List<List<String>>?,
    onDismissRequest: () -> Unit,
    onPlayClick: (index: Int, title: String) -> Unit,
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

        if (playList.isNullOrEmpty()) {
            ErrorItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                errorMessage = stringResource(id = R.string.error_empty)
            )
        } else {
            if (playList.size > 100) {
                val scope = rememberCoroutineScope()
                val groupTitleList = remember {
                    Utils.getGroupList(playList)
                }
                val pagerState = rememberPagerState(
                    initialPage = 0, initialPageOffsetFraction = 0f
                ) {
                    groupTitleList.size
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = pagerState.currentPage, edgePadding = 0.dp
                    ) {
                        groupTitleList.forEachIndexed { index, s ->
                            Tab(selected = pagerState.currentPage == index, onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }, text = {
                                Text(
                                    modifier = Modifier.padding(vertical = 16.dp), text = s
                                )
                            })
                        }
                    }
                }


                HorizontalPager(
                    modifier = Modifier.padding(top = 4.dp),
                    state = pagerState,
                    pageSpacing = 0.dp,
                    userScrollEnabled = true,
                    reverseLayout = false,
                    contentPadding = PaddingValues(0.dp),
                    beyondViewportPageCount = 0,
                    pageSize = PageSize.Fill,
                    flingBehavior = PagerDefaults.flingBehavior(state = pagerState),
                    key = null,
                    pageContent = { page ->
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(4),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(Utils.getGroupList(playList, page).size) { index ->
                                val title = Utils.getGroupList(playList, page)[index][0]
                                AnimePlayListGrid(title = title) {
                                    onPlayClick(playList.indexOfFirst { it[0] == title }, title)
                                }
                            }

                        }
                    })
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(playList.size) { item ->
                        val title = playList[item][0]
                        AnimePlayListGrid(title = title) {
                            onPlayClick(item, title)
                        }
                    }
                }
            }
        }
    }

}

/**
 * 网格集数列表
 * @param title 集数标题
 */
@Composable
fun AnimePlayListGrid(
    title: String, isSelected: Boolean = false, isDarkColor: Boolean = false, onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 2.dp, end = 2.dp)
            .clickable {
                onClick()
            },
        colors = CardDefaults.cardColors(
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
            color = if (isSelected) Color(0xffff6699) else MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun ListAnimeGrid(
    title: String = "",
    animeList: List<AnimeModel?>,
    onAnimeClick: (Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text(
            modifier = Modifier.padding(start = 12.dp),
            text = title,
            style = MaterialTheme.typography.titleSmall,
        )
        AnimeGrid(
            modifier = Modifier.padding(horizontal = 12.dp),
            animeList = animeList,
            onAnimeClick = onAnimeClick
        )
    }
}
