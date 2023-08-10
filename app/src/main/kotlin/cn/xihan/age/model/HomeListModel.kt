package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 番剧基类模型
 * @param aid  番剧ID
 * @param href   番剧链接
 * @param title 番剧名称
 * @param cover 番剧封面
 * @param subtitle 副标题
 */
@Keep
@Serializable
@Parcelize
data class BaseAnimeModel(
    @SerialName("AID") var aid: Int = 0,
    @SerialName("Href") var href: String = "",
    @SerialName("Title") var title: String = "",
    @SerialName("PicSmall") var cover: String = "",
    @SerialName("NewTitle") var subtitle: String = ""
) : Parcelable


@Keep
@Serializable
@Parcelize
data class HomeListModel(
    @SerialName("latest") var latest: List<BaseAnimeModel> = listOf(),
    @SerialName("recommend") var recommend: List<BaseAnimeModel> = listOf(),
    @SerialName("week_list") var weekList: Map<String, List<WeekModel>> = mapOf()
) : Parcelable {

    @Keep
    @Serializable
    @Parcelize
    data class WeekModel(
        @SerialName("id") var id: Int = 0,
        @SerialName("isnew") var isnew: Int = 0,
        @SerialName("mtime") var mtime: String = "",
        @SerialName("name") var title: String = "",
        @SerialName("namefornew") var sbutitle: String = "",
        @SerialName("wd") var wd: Int = 0
    ) : Parcelable


}

@Keep
@Serializable
@Parcelize
data class BannerItemModel(
    @SerialName("AID")
    var aid: Int = 0,
    @SerialName("PicUrl")
    var cover: String = "",
    @SerialName("Title")
    var title: String = ""
) : Parcelable

/**
 * 自定义主页模型
 * @param bannerList 轮播图数据
 * @param weekList 新番数据
 * @param recommend 今日推荐数据
 * @param latest 最近更新数据
 */
@Keep
@Serializable
@Parcelize
data class CustomHomeModel(
    var bannerList: List<BannerItemModel> = emptyList(),
    var weekList: ArrayList<List<HomeListModel.WeekModel>> = arrayListOf(),
    var recommend: List<BaseAnimeModel> = emptyList(),
    var latest: List<BaseAnimeModel> = emptyList(),
) : Parcelable