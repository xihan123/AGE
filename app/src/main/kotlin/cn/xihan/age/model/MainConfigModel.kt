package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class MainConfigModel(
    @SerialName("AGEEntity")
    var aGEEntity: AGEEntityModel = AGEEntityModel(),
    @SerialName("AppEntity")
    var appEntity: AppEntityModel = AppEntityModel(),
    @SerialName("UpdateEntity")
    var updateEntity: UpdateEntityModel = UpdateEntityModel()
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    data class AGEEntityModel(
        @SerialName("aidIn")
        var aidIn: List<Int> = listOf(),
        @SerialName("coreApi")
        var coreApi: String = "",
        @SerialName("labels")
        var labels: Map<String, List<String>> = mutableMapOf(),
        @SerialName("mobileWebsiteLink")
        var mobileWebsiteLink: String = "",
        @SerialName("officialGithubLink")
        var officialGithubLink: String = "",
        @SerialName("officialWebsiteLink")
        var officialWebsiteLink: String = "",
        @SerialName("playerLabelArr")
        var playerLabelArr: Map<String, String> = mapOf(),
    ) : Parcelable

    @Keep
    @Serializable
    @Parcelize
    data class AppEntityModel(
        @SerialName("appGraying")
        var appGraying: Boolean = false,
        @SerialName("customApi")
        var customApi: List<CustomApiModel> = listOf(),
        @SerialName("dailyPasswordLink")
        var dailyPasswordLink: String = "",
        @SerialName("disclaimerLink")
        var disclaimerLink: String = "",
        @SerialName("myAge")
        var myAge: Boolean = false,
        @SerialName("payCode")
        var payCode: String = "",
        @SerialName("splashLink")
        var splashLink: SplashLinkModel = SplashLinkModel()
    ) : Parcelable {
        @Keep
        @Serializable
        @Parcelize
        data class CustomApiModel(
            @SerialName("ApiName")
            var apiName: String = "",
            @SerialName("ApiUrl")
            var apiUrl: String = ""
        ) : Parcelable

        @Keep
        @Serializable
        @Parcelize
        data class SplashLinkModel(
            @SerialName("2k")
            var k1: List<KModel> = listOf(),
            @SerialName("1k")
            var k2: List<KModel> = listOf()
        ) : Parcelable {
            @Keep
            @Serializable
            @Parcelize
            data class KModel(
                @SerialName("md5")
                var md5: String = "",
                @SerialName("picUrl")
                var picUrl: String = ""
            ) : Parcelable
        }
    }

    @Keep
    @Serializable
    @Parcelize
    data class UpdateEntityModel(
        @SerialName("body")
        var body: String = "",
        @SerialName("browser_download_url")
        var browserDownloadUrl: String = "",
        @SerialName("onlineDiskDownloadLink")
        var onlineDiskDownloadLink: String = "",
        @SerialName("versionCode")
        var versionCode: Int = 0,
        @SerialName("versionName")
        var versionName: String = ""
    ) : Parcelable
}