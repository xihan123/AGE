package cn.xihan.age.persistence

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import cn.xihan.age.model.FavoriteDao
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.model.HistoryDao
import cn.xihan.age.model.HistoryModel

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/22 17:34
 * @介绍 :
 */
@Database(
    entities = [
//        (Video::class),
//        (CatalogModel.VideoModel::class),
        (FavoriteModel::class),
        (HistoryModel::class)],
    version = 8,
    exportSchema = true,
)
@TypeConverters(value = [MyConverter::class])
abstract class AppDatabase : RoomDatabase() {

//    abstract fun animeInfoModelDao(): AnimeDetailDao

    //    abstract fun animeCatalogModelDao(): AnimeCatalogModelDao
    abstract fun favouriteDao(): FavoriteDao
    abstract fun historyDao(): HistoryDao

    companion object {
        private const val DB_NAME = "AGE-DATABASE.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                create(context)
            }
        }

        private fun create(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                .fallbackToDestructiveMigration()
                //.allowMainThreadQueries()
                .build()
        }

    }

    fun closeDatabase() {
        if (INSTANCE != null && INSTANCE!!.isOpen) {
            INSTANCE!!.close()
            INSTANCE = null
        }
    }

}