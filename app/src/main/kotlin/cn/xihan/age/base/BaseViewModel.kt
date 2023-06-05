package cn.xihan.age.base

import androidx.annotation.Keep
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cn.xihan.age.util.extension.AgeException
import cn.xihan.age.util.extension.logError
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/25 0:14
 * @介绍 :
 */
@Keep
interface IUiState {
    var loading: Boolean
    var refreshing: Boolean
    var error: AgeException?
}

@Keep
interface IUiIntent

@Keep
interface IViewModel<S : IUiState, I : IUiIntent> : ContainerHost<S, I> {
    fun initViewState(): S
}

abstract class BaseViewModel<S : IUiState, I : IUiIntent> : IViewModel<S, I>, ViewModel(){

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, exception ->
        logError(exception.message)
        val errorResponse = if (exception is AgeException) {
            exception
        } else {
            AgeException.SnackBarException(message = exception.message ?: "未知错误")
        }
        showError(errorResponse)
    }

    override val container: Container<S, I> =
        container(
            initialState = initViewState(),
            buildSettings = {
                exceptionHandler = coroutineExceptionHandler
            }
        )

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


