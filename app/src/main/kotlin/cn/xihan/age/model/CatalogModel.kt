package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Upsert
import cn.xihan.age.base.BaseDao
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class CatalogModel(
    @SerialName("AllCnt")
    var allCnt: Int = 0,
    @SerialName("AniPreL")
    var aniPreL: List<AniPreLModel> = listOf(),
    @SerialName("Labels_genre")
    var labelsGenre: List<String> = listOf(),
    @SerialName("Labels_label")
    var labelsLabel: List<String> = listOf(),
    @SerialName("Labels_letter")
    var labelsLetter: List<String> = listOf(),
    @SerialName("Labels_order")
    var labelsOrder: List<String> = listOf(),
    @SerialName("Labels_region")
    var labelsRegion: List<String> = listOf(),
    @SerialName("Labels_resource")
    var labelsResource: List<String> = listOf(),
    @SerialName("Labels_season")
    var labelsSeason: List<String> = listOf(),
    @SerialName("Labels_status")
    var labelsStatus: List<String> = listOf(),
    @SerialName("Labels_year")
    var labelsYear: List<String> = listOf(),
    @SerialName("Tip")
    var tip: String = "",
    @SerialName("WebTitle")
    var webTitle: String = ""
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    @Entity
    data class AniPreLModel(
        @PrimaryKey
        @SerialName("AID")
        var aID: String = "",
        @SerialName("R其他名称")
        var r其他名称: String = "",
        @SerialName("R制作公司")
        var r制作公司: String = "",
        @SerialName("R剧情类型")
        var r剧情类型: List<String> = listOf(),
        @SerialName("R动画名称")
        var r动画名称: String = "",
        @SerialName("R动画种类")
        var r动画种类: String = "",
        @SerialName("R原作")
        var r原作: String = "",
        @SerialName("R原版名称")
        var r原版名称: String = "",
        @SerialName("R封面图小")
        var r封面图小: String = "",
        @SerialName("R播放状态")
        var r播放状态: String = "",
        @SerialName("R新番标题")
        var r新番标题: String = "",
        @SerialName("R简介")
        var r简介: String = "",
        @SerialName("R首播时间")
        var r首播时间: String = ""
    ) : Parcelable
}

@Dao
interface AniPreLModelDao: BaseDao<CatalogModel.AniPreLModel> {

    /**
     * 根据 r动画名称、r其他名称、r原版名称 查询
     */
    @Query("SELECT * FROM AniPreLModel WHERE r动画名称 LIKE '%' || :name || '%' OR r其他名称 LIKE '%' || :name || '%' OR r原版名称 LIKE '%' || :name || '%'")
    fun queryModel(name: String): PagingSource<Int, CatalogModel.AniPreLModel>

}