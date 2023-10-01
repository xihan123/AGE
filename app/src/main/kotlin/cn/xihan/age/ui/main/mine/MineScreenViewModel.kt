package cn.xihan.age.ui.main.mine

import cn.xihan.age.base.BaseViewModel
import cn.xihan.age.base.IUiIntent
import cn.xihan.age.base.IUiState
import cn.xihan.age.model.AlertDialogModel
import cn.xihan.age.model.SpacaptchaModel
import cn.xihan.age.model.UserDataModel
import cn.xihan.age.repository.LocalRepository
import cn.xihan.age.repository.RemoteRepository
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.Settings
import cn.xihan.age.util.error
import com.drake.channel.sendTag
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.syntax.simple.repeatOnSubscription
import javax.inject.Inject

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/29 16:59
 * @介绍 :
 */
@HiltViewModel
class MineScreenViewModel @Inject constructor(
    private val localRepository: LocalRepository,
    private val remoteRepository: RemoteRepository,
) : BaseViewModel<MineState, IUiIntent>() {

    override fun initViewState(): MineState = MineState()

    override fun showError(error: AgeException) {
        intent {
            reduce {
                state.copy(error = error)
            }
        }
    }

    override fun hideError() {
        intent {
            reduce {
                state.copy(error = null)
            }
        }
    }

    fun getSpacaptcha() = intent {
        reduce {
            state.copy(
                showLoginDialog = true,
                spacaptchaModel = null
            )
        }
        remoteRepository.getSpacaptcha()
            .error(this@MineScreenViewModel)
            .onEach {
                reduce {
                    state.copy(
                        spacaptchaModel = it
                    )
                }
            }
            .collect()
    }

    fun hideLoginDialog() = intent {
        reduce {
            state.copy(showLoginDialog = false)
        }
    }

    fun login(username: String, password: String, spacaptcha: String) = intent {
        if (state.spacaptchaModel == null) return@intent
        remoteRepository.login(username, password, spacaptcha, key = state.spacaptchaModel!!.key)
            .error(this@MineScreenViewModel)
            .onEach {
                it.takeIf { it.code == 200 }?.takeIf { it1 -> "登录成功" in it1.message }?.let { it1 ->
                    with(it1) {
                        localRepository.updateUser(data)
                        reduce {
                            state.copy(
                                userModel = data,
                                showLoginDialog = false
                            )
                        }

                    }
                }
                showError(
                    AgeException.AlertException(
                        AlertDialogModel(
                            title = "提示",
                            message = it.message
                        )
                    )
                )
            }
            .collect()
    }

    fun logout() = intent {
        localRepository.resetUser()
        reduce {
            state.copy(
                userModel = null
            )
        }
    }

    /**
     * 切换主题模式
     */
    fun changeThemeMode() =
        changeThemeMode(if (Settings.themeMode == 1) 2 else 1)

    fun changeThemeMode(followSystem: Boolean) =
        changeThemeMode(if (followSystem) 3 else 2)

    fun changeHideUserName(hideUserName: Boolean) = intent {
        Settings.hideUserName = hideUserName
        reduce {
            state.copy(
                hideUserName = hideUserName
            )
        }
    }

    fun changeAutoCheckUpdates(autoCheckUpdate: Boolean) = intent {
        Settings.autoCheckUpdate = autoCheckUpdate
        reduce {
            state.copy(
                autoCheckUpdate = autoCheckUpdate
            )
        }
    }

    private fun changeThemeMode(themeMode: Int) = intent {
        Settings.themeMode = themeMode
        reduce {
            state.copy(themeMode = themeMode)
        }
        sendTag("changeThemeMode")
    }

    init {
        intent {
            repeatOnSubscription {
                localRepository.userFlow
                    .onEach {
                        reduce {
                            state.copy(
                                userModel = it
                            )
                        }
                    }
                    .collect()
            }
        }
    }
}

data class MineState(
    override var loading: Boolean = false,
    override var refreshing: Boolean = false,
    override var error: AgeException? = null,
    val spacaptchaModel: SpacaptchaModel? = null,
    var userModel: UserDataModel? = null,
    var showLoginDialog: Boolean = false,
    var themeMode: Int = Settings.themeMode,
    var hideUserName: Boolean = Settings.hideUserName,
    var autoCheckUpdate: Boolean = Settings.autoCheckUpdate,
) : IUiState
