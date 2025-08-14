package cn.xihan.age.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.UnstableApi
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import cn.xihan.age.component.TopLevelNavRoute
import cn.xihan.age.component.UpdateDialog
import cn.xihan.age.component.topLevelScreen
import cn.xihan.age.ui.category.CatalogRoute
import cn.xihan.age.ui.category.categoryScreen
import cn.xihan.age.ui.generalized.GeneralizedNavRoute
import cn.xihan.age.ui.generalized.generalizedScreen
import cn.xihan.age.ui.player.AnimePlayNavRoute
import cn.xihan.age.ui.player.animePlayScreen
import cn.xihan.age.ui.rank.RankNavRoute
import cn.xihan.age.ui.rank.rankScreen
import cn.xihan.age.ui.search.SearchNavRoute
import cn.xihan.age.ui.search.searchScreen
import cn.xihan.age.ui.theme.AgeTheme
import cn.xihan.age.util.Settings
import cn.xihan.age.util.logDebug
import cn.xihan.age.util.logError
import cn.xihan.age.util.openUrl
import cn.xihan.age.util.setThemeMode
import com.drake.channel.receiveTag
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.compose.collectAsState
import kotlin.system.exitProcess

@UnstableApi
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    lateinit var mConfiguration: Configuration
    private val defaultTag: String
        get() = this.javaClass.simpleName

    private fun getTitleText(): String {
        return this::class.java.simpleName.replace("Activity", "")
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AgeTheme {
                ComposeContent()
            }
        }
        if (savedInstanceState == null) {
            registerUncaughtExceptionHandler()
        }
        mConfiguration = Configuration(resources.configuration)
        receiveTag("changeThemeMode") {
            setThemeMode(Settings.themeMode)
        }
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun ComposeContent() {

        val state by viewModel.collectAsState()
        val coroutineScope = rememberCoroutineScope()
        val snackbarHostState = remember { SnackbarHostState() }
        val navController = rememberNavController()
        val topLevelNavController = rememberNavController()
        val context = LocalContext.current
        val appState = rememberMainAppState(
            coroutineScope = coroutineScope,
            controller = navController,
            snackbarHost = snackbarHostState,
            context = context
        )

        Scaffold(
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.navigationBarsPadding(),
                )
            },
            contentWindowInsets = WindowInsets(0),
        ) {
            NavHost(
                navController = navController,
                startDestination = TopLevelNavRoute,
                contentAlignment = Alignment.Center,
                enterTransition = { slideInHorizontally { it } },
                exitTransition = { slideOutHorizontally { -it / 4 } },
                popEnterTransition = { slideInHorizontally { -it / 4 } },
                popExitTransition = { slideOutHorizontally { it } }) {
                topLevelScreen(
                    navController = topLevelNavController,
                    navigateToCategory = appState::navigateToCategory,
                    navigateToAnimePlay = appState::navigateToAnimePlay,
                    navigateToRank = appState::navigateToRank,
                    onShowSnackbar = appState::showSnackbar,
                    onNavigationClick = appState::navigateToGeneralized
                )

                searchScreen(
                    onAnimeClick = appState::navigateToAnimePlay,
                    onBackClick = appState::popBackStack
                )

                animePlayScreen(
                    onLabelClick = appState::navigateToCategory,
                    onAnimeClick = appState::navigateToAnimePlay,
                    onBackClick = appState::popBackStack,
                    onShowSnackbar = appState::showSnackbar
                )

                categoryScreen(
                    padding = it,
                    onAnimeClick = appState::navigateToAnimePlay,
                    onSearchClick = appState::navigateToSearch,
                    onBackClick = appState::popBackStack,
                    onShowSnackbar = appState::showSnackbar
                )


                generalizedScreen(
                    onAnimeClick = appState::navigateToAnimePlay,
                    onBackClick = appState::popBackStack,
                    onShowSnackbar = appState::showSnackbar
                )

                rankScreen(
                    onAnimeClick = appState::navigateToAnimePlay,
                    onBackClick = appState::popBackStack,
                    onSearchClick = appState::navigateToSearch,
                    onShowSnackbar = appState::showSnackbar
                )

            }

            if (state.isUpdating) {
                UpdateDialog(
                    versionName = state.updateTriple.first,
                    updateNotes = state.updateTriple.second,
                    onConfirm = {
                        viewModel.hideUpdateDialog()
                        openUrl(state.updateTriple.third)
                    },
                    onDismiss = viewModel::hideUpdateDialog
                )
            }
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (mConfiguration.diff(newConfig) and ActivityInfo.CONFIG_UI_MODE != 0) {
            recreate()
            logDebug("onConfigurationChanged")
        }
    }

    private fun registerUncaughtExceptionHandler() {
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->

            logError("App Crash!!", throwable)

            CoroutineScope(Dispatchers.Default).launch {
                withContext(Dispatchers.Main) {
                    AlertDialog.Builder(this@MainActivity).setTitle("(T ^ T) 崩溃了...")
                        .setMessage("AGE动漫 遇到了一个错误，是否要重启应用？")
                        .setPositiveButton("重启") { _, _ ->
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            applicationContext.startActivity(intent)
                            exitProcess(0)
                        }.setNegativeButton("退出") { _, _ ->
                            exitProcess(1)
                        }.setCancelable(false).show()
                }
            }
        }

    }
}

