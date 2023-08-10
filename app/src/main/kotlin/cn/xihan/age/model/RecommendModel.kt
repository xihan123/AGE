package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class RecommendModel(
    @SerialName("total")
    var total: Int = 0,
    @SerialName("videos")
    var videos: List<BaseAnimeModel> = listOf()
) : Parcelable