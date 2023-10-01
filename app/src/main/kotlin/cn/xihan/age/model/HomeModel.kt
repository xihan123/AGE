package cn.xihan.age.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/20 12:16
 * @介绍 :
 */
@Keep
@Serializable
@Parcelize
data class HomeModel(
    @SerialName("latest")
    var latest: List<AnimeModel> = listOf(),
    @SerialName("recommend")
    var recommend: List<AnimeModel> = listOf(),
    @SerialName("week_list")
    val weekItems: Map<String, List<WeekItem>> = emptyMap()
) : Parcelable

@Keep
@Serializable
@Parcelize
data class AnimeModel(
    @SerialName("AID")
    var aID: Int = 0,
    @SerialName("Href")
    var href: String = "",
    @SerialName("NewTitle")
    var newTitle: String = "",
    @SerialName("PicSmall")
    var picSmall: String = "",
    @SerialName("Title")
    var title: String = ""
) : Parcelable

@Serializable
@Parcelize
data class WeekItem(
    val isnew: Int,
    val id: Int,
    val wd: Int,
    val name: String,
    val mtime: String,
    val namefornew: String
) : Parcelable

class Banner : ArrayList<Banner.BannerItemModel>() {
    @Keep
    @Serializable
    @Parcelize
    data class BannerItemModel(
        @SerialName("AID")
        var aID: Int = 0,
        @SerialName("PicUrl")
        var picUrl: String = "",
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable
}