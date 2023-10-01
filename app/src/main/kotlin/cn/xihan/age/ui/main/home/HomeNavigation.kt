package cn.xihan.age.ui.main.home

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 0:26
 * @介绍 :
 */
const val HomeNavRoute = "home_route"

fun NavGraphBuilder.homeScreen(
    padding: PaddingValues,
    onAnimeClick: (Int) -> Unit,
    onCategoryClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    onNavigationClick: (String) -> Unit,
) {
    composable(HomeNavRoute) {
        HomeScreen(
            padding = padding,
            onCategoryClick = onCategoryClick,
            onAnimeClick = onAnimeClick,
            onShowSnackbar = onShowSnackbar,
            onNavigationClick = onNavigationClick
        )
    }
}