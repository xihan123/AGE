package cn.xihan.age.ui.history

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnywhereDropdown
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.HistoryItem
import cn.xihan.age.component.MyDropdownMenuItem
import cn.xihan.age.component.items
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.rememberMutableStateOf
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/16 11:37
 * @介绍 :
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    appState: MainAppState,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.collectAsState()
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val historyList = state.history.collectAsLazyPagingItems()
    val topAppBarExpanded = rememberMutableStateOf(value = false)
    val lifecycleOwner = LocalLifecycleOwner.current

    AgeScaffold(
        state = state,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.history))
                },
                modifier = Modifier.fillMaxWidth(),
                actions = {
                    IconButton(onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    }) {
                        Icon(Icons.Filled.KeyboardArrowUp, null)
                    }
                    AnywhereDropdown(
                        expanded = topAppBarExpanded.value,
                        onDismissRequest = { topAppBarExpanded.value = false },
                        onClick = { topAppBarExpanded.value = true },
                        surface = {
                            IconButton(onClick = {
                                topAppBarExpanded.value = true
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.MoreVert,
                                    contentDescription = null
                                )
                            }
                        }
                    ) {

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.clear_history)) },
                            onClick = {
                                viewModel.deleteHistoryByAnimeId("", true)
                            }
                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.clear_history_progress)) },
                            onClick = {
                                viewModel.deletePlayerHistoryProgress()
                            }
                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.watch_time_sort)) },
                            onClick = {
                                viewModel.changeHistorySortState(0)
                            }
                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.update_time_sort)) },
                            onClick = {
                                viewModel.changeHistorySortState(1)
                            }
                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.default_sort)) },
                            onClick = {
                                viewModel.changeHistorySortState(2)
                            }
                        )

                        /*
                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.get_remote_history)) },
                            onClick = {
                                viewModel.getRemoteHistoryList()
                            }
                        )

                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.update_remote_history)) },
                            onClick = {
                                viewModel.updateRemoteHistoryModel()
                            }
                        )


                        MyDropdownMenuItem(
                            topAppBarExpanded = topAppBarExpanded,
                            text = { Text(stringResource(R.string.clear_remote_history)) },
                            onClick = {
                                viewModel.clearRemoteHistoryModel()
                            }
                        )

                         */
                    }
                }, navigationIcon = {
                    IconButton(onClick = { appState.popBackStack() }) {
                        Icon(Icons.Filled.KeyboardArrowLeft, null)
                    }
                }, scrollBehavior = null
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
            if (historyList.itemCount == 0) {
                ErrorItem(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(8.dp),
                    errorMessage = stringResource(id = R.string.error_empty)
                ) {
                    viewModel.queryHistory()
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(1),
                    modifier = Modifier.fillMaxSize(),
                    state = listState,
                    contentPadding = PaddingValues(15.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = historyList,
                        key = { item -> item.animeId },
                        span = { GridItemSpan(1) }) { item ->
                        item?.let {
                            HistoryItem(
                                historyModel = item,
                                onPlayClick = {
                                    appState.navigateToPlayer(
                                        animeId = item.animeId,
                                        episodeId = item.animePlayListIndex,
                                        episodeName = item.animeLastPlayTitle
                                    )
                                },
                                onDescClick = {
                                    appState.navigateToDesc(item.animeId)
                                },
                                onRemoveClick = {
                                    viewModel.deleteHistoryByAnimeId(item.animeId)
                                }
                            )
                        }
                    }
                }

            }


        }


    }


}
