package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class LoginResponseModel(
    @SerialName("code")
    var code: Int = 0,
    @SerialName("data")
    var `data`: UserDataModel = UserDataModel(),
    @SerialName("message")
    var message: String = ""
) : Parcelable

@Keep
@Serializable
@Parcelize
data class UserDataModel(
    @SerialName("sign_k")
    var signK: Int = 0,
    @SerialName("sign_t")
    var signT: Int = 0,
    @SerialName("username")
    var username: String = ""
) : Parcelable


@Keep
@Serializable
@Parcelize
data class SpacaptchaModel(
    @SerialName("img")
    var img: String = "",
    @SerialName("key")
    var key: String = "",
    @SerialName("sensitive")
    var sensitive: Boolean = false
) : Parcelable