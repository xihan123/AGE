package cn.xihan.age.base

import androidx.annotation.Keep
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.xihan.age.util.AgeException
import cn.xihan.age.util.logError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container

/**
 * @项目名 : age-anime
 * @作者 : MissYang
 * @创建时间 : 2023/9/17 16:58
 * @介绍 :
 */
@Keep
sealed interface LoadingIntent : IUiIntent {
    data object IDLE : LoadingIntent
    data object LOADING : LoadingIntent
    data class FAILURE(val error: AgeException) : LoadingIntent
}

@Keep
interface IUiState {
    var loading: Boolean
    var refreshing: Boolean
    var error: AgeException?
}


@Keep
interface IUiIntent

/**
 * BaseViewModel is an abstract class that implements the MVI pattern for ViewModels.
 *
 * @param S Generic type that must extend IUiState to represent UI state
 * @param I Generic type that must extend IUiIntent for intents
 *
 * Contains:
 * - coroutineExceptionHandler - Handles exceptions in coroutines
 * - container - Holds the MVI container for state and intents
 * - initViewState() - Abstract function to provide initial state, must be implemented
 * - hideError() - Can override to hide errors
 * - showError() - Can override to show errors
 * - mainLaunch() - Launches coroutine on main thread
 * - ioLaunch() - Launches coroutine on IO thread
 *
 * All coroutines will use the exception handler.
 */

abstract class BaseViewModel<S : IUiState, I : IUiIntent> : ContainerHost<S, I>, ViewModel() {

    protected val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        logError(exception.message)
        val errorResponse = if (exception is AgeException) {
            exception
        } else {
            AgeException.SnackBarException(message = exception.message ?: "未知错误")
        }
        showError(errorResponse)
    }

    override val container: Container<S, I> by lazy {
        container(
            initialState = initViewState(),
            buildSettings = {
                exceptionHandler = coroutineExceptionHandler
            }
        )
    }

    abstract fun initViewState(): S

    open fun hideError() {}

    open fun showError(error: AgeException) {}

    /**
     * 主线程执行
     */
    fun mainLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.Main + coroutineExceptionHandler) {
            block.invoke(this)
        }
    }

    /**
     * IO线程执行
     */
    fun ioLaunch(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            block.invoke(this)
        }
    }

}