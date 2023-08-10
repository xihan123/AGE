package cn.xihan.age.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.xihan.age.R
import cn.xihan.age.di.IoDispatcher
import cn.xihan.age.model.AlertDialog
import cn.xihan.age.model.BannerItemModel
import cn.xihan.age.model.CustomHomeModel
import cn.xihan.age.model.HomeListModel
import cn.xihan.age.network.RemoteService
import cn.xihan.age.network.SPSettings
import cn.xihan.age.paging.CatalogPagingSource
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.kJson
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.migration.CustomInject
import dagger.hilt.android.qualifiers.ApplicationContext
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/8/1 19:17
 * @介绍 :
 */
@CustomInject
@WorkerThread
class RemoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val remoteService: RemoteService,
//    private val okHttpClient: OkHttpClient
) : Repository {

    init {
        logDebug("Injection RemoteRepository")
    }

    /**
     * 获取配置模型
     */
    fun getConfigModel(url: String) = remoteService.getConfigModel(url = url).flowOn(ioDispatcher)

    /**
     * 获取首页数据
     */
    fun getHomeModel() = flow {
        val customHomeModel = CustomHomeModel()

        val bannerList =
            kJson.decodeFromString<List<BannerItemModel>>(remoteService.getBannerModel())
        bannerList.whatIfNotNullOrEmpty { customHomeModel.bannerList = it }

        val homeListModel = remoteService.getHomeListModel()




        homeListModel.weekList.whatIfNotNullOrEmpty {
            val weekList =
                ArrayList<List<HomeListModel.WeekModel>>()
            it.forEach { (_, weekModels) ->
                weekList += weekModels
            }
            weekList.whatIfNotNullOrEmpty {
                customHomeModel.weekList = weekList
            }
        }

        homeListModel.recommend.whatIfNotNullOrEmpty { customHomeModel.recommend = it }

        homeListModel.latest.whatIfNotNullOrEmpty { customHomeModel.latest = it }


        emit(customHomeModel)
    }.catch {
//        throw AgeException.SnackBarException(
//            message = it.message ?: context.getString(R.string.error_unknown)
//        )
        throw AgeException.AlertException(
            AlertDialog(
                title = context.getString(R.string.error_tip),
                message = it.message ?: context.getString(R.string.error_unknown),
                positiveMessage = context.getString(R.string.retry),
                negativeMessage = ""
            )
        )
    }.flowOn(ioDispatcher)


    /**
     * 获取目录总数据
     */
    fun getCatalogListModel() = flow {
        val allCnt = remoteService.getCategoryModel(mapOf(), 1, 1).total
        if (allCnt == 0) {
            throw AgeException.ToastException(
                message = context.getString(R.string.error_unknown)
            )
        }
        val list = remoteService.getCategoryModel(mapOf(), 1, allCnt).videos
        if (list.isEmpty()) {
            throw AgeException.ToastException(
                message = context.getString(R.string.error_empty)
            )
        }
        emit(list)
    }.flowOn(ioDispatcher)


    /**
     * 分类筛选番剧
     * @param filterList 筛选条件 Map<String, String>
     */
    fun getFilterCategoryModel(filterList: Map<String, String>) =
        Pager(PagingConfig(pageSize = 10)) {
            CatalogPagingSource(remoteService, filterList)
        }

    /**
     * 获取番剧详情模型
     */
    fun getAnimeDetailModel(animeId: Int) = if (SPSettings.customAPI)
        remoteService.getCustomAnimeDetailModel("${SPSettings.customApiUrl}/$animeId")
    else
        remoteService.getAnimeDetailModel(animeId).flowOn(ioDispatcher)

    /**
     * 推荐番剧
     */
    fun getRecommendModel() =
        remoteService.getRecommendModel().flowOn(ioDispatcher)

    /**
     * 排行榜番剧
     * @param year 年份
     * @param page 页码
     */
    fun getRankModel(year: String) = remoteService.getRankModel(year = year).flowOn(ioDispatcher)

    /**
     * okHttp 获取 Url 的 返回长度和返回类型
     */
    fun getContent(url: String): Flow<String> = remoteService.getContent(url = url).transform {
        val contentType = it.headers[HttpHeaders.ContentType]
        val contentLength = it.headers[HttpHeaders.ContentLength]
        val url = if (contentType != null) {
            if ("video" in contentType.lowercase() || "mpegurl" in contentType.lowercase()) {
                url
            } else {
                ""
            }
        } else {
            ""
        }
        emit(url)
    }.flowOn(ioDispatcher)

}