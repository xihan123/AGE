package cn.xihan.age.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep


@Keep
@Serializable
@Parcelize
data class RankModel(
    @SerialName("AllCnt")
    var allCnt: Int = 0,
    @SerialName("AniRankPre")
    var aniRankPre: List<AniRankPreModel> = listOf(),
    @SerialName("Year")
    var year: Int = 0,
    var page: Int = 1,
    var pageCount: Int = 1
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    data class AniRankPreModel(
        @SerialName("AID")
        var aID: String = "",
        @SerialName("CCnt")
        var cCnt: Int = 0,
        @SerialName("Href")
        var href: String = "",
        @SerialName("NO")
        var nO: Int = 0,
        @SerialName("NewTitle")
        var newTitle: String = "",
        @SerialName("PicSmall")
        var picSmall: String = "",
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable
}