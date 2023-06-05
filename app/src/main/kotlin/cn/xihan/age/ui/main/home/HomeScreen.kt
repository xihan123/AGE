package cn.xihan.age.ui.main.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.MyBanner
import cn.xihan.age.component.VerticalAnimeGridCardItem
import cn.xihan.age.component.WeekTab
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.extension.AgeException
import com.skydoves.whatif.whatIfNotNull
import com.skydoves.whatif.whatIfNotNullOrEmpty
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/23 18:18
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    appState: MainAppState, viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val refreshState = rememberPullRefreshState(state.refreshing, onRefresh = {
        viewModel.getCustomHomeModel()
    })

    AgeScaffold(modifier = Modifier.fillMaxSize(),
        state = state,
        snackbarHostState = appState.snackbarHost,
        topBar = {
            CenterAlignedTopAppBar(title = {
                Text(text = stringResource(id = R.string.app_name))
            }, modifier = Modifier.fillMaxWidth(), actions = {
                IconButton(onClick = {
                    appState.navigateToSearch()
                }) {
                    Icon(Icons.Filled.Search, null)
                }
            }, navigationIcon = {

            }, scrollBehavior = null
            )
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onErrorPositiveAction = {
            viewModel.getCustomHomeModel()
        },
        onDismissErrorDialog = {
            viewModel.showError(AgeException.InlineException())
//            viewModel.hideError()
        }) { _, _ ->
        Box(modifier = Modifier.pullRefresh(refreshState)) {

            LazyColumn(modifier = Modifier.fillMaxSize()) {
                state.currentCustomHomeModel.whatIfNotNull {

                    it.bannerList.whatIfNotNullOrEmpty {
                        item(key = "banner") {
                            MyBanner(
                                list = it, appState = appState
                            )
                        }
                    }

                    it.xinFanDataList.whatIfNotNullOrEmpty { xinFanDataList ->
                        item(key = "xinFan") {
                            WeekTab(
                                scope = appState.coroutineScope,
                                xinFanDataList = xinFanDataList,
                                appState = appState
                            )
                        }
                    }

                    it.mrtjDataList.whatIfNotNullOrEmpty { mrtjDataList ->
                        item("mrtjtext") {
                            Text(
                                text = stringResource(id = R.string.recommend_title),
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        item(key = "mrtj") {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(mrtjDataList.size,
                                    key = { item -> mrtjDataList[item].aID }) { item ->
                                    VerticalAnimeGridCardItem(
                                        modifier = Modifier
                                            .fillParentMaxSize(.4f)
                                            .padding(
                                                start = 12.dp,
                                                top = 16.dp,
                                                end = 12.dp,
                                                bottom = 16.dp
                                            ),
                                        animeId = mrtjDataList[item].aID,
                                        title = mrtjDataList[item].title,
                                        subtitle = mrtjDataList[item].newTitle,
                                        cover = mrtjDataList[item].picSmall,
                                        appState = appState
                                    )
                                }
                            }
                        }

                    }

                    it.zjgxDataList.whatIfNotNullOrEmpty { zjgxDataList ->
                        item("zjgxtext") {
                            Text(
                                text = stringResource(id = R.string.recent_update_title),
                                modifier = Modifier.padding(8.dp),
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        item(key = "zjgx") {
                            LazyRow(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(zjgxDataList.size,
                                    key = { item -> zjgxDataList[item].aID }) { item ->
                                    VerticalAnimeGridCardItem(
                                        modifier = Modifier
                                            .fillParentMaxSize(.4f)
                                            .padding(
                                                start = 12.dp,
                                                top = 16.dp,
                                                end = 12.dp,
                                                bottom = 16.dp
                                            ),
                                        animeId = zjgxDataList[item].aID,
                                        title = zjgxDataList[item].title,
                                        subtitle = zjgxDataList[item].newTitle,
                                        cover = zjgxDataList[item].picSmall,
                                        appState = appState
                                    )
                                }
                            }
                        }

                    }

                }
            }

            PullRefreshIndicator(
                state.refreshing, refreshState, Modifier.align(Alignment.TopCenter)
            )
        }

    }
}