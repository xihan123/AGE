package cn.xihan.age.ui.main.ranking

import android.content.Context
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.RankModel
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
 * @创建时间 : 2023/4/10 6:26
 * @介绍 :
 */
@HiltViewModel
class RankingViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    savedStateHandle: SavedStateHandle,
    private val remoteRepository: RemoteRepository
) : BaseViewModel<RankingState, IUiIntent>() {

    var year: String = savedStateHandle["year"] ?: "all"

    override fun initViewState(): RankingState = RankingState()

    /**
     * 1秒内不可重复执行
     */
    private var lastClickTime = 0L

    /**
     * 获取 * 秒内不可重复执行
     * @param time 间隔时间
     */
    fun isFastClick(
        time: Long = 500,
    ): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeD = currentTime - lastClickTime
        if (timeD in 1 until time) {
            return true
        }

        lastClickTime = currentTime
        return false
    }

    fun getRankingModel() = intent {
        reduce {
            state.copy(
                currentRankingListData = remoteRepository.getRankModel(
                    year = year
                ).flow.cachedIn(
                    viewModelScope
                )
            )
        }

    }

    /**
     * 更改年份
     */
    fun changeYear(year: String) {
        if (isFastClick()) {
            return
        }
        this.year = if (year == "全部") "all" else year
        getRankingModel()
    }

    init {
        logDebug("year: $year")
        getRankingModel()
    }

}

data class RankingState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val currentRankingModel: RankModel? = null,
    var currentRankingListData: Flow<PagingData<RankModel.AniRankPreModel>> = flowOf(PagingData.empty())
) : IUiState
