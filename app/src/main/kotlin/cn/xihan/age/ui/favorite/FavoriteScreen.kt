package cn.xihan.age.ui.favorite

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.paging.compose.collectAsLazyPagingItems
import cn.xihan.age.R
import cn.xihan.age.component.AgeScaffold
import cn.xihan.age.component.AnywhereDropdown
import cn.xihan.age.component.ErrorItem
import cn.xihan.age.component.MyDropdownMenuItem
import cn.xihan.age.component.NiaNavigationBar
import cn.xihan.age.component.NiaNavigationBarItem
import cn.xihan.age.component.VerticalAnimeGridCardItem
import cn.xihan.age.component.items
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.rememberMutableStateOf
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/12 12:27
 * @介绍 :
 */
@Composable
fun LocalFavoriteScreen(
    appState: MainAppState,
    viewModel: FavoriteViewModel = hiltViewModel()
) {
    val state by viewModel.collectAsState()
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val topAppBarExpanded = rememberMutableStateOf(value = false)
    val favoriteList = state.localFavoriteList.collectAsLazyPagingItems()
    AgeScaffold(
        state = state,
        topBar = {
            AppBar(topAppBarExpanded = topAppBarExpanded, content = {
                if (cn.xihan.age.BuildConfig.DEBUG) {
                    MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                        text = { Text("设置全部收藏") },
                        onClick = {
                            viewModel.setAllFavorite(true)
                        })
                }
//                    MyDropdownMenuItem(
//                        topAppBarExpanded = topAppBarExpanded,
//                        text = { Text(stringResource(id = R.string.all_sync_network)) },
//                        onClick = {
//                            viewModel.syncAllLocalFavoriteToNet()
//                        }
//                    )

                MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                    text = { Text(stringResource(id = R.string.clear_collect)) },
                    onClick = {
                        viewModel.setAllFavorite(false)
                    })

                MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                    text = { Text(stringResource(R.string.default_sort)) },
                    onClick = {
                        viewModel.setLocalFavoriteType(0)
                    })

                MyDropdownMenuItem(topAppBarExpanded = topAppBarExpanded,
                    text = { Text(stringResource(R.string.reverse_sort)) },
                    onClick = {
                        viewModel.setLocalFavoriteType(1)
                    })
            }, onBackClick = {
                appState.popBackStack()
            }, onSlideClick = {
                coroutineScope.launch {
                    listState.animateScrollToItem(0)
                }
            })
        },
        onShowSnackbar = {
            appState.showSnackbar(it)
        },
        onDismissErrorDialog = {
            viewModel.hideError()
        }
    ) { _, _ ->
        if (favoriteList.itemCount == 0) {
            ErrorItem(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(8.dp),
                errorMessage = stringResource(id = R.string.error_empty)
            ) {
                viewModel.queryLocalFavorite()
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(15.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(items = favoriteList,
                    key = { item -> item.animeId },
                    span = { GridItemSpan(1) }) { item ->
                    item?.let {
                        var expanded by rememberMutableStateOf(value = false)
                        AnywhereDropdown(expanded = expanded,
                            onDismissRequest = { expanded = false },
                            onClick = {
//                                topAppBarExpanded = true
                            },
                            surface = {
                                VerticalAnimeGridCardItem(modifier = Modifier
                                    .wrapContentHeight()
//                                    .fillParentMaxWidth(.3f)
                                    .padding(4.dp),
                                    animeId = item.animeId,
                                    title = item.animeName,
                                    subtitle = item.animeSubtitle,
                                    cover = item.animeCover,
                                    appState = appState,
                                    onLongClick = {
                                        expanded = true
                                    })
                            }) {
                            DropdownMenuItem(text = { Text("移除收藏") }, onClick = {
                                viewModel.cancelLocalFavoriteByAnimeId(item.animeId)
                            })

                        }

                    }
                }
            }
        }
    }

}

