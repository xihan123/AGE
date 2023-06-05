package cn.xihan.age.model


import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Parcelize
@Serializable
data class RecommendModel(
    @SerialName("AllCnt")
    var allCnt: Int = 0,
    @SerialName("AniPre")
    var aniPre: List<AniPreModel> = listOf(),
    @SerialName("Tip")
    var tip: String = "",
) : Parcelable {
    @Parcelize
    @Serializable
    data class AniPreModel(
        @SerialName("AID")
        var aID: String = "",
        @SerialName("Href")
        var href: String = "",
        @SerialName("NewTitle")
        var newTitle: String = "",
        @SerialName("PicSmall")
        var picSmall: String = "",
        @SerialName("Title")
        var title: String = "",
    ) : Parcelable
}