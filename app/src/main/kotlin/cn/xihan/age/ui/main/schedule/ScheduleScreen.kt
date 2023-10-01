package cn.xihan.age.ui.main.schedule

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnimatedRadioButton
import cn.xihan.age.component.AnimeGrid
import cn.xihan.age.component.AnywhereDropdown
import cn.xihan.age.ui.theme.AgeAnimeIcons
import cn.xihan.age.ui.theme.basicBlack
import cn.xihan.age.ui.theme.pink40
import cn.xihan.age.ui.theme.pink90
import cn.xihan.age.util.VerticalSpace
import cn.xihan.age.util.getAspectRadio
import cn.xihan.age.util.isTablet
import cn.xihan.age.util.rememberMutableInteractionSource
import cn.xihan.age.util.rememberMutableStateOf
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 14:11
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleScreen(
    padding: PaddingValues,
    onAnimeClick: (Int) -> Unit,
    onRankClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    viewModel: ScheduleViewModel = hiltViewModel(),
) {
    val isTablet = isTablet()
    val aspectRadio = getAspectRadio()
    val state by viewModel.collectAsState()
    val topAppBarExpanded = rememberMutableStateOf(value = false)

    AgeScaffold(
        modifier = Modifier.padding(padding),
        state = state,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.schedule_title))
                },
                modifier = Modifier.fillMaxWidth(),
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
                                    painter = painterResource(id = AgeAnimeIcons.filter2),
                                    contentDescription = "",
                                    tint = if (state.filterType == ScheduleFilterType.ALL) MaterialTheme.colorScheme.onSurface else pink40,
                                )
                            }
                        }
                    ) {
                        FilterMenu(
                            currentFilterType = state.filterType,
                            onFilterChange = {
                                viewModel.changeFilterType(it)
                                topAppBarExpanded.value = false
                            }
                        )

                    }
                }, navigationIcon = {
                    IconButton(onClick = onRankClick) {
                        Icon(
                            painter = painterResource(id = AgeAnimeIcons.leaderboard),
                            contentDescription = "",
                            tint = if (state.filterType == ScheduleFilterType.ALL) MaterialTheme.colorScheme.onSurface else pink40,
                        )
                    }
                }, scrollBehavior = null
            )
        },
        onShowSnackbar = onShowSnackbar,
        onRefresh = viewModel::getUpdate,
        onErrorPositiveAction = {
            viewModel.getUpdate()
        },
        onDismissErrorDialog = viewModel::hideError
    ) { paddingValues, _ ->

        LazyColumn(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding())
        ) {
            state.filterMap.forEach { (title, animeList) ->
                stickyHeader(key = title) {
                    Text(
                        text = title,
                        color = basicBlack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(pink90),
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.W700,
                    )
                    VerticalSpace(dp = 6.dp)
                }

                item("Anime List $title") {
                    AnimeGrid(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        useExpandCardStyle = true,
                        animeList = animeList,
                        onAnimeClick = onAnimeClick
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterMenu(
    modifier: Modifier = Modifier,
    currentFilterType: ScheduleFilterType,
    onFilterChange: (ScheduleFilterType) -> Unit,
) {
    val isTablet = isTablet()

    Surface(
        modifier = modifier
            .zIndex(1f)
            .width(if (isTablet) 124.dp else 110.dp)
            .border(
                width = Dp.Hairline,
                brush = Brush.horizontalGradient(
                    0f to Color.Gray.copy(0.1f),
                    1f to Color.Gray.copy(0.3f)
                ),
                shape = RoundedCornerShape(10.dp)
            ),
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.background,
        content = {
            Column(
                modifier = Modifier.padding(10.dp, 5.dp)
            ) {
                ScheduleFilterType.entries.forEach {
                    RadioWithLabel(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isTablet) 36.dp else 28.dp),
                        label = it.label,
                        selected = currentFilterType == it,
                        onSelect = { onFilterChange(it) },
                    )
                }
            }
        }
    )
}

@Composable
private fun RadioWithLabel(
    modifier: Modifier,
    label: String,
    selected: Boolean,
    onSelect: () -> Unit
) {
    Row(
        modifier = modifier.clickable(
            interactionSource = rememberMutableInteractionSource(),
            indication = null,
            role = Role.RadioButton,
            onClick = onSelect
        ),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AnimatedRadioButton(
            modifier = Modifier.size(16.dp),
            selected = selected,
            durationMillis = 100
        )
        Text(
            text = label,
            color = if (selected) pink40 else MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

