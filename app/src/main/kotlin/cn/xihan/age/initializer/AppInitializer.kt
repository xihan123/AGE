package cn.xihan.age.initializer
import cn.xihan.age.BuildConfig
import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import cn.xihan.age.network.SPSettings
import cn.xihan.age.util.extension.activityCache
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.extension.doOnActivityLifecycle
import cn.xihan.age.util.extension.logDebug
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle
import com.tencent.mmkv.MMKV
import timber.log.Timber

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/25 1:43
 * @介绍 :
 */
class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        activityManage(context)
        mmkv(context)
        dialog(context)
        theme()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            logDebug("AppInitializer is initialized.")
        }
    }

    private fun activityManage(context: Context) {
        application = context as Application
        application.doOnActivityLifecycle(
            onActivityCreated = { activity, _ ->
                // 尝试使用原生方法沉浸状态栏
//                activity.window.statusBarColor = Color.TRANSPARENT
//                activity.immersive(Color.TRANSPARENT, !activity.isNightMode())
                //activity.appGraying()
                activityCache.add(activity)
            },
            onActivityDestroyed = { activity ->
                activityCache.remove(activity)
            }
        )
    }

    private fun theme() {
        // 设置夜间主题
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        when(SPSettings.themeMode) {
            0 -> {
                DialogX.globalTheme = DialogX.THEME.LIGHT
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            1 -> {
                DialogX.globalTheme = DialogX.THEME.DARK
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            else -> {
                DialogX.globalTheme = DialogX.THEME.AUTO
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }

    }

    /**
     * 初始化 DialogX
     */
    private fun dialog(context: Context) {
        DialogX.init(context)
        DialogX.globalStyle = MaterialYouStyle.style()
        DialogX.onlyOnePopTip = true
    }

    /**
     * 初始化 MMKV
     */
    private fun mmkv(context: Context) {
        MMKV.initialize(context)
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}