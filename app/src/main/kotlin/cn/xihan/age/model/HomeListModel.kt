package cn.xihan.age.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Serializable
@Parcelize
data class HomeListModel(
    @SerialName("AniPreEvDay")
    var aniPreEvDay: List<AniPreEvDayModel> = emptyList(),
    @SerialName("AniPreUP")
    var aniPreUP: List<AniPreUPModel> = emptyList(),
    @SerialName("Tip")
    var tip: String = "",
    @SerialName("XinfansInfo")
    var xinfansInfo: List<XinfansInfoModel> = emptyList()
) : Parcelable {

    @Keep
    @Serializable
    @Parcelize
    data class AniPreEvDayModel(
        @SerialName("AID")
        var aID: String = "",
        @SerialName("Href")
        var href: String = "",
        @SerialName("NewTitle")
        var newTitle: String = "",
        @SerialName("PicSmall")
        var picSmall: String = "",
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable

    @Keep
    @Serializable
    @Parcelize
    data class AniPreUPModel(
        @SerialName("AID")
        var aID: String = "",
        @SerialName("Href")
        var href: String = "",
        @SerialName("NewTitle")
        var newTitle: String = "",
        @SerialName("PicSmall")
        var picSmall: String = "",
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable

    @Keep
    @Serializable
    @Parcelize
    data class XinfansInfoModel(
        @SerialName("id")
        var id: String = "",
        @SerialName("isnew")
        var isnew: Boolean = false,
        @SerialName("mtime")
        var mtime: String = "",
        @SerialName("name")
        var name: String = "",
        @SerialName("namefornew")
        var namefornew: String = "",
        @SerialName("wd")
        var wd: Int = 0
    ) : Parcelable

}

@Keep
@Serializable
@Parcelize
data class BannerListItemModel(
    @SerialName("AID")
    var aID: String = "",
    @SerialName("PicUrl")
    var picUrl: String = "",
    @SerialName("Title")
    var title: String = ""
) : Parcelable


/**
 * 自定义主页模型
 * @param xinFanDataList 新番数据
 * @param mrtjDataList 今日推荐数据
 * @param zjgxDataList 最近更新数据
 */
@Keep
@Serializable
@Parcelize
data class CustomHomeModel(
    var bannerList: List<BannerListItemModel> = emptyList(),
    var xinFanDataList: ArrayList<ArrayList<HomeListModel.XinfansInfoModel>> = arrayListOf(),
    var mrtjDataList: List<HomeListModel.AniPreEvDayModel> = emptyList(),
    var zjgxDataList: List<HomeListModel.AniPreUPModel> = emptyList(),
): Parcelable