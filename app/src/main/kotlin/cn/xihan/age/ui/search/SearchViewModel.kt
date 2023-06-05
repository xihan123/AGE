package cn.xihan.age.ui.search

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.CatalogModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug
import cn.xihan.age.util.extension.whatMutableIfNotNullOrEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/24 21:03
 * @介绍 :
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<SearchState, IUiIntent>() {

    override fun initViewState(): SearchState = SearchState()

    fun queryAnimeData(query: String) = intent {
        addHistory(query)
        reduce {
            state.copy(
                searchFlow = localRepository.getAnimeDataByQuery(query).flow.cachedIn(
                    viewModelScope
                )
            )
        }
    }

    private fun addHistory(query: String) = intent {
        if (query.isBlank()) return@intent
        localRepository.addHistory(query)
    }

    fun clearSearchHistory() = intent {
        localRepository.clearSearchHistory()
        reduce {
            state.copy(searchHistoryList = emptySet())
        }
    }

    init {
        intent {
            localRepository.historyListFlow.collect {
                reduce {
                    state.copy(searchHistoryList = it.orEmpty().toSet())
                }
            }
        }
    }

}


data class SearchState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val searchFlow: Flow<PagingData<CatalogModel.AniPreLModel>> = flowOf(PagingData.empty()),
    val searchHistoryList: Set<String> = emptySet(),
) : IUiState