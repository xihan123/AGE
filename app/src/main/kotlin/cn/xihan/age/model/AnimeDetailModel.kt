package cn.xihan.age.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/22 15:56
 * @介绍 :
 */
/**
 * 视频模型
 * 链接： https://app.age-api.com:8443/v2/detail/20230131
 */
@Parcelize
@Serializable
//@Entity
data class Video(
//    @PrimaryKey
    val id: Int = 0,
    @SerialName("name_other")
    val nameOther: String = "",
    val company: String = "",
    val name: String = "",
    val type: String = "",
    val writer: String = "",
    @SerialName("name_original")
    val nameOriginal: String = "",
    val plot: String = "",
    @SerialName("plot_arr")
    val plotArr: List<String> = emptyList(),
    val playlists: Map<String, List<List<String>>> = mapOf(),
    val area: String = "",
    val letter: String = "",
    val website: String = "",
    val star: Int = 0,
    val status: String = "",
    @SerialName("uptodate")
    val upToDate: String = "",
    @SerialName("time_format_1")
    val timeFormat1: String = "",
    @SerialName("time_format_2")
    val timeFormat2: String = "",
    @SerialName("time_format_3")
    val timeFormat3: String = "",
    val time: Long = 0L,
    val tags: String = "",
    @SerialName("tags_arr")
    val tagsArr: List<String> = emptyList(),
    val intro: String = "",
    @SerialName("intro_html")
    val introHtml: String = "",
    @SerialName("intro_clean")
    val introClean: String = "",
    val series: String = "",
    @SerialName("net_disk")
    val netDisk: String = "",
    val resource: String = "",
    val year: Int = 0,
    val season: Int = 0,
    val premiere: String = "",
    @SerialName("rank_cnt")
    val rankCnt: String = "",
    val cover: String = "",
    @SerialName("comment_cnt")
    val commentCnt: String = "",
    @SerialName("collect_cnt")
    val collectCnt: String = ""
) : Parcelable

@Serializable
@Parcelize
data class PlayerJx(
    val vip: String = "https://43.240.74.134:8443/vip/?url=",
    val zj: String = "https://43.240.74.134:8443/m3u8/?url="
) : Parcelable

@Serializable
@Parcelize
data class AnimeDetailModel(
    val video: Video = Video(),
    val series: List<AnimeModel> = listOf(),
    val similar: List<AnimeModel> = listOf(),
    @SerialName("player_label_arr")
    val playerLabel: Map<String, String> = emptyMap(),//PlayerLabel = PlayerLabel(),
    @SerialName("player_vip")
    val playerVip: String = "",
    @SerialName("player_jx")
    val playerJx: PlayerJx = PlayerJx()
) : Parcelable

//@Dao
//interface AnimeDetailDao: BaseDao<Video> {
//
//    /**
//     * 根据 aid 查询 番剧模型
//     */
//    @Query("SELECT * FROM Video WHERE id = :aid LIMIT 1")
//    suspend fun queryByAid(aid: Int): Video?
//
//}
