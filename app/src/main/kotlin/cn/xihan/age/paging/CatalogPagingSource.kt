package cn.xihan.age.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.network.RemoteService
import cn.xihan.age.network.SPSettings
import cn.xihan.age.util.extension.logError
import kotlin.math.ceil

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/4 5:32
 * @介绍 :
 */
class CatalogPagingSource(
    private val remoteService: RemoteService,
    private val filterList: Map<String, String>
) : PagingSource<Int, CatalogModel.AniPreLModel>() {

    override fun getRefreshKey(state: PagingState<Int, CatalogModel.AniPreLModel>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, CatalogModel.AniPreLModel> =
        try {
            val nextPageNumber = params.key ?: 1
            val response = remoteService.getCategoryModel(
                type = filterList,
                page = nextPageNumber,
                size = 24
            )
            val list = response.aniPreL.toMutableList()
            val pageCount = ceil(response.allCnt.toDouble() / 24.toDouble()).toInt()
            val prevKey = if (nextPageNumber == 1) null else nextPageNumber - 1
            val nextKey = if (pageCount > nextPageNumber) nextPageNumber.plus(1) else null
            LoadResult.Page(
                data = list,
                prevKey = prevKey,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            logError("load", e)
            LoadResult.Error(e)
        }
}