package cn.xihan.age.di

import android.content.Context
import cn.xihan.age.model.AnimeCatalogModelDao
import cn.xihan.age.model.AnimeDetailModelDao
import cn.xihan.age.model.FavoriteDao
import cn.xihan.age.model.HistoryDao
import cn.xihan.age.network.RemoteService
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 16:59
 * @介绍 :
 */
@Module
//@InstallIn(ViewModelComponent::class)
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideRemoteRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        remoteService: RemoteService
    ): RemoteRepository {
        return RemoteRepository(
            context = context,
            ioDispatcher = ioDispatcher,
            remoteService = remoteService
        )
    }

    @Provides
    fun provideLocalRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        animeDetailModelDao: AnimeDetailModelDao,
        animeCatalogModelDao: AnimeCatalogModelDao,
        favoriteDao: FavoriteDao,
        historyDao: HistoryDao
    ): LocalRepository {
        return LocalRepository(
            context = context,
            ioDispatcher = ioDispatcher,
            animeDetailModelDao = animeDetailModelDao,
            animeCatalogModelDao = animeCatalogModelDao,
            favoriteDao = favoriteDao,
            historyDao = historyDao
        )
    }


}