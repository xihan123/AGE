package cn.xihan.age.ui.generalized

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AnimeModel
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.model.HistoryModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.error
import cn.xihan.age.util.logDebug
import cn.xihan.age.util.nullList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/28 22:46
 * @介绍 :
 */
@HiltViewModel
class GeneralizedViewModel @Inject constructor(
    stateHandle: SavedStateHandle,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<HomeGeneralizedState, IUiIntent>() {

    private val key: String = stateHandle.get<String>("key") ?: ""

    override fun initViewState(): HomeGeneralizedState = HomeGeneralizedState()

    fun getRecentUpdates() = intent {
        reduce {
            state.copy(
                loading = false,
                refreshing = false,
                error = null,
                recentUpdates = remoteRepository.getRecentUpdates().flow.cachedIn(viewModelScope)
            )
        }
    }

    fun getRecommend() = intent {
        reduce {
            state.copy(
                recommendList = nullList(18)
            )
        }
        remoteRepository.getRecommend()
            .error(this@GeneralizedViewModel)
            .onEmpty {
                reduce {
                    state.copy(
                        loading = false,
                        refreshing = false,
                        error = null,
                        recommendList = emptyList()
                    )
                }
            }
            .onEach {
                reduce {
                    state.copy(
                        loading = false,
                        refreshing = false,
                        error = null,
                        recommendList = it.videos
                    )
                }
            }
            .collect()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getCollects() = intent {
        localRepository
            .userFlow
            .flatMapConcat { userModel ->
                remoteRepository.getCollects(
                    userName = userModel.username,
                    signT = userModel.signT,
                    signK = userModel.signK
                )
            }
            .error(this@GeneralizedViewModel)
            .onEach {
                reduce {
                    state.copy(
                        loading = false,
                        refreshing = false,
                        error = null,
                        collectList = it.collects
                    )
                }
            }
            .collect()
    }

    fun queryFavorite(type: Int = 0) = intent {
        reduce {
            state.copy(
                loading = false,
                refreshing = false,
                error = null,
                localFavorites = localRepository
                    .queryCollectModelLocal(type).flow.cachedIn(viewModelScope)
            )
        }
    }

    fun changeLocalFavoriteType(type: Int) = queryFavorite(type)

    fun changeAllFavoriteState(state: Boolean) = intent {
        localRepository.updateAllFavorite(favorite = state)
        logDebug("设置全部为收藏: $state")
    }

    fun queryHistory(type: Int = 0) = intent {
        reduce {
            state.copy(
                loading = false,
                refreshing = false,
                error = null,
                historys = localRepository.queryHistoryPaging(type).flow.cachedIn(viewModelScope)
            )
        }
    }

    /**
     * 修改历史记录排序状态
     */
    fun changeHistorySortState(sortType: Int = 0) = queryHistory(sortType)

    /**
     * 根据 animeId 删除播放历史记录
     */
    fun deleteHistoryByAnimeId(animeId: Int, isAll: Boolean = false) = intent {
        if (animeId == 0 && isAll) {
            localRepository.deleteAllHistory()
        } else {
            localRepository.deleteHistoryByAnimeId(animeId)
        }
        queryHistory()
    }

    override fun showError(error: AgeException) {
        intent {
            reduce {
                state.copy(
                    loading = false,
                    refreshing = false,
                    error = error
                )
            }
        }
    }

    override fun hideError() {
        intent {
            reduce {
                state.copy(
                    loading = false,
                    refreshing = false,
                    error = null
                )
            }
        }
    }

    init {
        when (key) {
            "最近更新" -> getRecentUpdates()
            "每日推荐" -> getRecommend()
            "网络收藏" -> getCollects()
            "本地收藏" -> queryFavorite()
            "历史记录" -> queryHistory()
            else -> showError(AgeException.SnackBarException("key错误"))
        }
    }

}

data class HomeGeneralizedState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val localFavorites: Flow<PagingData<FavoriteModel>> = flowOf(PagingData.empty()),
    val historys: Flow<PagingData<HistoryModel>> = flowOf(PagingData.empty()),
    val recentUpdates: Flow<PagingData<AnimeModel>> = flowOf(PagingData.empty()),
    val recommendList: List<AnimeModel?> = nullList<AnimeModel>(18),
    val collectList: List<AnimeModel?> = nullList<AnimeModel>(18)
) : IUiState