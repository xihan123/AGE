package cn.xihan.age.di

import android.os.Build
import cn.xihan.age.BuildConfig
import cn.xihan.age.util.Api
import cn.xihan.age.util.JsoupService
import cn.xihan.age.util.RemoteService
import cn.xihan.age.util.SSLSocketClient
import cn.xihan.age.util.kJson
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
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/18 20:55
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
    fun provideHttpClient(): HttpClient {
        return HttpClient(OkHttp) {
            //install(HttpCache)
            install(ContentNegotiation) {
                json(kJson)
            }
//            install(Logging) {
//                logger = Logger.DEFAULT
//                level = LogLevel.HEADERS
//            }
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

            engine {
                config {

                    sslSocketFactory(
                        SSLSocketClient.getSSLSocketFactory(),
                        SSLSocketClient.getX509TrustManager()
                    )
                    hostnameVerifier(SSLSocketClient.getHostnameVerifier())

                    if (BuildConfig.DEBUG) {
                        addInterceptor(
                            LoggingInterceptor.Builder().setLevel(com.ihsanbal.logging.Level.BASIC)
                                .log(okhttp3.internal.platform.Platform.WARN).build()
                        )
                    }
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
            // 设置超时时间
            connectTimeout(10, TimeUnit.SECONDS)
            writeTimeout(10, TimeUnit.SECONDS)
            readTimeout(10, TimeUnit.SECONDS)

//            dns(OkHttpDns())
            sslSocketFactory(
                SSLSocketClient.getSSLSocketFactory(), SSLSocketClient.getX509TrustManager()
            )
            hostnameVerifier(SSLSocketClient.getHostnameVerifier())
            if (BuildConfig.DEBUG) {
                addInterceptor(
                    LoggingInterceptor.Builder().setLevel(com.ihsanbal.logging.Level.BASIC)
                        .log(okhttp3.internal.platform.Platform.WARN).build()
                )
            }
        }.build()
    }

    @Provides
    @Singleton
    fun provideKtorfit(httpClient: HttpClient, baseUrl: String): Ktorfit {
        return Ktorfit
            .Builder()
            .baseUrl(baseUrl)
            .httpClient(httpClient)
            .converterFactories(FlowConverterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideRemoteService(httpClient: HttpClient): RemoteService {
        return provideKtorfit(httpClient, Api.API_BASE_URL).create()
    }

    @Provides
    @Singleton
    fun provideJsoupService(httpClient: HttpClient): JsoupService {
        return provideKtorfit(httpClient, Api.JSOUP_BASE_URL).create()
    }

}
