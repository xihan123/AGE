package cn.xihan.age

import androidx.multidex.MultiDexApplication
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/23 11:04
 * @介绍 :
 */
@HiltAndroidApp
class MyApp : MultiDexApplication(), Configuration.Provider {

    @Inject
    lateinit var workerConfiguration: Configuration

    override fun getWorkManagerConfiguration(): Configuration = workerConfiguration
}