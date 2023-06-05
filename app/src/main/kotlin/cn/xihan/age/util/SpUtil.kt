package cn.xihan.age.util

import android.content.Context
import android.content.SharedPreferences
import android.os.Parcelable
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import cn.xihan.age.BuildConfig
import cn.xihan.age.util.extension.application
import com.tencent.mmkv.MMKV
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/5/5 15:19
 * @介绍 :
 */
class SpUtil(name: String = "") {

    var mmkv: MMKV? = null

    init {
        mmkv = if (name.isBlank()) {
            MMKV.defaultMMKV()
        } else {
            MMKV.mmkvWithID(name)
        }
    }

    fun encode(key: String, value: Any?) = when (value) {
        is String -> mmkv?.encode(key, value) ?: false
        is Float -> mmkv?.encode(key, value) ?: false
        is Boolean -> mmkv?.encode(key, value) ?: false
        is Int -> mmkv?.encode(key, value) ?: false
        is Long -> mmkv?.encode(key, value) ?: false
        is Double -> mmkv?.encode(key, value) ?: false
        is ByteArray -> mmkv?.encode(key, value) ?: false
        is Parcelable -> mmkv?.encode(key, value) ?: false
        else -> false
    }

    fun encode(key: String, sets: Set<String>?) = sets?.let { mmkv?.encode(key, sets) } ?: false

    inline fun <reified T> encode(key: String, sets: List<T>) =
        mmkv?.encode(key, Json.encodeToString(sets)) ?: false

    inline fun <reified T> decodeList(key: String): MutableList<T> = runCatching {
        val list = mutableListOf<T>()
        val strJson = decodeString(key)
        if (strJson.isNotBlank()) {
            list.addAll(kJson.decodeFromString(strJson))
        }
        list
    }.getOrElse {
        mutableListOf()
    }

    fun decodeInt(key: String, default: Int = 0): Int = mmkv?.decodeInt(key, default) ?: default


    fun decodeDouble(key: String, default: Double = 0.00): Double =
        mmkv?.decodeDouble(key, default) ?: default


    fun decodeLong(key: String, default: Long = 0L): Long =
        mmkv?.decodeLong(key, default) ?: default


    fun decodeBoolean(key: String, default: Boolean = false): Boolean =
        mmkv?.decodeBool(key, default) ?: default


    fun decodeFloat(key: String, default: Float = 0F): Float =
        mmkv?.decodeFloat(key, default) ?: default


    fun decodeByteArray(key: String, default: ByteArray = ByteArray(0)): ByteArray =
        mmkv?.decodeBytes(key) ?: default


    fun decodeString(key: String, default: String = ""): String =
        mmkv?.decodeString(key, default) ?: default


    inline fun <reified T : Parcelable> decodeParcelable(key: String, tClass: Class<T>): T? =
        mmkv?.decodeParcelable(key, tClass)// ?: T::class.java.newInstance()


    inline fun <reified T : Parcelable> decodeParcelableNotNull(key: String, tClass: Class<T>): T =
        mmkv?.decodeParcelable(key, tClass) ?: T::class.java.newInstance()


    fun decodeStringSet(
        key: String,
        default: MutableSet<String> = mutableSetOf(),
    ): MutableSet<String> =
        mmkv?.decodeStringSet(key, mutableSetOf()) ?: default


    fun removeKey(key: String) = mmkv?.removeValueForKey(key)


    fun clearAll() = mmkv?.clearAll()


}

val kJson = Json {
    ignoreUnknownKeys = true
    isLenient = true
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

/**
 * 全局 SharedPreferences 工具类
 */
class SharedPreferencesUtil(name: String = "") {

    private var prefs: SharedPreferences? = null

    init {
        prefs = if (name.isBlank()) {
            application.applicationContext.getSharedPreferences(
                "${BuildConfig.APPLICATION_ID}_preferences", Context.MODE_PRIVATE
            )
        } else {
            application.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
    }

    fun encode(key: String, value: Any?) = when (value) {
        is String -> prefs?.edit()?.putString(key, value)?.commit() ?: false
        is Float -> prefs?.edit()?.putFloat(key, value)?.commit() ?: false
        is Boolean -> prefs?.edit()?.putBoolean(key, value)?.commit() ?: false
        is Int -> prefs?.edit()?.putInt(key, value)?.commit() ?: false
        is Long -> prefs?.edit()?.putLong(key, value)?.commit() ?: false
        is Set<*> -> prefs?.edit()?.putStringSet(key, value as MutableSet<String>)?.commit()
            ?: false

        else -> false
    }

    fun decodeString(key: String, default: String = ""): String =
        prefs?.getString(key, default) ?: default

    fun decodeLong(key: String, default: Long = 0L): Long = prefs?.getLong(key, default) ?: default

    fun decodeBoolean(key: String, default: Boolean = false): Boolean =
        prefs?.getBoolean(key, default) ?: default

    fun decodeFloat(key: String, default: Float = 0F): Float =
        prefs?.getFloat(key, default) ?: default

    fun decodeStringSet(
        key: String,
        default: MutableSet<String> = mutableSetOf(),
    ): MutableSet<String> =
        prefs?.getStringSet(key, mutableSetOf()) ?: default

    fun decodeInt(key: String, default: Int = 0): Int = prefs?.getInt(key, default) ?: default

    fun removeKey(key: String) = prefs?.edit()?.remove(key)?.commit() ?: false

    fun clearAll() = prefs?.edit()?.clear()?.commit() ?: false

}

fun SharedPreferencesUtil.encodes(action: SharedPreferencesUtil.() -> Unit) {
    action()
}

/**
 * Datastore 封装
 */
operator fun <V> Preferences.get(preference: DataStorePreference<V>) = this[preference.key]

open class DataStorePreference<V>(
    private val dataStore: DataStore<Preferences>,
    val key: Preferences.Key<V>,
    open val default: V?
){

    suspend fun set(block: suspend V?.(Preferences) -> V?): Preferences =
        dataStore.edit { preferences ->
            val value = block(preferences[key] ?: default, preferences)
            if (value == null) {
                preferences.remove(key)
            } else {
                preferences[key] = value
            }
        }

    suspend fun set(value: V?): Preferences = set { value }

    fun asFlow(): Flow<V?> =
        dataStore.data.map { it[key] ?: default }

    suspend fun get(): V? = asFlow().first()

    suspend fun getOrDefault(): V = get() ?: throw IllegalStateException("No default value")


}


