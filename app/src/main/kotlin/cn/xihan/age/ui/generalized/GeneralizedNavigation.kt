package cn.xihan.age.ui.generalized

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/28 22:43
 * @介绍 :
 */
const val GeneralizedNavRoute = "generalized_route"

fun NavGraphBuilder.generalizedScreen(
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
) {
    composable("$GeneralizedNavRoute/?key={key}") {
        GeneralizedScreen(
            key = it.arguments?.getString("key") ?: "",
            onAnimeClick = onAnimeClick,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}