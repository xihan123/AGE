package cn.xihan.age.base

import android.os.Bundle
import android.view.View
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.parfoismeng.slidebacklib.registerSlideBack
import com.parfoismeng.slidebacklib.unregisterSlideBack

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/25 1:15
 * @介绍 :
 */
abstract class BaseActivity : AppCompatActivity() {

//    private val _viewModel by viewModels<BaseViewModel<IUiState, IUiIntent>>()

    protected val defaultTag: String
        get() = this.javaClass.simpleName

    open fun getTitleText(): String {
        return this::class.java.simpleName.replace("Activity", "")
    }

    /**
     * 启用侧滑返回
     */
    open fun enableSwipeBack(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)
        init()
        setContent {
            CompositionLocalProvider {
                Mdc3Theme {
                    val systemUiController = rememberSystemUiController()
                    val darkIcons = isSystemInDarkTheme()
                    // We're using the system default here, but you could use any boolean value here

                    SideEffect {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = !darkIcons
                        )

                        systemUiController.setNavigationBarColor(
                            Color.Transparent,
                            darkIcons = !darkIcons
                        )
                    }
                    ComposeContent()
                }
            }
        }
        if (enableSwipeBack()) {
            registerSlideBack(true) {
                slideBack()
            }
        }
    }

    override fun onDestroy() {
        if (enableSwipeBack()) {
            unregisterSlideBack()
        }
        super.onDestroy()
    }

    open fun init() {}

    /**
     * 滑动返回事件
     */
    open fun slideBack() {}

    @Composable
    abstract fun ComposeContent()

    /**
     * 隐藏虚拟导航按键
     */
    protected open fun hideNavBar() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
    }

    fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // 如果你在Composable里面，可以参考rememberSystemUiController() 一样使用LocalView.current也可以
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            // 修改行为
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    fun showSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, true)
        WindowInsetsControllerCompat(
            window,
            window.decorView
        ).show(WindowInsetsCompat.Type.systemBars())
    }

}