package cn.xihan.age.ui.main.schedule

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 14:12
 * @介绍 :
 */
const val ScheduleNavRoute = "schedule_route"

fun NavGraphBuilder.scheduleScreen(
    padding: PaddingValues,
    onAnimeClick: (Int) -> Unit,
    onRankClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit
) {
    composable(route = ScheduleNavRoute) {
        ScheduleScreen(
            padding = padding,
            onAnimeClick = onAnimeClick,
            onRankClick = onRankClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}