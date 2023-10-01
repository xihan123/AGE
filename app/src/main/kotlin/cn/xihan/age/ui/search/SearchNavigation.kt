package cn.xihan.age.ui.search

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import cn.xihan.age.component.rememberSearchBarState
import cn.xihan.age.util.rememberSavableMutableStateOf

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/21 22:15
 * @介绍 :
 */
const val SearchNavRoute = "search_route"

fun NavGraphBuilder.searchScreen(
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit,
) {
    composable(route = SearchNavRoute) {
        val focusRequester = remember { FocusRequester() }

        // 避免屏幕旋转导致搜索框被无意中 focused 并丢失搜索结果
        var shouldRequestFocus by rememberSavableMutableStateOf(value = true)
        if (shouldRequestFocus) {
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
                shouldRequestFocus = false
            }
        }

        SearchScreen(
            modifier = Modifier.fillMaxSize(),
            uiState = rememberSearchBarState(searching = true, focusRequester, scrollProgress = 1f),
            onEnterExit = { if (!it) onBackClick() },
            onAnimeClick = onAnimeClick
        )
    }
}
