package cn.xihan.age.ui.rank

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.RankModel
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.error
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach

import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/30 20:47
 * @介绍 :
 */
@HiltViewModel
class RankingViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : BaseViewModel<RankingState, IUiIntent>() {

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
                loading = true,
                error = null
            )
        }
        remoteRepository.getRankModel(year = state.year)
            .error(this@RankingViewModel)
            .onEach {
                reduce {
                    state.copy(
                        loading = false,
                        refreshing = false,
                        error = null,
                        rankingList = it.rank
                    )
                }
            }
            .collect()
    }

    /**
     * 更改年份
     */
    fun changeYear(year: String) = intent {
        if (isFastClick()) return@intent
        val copyYear = if (year == "全部") "all" else year.replace("以前", "")
        reduce {
            state.copy(
                year = copyYear
            )
        }
        getRankingModel()
    }

    init {
        getRankingModel()
    }


}

data class RankingState(
    override var loading: Boolean = true,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val year: String = "",
    val rankingList: List<List<RankModel>> = emptyList(),
) : IUiState

