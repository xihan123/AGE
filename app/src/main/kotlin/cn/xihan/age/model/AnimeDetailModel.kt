package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import cn.xihan.age.base.BaseDao
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class AnimeDetailModel(
    @SerialName("series")
    var series: List<BaseAnimeModel> = listOf(),
    @SerialName("similar")
    var similar: List<BaseAnimeModel> = listOf(),
    @SerialName("video")
    var video: VideoModel = VideoModel(),
    @SerialName("player_label_arr")
    var playerLabelArr: Map<String, String> = mapOf()
) : Parcelable {

    @Keep
    @Serializable
    @Parcelize
    @Entity("AnimeDetailVideoModel")
    data class VideoModel(
        @SerialName("area")
        var area: String = "",
        @SerialName("collect_cnt")
        var collectCnt: String = "",
        @SerialName("comment_cnt")
        var commentCnt: String = "",
        @SerialName("company")
        var company: String = "",
        @SerialName("cover")
        var cover: String = "",
        @PrimaryKey
        @SerialName("id")
        var id: Int = 0,
        @SerialName("intro")
        var intro: String = "",
        @SerialName("intro_clean")
        var introClean: String = "",
        @SerialName("intro_html")
        var introHtml: String = "",
        @SerialName("letter")
        var letter: String = "",
        @SerialName("name")
        var name: String = "",
        @SerialName("name_original")
        var nameOriginal: String = "",
        @SerialName("name_other")
        var nameOther: String = "",
        @SerialName("plot")
        var plot: String = "",
        @SerialName("plot_arr")
        var plotArr: List<String> = listOf(),
        @SerialName("playlists")
        var playlists: Map<String, List<List<String>>> = mapOf(),
        @SerialName("premiere")
        var premiere: String = "",
        @SerialName("rank_cnt")
        var rankCnt: String = "",
        @SerialName("resource")
        var resource: String = "",
        @SerialName("season")
        var season: Int = 0,
        @SerialName("series")
        var series: String = "",
        @SerialName("star")
        var star: Int = 0,
        @SerialName("status")
        var status: String = "",
        @SerialName("tags")
        var tags: String = "",
        @SerialName("tags_arr")
        var tagsArr: List<String> = listOf(),
        @SerialName("time")
        var time: Int = 0,
        @SerialName("time_format_1")
        var timeFormat1: String = "",
        @SerialName("time_format_2")
        var timeFormat2: String = "",
        @SerialName("time_format_3")
        var timeFormat3: String = "",
        @SerialName("type")
        var type: String = "",
        @SerialName("uptodate")
        var uptodate: String = "",
        @SerialName("website")
        var website: String = "",
        @SerialName("writer")
        var writer: String = "",
        @SerialName("year")
        var year: Int = 0
    ) : Parcelable

}

@Dao
interface AnimeDetailModelDao : BaseDao<AnimeDetailModel.VideoModel> {

    /**
     * 根据 aid 查询 番剧模型
     */
    @Query("SELECT * FROM AnimeDetailVideoModel WHERE id = :aid LIMIT 1")
    suspend fun queryAnimeDetailModel(aid: Int): AnimeDetailModel.VideoModel?
}