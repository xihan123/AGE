package cn.xihan.age.repository

import android.content.Context
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.WorkerThread
import androidx.paging.Pager
import androidx.paging.PagingConfig
import cn.xihan.age.BuildConfig
import cn.xihan.age.di.IoDispatcher
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.paging.CatalogPagingSource
import cn.xihan.age.paging.CommentPagingSource
import cn.xihan.age.paging.GeneralizedPagingSource
import cn.xihan.age.paging.SearchPagingSource
import cn.xihan.age.util.Api
import cn.xihan.age.util.JsoupService
import cn.xihan.age.util.RemoteService
import cn.xihan.age.util.aid
import cn.xihan.age.util.logDebug
import dagger.hilt.android.migration.CustomInject
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/18 15:07
 * @介绍 :
 */
@CustomInject
@WorkerThread
class RemoteRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val okhttpClient: OkHttpClient,
    private val remoteService: RemoteService,
    private val jsoupService: JsoupService
) : Repository {


    init {
        logDebug("Injection RemoteRepository")
    }

    /**
     * 获取 Github  最新版本
     */
    fun getReleaseLatest(): Flow<Triple<String, String, String>> = flow {
        remoteService
            .getResponseBody(Api.RELEASE_LATEST)
            .takeIf {
                it.isNotBlank()
            }
            ?.let {
                val jb = org.json.JSONObject(it)
                val browserDownloadUrl =
                    jb.optJSONArray("assets")?.optJSONObject(0)?.optString("browser_download_url")
                        ?: ""
                val versionName = jb.optString("tag_name").replace("v", "")
                val description = jb.optString("body")
                if (BuildConfig.VERSION_NAME != versionName) {
                    emit(Triple(versionName, description, browserDownloadUrl))
                }
            }
    }.flowOn(ioDispatcher)

    fun getHomePageModel() = remoteService.getHomeModel().flowOn(ioDispatcher)

    fun getBannerModel() = remoteService.getBannerModel().flowOn(ioDispatcher)

    fun getSearchModel(query: String) = Pager(PagingConfig(pageSize = 24)) {
        SearchPagingSource(remoteService, query)
    }

    fun getAnimeDetailModel(animeId: Int) =
        remoteService.getAnimeDetailModel(animeId).flowOn(ioDispatcher)

    fun getAnimeCommentModel(animeId: Int) = Pager(
        config = PagingConfig(
            pageSize = 10,
            enablePlaceholders = true, // 是否启用占位符
            initialLoadSize = 10,
            prefetchDistance = 3
        ),
        pagingSourceFactory = { CommentPagingSource(okhttpClient, animeId) }
    )

    /**
     * 分类筛选番剧
     * @param filterList 筛选条件 Map<String, String>
     */
    fun getFilterCategoryModel(filterList: Map<String, String>) =
        Pager(PagingConfig(pageSize = 10)) {
            CatalogPagingSource(remoteService, filterList)
        }

    private var webView: WebView? = null

    fun getVideoSource(animeId: Int, playIndex: Int, episodeIndex: Int) = tryFetchVideoUrl(
        animeId = animeId, playIndex = playIndex, episodeIndex = episodeIndex
    ).filter { it.isNotBlank() }

    fun tryFetchVideoUrl(animeId: Int, playIndex: Int, episodeIndex: Int) = channelFlow {
        val string = jsoupService.getAnimeUrl(
            animeId = animeId, playIndex = playIndex, episodeIndex = episodeIndex
        )
        val document = Jsoup.parse(string)
        val videoUrl = document.select("#iframeForVideo").attr("src")
        logDebug("videoUrl: $videoUrl")
        if (webView == null) {
            webView = WebView(context).apply { settings.javaScriptEnabled = true }
        }
        webView!!.run {
            logDebug("运行 WebView")
            webViewClient = object : WebViewClient() {
                override fun shouldInterceptRequest(
                    view: WebView, request: WebResourceRequest
                ): WebResourceResponse? {
                    request.url.toString().takeIf { it.isNotBlank() }?.let {
                        parseAndSend(it)
                    }
                    return super.shouldInterceptRequest(view, request)
                }
            }.apply {
                settings.userAgentString =
                    "AGE/${BuildConfig.VERSION_NAME} ${System.getProperty("http.agent") ?: "(Android ${Build.VERSION.RELEASE})"}"
            }
            loadUrl(videoUrl)
        }
        awaitClose {
            webView?.destroy()
            webView = null
        }
    }.flowOn(Dispatchers.Main)

    private fun ProducerScope<String>.parseAndSend(url: String) {
        var urlConnection: HttpURLConnection? = null
        try {
            urlConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.requestMethod = "HEAD"
            val responseCode = urlConnection.responseCode
            if (responseCode == 200) {
                val contentLength = urlConnection.contentLength
                val contentType = urlConnection.contentType
                if (contentType != null && contentType.startsWith("video/")) {
                    trySend(url)
                    close()
                }
            }
        } catch (_: Exception) {
        } finally {
            urlConnection?.disconnect()
        }
    }

    fun getWeeklyUpdate() = flow<Map<String, List<AnimeModel>>> {
        val update = jsoupService.getUpdate()
        val doc = Jsoup.parse(update)
        val recentUpdateVideoWrapper = doc.select("#recent_update_video_wrapper > div")
        val map = recentUpdateVideoWrapper.associate { element ->
            val title = element.select("div > button").text()
            val list =
                element.select("div > div.video_list_box--bd > div > div > div").map { item ->
                    val img = item.select("img").attr("data-original")
                    val span = item.select("span").text()
                    val a = item.select("a")
                    val href = a.attr("href")
                    val name = a.text()
                    AnimeModel(aID = href.aid(), newTitle = span, picSmall = img, title = name)
                }
            title to list
        }
        emit(map)
    }.flowOn(ioDispatcher)

    fun getRecentUpdates() = Pager(PagingConfig(pageSize = 18)) {
        GeneralizedPagingSource("", remoteService)
    }

    fun getRecommend() = remoteService.getRecommend().flowOn(ioDispatcher)

    fun getSpacaptcha() = remoteService.getSpacaptcha().flowOn(ioDispatcher)

    fun login(
        username: String,
        password: String,
        captcha: String,
        key: String
    ) = remoteService.login(
        username, password, captcha, key, "login"
    ).flowOn(ioDispatcher)

    fun getCollects(
        userName: String,
        signT: Int,
        signK: Int
    ) = remoteService.getCollects(username = userName, signT = signT, signK = signK)
        .flowOn(ioDispatcher)

    fun getRankModel(year: String) = remoteService.getRankModel(year = year).flowOn(ioDispatcher)
}