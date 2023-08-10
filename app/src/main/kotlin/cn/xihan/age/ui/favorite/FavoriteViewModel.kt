package cn.xihan.age.ui.favorite

import android.content.Context
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.FavoriteModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug
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
 * @创建时间 : 2023/4/12 13:09
 * @介绍 :
 */
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<FavoriteState, IUiIntent>() {

    override fun initViewState(): FavoriteState = FavoriteState()

    /**
     * 查询本地收藏
     */
    fun queryLocalFavorite() = intent {
        reduce {
            state.copy(
                localFavoriteList = localRepository.queryCollectModelLocal(state.localFavoriteType).flow.cachedIn(
                    viewModelScope
                )
            )
        }
    }

    /**
     * 设置本地收藏排序类型
     */
    fun setLocalFavoriteType(type: Int) = intent {
        reduce {
            state.copy(
                localFavoriteType = type
            )
        }
        queryLocalFavorite()
    }

    fun cancelLocalFavoriteByAnimeId(animeId: Int) = intent {
        localRepository.updateFavorite(animeId = animeId, favorite = false)
    }

    fun setAllFavorite(favorite: Boolean) = intent {
        logDebug("设置全部为收藏: ${localRepository.updateAllFavorite(favorite = favorite)}")
    }

    init {
        queryLocalFavorite()
    }

}

data class FavoriteState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
//    val userInfoModel: UserInfoModel? = null,
    val localFavoriteType: Int = 0,
//    val netFavoriteType: Int = 0,
//    val netFavoriteList: Flow<PagingData<CollectModel.DataDTO.ListDTO>> = flowOf(PagingData.empty()),
//    val netFavoriteList: Flow<PagingData<FavoriteModel>> = flowOf(PagingData.empty()),
    val localFavoriteList: Flow<PagingData<FavoriteModel>> = flowOf(PagingData.empty())
) : IUiState