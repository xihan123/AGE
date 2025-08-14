package cn.xihan.age.ui.search

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.SearchModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/20 20:54
 * @介绍 :
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<SearchState, IUiIntent>() {

    override fun initViewState(): SearchState = SearchState()

    fun fetchAnimeResults(keyword: String) = intent {
        reduce {
            state.copy(
                searchPagingData = remoteRepository.getSearchModel(keyword).flow.cachedIn(
                    viewModelScope
                ),
                refreshing = false,
                loading = false
            )
        }
    }

    fun addHistory(query: String) = intent {
        if (query.isBlank()) return@intent
        localRepository.addHistory(query)
    }

    fun clearSearchHistory() = intent {
        localRepository.clearSearchHistory()
        reduce {
            state.copy(searchHistoryList = emptySet())
        }
    }

    fun updateSearchText(keyword: String = "") = intent {
        reduce {
            state.copy(searchText = keyword)
        }
    }

    fun queryFavorite(animeId: Int) = localRepository.queryFavorite(animeId)

    fun updateAnimeFavorites(anime: SearchModel.DataModel.VideoModel, favorite: Boolean) = intent {
        with(anime) {
            localRepository.upsertFavoriteModel(
                animeId = id,
                animeName = name,
                animeCover = cover,
                animeSubtitle = uptodate
            )
            localRepository.updateFavorite(id, favorite)
        }
    }

    init {
        intent {
            repeatOnSubscription {
                localRepository.historyListFlow.collect {
                    reduce {
                        state.copy(searchHistoryList = it.orEmpty().toSet())
                    }
                }
            }
        }
    }

}

data class SearchState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val searchHistoryList: Set<String> = emptySet(),
    val favorites: List<Int> = emptyList(),
    val searchPagingData: Flow<PagingData<SearchModel.DataModel.VideoModel>> = flowOf(PagingData.empty()),
    var searchText: String = ""
) : IUiState