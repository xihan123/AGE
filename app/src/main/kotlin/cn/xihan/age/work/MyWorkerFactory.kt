package cn.xihan.age.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import cn.xihan.age.repository.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 21:07
 * @介绍 :
 */
@Singleton
class MyWorkerFactory @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
): WorkerFactory()  {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when(workerClassName){
            MainConfigWorker::class.java.name -> MainConfigWorker(appContext, workerParameters, localRepository, remoteRepository)
            AnimeDataWorker::class.java.name -> AnimeDataWorker(appContext, workerParameters, localRepository, remoteRepository)
            else -> null
        }
    }


}