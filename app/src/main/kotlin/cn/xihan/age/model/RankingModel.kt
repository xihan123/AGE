package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class RankingModel(
    @SerialName("rank")
    var rank: List<List<RankModel>> = listOf(),
    @SerialName("total")
    var total: Int = 0,
    @SerialName("year")
    var year: String = ""
) : Parcelable

@Keep
@Serializable
@Parcelize
data class RankModel(
    @SerialName("AID")
    var aID: Int = 0,
    @SerialName("CCnt")
    var cCnt: String = "",
    @SerialName("NO")
    var nO: Int = 0,
    @SerialName("Title")
    var title: String = ""
) : Parcelable