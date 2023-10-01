package cn.xihan.age.ui.player

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.xihan.age.util.logDebug

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/22 18:00
 * @介绍 :
 */
const val AnimePlayNavRoute = "anime_play_route"

@UnstableApi
@SuppressLint("SourceLockedOrientationActivity")
fun NavGraphBuilder.animePlayScreen(
    onLabelClick: (String) -> Unit,
    onAnimeClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit
) {
    composable(
        route = "$AnimePlayNavRoute/{animeId}",
        arguments = listOf(navArgument("animeId") { type = NavType.IntType }),
    ) { backStackEntry ->

        val activity = LocalContext.current as? Activity
        val shouldLockOrientation = rememberSaveable {
            activity?.let {
                it.requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            } ?: false
        }
        val restoreOrientationSetting = remember {
            {
                if (shouldLockOrientation) {
                    logDebug("Lock")
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                } else {
                    logDebug("UnLock")
                    activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        }

        AnimePlayScreen(
            animeId = backStackEntry.arguments?.getInt("animeId")!!,
            onAnimeClick = onAnimeClick,
            onLabelClick = {
                onLabelClick(it)
                restoreOrientationSetting()
            },
            onBackClick = {
                onBackClick()
                restoreOrientationSetting()
            },
            onShowSnackbar = onShowSnackbar
        )
    }
}