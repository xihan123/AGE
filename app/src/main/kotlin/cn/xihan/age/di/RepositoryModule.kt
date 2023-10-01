package cn.xihan.age.di

import android.content.Context
import cn.xihan.age.model.FavoriteDao
import cn.xihan.age.model.HistoryDao
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.JsoupService
import cn.xihan.age.util.RemoteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import javax.inject.Singleton

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/18 20:55
 * @介绍 :
 */
@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideLocalRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
//        animeDetailModelDao: AnimeDetailDao,
//        animeCatalogModelDao: AnimeCatalogModelDao,
        favoriteDao: FavoriteDao,
        historyDao: HistoryDao
    ): LocalRepository {
        return LocalRepository(
            context = context,
            ioDispatcher = ioDispatcher,
//            animeDetailModelDao = animeDetailModelDao,
//            animeCatalogModelDao = animeCatalogModelDao,
            favoriteDao = favoriteDao,
            historyDao = historyDao
        )
    }

    @Provides
    @Singleton
    fun provideRemoteRepository(
        @ApplicationContext context: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher,
        okhttpClient: OkHttpClient,
        remoteService: RemoteService,
        jsoupService: JsoupService
    ): RemoteRepository {
        return RemoteRepository(
            context = context,
            ioDispatcher = ioDispatcher,
            okhttpClient = okhttpClient,
            remoteService = remoteService,
            jsoupService = jsoupService
        )
    }


}