package cn.xihan.age.initializer

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.startup.Initializer
import cn.xihan.age.BuildConfig
import cn.xihan.age.util.Settings
import cn.xihan.age.util.SimpleLoggerPrinter
import cn.xihan.age.util.initLogger
import cn.xihan.age.util.logDebug
import com.kongzue.dialogx.DialogX
import com.tencent.mmkv.MMKV
import timber.log.Timber

/**
 * @项目名 : age-anime
 * @作者 : MissYang
 * @创建时间 : 2023/9/17 18:30
 * @介绍 :
 */
class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        MMKV.initialize(context)
        initLogger(BuildConfig.DEBUG, SimpleLoggerPrinter())
        theme()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
            logDebug("AppInitializer is initialized.")
        }
    }


    private fun theme() {
        when (Settings.themeMode) {
            1 -> {
                DialogX.globalTheme = DialogX.THEME.LIGHT
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            2 -> {
                DialogX.globalTheme = DialogX.THEME.DARK
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }

            else -> {
                DialogX.globalTheme = DialogX.THEME.AUTO
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }

    override fun dependencies() = emptyList<Class<out Initializer<*>>>()
}