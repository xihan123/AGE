package cn.xihan.age.persistence

import androidx.annotation.Keep
import androidx.room.TypeConverter
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.model.HistoryModel
import cn.xihan.age.util.kJson

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/22 17:33
 * @介绍 :
 */
@Keep
class MyConverter {


    @TypeConverter
    fun listToJson(value: List<String>): String {
        return kJson.encodeToString(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<String> {
        return kJson.decodeFromString(value)
    }

//    @TypeConverter
//    fun animeBasieModelToString(value: CatalogModel.VideoModel): String {
//        return kJson.encodeToString(value)
//    }
//
//    @TypeConverter
//    fun stringToAnimeBasieModel(value: String): CatalogModel.VideoModel {
//        return kJson.decodeFromString(value)
//    }

    @TypeConverter
    fun favoriteEntityToString(favoriteEntity: FavoriteModel): String {
        return kJson.encodeToString(favoriteEntity)
    }

    @TypeConverter
    fun stringToFavoriteEntity(json: String): FavoriteModel {
        return kJson.decodeFromString(json)
    }

//    @TypeConverter
//    fun stringToPlayModel(json: String): Video {
//        return kJson.decodeFromString(json)
//    }
//
//    @TypeConverter
//    fun playModelToString(playModel: Video): String {
//        return kJson.encodeToString(playModel)
//    }

    @TypeConverter
    fun historyEntityToString(historyEntity: HistoryModel): String {
        return kJson.encodeToString(historyEntity)
    }

    @TypeConverter
    fun stringToHistoryEntity(json: String): HistoryModel {
        return kJson.decodeFromString(json)
    }

    @TypeConverter
    fun stringToAnimePlayListModel(json: String): Map<String, List<List<String>>> {
        return kJson.decodeFromString(json)
    }

    @TypeConverter
    fun animePlayListModelToString(animePlayListModel: Map<String, List<List<String>>>): String {
        return kJson.encodeToString(animePlayListModel)
    }

}
