package cn.xihan.age.ui.main.recommend

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.VerticalAnimeGridCardItem
import cn.xihan.age.ui.main.MainAppState
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/10 5:54
 * @介绍 :
 */
@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RecommendScreen(
    viewModel: RecommendViewModel = hiltViewModel(),
    appState: MainAppState
) {
    val state by viewModel.collectAsState()
    val refreshState = rememberPullRefreshState(state.refreshing, onRefresh = {
        viewModel.getRecommendModel()
    })
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val isTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex == 0
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
                    IconButton(onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(if (isTop) 100 else 0)
                        }
                    }) {
                        if (isTop) {
                            Icon(Icons.Filled.KeyboardArrowDown, null)
                        } else {
                            Icon(Icons.Filled.KeyboardArrowUp, null)
                        }
                    }
                },
                navigationIcon = {

                },
                scrollBehavior = null
            )
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        }
    ) { _, _ ->
        Box(modifier = Modifier.pullRefresh(refreshState)) {
            if (state.error != null) {
                ErrorItem(
                    errorMessage = state.error?.message
                        ?: stringResource(id = R.string.error_unknown)
                ) {
                    viewModel.getRecommendModel()
                }
            }else{
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = listState
                ) {
                    state.currentRecommendListData.whatIfNotNullOrEmpty { list ->
                        items(list.windowed(3, 3, true),key = {
                            it.hashCode()
                        }) { sublist ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                sublist.forEach { item ->
                                    VerticalAnimeGridCardItem(
                                        modifier = Modifier
                                            .wrapContentHeight()
                                            .fillParentMaxWidth(.3f)
                                            .padding(4.dp),
                                        animeId = item.aid,
                                        title = item.title,
                                        subtitle = item.subtitle,
                                        cover = item.cover,
                                        appState = appState
                                    )
                                }
                            }

                        }
                    }
                }
            }

            PullRefreshIndicator(
                refreshing = state.refreshing,
                state = refreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }

    }


}