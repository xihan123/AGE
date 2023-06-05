@file:Suppress("unused")

package cn.xihan.age.util.extension

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.cancellation.CancellationException

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/5/1 12:37
 * @介绍 :
 */
inline fun <T> Flow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit,
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

inline fun <T> SharedFlow<T>.launchAndCollectIn(
    owner: LifecycleOwner,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    crossinline action: suspend CoroutineScope.(T) -> Unit,
) = owner.lifecycleScope.launch {
    owner.repeatOnLifecycle(minActiveState) {
        collect {
            action(it)
        }
    }
}

fun <T> Throwable.asFlow(): Flow<T> = flow {
    emit(suspendCancellableCoroutine { cancellableContinuation ->
        cancellableContinuation.cancel(this@asFlow)
    })
}
/*
@Composable
fun <T : R, R> Flow<T>.collectAsStateWithLifecycle(
    initial: R,
    context: CoroutineContext = EmptyCoroutineContext
): State<R> {
    val lifecycleOwner = checkNotNull(LocalLifecycleOwner.current)
    val flow = remember(this, lifecycleOwner) {
        flowWithLifecycle(lifecycleOwner.lifecycle)
    }
    return flow.collectAsState(initial = initial, context = context)
}

 */

/**
 * 创建一个输出 [Flow]，它按顺序发出第一个给定 [Flow] 的所有值，然后移动到下一个。
 */
fun <T> concat(flow1: Flow<T>, flow2: Flow<T>): Flow<T> = flow {
    emitAll(flow1)
    emitAll(flow2)
}

/**
 * 创建一个输出 [Flow]，它按顺序发出第一个给定 [Flow] 的所有值，然后移动到下一个。
 */
fun <T> concat(flows: Iterable<Flow<T>>): Flow<T> {
    return flow { flows.forEach { emitAll(it) } }
}

/**
 * 返回一个 [Flow]，它在开始发出由当前 [Flow] 发出的项目之前发出指定的项目。
 */
fun <T> Flow<T>.startWith(others: Iterable<T>): Flow<T> = concat(others.asFlow(), this)

fun <T> MutableStateFlow<T>.set(block: T.() -> T) {
    this.value = this.value.block()
}

class FlowDebouncer<T>(timeoutMillis: Long) : Flow<T> {

    private val sourceChannel: Channel<T> = Channel(capacity = 1)

    @OptIn(FlowPreview::class)
    private val flow: Flow<T> = sourceChannel.consumeAsFlow().debounce(timeoutMillis)

    suspend fun put(item: T) {
        sourceChannel.send(item)
    }

    @InternalCoroutinesApi
    override suspend fun collect(collector: FlowCollector<T>) {
        flow.collect(collector)
    }

}




