package cn.xihan.age.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.util.RemoteService
import cn.xihan.age.util.logError
import kotlin.math.ceil

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/28 22:36
 * @介绍 :
 */
class GeneralizedPagingSource(
    private val key: String,
    private val remoteService: RemoteService
) : PagingSource<Int, AnimeModel>() {
    override fun getRefreshKey(state: PagingState<Int, AnimeModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, AnimeModel> =
        try {
            val nextPageNumber = params.key ?: 1
            val response = remoteService.getRecentUpdates(nextPageNumber, params.loadSize)
            val totalPage = ceil(response.total.toDouble() / params.loadSize.toDouble()).toInt()
            val prevKey = if (nextPageNumber == 1) null else nextPageNumber.minus(1)
            val nextKey = if (totalPage > nextPageNumber) nextPageNumber.plus(1) else null
            LoadResult.Page(
                data = response.videos,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            logError("load", e)
            LoadResult.Error(e)
        }


}