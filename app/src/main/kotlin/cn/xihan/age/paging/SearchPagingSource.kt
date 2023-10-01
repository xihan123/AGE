package cn.xihan.age.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xihan.age.model.SearchModel
import cn.xihan.age.util.RemoteService
import cn.xihan.age.util.logError

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/21 21:27
 * @介绍 :
 */
class SearchPagingSource(
    private val remoteService: RemoteService,
    private val searchText: String
) : PagingSource<Int, SearchModel.DataModel.VideoModel>() {
    override fun getRefreshKey(state: PagingState<Int, SearchModel.DataModel.VideoModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchModel.DataModel.VideoModel> =
        try {
            val nextPageNumber = params.key ?: 1
            val response = remoteService.getSearchModel(searchText, nextPageNumber)
            val totalPage = response.data.totalPage
            val prevKey = if (nextPageNumber == 1) null else nextPageNumber.minus(1)
            val nextKey = if (totalPage > nextPageNumber) nextPageNumber.plus(1) else null
            LoadResult.Page(
                data = response.data.videos,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            logError("load", e)
            LoadResult.Error(e)
        }


}