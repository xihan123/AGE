package cn.xihan.age.model


import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import cn.xihan.age.base.BaseDao
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Keep
@Serializable
@Parcelize
data class AnimeDetailModel(
    @SerialName("AniInfo")
    var aniInfo: AniInfoModel = AniInfoModel(),
    @SerialName("AniPreRel")
    var aniPreRel: List<AniPreRelModel> = listOf(),
    @SerialName("AniPreSim")
    var aniPreSim: List<AniPreSimModel> = listOf(),
    @SerialName("Tip")
    var tip: String = ""
) : Parcelable {
    @Keep
    @Serializable
    @Parcelize
    @Entity
    data class AniInfoModel(
        @PrimaryKey
        @SerialName("AID")
        var aID: String = "",
        @SerialName("CollectCnt")
        var collectCnt: Int = 0,
        @SerialName("CommentCnt")
        var commentCnt: Int = 0,
        @SerialName("DEF_PLAYINDEX")
        var dEFPLAYINDEX: String = "",
        @SerialName("FilePath")
        var filePath: String = "",
        @SerialName("LastModified")
        var lastModified: String = "",
        @SerialName("ModifiedTime")
        var modifiedTime: Int = 0,
        @SerialName("RankCnt")
        var rankCnt: Int = 0,
        @SerialName("R其它名称")
        var r其它名称: String = "",
        @SerialName("R制作公司")
        var r制作公司: String = "",
        @SerialName("R剧情类型")
        var r剧情类型: String = "",
        @SerialName("R剧情类型2")
        var r剧情类型2: List<String> = listOf(),
        @SerialName("R动画名称")
        var r动画名称: String = "",
        @SerialName("R动画种类")
        var r动画种类: String = "",
        @SerialName("R原作")
        var r原作: String = "",
        @SerialName("R原版名称")
        var r原版名称: String = "",
        @SerialName("R在线播放")
        var r在线播放: String = "",
        @SerialName("R在线播放2")
        var r在线播放2: String = "",
        @SerialName("R在线播放3")
        var r在线播放3: String = "",
        @SerialName("R在线播放4")
        var r在线播放4: String = "",
        @SerialName("R在线播放All")
        var r在线播放All: List<List<PlayModel>> = listOf(),
        @SerialName("R地区")
        var r地区: String = "",
        @SerialName("R备用")
        var r备用: String = "",
        @SerialName("R字母索引")
        var r字母索引: String = "",
        @SerialName("R官方网站")
        var r官方网站: String = "",
        @SerialName("R封面图")
        var r封面图: String = "",
        @SerialName("R封面图小")
        var r封面图小: String = "",
        @SerialName("R推荐星级")
        var r推荐星级: Int = 0,
        @SerialName("R播放状态")
        var r播放状态: String = "",
        @SerialName("R新番标题")
        var r新番标题: String = "",
        @SerialName("R更新时间")
        var r更新时间: String = "",
        @SerialName("R更新时间str")
        var r更新时间str: String = "",
        @SerialName("R更新时间str2")
        var r更新时间str2: String = "",
        @SerialName("R更新时间unix")
        var r更新时间unix: Int = 0,
        @SerialName("R标签")
        var r标签: String = "",
        @SerialName("R标签2")
        var r标签2: List<String> = listOf(),
        @SerialName("R标题V2")
        var r标题V2: String = "",
        @SerialName("R简介")
        var r简介: String = "",
        @SerialName("R简介_br")
        var r简介Br: String = "",
        @SerialName("R系列")
        var r系列: String = "",
        @SerialName("R网盘资源")
        var r网盘资源: String = "",
        @SerialName("R网盘资源2")
        var r网盘资源2: List<CloudDiskModel> = listOf(),
        @SerialName("R视频尺寸")
        var r视频尺寸: String = "",
        @SerialName("R资源类型")
        var r资源类型: String = "",
        @SerialName("R首播季度")
        var r首播季度: String = "",
        @SerialName("R首播年份")
        var r首播年份: String = "",
        @SerialName("R首播时间")
        var r首播时间: String = ""
    ) : Parcelable {
        @Keep
        @Serializable
        @Parcelize
        data class PlayModel(
            @SerialName("EpName")
            var epName: String = "",
            @SerialName("EpPic")
            var epPic: String = "",
            @SerialName("Ex")
            var ex: String = "",
            @SerialName("PlayId")
            var playId: String = "",
            @SerialName("PlayVid")
            var playVid: String = "",
            @SerialName("Title")
            var title: String = "",
            @SerialName("Title_l")
            var titleL: String = "",
            var playing: Boolean = false
        ) : Parcelable

        @Keep
        @Serializable
        @Parcelize
        data class CloudDiskModel(
            @SerialName("ExCode")
            var exCode: String = "",
            @SerialName("Link")
            var link: String = "",
            @SerialName("Title")
            var title: String = ""
        ) : Parcelable
    }

    @Keep
    @Serializable
    @Parcelize
    data class AniPreRelModel(
        @SerialName("AID")
        var aID: String = "",
        @SerialName("Href")
        var href: String = "",
        @SerialName("NewTitle")
        var newTitle: String = "",
        @SerialName("PicSmall")
        var picSmall: String = "",
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable

    @Keep
    @Serializable
    @Parcelize
    data class AniPreSimModel(
        @SerialName("AID")
        var aID: String = "",
        @SerialName("Href")
        var href: String = "",
        @SerialName("NewTitle")
        var newTitle: String = "",
        @SerialName("PicSmall")
        var picSmall: String = "",
        @SerialName("Title")
        var title: String = ""
    ) : Parcelable
}

@Dao
interface AnimeDetailModelDao : BaseDao<AnimeDetailModel.AniInfoModel> {

    /**
     * 根据 aid 查询 番剧模型
     */
    @Query("SELECT * FROM AniInfoModel WHERE aid = :aid LIMIT 1")
    suspend fun queryAnimeDetailModel(aid: String): AnimeDetailModel.AniInfoModel?
}