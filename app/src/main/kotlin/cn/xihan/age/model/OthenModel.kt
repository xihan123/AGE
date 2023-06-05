package cn.xihan.age.model

import android.os.Parcelable
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import cn.xihan.age.base.BaseDao
import kotlinx.coroutines.flow.Flow
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/25 17:57
 * @介绍 :
 */
/**
 * 收藏模型
 * @param animeId 番剧ID
 * @param animeName 番剧名称
 * @param animeCover 番剧封面
 * @param animeSubtitle 番剧副标题
 * @param animeFavorite 是否收藏
 */
@Entity
@Parcelize
@Serializable
data class FavoriteModel(
    @PrimaryKey val animeId: String,
    var animeName: String,
    var animeCover: String,
    var animeSubtitle: String,
    var animeFavorite: Boolean
) : Parcelable

/**
 * 收藏 Dao
 */
@Dao
interface FavoriteDao: BaseDao<FavoriteModel> {

    @Query("SELECT * FROM FavoriteModel WHERE animeId LIKE :animeId LIMIT 1")
    suspend fun findByAnimeId(animeId: String): FavoriteModel?

    /**
     * 根据 animeId 查询是否收藏
     */
    @Query("SELECT animeFavorite FROM FavoriteModel WHERE animeId LIKE :animeId LIMIT 1")
    fun isFavorite(animeId: String): Flow<Boolean>

    /**
     * 根据 animeId 更新收藏状态
     */
    @Query("UPDATE FavoriteModel SET animeFavorite = :animeFavorite WHERE animeId LIKE :animeId")
    suspend fun updateFavorite(animeId: String, animeFavorite: Boolean)

    /**
     * 设置全部收藏状态
     */
    @Query("UPDATE FavoriteModel SET animeFavorite = :animeFavorite")
    suspend fun updateAllFavorite(animeFavorite: Boolean)

    /**
     * 根据 animeId 更新 animeName animeCover animeSubtitle
     */
    @Query("UPDATE FavoriteModel SET animeName = :animeName, animeCover = :animeCover, animeSubtitle = :animeSubtitle WHERE animeId LIKE :animeId")
    suspend fun updateAnimeInfo(
        animeId: String,
        animeName: String,
        animeCover: String,
        animeSubtitle: String
    )

    /**
     * 分页查询所有 已收藏
     */
    @Query("SELECT * FROM FavoriteModel WHERE animeFavorite = 1")
    fun queryAllFavorite(): PagingSource<Int, FavoriteModel>

    /**
     * 分页查询所有 已收藏 并按照倒序
     */
    @Query("SELECT * FROM FavoriteModel WHERE animeFavorite = 1 ORDER BY animeId DESC")
    fun queryAllFavoriteDesc(): PagingSource<Int, FavoriteModel>

}

/**
 * 历史记录模型
 * @param animeId 番剧ID
 * @param animeName 番剧名称
 * @param animeCover 番剧封面
 * @param animeLatestPlayTitle 番剧最新集数标题
 * @param animeLatestUpdateTime 番剧最新集数更新时间
 * @param animeLastPlayTitle 番剧最近播放标题
 * @param animeLastPlayProgress 番剧最近播放进度
 * @param animeLastPlayDuration 番剧最近播放时长
 * @param animeLastPlayingTime 番剧最近播放时间
 * @param animePlayList 番剧播放列表
 */
@Entity
@Parcelize
@Serializable
data class HistoryModel(
    @PrimaryKey val animeId: String,
    var animeName: String,
    var animeCover: String,
    var animeLatestPlayTitle: String,
    var animeLatestUpdateTime: String,
    var animeLastPlayTitle: String,
    var animeLastPlayProgress: Long,
    var animeLastPlayDuration: Long,
    var animeLastPlayingTime: String,
    var animePlayListIndex: Int = 0
) : Parcelable

/**
 * 历史记录 Dao
 */
@Dao
interface HistoryDao: BaseDao<HistoryModel> {

    @Query("SELECT * FROM HistoryModel WHERE animeId LIKE :animeId LIMIT 1")
    suspend fun findByAnimeId(animeId: String): HistoryModel?

    /**
     * 根据 animeId 更新 animeName animeCover animeLatestPlayTitle animeLatestUpdateTime
     */
    @Query("UPDATE HistoryModel SET animeName = :animeName, animeCover = :animeCover, animeLatestPlayTitle = :animeLatestPlayTitle, animeLatestUpdateTime = :animeLatestUpdateTime WHERE animeId = :animeId")
    suspend fun updateHistory(
        animeId: String,
        animeName: String,
        animeCover: String,
        animeLatestPlayTitle: String,
        animeLatestUpdateTime: String,
    )

    /**
     * 根据 animeId 更新  animePlayList
     */
    @Query("UPDATE HistoryModel SET animePlayListIndex = :animePlayListIndex WHERE animeId LIKE :animeId")
    suspend fun updatePlayListIndexByAnimeId(
        animeId: String,
        animePlayListIndex: Int,
    )

    /**
     * 获取 pagingsource 用于分页
     */
    @Query("SELECT * FROM HistoryModel")
    fun queryAllHistoryPaging(): PagingSource<Int, HistoryModel>

    /**
     * 获取 pagingsource 用于分页
     */
    @Query("SELECT * FROM HistoryModel ORDER BY animeLastPlayingTime DESC")
    fun queryAllHistoryPagingPlayingTimeDesc(): PagingSource<Int, HistoryModel>

    /**
     * 获取 pagingsource 用于分页
     */
    @Query("SELECT * FROM HistoryModel ORDER BY animeLatestUpdateTime DESC")
    fun queryAllHistoryPagingUpdateTimeDesc(): PagingSource<Int, HistoryModel>

    /**
     * 删除全部历史记录
     */
    @Query("DELETE FROM HistoryModel")
    suspend fun deleteAllHistory()

    /**
     * 根据 animeId 删除历史记录
     */
    @Query("DELETE FROM HistoryModel WHERE animeId LIKE :animeId")
    suspend fun deleteHistoryByAnimeId(animeId: String)
}