package cn.xihan.age.ui.web

import android.content.pm.ActivityInfo
import android.view.View
import android.view.WindowManager
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import cn.xihan.age.component.AccompanistWebChromeClient
import cn.xihan.age.component.AccompanistWebViewClient
import cn.xihan.age.component.LoadingState
import cn.xihan.age.component.WebView
import cn.xihan.age.component.rememberWebViewNavigator
import cn.xihan.age.component.rememberWebViewState
import cn.xihan.age.network.Api
import cn.xihan.age.ui.main.MainAppState
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.extension.thread
import cn.xihan.age.util.extension.topActivity
import cn.xihan.age.util.isNotNightMode
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/8/8 17:25
 * @介绍 :
 */
@Composable
fun WebScreen(
    appState: MainAppState,
    url: String? = null
) {
    val state = rememberWebViewState(url = url ?: Api.X5_DEBUG_URL)
    val navigator = rememberWebViewNavigator()
    val context = LocalContext.current
    Column {

        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress, modifier = Modifier.fillMaxWidth()
            )
        }
        var fullScreenView: View? = null

        val webChromeClient = remember {
            object : AccompanistWebChromeClient() {
                override fun onShowCustomView(
                    view: View, callback: IX5WebChromeClient.CustomViewCallback?
                ) {
                    topActivity.windowManager.addView(
                        view,
                        WindowManager.LayoutParams(WindowManager.LayoutParams.TYPE_APPLICATION)
                    )
                    fullScreen(view)
                    fullScreenView = view
                    topActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                }

                override fun onHideCustomView() {
                    topActivity.windowManager.removeViewImmediate(fullScreenView)
                    fullScreenView = null
                    topActivity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                }
            }
        }

        WebView(
            state = state,
            navigator = navigator,
            onCreated = {
                it.settings.apply {
                    javaScriptEnabled = true
                    domStorageEnabled = true
                    builtInZoomControls = true
                    defaultTextEncodingName = "GBK"
                    useWideViewPort = true
                    loadWithOverviewMode = true
                    domStorageEnabled = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    textZoom = 100
                    setSupportZoom(true)
                    setGeolocationEnabled(false)
                    setSupportMultipleWindows(true)
                }
                it.settingsExtension?.apply {
                    setDisplayCutoutEnable(true)
                    setDayOrNight(isNotNightMode)
                }
            },
            onDispose = {
                it.destroy()
            },
            chromeClient = webChromeClient
        )

    }

}

private fun fullScreen(view: View) {
    view.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_LOW_PROFILE or View.SYSTEM_UI_FLAG_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
}

fun getContent2(url: String): Array<Any?> {
    val objects = arrayOfNulls<Any>(2)
    val client = OkHttpClient()
    try {
        val request = Request.Builder()
            .url(url)
            .head()
            .build()
        val response: Response = client.newCall(request).execute()
        val responseCode = response.code
        if (responseCode == 200) {
            objects[0] = response.header("Content-Length")
            objects[1] = response.header("Content-Type")
        }
        logDebug("getContent code = $responseCode")
    } catch (e: Exception) {
        logDebug("getContent error = ${e.message}", e)
    }
    if (objects[0] == null) objects[0] = -1
    if (objects[1] == null) objects[1] = ""
    return objects
}