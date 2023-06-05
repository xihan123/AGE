@file:JvmMultifileClass
@file:OptIn(ExperimentalContracts::class)

package cn.xihan.age.util.extension


import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/26 17:17
 * @介绍 :
 */

@JvmSynthetic
inline fun <T> List<T>?.whatMutableIfNotNullOrEmpty(
    whatIf: (MutableList<T>) -> Unit,
    whatIfNot: () -> Unit
): List<T>? {
    contract {
        callsInPlace(whatIf, InvocationKind.AT_MOST_ONCE)
        callsInPlace(whatIfNot, InvocationKind.AT_MOST_ONCE)
    }
    if (!this.isNullOrEmpty()) {
        whatIf(this.toMutableList())
    } else {
        whatIfNot()
    }
    return this
}

@OptIn(ExperimentalContracts::class)
@JvmSynthetic
inline fun <T> List<T>?.whatMutableIfNotNullOrEmpty(
    whatIf: (MutableList<T>) -> Unit
): List<T>? {
    contract {
        callsInPlace(whatIf, InvocationKind.AT_MOST_ONCE)
    }
    if (!this.isNullOrEmpty()) {
        whatIf(this.toMutableList())
    }
    return this
}