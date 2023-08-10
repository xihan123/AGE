package cn.xihan.age.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import android.os.Parcelable

@Keep
@Serializable
@Parcelize
data class RankModel(
    @SerialName("rank")
    var rank: List<List<RankItemModel>> = listOf(),
    @SerialName("total")
    var total: Int = 0,
    @SerialName("year")
    var year: String = ""
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    data class RankItemModel(
        @SerialName("AID")
        var id: Int = 0,
        @SerialName("CCnt")
        var cCnt: String = "",
        @SerialName("NO")
        var nO: Int = 0,
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable
}