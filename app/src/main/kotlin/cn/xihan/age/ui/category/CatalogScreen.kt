package cn.xihan.age.ui.category

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.ExpandedAnimeCard
import cn.xihan.age.component.SolidTopBar
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.Category
import cn.xihan.age.model.defaultLabel
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.basicBlack
import cn.xihan.age.ui.theme.darkPink80
import cn.xihan.age.ui.theme.pink40
import cn.xihan.age.ui.theme.pink90
import cn.xihan.age.util.VerticalSpace
import cn.xihan.age.util.getAspectRadio
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.items
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberMutableStateOf
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.SimpleColorFilter
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 0:30
 * @介绍 :
 */
@Composable
fun CatalogScreen(
    filter: Map<Category, Pair<String, String>>,
    padding: PaddingValues,
    onAnimeClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: CatalogViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val aspectRatio = getAspectRadio()
    val isTablet = isTablet()
    val lazyGridState = rememberLazyGridState()
    val catalogList = state.catalogFlow.collectAsLazyPagingItems()

    AgeScaffold(
        state = state,
        topBar = {
            SolidTopBar(
                title = stringResource(id = R.string.category_title),
                onLeftIconClick = onBackClick,
                rightIconId = AgeAnimeIcons.search,
                onRightIconClick = onSearchClick
            )
        },
        onShowSnackbar = onShowSnackbar,
        onDismissErrorDialog = viewModel::hideError,
        onRefresh = viewModel::getFilterCategoryModel
    ) { padding, _ ->

        FiltersBar(
            modifier = Modifier
                .padding(padding)
                .zIndex(1f),
            filter = viewModel.filter,
            onFilter = viewModel::changeFilterCategory
        )

        VerticalSpace(dp = 36.dp)

        if (catalogList.itemCount == 0) {
            ErrorItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(padding)
                    .padding(top = 36.dp),
                errorMessage = stringResource(id = R.string.error_empty),
                onRetryClick = viewModel::getFilterCategoryModel
            )
        } else {
            LazyVerticalGrid(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                columns = GridCells.Fixed(if (isTablet) 4 else if (aspectRatio < 0.56) 6 else 3),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    end = 12.dp,
                    top = 36.dp,
                    bottom = 12.dp
                ),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                state = lazyGridState,
            ) {
                items(
                    items = catalogList,
                    key = { item -> item.id },
                    span = { GridItemSpan(1) }
                ) { item ->
                    item?.let {
                        ExpandedAnimeCard(
                            anime = AnimeModel(
                                aID = item.id,
                                newTitle = item.uptodate,
                                picSmall = item.cover,
                                title = item.name
                            ), onClick = onAnimeClick
                        )
                    }
                }
            }
        }


    }

}

@Composable
fun FiltersBar(
    modifier: Modifier = Modifier,
    filter: Map<Category, Pair<String, String>>,
    mainCategories: List<Category> = listOf(Category.Label, Category.Year, Category.Letter),
    onFilter: (Category, Pair<String, String>) -> Unit,
) {
    val interactionSource = rememberMutableInteractionSource()
    var drawer: Category? by rememberMutableStateOf(value = null)
    val otherCategories = remember { Category.entries.toSet() - mainCategories.toSet() }
    val hideDrawer = { drawer = null }

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 5.dp)
                .zIndex(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (category in mainCategories) {
                CategoryHead(
                    modifier = Modifier
                        .weight(1f)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null,
                            role = Role.DropdownList,
                            onClick = { drawer = if (drawer != category) category else null }
                        ),
                    expand = drawer == category,
                    active = filter[category]!!.second != category.defaultLabel(),
                    category = category,
                    label = filter[category]!!.second,
                )
            }
            Icon(
                modifier = Modifier
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.DropdownList,
                        onClick = {
                            drawer =
                                if (drawer !in otherCategories) otherCategories.first() else null
                        }
                    )
                    .weight(0.6f),
                painter = painterResource(AgeAnimeIcons.filter),
                contentDescription = null,
                tint = if (filter.filterKeys { it in otherCategories }
                        .any { (category, pair) ->
                            pair.second != category.defaultLabel()
                        })
                    pink40 else MaterialTheme.colorScheme.onSurface
            )
        }
        Box {
            androidx.compose.animation.AnimatedVisibility(
                visible = drawer != null,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background.copy(alpha = 0.1f))
                    .pointerInput(Unit) { detectTapGestures { drawer = null } })
            }
            for (category in mainCategories) {
                AnimatedDrawer(drawer == category) {
                    MainFilterDrawer(
                        filter = filter,
                        onFilter = { category, option ->
                            onFilter(category, option)
                            hideDrawer()
                        },
                        drawer = category
                    )
                }
            }
            AnimatedDrawer(visible = drawer == otherCategories.first()) {
                ExtraFilterDrawer(
                    filter = filter,
                    onFilter = { category, option ->
                        onFilter(category, option)
                        hideDrawer()
                    }
                )
            }
        }
    }
}

