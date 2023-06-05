package cn.xihan.age.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import cn.xihan.age.R
import cn.xihan.age.base.BaseActivity
import cn.xihan.age.component.BottomNavigationBar
import cn.xihan.age.component.Screen
import cn.xihan.age.network.Api
import cn.xihan.age.network.SPSettings
import cn.xihan.age.persistence.AppDatabase
import cn.xihan.age.ui.about.AboutScreen
import cn.xihan.age.ui.desc.DescScreen
import cn.xihan.age.ui.favorite.LocalFavoriteScreen
import cn.xihan.age.ui.history.HistoryScreen
import cn.xihan.age.ui.main.catalog.CatalogScreen
import cn.xihan.age.ui.main.home.HomeScreen
import cn.xihan.age.ui.main.mine.MineScreen
import cn.xihan.age.ui.main.ranking.RankingScreen
import cn.xihan.age.ui.main.recommend.RecommendScreen
import cn.xihan.age.ui.player.PlayerScreen
import cn.xihan.age.ui.search.SearchScreen
import cn.xihan.age.ui.setting.SettingScreen
import cn.xihan.age.util.LockScreenOrientation
import cn.xihan.age.util.SpUtil
import cn.xihan.age.util.Utils.showDisclaimers
import cn.xihan.age.util.VideoPlayerCacheManager
import cn.xihan.age.util.is2kScreen
import cn.xihan.age.util.rememberMutableStateOf
import cn.xihan.age.work.AnimeDataWorker
import cn.xihan.age.work.MainConfigWorker
import com.skydoves.landscapist.coil.CoilImage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1GB缓存
        VideoPlayerCacheManager.initialize(this, 1024 * 1024 * 1024)

        val getConfigModelWorkRequest = OneTimeWorkRequestBuilder<MainConfigWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.UNMETERED)
            .setRequiresCharging(true)
            .build()
        // TODO: 间隔30分钟获取全部番剧数据
        val getAnimeDataWorkRequest =
            PeriodicWorkRequestBuilder<AnimeDataWorker>(30, TimeUnit.MINUTES)
                .setConstraints(constraints)
                .addTag("AnimeDataWorker")
                .build()

        WorkManager.getInstance(this)
            .enqueue(
                listOf(
                    getConfigModelWorkRequest,
                    getAnimeDataWorkRequest
                )
            )
    }

    override fun onStop() {
        AppDatabase.getInstance(this).closeDatabase()
        super.onStop()
    }

    override fun onDestroy() {
        WorkManager.getInstance(this).cancelAllWorkByTag("AnimeDataWorker")
        AppDatabase.getInstance(this).closeDatabase()
        super.onDestroy()
    }

    @Composable
    override fun ComposeContent() {
        val scope = rememberCoroutineScope()
        var showMainPage by rememberMutableStateOf(value = false)
        val context = LocalContext.current
        val lifecycleOwner = LocalLifecycleOwner.current

        /*
        // 生命周期监控
        DisposableEffect(lifecycleOwner) {
            val observer = object : DefaultLifecycleObserver {

                override fun onCreate(owner: LifecycleOwner) {

                }

                override fun onPause(owner: LifecycleOwner) {

                }

                override fun onDestroy(owner: LifecycleOwner) {

                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

         */

        if (SPSettings.isTheFirstTime) {
            context.showDisclaimers()
        }

        LaunchedEffect(true) {
            delay(2500)
            showMainPage = true
        }


        if (showMainPage) {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR, false)
            MainPage()
//            TestPage()
        } else {
            LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
            val splashImg = SpUtil("Settings").decodeString(
                "splash_picture",
                if (context.is2kScreen()) Api.DEFAULT_2K_SPLASH_PIC else Api.DEFAULT_1K_SPLASH_PIC
            )
            CoilImage(
                modifier = Modifier.fillMaxSize(),
                imageModel = { splashImg },
            )
        }

    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @Composable
    private fun MainPage() {
        val items = listOf(
            Screen("home", R.string.title_home, R.drawable.house),
            Screen("catalog", R.string.title_catalog, R.drawable.reorder),
            Screen("recommend", R.string.title_recommend, R.drawable.thumb),
            Screen("ranking", R.string.title_ranking, R.drawable.leaderboard),
            Screen("mine", R.string.title_mine, R.drawable.account)
        )
        val navController = rememberNavController()
        val coroutineScope = rememberCoroutineScope()
        val showBottomBar = rememberMutableStateOf(value = true)
        val snackbarHostState = remember { SnackbarHostState() }
        val context = LocalContext.current
        val appState = rememberMainAppState(
            coroutineScope = coroutineScope,
            controller = navController,
            snackbarHost = snackbarHostState,
            context = context
        )

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomNavigationBar(
                    navController = navController,
                    items = items,
                    modifier = if (showBottomBar.value) Modifier.wrapContentHeight() else Modifier.height(
                        0.dp
                    )
                )
            },
            snackbarHost = {
                SnackbarHost(
                    hostState = snackbarHostState,
                    modifier = Modifier.navigationBarsPadding(),
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = items[0].route,
                modifier = Modifier.padding(bottom = if (showBottomBar.value) 80.dp else 0.dp)
            ) {

                items.forEachIndexed { index, item ->
                    composable(
                        route = item.route
                    ) {
                        when (index) {
                            0 -> HomeScreen(appState = appState)
                            1 -> CatalogScreen(appState = appState)
                            2 -> RecommendScreen(appState = appState)
                            3 -> RankingScreen(appState = appState)
                            4 -> MineScreen(appState = appState)
                        }
                        showBottomBar.value = true
                    }
                }

                /*
                composable(
                    route = items[0].route
                ) {
                    HomeScreen(appState = appState)
                    showBottomBar.value = true
                }

                composable(
                    route = items[1].route
                ) {
                    CatalogScreen(appState = appState)
                    showBottomBar.value = true
                }

                composable(
                    route = items[2].route
                ) {
                    RecommendScreen(appState = appState)
                    showBottomBar.value = true
                }

                composable(
                    route = items[3].route
                ) {
                    RankingScreen(appState = appState)
                    showBottomBar.value = true
                }



                composable(
                    route = items[4].route
                ) {
                    MineScreen(appState = appState)
                    showBottomBar.value = true
                }
   */

                composable(
                    "catalog/{type}/{animeId}",
                    arguments = listOf(
                        navArgument("type") { type = NavType.StringType },
                        navArgument("animeId") { type = NavType.StringType }),
                ) { backStackEntry ->
                    CatalogScreen(
                        appState = appState,
                        type = backStackEntry.arguments?.getString("type"),
                        animeId = backStackEntry.arguments?.getString("animeId")
                    )
                    showBottomBar.value = false
                }

                composable("search") {
                    SearchScreen(appState = appState)
                    showBottomBar.value = false
                }

                composable(
                    "Desc/{animeId}",
                    arguments = listOf(navArgument("animeId") { type = NavType.StringType }),
                ) { backStackEntry ->
                    DescScreen(
                        animeId = backStackEntry.arguments?.getString("animeId"),
                        appState = appState
                    )
                    showBottomBar.value = false
                }

                composable(
                    "player/{animeId}/{episodeId}/{episodeName}",
                    arguments = listOf(
                        navArgument("animeId") { type = NavType.StringType },
                        navArgument("episodeId") { type = NavType.IntType },
                        navArgument("episodeName") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)
                    PlayerScreen(
                        appState = appState,
                        animeId = backStackEntry.arguments?.getString("animeId"),
                        episodeId = backStackEntry.arguments?.getInt("episodeId"),
                        episodeName = backStackEntry.arguments?.getString("episodeName")
                    )
                    showBottomBar.value = false
                }

                composable("favorite") {
                    LocalFavoriteScreen(
                        appState = appState
                    )
                    showBottomBar.value = false
                }

                composable("history") {
                    HistoryScreen(
                        appState = appState
                    )
                    showBottomBar.value = false
                }

                composable("settings") {
                    SettingScreen(
                        appState = appState
                    )
                    showBottomBar.value = false
                }

                composable("about") {
                    AboutScreen(
                        appState = appState
                    )
                    showBottomBar.value = false
                }
            }
        }
    }

    @Composable
    fun TestPage() {

        /*
        thread {
            val webClient = WebClient(BrowserVersion.CHROME,true,null,-1)
            webClient.options.apply {
                isCssEnabled = false
                isThrowExceptionOnScriptError = false
                isThrowExceptionOnFailingStatusCode = false
                isPrintContentOnFailingStatusCode = false
                isUseInsecureSSL = true
            }
            webClient.waitForBackgroundJavaScript(3000)
            val html = webClient.getPage<HtmlPage>("https://web.age-spa.com:8443/#/play/20220030/2/1").asXml()
            logDebug("html: $html")
        }

         */


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
     * 导航到搜索
     */
    fun navigateToSearch() = navigateToSinglePage("search")

    /**
     * 导航到详情
     */
    fun navigateToDesc(animeId: String) {

        /**
         * 测试播放页面
         */
//        navigateToPlayer("20100015", 2, "1")
//        navigateToMultiPage("desc/20100015")
        navigateToMultiPage("desc/$animeId")
    }

    /**
     * 导航到目录
     */
    fun navigateToCatalog(type: String?, animeId: String?) =
        navigateToMultiPage("catalog/${type}/${animeId}")

    /**
     * 导航到播放页面
     */
    fun navigateToPlayer(
        animeId: String?,
        episodeId: Int?,
        episodeName: String?
    ) = navigateToMultiPage("player/${animeId}/${episodeId}/${episodeName}")

    /**
     * 导航到收藏
     */
    fun navigateToFavorite() = navigateToSinglePage("favorite")

    /**
     * 导航到历史记录
     */
    fun navigateToHistory() = navigateToSinglePage("history")

    /**
     * 导航到设置
     */
    fun navigateToSettings() = navigateToSinglePage("settings")

    /**
     * 导航到关于
     */
    fun navigateToAbout() = navigateToSinglePage("about")

    /**
     * 导航到单例页面
     */
    fun navigateToSinglePage(route: String) {
        controller.navigate(route) {
            popUpTo(controller.graph.findStartDestination().id) {
                saveState = true
            }
            restoreState = true
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