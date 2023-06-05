package cn.xihan.age.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.extension.logDebug
import com.kongzue.dialogx.dialogs.PopTip
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/24 19:26
 * @介绍 :
 */
@HiltWorker
class AnimeDataWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        remoteRepository.getCatalogListModel()
            .catch {
                logDebug("获取目录数据失败: ${it.message}")
                PopTip.show("获取目录数据失败: ${it.message}").autoDismiss(2000)
            }.collect { list ->
                localRepository.upsertAnimeData(list)
                PopTip.show("本次更新 ${list.size} 条数据成功~ ").autoDismiss(2000)
            }
        return Result.success()
    }

}