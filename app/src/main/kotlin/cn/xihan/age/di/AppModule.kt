package cn.xihan.age.di

import android.content.Context
import android.content.res.Resources
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/17 20:33
 * @介绍 :
 */
@InstallIn(SingletonComponent::class)
@Module
object AppModule {

    @Provides
    @Singleton
    fun resources(@ApplicationContext context: Context): Resources = context.resources


}