@Composable
fun rememberMainAppState(
    coroutineScope: CoroutineScope,
    controller: NavController,
    snackbarHost: SnackbarHostState,
    context: Context
) = remember(controller, coroutineScope, snackbarHost, context) {
    MainAppState(
        coroutineScope = coroutineScope,
        controller = controller,
        snackbarHost = snackbarHost,
        context = context
    )
}

class MainAppState(
    val coroutineScope: CoroutineScope,
    val controller: NavController,
    val snackbarHost: SnackbarHostState,
    private val context: Context
) {

    fun <T> getDataFromNextScreen(key: String, defaultValue: T): StateFlow<T>? =
        controller.currentBackStackEntry?.savedStateHandle?.getStateFlow(key, defaultValue)

    fun <T> removeDataFromNextScreen(key: String) {
        controller.currentBackStackEntry?.savedStateHandle?.remove<T>(key)
    }

    fun showSnackbar(message: String) {
        coroutineScope.launch {
            snackbarHost.showSnackbar(message)
        }
    }

    fun popBackStack(popToRoute: String? = null, params: Map<String, Any>? = null) {
        controller.previousBackStackEntry ?: return
        if (popToRoute == null) {
            params?.forEach { data ->
                controller.previousBackStackEntry?.savedStateHandle?.set(data.key, data.value)
            }
            controller.popBackStack()
        } else {
            params?.forEach { data ->
                controller.getBackStackEntry(popToRoute).savedStateHandle[data.key] = data.value
            }
            controller.popBackStack(route = popToRoute, inclusive = false)
        }
    }

    /**
     * 导航到顶级路由
     */
    fun navigateToTopLevel() = navigateToSinglePage("top_level_route")

    /**
     * 导航到搜索
     */
    fun navigateToSearch() = navigateToSinglePage(SearchNavRoute)

    /**
     * 导航到详情
     */
    fun navigateToAnimePlay(animeId: Int) = navigateToMultiPage("$AnimePlayNavRoute/$animeId")

    /**
     * 导航到排行榜
     */
    fun navigateToRank() = navigateToSinglePage(RankNavRoute)

    /**
     * 导航到目录
     */
    fun navigateToCategory(
        label: String = "all",
        genre: String = "all",
        year: String = "all",
        letter: String = "all",
        resource: String = "all",
        order: String = "time",
        region: String = "all",
        season: String = "all",
        status: String = "all"
    ) =
        navigateToMultiPage("$CatalogRoute/?region=$region&genre=$genre&letter=$letter&year=$year&season=$season&status=$status&label=$label&resource=$resource&order=$order")

    fun navigateToGeneralized(key: String) = navigateToSinglePage("$GeneralizedNavRoute/?key=$key")

    /**
     * 导航到设置
     */
    fun navigateToSettings() = navigateToSinglePage("settings")

    /**
     * 导航到关于
     */
    fun navigateToAbout() = navigateToSinglePage("about")

//    /**
//     * 导航到Web
//     */
//    fun navigateToWeb(url: String = Api.X5_DEBUG_URL) =
//        navigateToMultiPage("web/${url.encodeBase64()}")

    /**
     * 导航到单例页面
     */
    fun navigateToSinglePage(route: String) {
        controller.navigate(route) {
            popUpTo(controller.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
            launchSingleTop = true
        }
    }

    /**
     * 导航到多例页面
     */
    fun navigateToMultiPage(route: String) {
        controller.navigate(route) {
            restoreState = true
        }
    }
}