@Composable
private fun CategoryHead(
    modifier: Modifier = Modifier,
    expand: Boolean,
    active: Boolean,
    category: Category,
    label: String
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Text(
            text = if (active) label else category.title,
            color = if (active) pink40 else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodySmall
        )

        val arrowUpDown by rememberLottieComposition(
            LottieCompositionSpec.RawRes(AgeAnimeIcons.Animated.arrowUpDown)
        )
        val animationProgress by animateFloatAsState(
            targetValue = if (expand) 1f else 0f,
            animationSpec = tween(300, easing = LinearEasing), label = ""
        )

        val arrowColor = rememberLottieDynamicProperties(
            rememberLottieDynamicProperty(
                property = LottieProperty.COLOR_FILTER,
                value = if (active) SimpleColorFilter(pink40.toArgb())
                else SimpleColorFilter(basicBlack.toArgb()),
                "**"
            )
        )

        LottieAnimation(
            composition = arrowUpDown,
            progress = { animationProgress },
            dynamicProperties = arrowColor
        )
    }
}

@Composable
private fun AnimatedDrawer(
    visible: Boolean,
    content: @Composable () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { -it },
        exit = slideOutVertically(
            animationSpec = spring(
                stiffness = Spring.StiffnessHigh,
                visibilityThreshold = IntOffset.VisibilityThreshold
            ),
            targetOffsetY = { -it }
        )
    ) {
        Surface(
            Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(bottomEnd = 10.dp, bottomStart = 10.dp)
                )
                .padding(
                    top = 5.dp,
                    start = 24.dp,
                    end = 24.dp,
                )
        ) {
            content()
        }
    }
}


@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun MainFilterDrawer(
    filter: Map<Category, Pair<String, String>>,
    onFilter: (Category, Pair<String, String>) -> Unit,
    drawer: Category
) {
    val isTablet = isTablet()
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(if (isTablet) 16.dp else 12.dp),
    ) {
        for (option in drawer.options) {
            OptionChip(
                modifier = Modifier
                    .padding(bottom = if (isTablet) 16.dp else 12.dp)
                    .widthIn(min = 56.dp, max = 94.dp)
                    .background(
                        color = if (filter[drawer] == option) pink90 else MaterialTheme.colorScheme.background,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        role = Role.Checkbox,
                        onClick = { onFilter(drawer, option) }
                    ),
                text = option.second,
                fontColor = if (filter[drawer] == option) darkPink80 else MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun ExtraFilterDrawer(
    filter: Map<Category, Pair<String, String>>,
    categories: List<Category> = listOf(
        Category.Region,
        Category.Genre,
        Category.Season,
        Category.Status,
        Category.Resource,
        Category.Order
    ),
    onFilter: (Category, Pair<String, String>) -> Unit,
) {
    Column {
        for (category in categories) {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(text = category.title, style = MaterialTheme.typography.bodySmall)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (option in category.options) {
                        OptionChip(
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .background(
                                    color = if (filter[category] == option) pink90 else MaterialTheme.colorScheme.background,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    role = Role.Checkbox,
                                    onClick = { onFilter(category, option) }
                                ),
                            text = option.second,
                            fontColor = if (filter[category] == option) darkPink80 else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun OptionChip(
    modifier: Modifier,
    text: String,
    fontColor: Color,
) {
    Box(
        modifier = modifier
            .padding(vertical = 5.dp, horizontal = 14.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            color = fontColor,
            style = MaterialTheme.typography.bodySmall
        )
    }
}
