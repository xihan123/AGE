package cn.xihan.age.ui.category

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import cn.xihan.age.model.Category
import cn.xihan.age.model.labelValueOf

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/19 0:31
 * @介绍 :
 */
const val CatalogRoute = "catalog_route"

fun NavGraphBuilder.categoryScreen(
    padding: PaddingValues,
    onAnimeClick: (Int) -> Unit,
    onSearchClick: () -> Unit,
    onBackClick: () -> Unit,
    onShowSnackbar: (message: String) -> Unit,
) {
    composable(
        route = "$CatalogRoute/?region={region}&genre={genre}&letter={letter}&year={year}&season={season}&status={status}&label={label}&resource={resource}&order={order}",
        arguments = buildList {
            Category.entries.forEach { category ->
                add(navArgument(category.toString().lowercase()) {})
            }
        },
    ) { backStackEntry ->
        CatalogScreen(
            filter = buildMap {
                Category.entries.forEach { category ->
                    val value =
                        backStackEntry.arguments?.getString(category.toString().lowercase()) ?: ""
                    put(category, value to category.labelValueOf(value))
                }
            },
            padding = padding,
            onAnimeClick = onAnimeClick,
            onSearchClick = onSearchClick,
            onBackClick = onBackClick,
            onShowSnackbar = onShowSnackbar
        )
    }
}

