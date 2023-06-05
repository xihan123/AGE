package cn.xihan.age.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cn.xihan.age.R
import cn.xihan.age.model.MainConfigModel
import cn.xihan.age.network.Api
import cn.xihan.age.network.Settings
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.SpUtil
import cn.xihan.age.util.Utils
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.is2kScreen
import com.kongzue.dialogx.dialogs.PopTip
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.catch
import kotlin.random.Random

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 18:08
 * @介绍 :
 */
@HiltWorker
class MainConfigWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        remoteRepository.getConfigModel(Api.CONFIG_URL)
            .catch {
                setSplash(Settings.mainConfigModel.appEntity.splashLink.onlinePic)
                PopTip.show(
                    String.format(
                        appContext.getString(R.string.retry_Tip),
                        it.message
                    )
                ).autoDismiss(2500)
            }
            .collect {
                SpUtil("Settings").encode("mainConfigModel", it)
                Utils.saveLabel(it.aGEEntity.labels)
                setSplash(it.appEntity.splashLink.onlinePic)
            }
        return Result.success()
    }

    private fun setSplash(it: MainConfigModel.AppEntityModel.SplashLinkModel.OnlinePicModel) {
        val list = if (application.is2kScreen()) it.k2 else it.k1
        SpUtil("Settings").encode(
            "splash_picture", list[Random.nextInt(list.size)].picUrl
        )
    }

}