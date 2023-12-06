package cn.xihan.age.paging


import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xihan.age.model.CommentResponseModel
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Settings
import cn.xihan.age.util.kJson
import cn.xihan.age.util.logError
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/24 15:43
 * @介绍 :
 */

class CommentPagingSource(
    private val okhttpClient: OkHttpClient,
    private val animeId: Int
) : PagingSource<Int, CommentResponseModel.DataModel.CommentModel>() {

    override fun getRefreshKey(state: PagingState<Int, CommentResponseModel.DataModel.CommentModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CommentResponseModel.DataModel.CommentModel> =
        try {
            val nextPageNumber = params.key ?: 1
            val response =
                "${Settings.API_BASE_URL}comment/$animeId?page=$nextPageNumber".await(okhttpClient)
            val model = response
                .takeIf { it.isSuccessful }
                ?.body
                ?.let {
                    kJson.decodeFromString<CommentResponseModel>(it.string())
                }
            if (model == null) {
                LoadResult.Error(AgeException.OnPageException(""))
            } else {
                val prevKey = if (nextPageNumber == 1) null else nextPageNumber.minus(1)
                val nextKey =
                    if (model.data.pagination.totalPage > nextPageNumber) nextPageNumber.plus(1) else null
                LoadResult.Page(
                    data = model.data.comments,
                    prevKey = prevKey,
                    nextKey = nextKey
                )
            }
        } catch (e: Exception) {
            logError("load", e)
            LoadResult.Error(e)
        }


}

suspend fun String.await(
    client: OkHttpClient,
    scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
): Response {
    // 创建一个请求对象，使用 this 关键字表示 URL 字符串
    val request = Request.Builder()
        .url(this)
        .build()
    // 创建一个 CompletableDeferred 对象，用于存储响应结果
    val deferred = CompletableDeferred<Response>()
    // 在协程作用域内执行异步请求
    return withContext(scope.coroutineContext) {
        // 使用 suspendCoroutine 函数，将回调函数转换为挂起函数
        suspendCoroutine { continuation ->
            // 执行异步请求，并注册一个回调函数
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: Response) {
                    // 请求成功，将响应结果恢复给 continuation 对象
                    continuation.resume(response)
                }

                override fun onFailure(call: Call, e: IOException) {
                    // 请求失败，将异常抛出给 continuation 对象
                    continuation.resumeWithException(e)
                }
            })
        }
    }
}
