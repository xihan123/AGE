package cn.xihan.age.repository

import android.content.Context
import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.xihan.age.R
import cn.xihan.age.di.IoDispatcher
import cn.xihan.age.model.AniPreLModelDao
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.model.AnimeDetailModelDao
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.model.FavoriteDao
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.model.HistoryDao
import cn.xihan.age.model.HistoryModel
import cn.xihan.age.model.MainConfigModel
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.KStore
import cn.xihan.age.util.extension.asFlow
import cn.xihan.age.util.extension.listStoreOf
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.extension.storeOf
import cn.xihan.age.util.extension.toDate
import dagger.hilt.android.migration.CustomInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 16:46
 * @介绍 :
 */
@CustomInject
@WorkerThread
class LocalRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val animeDetailModelDao: AnimeDetailModelDao,
    private val animePreLModelDao: AniPreLModelDao,
    private val favoriteDao: FavoriteDao,
    private val historyDao: HistoryDao
) {

    init {
        logDebug("Injection LocalRepository")
    }

    private val _historyList = listStoreOf(
        filePath = "${context.filesDir.absolutePath}/SearchHistory.json",
        default = emptyList<String>()
    )

    val historyListFlow = _historyList.updates

    suspend fun updateHistoryList(historyList: List<String>) = _historyList.update { historyList }

    suspend fun addHistory(history: String) =
        _historyList.update {
            it?.minus(history)?.plus(history)
        }

    suspend fun clearSearchHistory() = _historyList.reset()

    /**
     * 保存总数据到本地数据库
     */
    suspend fun upsertAnimeData(animePreLModel: List<CatalogModel.AniPreLModel>) =
        animePreLModelDao.insertAll(animePreLModel)

    /**
     * 根据 关键词 模糊查询 动漫数据
     */
    fun getAnimeDataByQuery(query: String) = Pager(PagingConfig(pageSize = 20)) {
        animePreLModelDao.queryModel(query)
    }

    /**
     * 根据 animeId 更新番剧模型
     */
    suspend fun upsertAnimeDetailModel(animeDetailModel: AnimeDetailModel.AniInfoModel) =
        animeDetailModelDao.upsert(animeDetailModel)

    /**
     * 新建或者更新收藏模型
     */
    suspend fun upsertFavoriteModel(
        animeId: String,
        animeName: String,
        animeCover: String,
        animeSubtitle: String
    ) = if (favoriteDao.findByAnimeId(animeId) != null) {
        favoriteDao.updateAnimeInfo(
            animeId = animeId,
            animeName = animeName,
            animeCover = animeCover,
            animeSubtitle = animeSubtitle
        )
    } else {
        favoriteDao.upsert(
            FavoriteModel(
                animeId = animeId,
                animeName = animeName,
                animeCover = animeCover,
                animeSubtitle = animeSubtitle,
                animeFavorite = false
            )
        )
    }

    /**
     * 根据 animeId 查询是否收藏
     */
    fun isFavorite(animeId: String) = favoriteDao.isFavorite(animeId)

    /**
     * 根据 animeId 更新收藏状态
     */
    suspend fun updateFavorite(animeId: String, favorite: Boolean) =
        favoriteDao.updateFavorite(animeId, favorite)

    /**
     * 设置全部收藏状态
     */
    suspend fun updateAllFavorite(favorite: Boolean) = favoriteDao.updateAllFavorite(favorite)

    /**
     * 分页查询收藏列表
     */
    fun queryCollectModelLocal(type: Int = 0) = Pager(PagingConfig(pageSize = 20)) {
        if (type == 0)  favoriteDao.queryAllFavorite() else favoriteDao.queryAllFavoriteDesc()
    }

    /**
     * 更新历史记录
     * @param animeId 番剧ID
     * @param animeName 番剧名称
     * @param animeCover 番剧封面
     * @param animeLatestPlayTitle 番剧最新播放标题
     * @param animeLastPlayTitle 番剧上次播放标题
     * @param animeLastPlayProgress 番剧上次播放进度
     * @param animeLastPlayDuration 番剧上次播放时长
     * @param animePlayList 番剧播放列表
     */
    suspend fun updateHistory(
        animeId: String,
        animeName: String = "",
        animeCover: String = "",
        animeLatestPlayTitle: String = "",
        animeLastPlayTitle: String = "",
        animeLastPlayProgress: Long = 0,
        animeLastPlayDuration: Long = 0,
        animePlayListIndex: Int = 0,
    ) {
        val latestTime = System.currentTimeMillis().toDate()
        historyDao.findByAnimeId(animeId)?.let {
            if (animeLatestPlayTitle.isNotBlank()) {
                it.animeLatestPlayTitle = animeLatestPlayTitle
            }
            if (animeLastPlayTitle.isNotBlank()) {
                it.animeLastPlayTitle = animeLastPlayTitle
            }
            if (animeLastPlayProgress != 0L) {
                it.animeLastPlayProgress = animeLastPlayProgress
            }
            if (animeLastPlayDuration != 0L) {
                it.animeLastPlayDuration = animeLastPlayDuration
            }
            it.animePlayListIndex = animePlayListIndex
            it.animeLatestUpdateTime = latestTime
            historyDao.upsert(it)
        } ?: historyDao.upsert(
            HistoryModel(
                animeId = animeId,
                animeName = animeName,
                animeCover = animeCover,
                animeLatestPlayTitle = animeLatestPlayTitle,
                animeLatestUpdateTime = latestTime,
                animeLastPlayTitle = animeLastPlayTitle,
                animeLastPlayProgress = animeLastPlayProgress,
                animeLastPlayDuration = animeLastPlayDuration,
                animeLastPlayingTime = latestTime,
                animePlayListIndex = animePlayListIndex
            )
        )

    }

    /**
     * 更新历史记录模型
     * @param animeId 番剧ID
     * @param animeName 番剧名称
     * @param animeCover 番剧封面
     * @param animeLatestPlayTitle 番剧最新播放标题
     * @param animeLatestUpdateTime 番剧最新更新时间
     */
    suspend fun updateHistoryModel(
        animeId: String,
        animeName: String = "",
        animeCover: String = "",
        animeLatestPlayTitle: String = "",
        animeLatestUpdateTime: String = "",
    ) = historyDao.updateHistory(
        animeId = animeId,
        animeName = animeName,
        animeCover = animeCover,
        animeLatestPlayTitle = animeLatestPlayTitle,
        animeLatestUpdateTime = animeLatestUpdateTime
    )

    /**
     * 分页查询历史记录
     */
    fun queryHistoryPaging(type: Int) = Pager(PagingConfig(pageSize = 20)) {
        if (type == 0) historyDao.queryAllHistoryPagingPlayingTimeDesc() else if (type == 1) historyDao.queryAllHistoryPagingUpdateTimeDesc() else historyDao.queryAllHistoryPaging()
    }

    /**
     * 根据 animeId 删除历史记录
     */
    suspend fun deleteHistoryByAnimeId(animeId: String) = historyDao.deleteHistoryByAnimeId(animeId)

    /**
     * 清空历史记录
     */
    suspend fun deleteAllHistory() = historyDao.deleteAllHistory()

    /**
     * 根据 animeId 查询历史模型
     */
    suspend fun queryHistoryModelByAnimeId(animeId: String) = historyDao.findByAnimeId(animeId)

    /**
     * 根据 animeId 查询番剧模型
     */
    fun queryAnimeDetailModelByAnimeId(animeId: String) = flow {
        val model = animeDetailModelDao.queryAnimeDetailModel(animeId)
        if (model == null) emitAll(
            AgeException.SnackBarException(context.getString(R.string.error_unknown)).asFlow()
        ) else emit(model)
    }.flowOn(ioDispatcher)


}