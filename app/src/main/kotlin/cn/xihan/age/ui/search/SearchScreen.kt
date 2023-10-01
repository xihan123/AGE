package cn.xihan.age.ui.search

import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.component.AgeAnimeSearchBar
import cn.xihan.age.component.AnimatedFollowIcon
import cn.xihan.age.component.LoadingDots
import cn.xihan.age.component.SearchBarState
import cn.xihan.age.component.SearchHistoryView
import cn.xihan.age.model.SearchModel
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.neutral03
import cn.xihan.age.ui.theme.pink30
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.items
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberSavableMutableStateOf
import com.skydoves.landscapist.coil.CoilImage
import kotlinx.coroutines.flow.Flow
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/20 20:53
 * @介绍 :
 */
@Composable
fun SearchScreen(
    uiState: SearchBarState,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
    onEnterExit: (Boolean) -> Unit,
    onAnimeClick: (Int) -> Unit,
    onCategoryClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
) {

    val state by viewModel.collectAsState()
    val focusManager = LocalFocusManager.current
    val backDispatcher = LocalOnBackPressedDispatcherOwner.current?.onBackPressedDispatcher

    var shouldShowResults by rememberSavableMutableStateOf(value = false)
    val onSearch = {
        val keyword = state.searchText.trim()
        if (keyword.isNotEmpty()) {
            focusManager.clearFocus()
            viewModel.addHistory(keyword)
            viewModel.fetchAnimeResults(keyword)
            shouldShowResults = true
        }
    }

    val onExit = {
        onEnterExit(false)
        shouldShowResults = false
        focusManager.clearFocus()
        viewModel.updateSearchText()
    }

    if (uiState.searching) {

        DisposableEffect(backDispatcher) {
            val backCallback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (shouldShowResults) {
                        shouldShowResults = false
                        viewModel.updateSearchText()
                        uiState.focusRequester.requestFocus()
                    } else onExit()
                }
            }
            backDispatcher?.addCallback(backCallback)
            onDispose { backCallback.remove() }
        }
    }

    Column(modifier) {
        AgeAnimeSearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(1f),
            text = state.searchText,
            searching = uiState.searching,
            focusRequester = uiState.focusRequester,
            searchBarState = uiState,
            onInputChange = viewModel::updateSearchText,
            onLeftIconClick = onHistoryClick,
            onRightIconClick = onCategoryClick,
            onFocusChange = { focused ->
                if (focused) {
                    shouldShowResults = false
                    onEnterExit(true)
                } else {
                    onExit()
                }
            },
            onSearch = onSearch,
        )

        Box {
            androidx.compose.animation.AnimatedVisibility(
                visible = uiState.searching,
                enter = fadeIn(), exit = fadeOut()
            ) {
                SearchHistoryView(
                    searchHistory = state.searchHistoryList,
                    onClearHistory = viewModel::clearSearchHistory,
                    onRecordClick = {
                        viewModel.updateSearchText(it)
                        onSearch()
                    }
                )
            }

            androidx.compose.animation.AnimatedVisibility(
                visible = shouldShowResults,
                enter = fadeIn(), exit = fadeOut()
            ) {
                ResultsView(
                    searchViewModel = viewModel,
                    searchPagingData = state.searchPagingData,
                    onAnimeClick = onAnimeClick
                )
            }
        }

    }
}

@Composable
internal fun ResultsView(
    searchViewModel: SearchViewModel,
    searchPagingData: Flow<PagingData<SearchModel.DataModel.VideoModel>>,
    onAnimeClick: (Int) -> Unit,
) {
    val lazyListState = rememberLazyGridState()
    val searchList = searchPagingData.collectAsLazyPagingItems()

    LazyVerticalGrid(
        modifier = Modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .background(MaterialTheme.colorScheme.background)
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
            items = searchList,
            key = { item -> item.id },
            span = { GridItemSpan(1) }
        ) { item ->
            item?.let {
                val followed by searchViewModel.queryFavorite(item.id)
                    .collectAsStateWithLifecycle(true)
                SearchResult(
                    modifier = Modifier.fillMaxWidth(),
                    anime = item,
                    isFollowed = followed,
                    onClick = onAnimeClick,
                    onUpdateFavorite = searchViewModel::updateAnimeFavorites
                )
            }

        }

        if (searchList.loadState.refresh is LoadState.Loading) {
            item(
                key = "Loading",
                contentType = "Loading",
                content = { LoadingDots() }
            )
        }
    }
}

