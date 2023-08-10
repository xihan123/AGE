package cn.xihan.age.initializer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import cn.xihan.age.BuildConfig
import cn.xihan.age.network.SPSettings
import cn.xihan.age.util.extension.SimpleLoggerPrinter
import cn.xihan.age.util.extension.activityCache
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.extension.doOnActivityLifecycle
import cn.xihan.age.util.extension.initLogger
import cn.xihan.age.util.extension.logDebug
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogxmaterialyou.style.MaterialYouStyle
import com.tencent.mmkv.MMKV
import com.tencent.smtt.export.external.TbsCoreSettings
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback
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
        x5(context)
        initLogger(BuildConfig.DEBUG, SimpleLoggerPrinter())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            logDebug("AppInitializer is initialized.")
        }
    }

    private fun x5(context: Context) {
        QbSdk.initX5Environment(context, object : PreInitCallback {
            override fun onCoreInitFinished() {

            }

            /**
             * 预初始化结束
             * 由于X5内核体积较大，需要依赖网络动态下发，所以当内核不存在的时候，默认会回调false，此时将会使用系统内核代替
             * @param isX5 是否使用X5内核
             */
            override fun onViewInitFinished(isX5: Boolean) {
                SPSettings.x5Available = isX5
                logDebug("X5内核是否加载成功：$isX5")
            }
        })
        val map: HashMap<String, Any> = HashMap()
        map[TbsCoreSettings.TBS_SETTINGS_USE_SPEEDY_CLASSLOADER] = true
        map[TbsCoreSettings.TBS_SETTINGS_USE_DEXLOADER_SERVICE] = true
        QbSdk.initTbsSettings(map)
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

        when (SPSettings.themeMode) {
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