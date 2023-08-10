package cn.xihan.age.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import cn.xihan.age.base.BaseDao

@Keep
@Serializable
@Parcelize
data class CatalogModel(
    @SerialName("total")
    var total: Int = 0,
    @SerialName("videos")
    var videos: List<VideoModel> = listOf()
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    @Entity("AnimeCatalogVideoModel")
    data class VideoModel(
        @SerialName("company")
        var company: String = "",
        @SerialName("cover")
        var cover: String = "",
        @PrimaryKey
        @SerialName("id")
        var id: Int = 0,
        @SerialName("intro")
        var intro: String = "",
        @SerialName("name")
        var name: String = "",
        @SerialName("name_original")
        var nameOriginal: String = "",
        @SerialName("name_other")
        var nameOther: String = "",
        @SerialName("play_time")
        var playTime: String = "",
        @SerialName("premiere")
        var premiere: String = "",
        @SerialName("status")
        var status: String = "",
        @SerialName("tags")
        var tags: String = "",
        @SerialName("tags_arr")
        var tagsArr: List<String> = listOf(),
        @SerialName("time")
        var time: Int = 0,
        @SerialName("type")
        var type: String = "",
        @SerialName("uptodate")
        var uptodate: String = "",
        @SerialName("writer")
        var writer: String = ""
    ) : Parcelable
}

@Dao
interface AnimeCatalogModelDao: BaseDao<CatalogModel.VideoModel> {

    /**
     * 根据 r动画名称、r其他名称、r原版名称 查询
     */
    @Query("SELECT * FROM AnimeCatalogVideoModel WHERE name LIKE '%' || :name || '%' OR nameOther LIKE '%' || :name || '%' OR nameOriginal LIKE '%' || :name || '%'")
    fun queryAnimeCatalogModel(name: String): PagingSource<Int, CatalogModel.VideoModel>

}