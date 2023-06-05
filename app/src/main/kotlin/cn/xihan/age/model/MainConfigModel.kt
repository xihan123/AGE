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
    @SerialName("AGEEntity") var aGEEntity: AGEEntityModel = AGEEntityModel(),
    @SerialName("AppEntity") var appEntity: AppEntityModel = AppEntityModel(),
    @SerialName("UpdateEntity") var updateEntity: UpdateEntityModel = UpdateEntityModel()
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    data class AGEEntityModel(
        @SerialName("aidIn") var aidIn: List<String> = listOf(),
        @SerialName("coreApi") var coreApi: String = "",
        @SerialName("labels") var labels: LabelsModel = LabelsModel(),
        @SerialName("mobileWebsiteLink") var mobileWebsiteLink: String = "",
        @SerialName("officialWebsiteLink") var officialWebsiteLink: String = "",
        @SerialName("tmm3pPlay") var tmm3pPlay: String = ""
    ) : Parcelable {
        @Keep
        @Serializable
        @Parcelize
        data class LabelsModel(
            @SerialName("Labels_genre") var labelsGenre: List<String> = listOf(),
            @SerialName("Labels_label") var labelsLabel: List<String> = listOf(),
            @SerialName("Labels_letter") var labelsLetter: List<String> = listOf(),
            @SerialName("Labels_order") var labelsOrder: List<String> = listOf(),
            @SerialName("Labels_region") var labelsRegion: List<String> = listOf(),
            @SerialName("Labels_resource") var labelsResource: List<String> = listOf(),
            @SerialName("Labels_season") var labelsSeason: List<String> = listOf(),
            @SerialName("Labels_status") var labelsStatus: List<String> = listOf(),
            @SerialName("Labels_year") var labelsYear: List<String> = listOf()
        ) : Parcelable
    }

    @Keep
    @Serializable
    @Parcelize
    data class AppEntityModel(
        @SerialName("customApi") var customApi: List<CustomApiModel> = listOf(),
        @SerialName("dailyPasswordLink") var dailyPasswordLink: String = "",
        @SerialName("disclaimerLink") var disclaimerLink: String = "",
        @SerialName("myAge") var myAge: Boolean = false,
        @SerialName("payCode") var payCode: String = "fkx10986uqscudavhqufq1a",
        @SerialName("splashLink") var splashLink: SplashLinkModel = SplashLinkModel()
    ) : Parcelable {
        @Keep
        @Serializable
        @Parcelize
        data class CustomApiModel(
            @SerialName("ApiName") var apiName: String = "",
            @SerialName("ApiUrl") var apiUrl: String = ""
        ) : Parcelable

        @Keep
        @Serializable
        @Parcelize
        data class SplashLinkModel(
            @SerialName("onlinePic") var onlinePic: OnlinePicModel = OnlinePicModel(),
            @SerialName("splashDownloadLink") var splashDownloadLink: String = "",
            @SerialName("splashFileMD5") var splashFileMD5: String = "",
            @SerialName("splashVersion") var splashVersion: String = ""
        ) : Parcelable {
            @Keep
            @Serializable
            @Parcelize
            data class OnlinePicModel(
                @SerialName("1k") var k1: List<KModel> = listOf(),
                @SerialName("2k") var k2: List<KModel> = listOf()
            ) : Parcelable {
                @Keep
                @Serializable
                @Parcelize
                data class KModel(
                    @SerialName("md5") var md5: String = "",
                    @SerialName("picUrl") var picUrl: String = ""
                ) : Parcelable
            }
        }
    }

    @Keep
    @Serializable
    @Parcelize
    data class UpdateEntityModel(
        @SerialName("downLink") var downLink: String = "",
        @SerialName("onlineDiskDownloadLink") var onlineDiskDownloadLink: String = "",
        @SerialName("updateLog") var updateLog: String = "",
        @SerialName("versionCode") var versionCode: String = "",
        @SerialName("versionName") var versionName: String = ""
    ) : Parcelable
}