package cn.xihan.age.ui.main.recommend

import android.content.Context
import cn.xihan.age.R
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.RecommendModel
import cn.xihan.age.network.SPSettings
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.whatMutableIfNotNullOrEmpty
import com.hadiyarajesh.flower_core.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/4/10 5:54
 * @介绍 :
 */
@HiltViewModel
class RecommendViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<RecommendState, IUiIntent>() {

    override fun initViewState(): RecommendState = RecommendState()

    fun getRecommendModel() = intent {
        remoteRepository.getRecommendModel(size = 100).collect { recommendModel ->
            reduce {
                state.copy(
                    loading = false,
                    refreshing = false,
                    error = null,
                    currentRecommendListData = recommendModel.aniPre
                )
            }
        }
    }

    init {
        getRecommendModel()
    }
}

data class RecommendState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    var currentRecommendListData: List<RecommendModel.AniPreModel> = emptyList(),
) : IUiState
