package cn.xihan.age.component

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import cn.xihan.age.ui.main.home.HomeNavRoute
import cn.xihan.age.ui.main.home.homeScreen
import cn.xihan.age.ui.main.mine.MineNavRoute
import cn.xihan.age.ui.main.mine.mineScreen
import cn.xihan.age.ui.main.schedule.ScheduleNavRoute
import cn.xihan.age.ui.main.schedule.scheduleScreen
import cn.xihan.age.ui.theme.AgeAnimeIcons

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/17 19:07
 * @介绍 :
 */
const val TopLevelNavRoute = "top_level_route"

fun NavGraphBuilder.topLevelScreen(
    navController: NavHostController,
    navigateToCategory: (label: String) -> Unit,
    navigateToAnimePlay: (Int) -> Unit,
    navigateToRank: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
    onNavigationClick: (String) -> Unit,
) {
    composable(TopLevelNavRoute) {

        val currentDestination = navController.currentBackStackEntryAsState().value?.destination

        Scaffold(
            bottomBar = {
                AgeAnimeBottomAppBar(
                    currentDestination = currentDestination,
                    onNavigateTo = {
                        navController.navigate(it.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    })
            },
            contentWindowInsets = WindowInsets.navigationBars
        ) { padding ->

            NavHost(
                modifier = Modifier.zIndex(1f),
                navController = navController,
                startDestination = HomeNavRoute,
//                contentAlignment = Alignment.Center,
//                enterTransition = { fadeIn() },
//                exitTransition = { fadeOut() }
            ) {

                homeScreen(
                    padding = padding,
                    onCategoryClick = { navigateToCategory("") },
                    onAnimeClick = navigateToAnimePlay,
                    onShowSnackbar = onShowSnackbar,
                    onNavigationClick = onNavigationClick
                )

                scheduleScreen(
                    padding = padding,
                    onAnimeClick = navigateToAnimePlay,
                    onRankClick = navigateToRank,
                    onShowSnackbar = onShowSnackbar
                )

                mineScreen(
                    padding = padding,
                    onNavigationClick = onNavigationClick,
                    onShowSnackbar = onShowSnackbar
                )
            }
        }
    }
}

enum class TopLevelScreen(
    val route: String,
    val iconId: Int,
    val label: String,
) {
    HOME(
        HomeNavRoute, AgeAnimeIcons.Animated.home, "首页"
    ),
    SCHEDULE(
        ScheduleNavRoute, AgeAnimeIcons.Animated.calendar, "时间表"
    ),
    MINE(
        MineNavRoute, AgeAnimeIcons.Animated.mine, "我的"
    )
}