@Composable
fun SearchResult(
    modifier: Modifier,
    anime: SearchModel.DataModel.VideoModel,
    isFollowed: Boolean,
    onClick: (Int) -> Unit,
    onUpdateFavorite: (SearchModel.DataModel.VideoModel, Boolean) -> Unit,
) {
    val isTablet = isTablet()
    Row(
        modifier = modifier
            .padding(horizontal = 15.dp)
            .clickable(
                interactionSource = rememberMutableInteractionSource(),
                indication = null,
                role = Role.Button,
                onClick = { onClick(anime.id) }
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        CoilImage(
            modifier = Modifier
                .size(if (isTablet) DpSize(72.dp, 98.dp) else DpSize(60.dp, 82.dp))
                .clip(MaterialTheme.shapes.small),
            imageModel = { anime.cover }
        )
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Text(
                        text = anime.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        InfoTag(text = anime.premiere, iconId = AgeAnimeIcons.date2)
                        InfoTag(text = anime.status, iconId = AgeAnimeIcons.status)
                        InfoTag(
                            text = anime.uptodate,
                            iconId = AgeAnimeIcons.episode
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .width(48.dp)
                        .padding(top = 6.dp)
                        .clickable(
                            interactionSource = rememberMutableInteractionSource(),
                            indication = null,
                            role = Role.Switch,
                            onClick = {
                                onUpdateFavorite(anime, !isFollowed)
                            }
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AnimatedFollowIcon(
                        modifier = Modifier.scale(1.5f),
                        isFollowed = isFollowed
                    )

                    Text(
                        text = if (isFollowed) "已追番" else "追番",
                        color = pink30,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }

            GenresRow(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(vertical = 7.dp),
                spacing = 6.dp,
            ) {
                for (genre in anime.tagsArr + List(MAX_OVERFLOW_SIZE) { "+$it" }) {
                    GenreTag(text = genre)
                }
            }
        }
    }
}

private const val MAX_OVERFLOW_SIZE = 10

/**
 * 风格标签过多导致溢出时，补充一个额外标签（显示溢出个数）
 */
@Composable
fun GenresRow(
    modifier: Modifier,
    spacing: Dp,
    content: @Composable () -> Unit
) {
    Layout(content, modifier) { measurables, constraints ->
        val placeables = measurables.map { it.measure(constraints.copy(minWidth = 0)) }
        val genreTags = placeables.subList(0, placeables.size - MAX_OVERFLOW_SIZE)
        val overflowTags = placeables.subList(placeables.size - MAX_OVERFLOW_SIZE, placeables.size)

        val totalWidth = genreTags.fold(0) { acc, placeable -> acc + placeable.width }

        val overflow =
            totalWidth + (genreTags.size - 1) * spacing.roundToPx() > constraints.maxWidth
        var overflowCount = 0


        layout(constraints.maxWidth, placeables[0].height) {
            var xPos = 0
            val maxWidthWhenOverflow = constraints.maxWidth - (spacing + 32.dp).roundToPx()

            genreTags.forEach { placeable ->
                if (overflow && xPos + placeable.width > maxWidthWhenOverflow) {
                    overflowCount++
                    return@forEach
                }
                placeable.placeRelative(xPos, 0)
                xPos += placeable.width + spacing.roundToPx()
            }

            if (overflow) overflowTags[overflowCount].placeRelative(xPos, 0)
        }
    }
}

fun layout(maxWidth: Any, height: Any, function: () -> Unit) {

}


@Composable
private fun GenreTag(
    modifier: Modifier = Modifier,
    text: String,
) {
    Box(
        modifier = modifier
            .widthIn(min = 32.dp)
            .border(1.dp, neutral03, CircleShape)
            .padding(8.dp, 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun InfoTag(
    text: String,
    iconId: Int,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        Icon(
            modifier = Modifier.size(14.dp),
            painter = painterResource(iconId),
            contentDescription = null,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall
        )
    }
}
