package cn.xihan.age.ui.main.home

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.CustomHomeModel
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logDebug
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/23 19:16
 * @介绍 :
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : BaseViewModel<HomeState, IUiIntent>() {

    override fun initViewState(): HomeState = HomeState()

    fun getCustomHomeModel() = intent {
        showLoading()
        remoteRepository.getHomeModel()
            .collect {
                reduce {
                    state.copy(
                        loading = false,
                        refreshing = false,
                        currentCustomHomeModel = it
                    )
                }
            }
    }

    fun showLoading() {
        intent {
            reduce {
                state.copy(
                    loading = true,
                    refreshing = true
                )
            }
        }
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
                    error = null
                )
            }
        }
    }

    init {
        getCustomHomeModel()
    }

}


data class HomeState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val currentCustomHomeModel: CustomHomeModel? = null,
) : IUiState
