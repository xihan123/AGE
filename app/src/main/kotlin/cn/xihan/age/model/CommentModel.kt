package cn.xihan.age.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Keep
@Serializable
@Parcelize
data class CommentResponseModel(
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
        @SerialName("comments")
        var comments: List<CommentModel> = listOf(),
        @SerialName("pagination")
        var pagination: PaginationModel = PaginationModel()
    ) : Parcelable {
        @Keep
        @Serializable
        @Parcelize
        data class CommentModel(
            @SerialName("cid")
            var cid: Int = 0,
            @SerialName("content")
            var content: String = "",
            @SerialName("floor")
            var floor: Int = 0,
            @SerialName("ip")
            var ip: String = "",
            @SerialName("sid")
            var sid: Int = 0,
            @SerialName("status")
            var status: Int = 0,
            @SerialName("time")
            var time: String = "",
            @SerialName("uid")
            var uid: Int = 0,
            @SerialName("username")
            var username: String = ""
        ) : Parcelable

        @Keep
        @Serializable
        @Parcelize
        data class PaginationModel(
            @SerialName("curPage")
            var curPage: Int = 0,
            @SerialName("pageCount")
            var pageCount: Int = 0,
            @SerialName("pageNoList")
            var pageNoList: List<Int> = listOf(),
            @SerialName("total")
            var total: Int = 0,
            @SerialName("totalPage")
            var totalPage: Int = 0
        ) : Parcelable
    }
}