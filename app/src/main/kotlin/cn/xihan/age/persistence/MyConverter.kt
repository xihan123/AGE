package cn.xihan.age.persistence

import androidx.annotation.Keep
import androidx.room.TypeConverter
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.model.HistoryModel
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 16:49
 * @介绍 :
 */
@Keep
class MyConverter {

    private val jsonDecoder = Json {
        ignoreUnknownKeys = true // JSON和数据模型字段可以不匹配
        coerceInputValues = true // 如果JSON字段是Null则使用默认值
        isLenient = true // 宽松模式
    }

    @TypeConverter
    fun listToJson(value: List<String>): String {
        return jsonDecoder.encodeToString(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String> {
        return jsonDecoder.decodeFromString(value)
    }

    @TypeConverter
    fun animeBasieModelToString(value: CatalogModel.AniPreLModel): String {
        return jsonDecoder.encodeToString(value)
    }

    @TypeConverter
    fun stringToAnimeBasieModel(value: String): CatalogModel.AniPreLModel {
        return jsonDecoder.decodeFromString(value)
    }

    @TypeConverter
    fun favoriteEntityToString(favoriteEntity: FavoriteModel): String {
        return jsonDecoder.encodeToString(favoriteEntity)
    }

    @TypeConverter
    fun stringToFavoriteEntity(json: String): FavoriteModel {
        return jsonDecoder.decodeFromString(json)
    }

    @TypeConverter
    fun listToString(list: List<AnimeDetailModel.AniInfoModel.PlayModel>): String {
        return jsonDecoder.encodeToString(list)
    }

    @TypeConverter
    fun stringToList(json: String): List<AnimeDetailModel.AniInfoModel.PlayModel> {
        return jsonDecoder.decodeFromString(json)
    }

    @TypeConverter
    fun stringToPlayModel(json: String): AnimeDetailModel.AniInfoModel.PlayModel {
        return jsonDecoder.decodeFromString(json)
    }

    @TypeConverter
    fun playModelToString(playModel: AnimeDetailModel.AniInfoModel.PlayModel): String {
        return jsonDecoder.encodeToString(playModel)
    }

    @TypeConverter
    fun stringToPlayListModel(json: String): List<List<AnimeDetailModel.AniInfoModel.PlayModel>> {
        return jsonDecoder.decodeFromString(json)
    }

    @TypeConverter
    fun playListModelToString(playListModel: List<List<AnimeDetailModel.AniInfoModel.PlayModel>>): String {
        return jsonDecoder.encodeToString(playListModel)
    }

    @TypeConverter
    fun stringToCloudModel(json: String): List<AnimeDetailModel.AniInfoModel.CloudDiskModel> {
        return jsonDecoder.decodeFromString(json)
    }

    @TypeConverter
    fun cloudModelToString(cloudModel: List<AnimeDetailModel.AniInfoModel.CloudDiskModel>): String {
        return jsonDecoder.encodeToString(cloudModel)
    }

    @TypeConverter
    fun animeInfoModelToString(animeInfoModel: AnimeDetailModel.AniInfoModel): String {
        return jsonDecoder.encodeToString(animeInfoModel)
    }

    @TypeConverter
    fun historyEntityToString(historyEntity: HistoryModel): String {
        return jsonDecoder.encodeToString(historyEntity)
    }

    @TypeConverter
    fun stringToHistoryEntity(json: String): HistoryModel {
        return jsonDecoder.decodeFromString(json)
    }

}
