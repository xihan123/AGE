@file:JvmName("Thread")

package cn.xihan.age.util.extension

import android.os.Looper
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.Closeable
import java.util.concurrent.atomic.AtomicReference
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/2/7 20:57
 * @介绍 :
 */


// 保存 CoroutineScope
private var scopeRef: AtomicReference<Any> = AtomicReference()

// 自定义的 CoroutineScope
val appGlobalScope: CoroutineScope
    get() {
        while (true) {
            val existing = scopeRef.get() as CoroutineScope?
            if (existing != null) {
                return existing
            }
            val newScope = SafeCoroutineScope(Dispatchers.Main.immediate)
            if (scopeRef.compareAndSet(null, newScope)) {
                return newScope
            }
        }
    }

// 不会崩溃的 CoroutineScope
private class SafeCoroutineScope(context: CoroutineContext) : CoroutineScope, Closeable {
    override val coroutineContext: CoroutineContext =
        SupervisorJob() + context + UncaughtCoroutineExceptionHandler()

    override fun close() {
        coroutineContext.cancelChildren()
    }
}

// 自定义 CoroutineExceptionHandler
private class UncaughtCoroutineExceptionHandler : CoroutineExceptionHandler,
    AbstractCoroutineContextElement(CoroutineExceptionHandler) {
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        // 处理异常
    }
}


val isOnMainThread: Boolean
    get() = Looper.myLooper() == Looper.getMainLooper()

fun mainThread(timeMillis: Long = 0L, block: () -> Unit) {
    appGlobalScope.launch(Dispatchers.Main) {
        if (timeMillis != 0L) {
            delay(timeMillis = timeMillis)
        }
        block.invoke()
    }
}

/**
 * 非挂起函数运行主线程并返回引用
 */
inline fun <reified T> mainThreadForResult(timeMillis: Long = 0L, crossinline block: () -> T): T {
    return runBlocking(Dispatchers.Main) {
        if (timeMillis != 0L) {
            delay(timeMillis = timeMillis)
        }
        block.invoke()
    }
}


fun thread(timeMillis: Long = 0L, block: suspend () -> Unit) {
    appGlobalScope.launch(Dispatchers.IO) {
        if (timeMillis != 0L) {
            delay(timeMillis = timeMillis)
        }
        block.invoke()
    }
}



