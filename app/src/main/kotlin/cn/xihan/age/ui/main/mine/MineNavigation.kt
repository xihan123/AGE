package cn.xihan.age.ui.main.mine

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 14:15
 * @介绍 :
 */
const val MineNavRoute = "mine_route"

fun NavGraphBuilder.mineScreen(
    padding: PaddingValues,
    onNavigationClick: (String) -> Unit,
    onShowSnackbar: (message: String) -> Unit,
) {
    composable(
        route = MineNavRoute,
        arguments = emptyList(),
        deepLinks = emptyList(),
        enterTransition = null,
        exitTransition = null,
        popEnterTransition = null,
        popExitTransition = null,
    ) {
        MineScreen(
            padding = padding,
            onNavigationClick = onNavigationClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}