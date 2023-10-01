package cn.xihan.age.util

import android.os.Parcelable
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty1

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/5/5 15:19
 * @介绍 :
 */
class SpUtil(name: String = "") {

    val mmkv: MMKV = MMKV.mmkvWithID(name.ifBlank { "default" })

    inline fun <reified T> encode(key: String, value: T) = when (value) {
        is String -> mmkv.encode(key, value)
        is Float -> mmkv.encode(key, value)
        is Boolean -> mmkv.encode(key, value)
        is Int -> mmkv.encode(key, value)
        is Long -> mmkv.encode(key, value)
        is Double -> mmkv.encode(key, value)
        is ByteArray -> mmkv.encode(key, value)
        is Parcelable -> mmkv.encode(key, value)
        else -> mmkv.encode(key, Json.encodeToString(value))
    }

    fun encode(key: String, sets: Set<String>?) = sets?.let { mmkv.encode(key, sets) } ?: false

    inline fun <reified T> encode(key: String, sets: List<T>) =
        mmkv.encode(key, Json.encodeToString(sets))

    inline fun <reified T> decode(key: String, default: T? = null): T? = when (T::class) {
        String::class -> mmkv.decodeString(key, default as? String ?: "") as? T
        Float::class -> mmkv.decodeFloat(key, default as? Float ?: 0F) as? T
        Boolean::class -> mmkv.decodeBool(key, default as? Boolean ?: false) as? T
        Int::class -> mmkv.decodeInt(key, default as? Int ?: 0) as? T
        Long::class -> mmkv.decodeLong(key, default as? Long ?: 0L) as? T
        Double::class -> mmkv.decodeDouble(key, default as? Double ?: 0.00) as? T
        ByteArray::class -> mmkv.decodeBytes(key) as? T
        else -> runCatching {
            mmkv.decodeString(key, "")?.let { Json.decodeFromString<T>(it) }
        }.getOrNull()
    }

    inline fun <reified T : Parcelable> decode(key: String, default: T? = null): T? =
        mmkv.decodeParcelable(key, T::class.java)

//    inline fun <reified T> decodeList(key: String): MutableList<T> = runCatching {
//        val list = mutableListOf<T>()
//        val strJson = decodeString(key)
//        if (strJson.isNotBlank()) {
//            list.addAll(kJson.decodeFromString(strJson))
//        }
//        list
//    }.getOrElse {
//        mutableListOf()
//    }

    fun decodeInt(key: String, default: Int = 0): Int = mmkv.decodeInt(key, default)


    fun decodeDouble(key: String, default: Double = 0.00): Double =
        mmkv.decodeDouble(key, default)


    fun decodeLong(key: String, default: Long = 0L): Long =
        mmkv.decodeLong(key, default)


    fun decodeBoolean(key: String, default: Boolean = false): Boolean =
        mmkv.decodeBool(key, default)


    fun decodeFloat(key: String, default: Float = 0F): Float =
        mmkv.decodeFloat(key, default)


    fun decodeByteArray(key: String, default: ByteArray = ByteArray(0)): ByteArray? =
        mmkv.decodeBytes(key)


    fun decodeString(key: String, default: String = ""): String =
        mmkv.decodeString(key, default) ?: default


    inline fun <reified T : Parcelable> decodeParcelable(key: String, tClass: Class<T>): T? =
        mmkv.decodeParcelable(key, tClass)


//    inline fun <reified T : Parcelable> decodeParcelableNotNull(key: String, tClass: Class<T>): T =
//        mmkv.decodeParcelable(key, tClass) ?: T::class.java.newInstance()


    fun decodeStringSet(
        key: String,
        default: MutableSet<String> = mutableSetOf(),
    ): MutableSet<String> =
        mmkv.decodeStringSet(key, mutableSetOf()) ?: default


    fun removeKey(key: String) = mmkv.removeValueForKey(key)

    fun clearAll() = mmkv.clearAll()

}

val kJson = Json {
    encodeDefaults = true
    isLenient = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    prettyPrint = false
    useArrayPolymorphism = false
    ignoreUnknownKeys = true // JSON和数据模型字段可以不匹配
    coerceInputValues = true // 如果JSON字段是Null则使用默认值
}

/**
 * 将对象转为JSON字符串
 */
fun Any?.toJson(): String = kJson.encodeToString(this)

/**
 * 将JSON字符串转为对象
 */
inline fun <reified T> String.toObject(): T = kJson.decodeFromString(this)

/**
 * 批处理
 */
fun SpUtil.encodes(action: SpUtil.() -> Unit) {
    action()
}

interface IMMKVOwner {
    val mmapID: String
    val kv: MMKV
    fun clearAllKV() = kv.clearAll()
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

fun <V> MMKVProperty<V>.asFlow() = object : ReadOnlyProperty<IMMKVOwner, Flow<V>> {
    private var cache: Flow<V>? = null


    override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): Flow<V> =
        cache ?: flow {
            emit(this@asFlow.getValue(thisRef, property))
        }.also { cache = it }


}

inline fun <reified R : KProperty1<*, *>> Collection<*>.filerProperties(vararg exceptNames: String): List<R> =
    ArrayList<R>().also { destination ->
        for (element in this) if (element is R && !exceptNames.contains(element.name)) destination.add(
            element
        )
    }

class MMKVProperty<V>(
    private val decode: (String) -> V,
    private val encode: Pair<String, V>.() -> Boolean
) : ReadWriteProperty<IMMKVOwner, V> by object : ReadWriteProperty<IMMKVOwner, V> {
    override fun getValue(thisRef: IMMKVOwner, property: KProperty<*>): V =
        decode(property.name)

    override fun setValue(thisRef: IMMKVOwner, property: KProperty<*>, value: V) {
        encode((property.name) to value)
    }
}
