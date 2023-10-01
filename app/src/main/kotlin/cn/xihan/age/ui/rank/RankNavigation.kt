package cn.xihan.age.ui.rank

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 14:18
 * @介绍 :
 */
const val RankNavRoute = "rank_route"

fun NavGraphBuilder.rankScreen(
    onAnimeClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
) {
    composable(
        route = RankNavRoute
    ) {
        RankingScreen(
            onAnimeClick = onAnimeClick,
            onSearchClick = onSearchClick,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}