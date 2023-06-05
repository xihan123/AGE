package cn.xihan.age.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.xihan.age.R
import cn.xihan.age.di.IoDispatcher
import cn.xihan.age.model.AlertDialog
import cn.xihan.age.model.BannerListItemModel
import cn.xihan.age.model.CustomHomeModel
import cn.xihan.age.model.HomeListModel
import cn.xihan.age.network.Api
import cn.xihan.age.network.RemoteService
import cn.xihan.age.network.SPSettings
import cn.xihan.age.paging.CatalogPagingSource
import cn.xihan.age.paging.RankPagingSource
import cn.xihan.age.util.JsoupUtil
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.kJson
import com.skydoves.whatif.whatIfNotNullOrEmpty
import dagger.hilt.android.migration.CustomInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 16:22
 * @介绍 :
 */
@CustomInject
@WorkerThread
class RemoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val remoteService: RemoteService
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
    fun getHomeModel(
        recommend: Int = 12,
        update: Int = 12
    ) = flow {
        val customHomeModel = CustomHomeModel()

        val bannerList =
            kJson.decodeFromString<List<BannerListItemModel>>(remoteService.getBannerModel())
        bannerList.whatIfNotNullOrEmpty { customHomeModel.bannerList = it }

        val homeListModel = remoteService.getHomeListModel(recommend, update)

        homeListModel.xinfansInfo.whatIfNotNullOrEmpty {
            val listXinfansInfoList =
                ArrayList<ArrayList<HomeListModel.XinfansInfoModel>>()
            for (i in 0..6) {
                val listXinfansInfo =
                    ArrayList<HomeListModel.XinfansInfoModel>()
                listXinfansInfo.addAll(it.filter { it1 -> it1.wd == i })
                listXinfansInfoList.add(listXinfansInfo)
            }
            listXinfansInfoList.whatIfNotNullOrEmpty {
                customHomeModel.xinFanDataList = (listXinfansInfoList)
            }
        }

        homeListModel.aniPreEvDay.whatIfNotNullOrEmpty { customHomeModel.mrtjDataList = it }

        homeListModel.aniPreUP.whatIfNotNullOrEmpty { customHomeModel.zjgxDataList = it }


        emit(customHomeModel)
    }.catch {
//        throw AgeException.SnackBarException(
//            message = it.message ?: context.getString(R.string.error_unknown)
//        )
        throw AgeException.AlertException(
            0,
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
        val allCnt = remoteService.getCategoryModel(mapOf(), 1, 1).allCnt
        if (allCnt == 0) {
            throw AgeException.ToastException(
                -1,
                message = context.getString(R.string.error_unknown)
            )
        }
        val list = remoteService.getCategoryModel(mapOf(), 1, allCnt).aniPreL
        if (list.isEmpty()) {
            throw AgeException.ToastException(
                -1,
                message = context.getString(R.string.error_empty)
            )
        }
        emit(list)
    }.flowOn(ioDispatcher)

    /**
     * 分类筛选番剧
     * @param filterList 筛选条件 Map<String, String>
     * @param page 页码
     */
    fun getFilterCategoryModel(filterList: Map<String, String>) =
        Pager(PagingConfig(pageSize = 24)) {
            CatalogPagingSource(remoteService, filterList)
        }

    /**
     * 获取番剧详情模型
     */
    fun getAnimeDetailModel(animeId: String) = if (SPSettings.customAPI)
        remoteService.getCustomAnimeDetailModel("${SPSettings.customApiUrl}/$animeId")
    else
        remoteService.getAnimeDetailModel(animeId).flowOn(ioDispatcher)

    /**
     * 获取番剧播放地址
     */
    suspend fun getAnimePlayUrl(vid: String): String {
        val playUrl = "${Api.PLAYER_PARSER}$vid"
        val html = remoteService.getAnimePlayUrl(playUrl)
        return JsoupUtil.parseAnimeUrl(html)
    }

    /**
     * 推荐番剧
     */
    fun getRecommendModel(size: Int) =
        remoteService.getRecommendModel(size = size).flowOn(ioDispatcher)

    /**
     * 排行榜番剧
     * @param year 年份
     * @param page 页码
     */
    fun getRankModel(year: String) =
        Pager(PagingConfig(pageSize = 24)) {
            RankPagingSource(remoteService, value = year)
        }

}