/*
@Composable
fun NetFavoriteScreen(
    state: FavoriteState,
    appState: MainAppState,
    favoriteList: LazyPagingItems<FavoriteModel>,
    viewModel: FavoriteViewModel
){
    val listState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()
    val topAppBarExpanded = rememberMutableStateOf(value = false)

    Scaffold(
        topBar = {
            AppBar(
                topAppBarExpanded = topAppBarExpanded,
                content = {
                    MyDropdownMenuItem(
                        topAppBarExpanded = topAppBarExpanded,
                        text = { Text(stringResource(id = R.string.all_sync_local)) },
                        onClick = { viewModel.allSyncLocal() })

                    MyDropdownMenuItem(
                        topAppBarExpanded = topAppBarExpanded,
                        text = { Text(stringResource(id = R.string.clear_collect)) },
                        onClick = {
                            viewModel.cancelAllNetFavorite()
                        }
                    )

                    MyDropdownMenuItem(
                        topAppBarExpanded = topAppBarExpanded,
                        text = { Text(stringResource(R.string.default_sort)) },
                        onClick = {
                            viewModel.setNetFavoriteType(0)
                        }
                    )

                    MyDropdownMenuItem(
                        topAppBarExpanded = topAppBarExpanded,
                        text = { Text(stringResource(R.string.reverse_sort)) },
                        onClick = {
                            viewModel.setNetFavoriteType(1)
                        }
                    )
                },
                onBackClick = {
                    appState.popBackStack()
                },
                onSlideClick = {
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = M.padding(paddingValues)
        ){
            if (state.userInfoModel == null){
                ErrorItem(
                    errorMessage = stringResource(id = R.string.not_login)
                ) {
                    appState.navigateToLogin()
                }
            }else{
                if (favoriteList.itemCount == 0) {
                    ErrorItem(
                        modifier = M
                            .wrapContentSize()
                            .padding(8.dp), errorMessage = stringResource(id = R.string.error_empty)
                    ) {
                        viewModel.queryNetFavorite()
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(3),
                        modifier = M.fillMaxSize(),
                        state = listState,
                        contentPadding = PaddingValues(15.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(
                            items = favoriteList,
                            key = { item -> item.animeId },
                            span = { GridItemSpan(1) }
                        ) { item ->
                            item?.let { item ->
                                var expanded by rememberMutableStateOf(value = false)
                                AnywhereDropdown(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false },
                                    onClick = {
//                                topAppBarExpanded = true
                                    },
                                    surface = {
                                        VerticalAnimeGridCardItem(
                                            modifier = M
                                                .wrapContentHeight()
//                                    .fillParentMaxWidth(.3f)
                                                .padding(4.dp),
                                            animeId = item.animeId,
                                            title = item.animeName,
                                            subtitle = item.animeSubtitle,
                                            cover = item.animeCover,
                                            appState = appState,
                                            onLongClick = {
                                                expanded = true
                                            }
                                        )
                                    }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("移除收藏") },
                                        onClick = {
                                            viewModel.cancelNetFavoriteByAnimeIds(item.animeId,1,false)
                                        }
                                    )

                                }

                            }
                        }
                    }

                    if (favoriteList.loadState.append is LoadState.Loading) {
                        Box(
                            modifier = M
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            CircularProgressIndicator(modifier = M.align(alignment = Alignment.Center))
                        }
                    } else if (favoriteList.loadState.append is LoadState.Error) {
                        Box(
                            modifier = M
                                .fillMaxWidth()
                                .height(50.dp)
                        ) {
                            Text(
                                modifier = M.align(alignment = Alignment.Center),
                                text = stringResource(id = R.string.error_unknown)
                            )
                        }
                    }
                }
            }
        }

    }

}

 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    topAppBarExpanded: MutableState<Boolean>,
    content: @Composable() (ColumnScope.() -> Unit),
    onBackClick: () -> Unit = {},
    onSlideClick: () -> Unit = {},
) {

    CenterAlignedTopAppBar(title = {
        Text(text = stringResource(id = R.string.app_name))
    }, modifier = Modifier.fillMaxWidth(), actions = {
        IconButton(onClick = {
            onSlideClick()
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
                        imageVector = Icons.Filled.MoreVert, contentDescription = null
                    )
                }
            },
            content = content
        )

    }, navigationIcon = {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.KeyboardArrowLeft, null)
        }
    }, scrollBehavior = null
    )
}

@Composable
private fun BottomNavigationBar(
    navController: NavController, items: List<FavoritesScreen>, modifier: Modifier = Modifier
) {
    NiaNavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        items.forEach { item ->
            NiaNavigationBarItem(
                icon = { Icon(painterResource(id = item.iconId), contentDescription = null) },
                label = { Text(stringResource(id = item.resourceId)) },
                selected = currentDestination?.hierarchy?.any { it.route == item.route } == true,
                onClick = {
                    navController.navigate(item.route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                },
//                    alwaysShowLabel = false
            )
        }
    }

}

private sealed class FavoritesScreen(
    val route: String, @DrawableRes val iconId: Int, @StringRes val resourceId: Int
) {
    /**
     * 本地收藏
     */
    object Local : FavoritesScreen("local", R.drawable.app_shortcut, R.string.local_collect)

    /**
     * 目录
     */
    object Net : FavoritesScreen("net", R.drawable.cloud, R.string.network_collect)

}