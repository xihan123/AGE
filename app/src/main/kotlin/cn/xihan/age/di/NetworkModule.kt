package cn.xihan.age.di

import android.os.Build
import cn.xihan.age.BuildConfig
import cn.xihan.age.network.Api
import cn.xihan.age.network.RemoteService
import cn.xihan.age.util.OkHttpDns
import cn.xihan.age.util.SSLSocketClient
import cn.xihan.age.util.SharedPreferencesUtil
import cn.xihan.age.util.kJson
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.jensklingenberg.ktorfit.Ktorfit
import de.jensklingenberg.ktorfit.converter.builtin.FlowConverterFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import javax.inject.Singleton

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/10/15 22:26
 * @介绍 :
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * 创建单例 client
     */
    @Provides
    @Singleton
    fun provideKtorClient(): HttpClient {
        return HttpClient(OkHttp) {
            //install(HttpCache)
            install(ContentNegotiation) {
                json(kJson)
            }
//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.HEADERS
//            }
            install(WebSockets)
            install(UserAgent) {
                agent =
                    "AGE/${BuildConfig.VERSION_NAME} ${System.getProperty("http.agent") ?: "(Android ${Build.VERSION.RELEASE})"}"
            }
//            install(HttpTimeout) {
//                requestTimeoutMillis = 20000
////                    SharedPreferencesUtil("SPSetting").decodeLong("requestTimeoutMillis", 10000)
//                connectTimeoutMillis = 20000
////                    SharedPreferencesUtil("SPSetting").decodeLong("connectTimeoutMillis", 10000)
//                socketTimeoutMillis = 20000
////                    SharedPreferencesUtil("SPSetting").decodeLong("socketTimeoutMillis", 10000)
//            }
//        defaultRequest {
//            url(Api.BASE_URL)
//            headers.appendIfNameAbsent("Connection", "close")
//            headers.appendIfNameAbsent("AppVersion", BuildConfig.VERSION_NAME)
//        }

            engine {
                config {
                    if (SharedPreferencesUtil("SPSetting").decodeBoolean("customDns")) {
                        dns(OkHttpDns())
                    }

                    sslSocketFactory(
                        SSLSocketClient.getSSLSocketFactory(),
                        SSLSocketClient.getX509TrustManager()
                    )
                    hostnameVerifier(SSLSocketClient.getHostnameVerifier())

                    addInterceptor(
                        LoggingInterceptor.Builder()
                            .setLevel(Level.BASIC)
                            .log(Platform.WARN)
                            .build()
                    )

                }
            }
        }
    }

    /**
     * 创建单例OkhttpClient
     */
    @Provides
    @Singleton
    fun provideOkhttpClient(): OkHttpClient {
        return OkHttpClient.Builder().apply {
            sslSocketFactory(
                SSLSocketClient.getSSLSocketFactory(),
                SSLSocketClient.getX509TrustManager()
            )
            hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            if (BuildConfig.DEBUG){
                addInterceptor(
                    LoggingInterceptor.Builder()
                        .setLevel(Level.BASIC)
                        .log(Platform.WARN)
                        .build()
                )
            }

            if (SharedPreferencesUtil("SPSetting").decodeBoolean("customDns")) {
                dns(OkHttpDns())
            }

        }.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(httpClient: HttpClient): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(Api.BASE_URL)
            .httpClient(httpClient)
            .converterFactories(FlowConverterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRemoteService(retrofit: Ktorfit): RemoteService {
        return retrofit.create()
    }

}