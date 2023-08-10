package cn.xihan.age.util.extension

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/5/5 15:18
 * @介绍 :
 */
interface IMMKVOwner {
    val mmapID: String
    val kv: MMKV
}

open class MMKVOwner(override val mmapID: String) : IMMKVOwner {
    override val kv: MMKV by lazy { MMKV.mmkvWithID(mmapID) }
}

fun IMMKVOwner.mmkvInt(default: Int = 0) =
    MMKVProperty({ kv.decodeInt(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvLong(default: Long = 0L) =
    MMKVProperty({ kv.decodeLong(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBool(default: Boolean = false) =
    MMKVProperty({ kv.decodeBool(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvFloat(default: Float = 0f) =
    MMKVProperty({ kv.decodeFloat(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvDouble(default: Double = 0.0) =
    MMKVProperty({ kv.decodeDouble(it, default) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvString() =
    MMKVProperty({ kv.decodeString(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvString(default: String) =
    MMKVProperty({ kv.decodeString(it) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvStringSet() =
    MMKVProperty({ kv.decodeStringSet(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvStringSet(default: Set<String>) =
    MMKVProperty({ kv.decodeStringSet(it) ?: default }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBytes() =
    MMKVProperty({ kv.decodeBytes(it) }, { kv.encode(first, second) })

fun IMMKVOwner.mmkvBytes(default: ByteArray) =
    MMKVProperty({ kv.decodeBytes(it) ?: default }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable() =
    MMKVProperty({ kv.decodeParcelable(it, T::class.java) }, { kv.encode(first, second) })

inline fun <reified T : Parcelable> IMMKVOwner.mmkvParcelable(default: T) =
    MMKVProperty(
        { kv.decodeParcelable(it, T::class.java) ?: default },
        { kv.encode(first, second) })

class MMKVProperty<V>(
    private val decode: (String) -> V,
    private val encode: Pair<String, V>.() -> Boolean,
    var key: String? = null
) : ReadWriteProperty<IMMKVOwner, V> {
    override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): V =
        decode(key ?: property.name)

    override fun setValue(thisRef: IMMKVOwner, property: KProperty<*>, value: V) {
        encode((key ?: property.name) to value)
    }
}
