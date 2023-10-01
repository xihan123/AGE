package cn.xihan.age.ui.main

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Settings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val remoteRepository: RemoteRepository
) : BaseViewModel<MainState, MainIntent>() {

    override fun initViewState(): MainState = MainState()

    private fun getReleaseLatest() = intent {
        remoteRepository
            .getReleaseLatest()
            .onEach {
                reduce {
                    state.copy(
                        updateTriple = it,
                        isUpdating = true
                    )
                }
            }
            .collect()
    }

    fun hideUpdateDialog() = intent {
        reduce {
            state.copy(
                isUpdating = false
            )
        }
    }

    init {
        if (Settings.autoCheckUpdate) {
            getReleaseLatest()
        }
        intent {
            delay(1000)
            reduce {
                state.copy(
                    isSplashScreenVisible = false
                )
            }
        }
    }

}

data class MainState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val isSplashScreenVisible: Boolean = false,
    val isUpdating: Boolean = false,
    val updateTriple: Triple<String, String, String> = Triple("", "", "")
) : IUiState

sealed class MainIntent : IUiIntent {
    data object SplashScreenDismiss : MainIntent()
}