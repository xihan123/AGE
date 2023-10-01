package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class SearchModel(
    @SerialName("code")
    var code: Int = 0,
    @SerialName("data")
    var `data`: DataModel = DataModel(),
    @SerialName("message")
    var message: String = ""
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    data class DataModel(
        @SerialName("conditions")
        var conditions: ConditionsModel = ConditionsModel(),
        @SerialName("total")
        var total: Int = 0,
        @SerialName("totalPage")
        var totalPage: Int = 0,
        @SerialName("videos")
        var videos: List<VideoModel> = listOf()
    ) : Parcelable {
        @Keep
        @Serializable
        @Parcelize
        data class ConditionsModel(
            @SerialName("page")
            var page: String = "",
            @SerialName("query")
            var query: String = ""
        ) : Parcelable

        @Keep
        @Serializable
        @Parcelize
        data class VideoModel(
            @SerialName("company")
            var company: String = "",
            @SerialName("cover")
            var cover: String = "",
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
            var playTime: String? = null,
            @SerialName("premiere")
            var premiere: String = "",
            @SerialName("status")
            var status: String = "",
            @SerialName("tags")
            var tags: String = "",
            @SerialName("tagsArr")
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
}