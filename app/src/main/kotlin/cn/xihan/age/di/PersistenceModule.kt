package cn.xihan.age.di

import android.content.Context
import cn.xihan.age.model.FavoriteDao
import cn.xihan.age.model.HistoryDao
import cn.xihan.age.persistence.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 16:47
 * @介绍 :
 */
@Module
@InstallIn(SingletonComponent::class)
object PersistenceModule {

    @Provides
    @Singleton
    fun provideRoomDataBase(@ApplicationContext context: Context): AppDatabase =
        AppDatabase.getInstance(context)

    @Provides
    @Singleton
    fun provideAnimeInfoModelDao(appDatabase: AppDatabase) =
        appDatabase.animeInfoModelDao()

    @Provides
    @Singleton
    fun provideAnimeCatalogModelDao(appDatabase: AppDatabase) =
        appDatabase.animeCatalogModelDao()

    @Provides
    @Singleton
    fun provideFavoriteDao(appDatabase: AppDatabase): FavoriteDao {
        return appDatabase.favouriteDao()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

}