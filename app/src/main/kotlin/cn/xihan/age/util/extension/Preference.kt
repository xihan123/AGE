package cn.xihan.age.util.extension

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/10/11 20:57
 * @介绍 :
 */
interface PreferenceOwner {

    val prefs: SharedPreferences
        get() = default ?: throw IllegalStateException("No default shared preferences")

    companion object {
        @JvmStatic
        var default: SharedPreferences? = null
    }
}

fun PreferenceOwner.preferenceInt(default: Int = 0) =
    PreferenceProperty(
        { prefs.getInt(it, default) },
        { prefs.edit().putInt(first, second).commit() })

fun PreferenceOwner.preferenceLong(default: Long = 0L) =
    PreferenceProperty(
        { prefs.getLong(it, default) },
        { prefs.edit().putLong(first, second).commit() })

fun PreferenceOwner.preferenceBool(default: Boolean = false) =
    PreferenceProperty(
        { prefs.getBoolean(it, default) },
        { prefs.edit().putBoolean(first, second).commit() })

fun PreferenceOwner.preferenceFloat(default: Float = 0f) =
    PreferenceProperty(
        { prefs.getFloat(it, default) },
        { prefs.edit().putFloat(first, second).commit() })

fun PreferenceOwner.preferenceString(default: String = "") =
    PreferenceProperty(
        { prefs.getString(it, default) ?: default },
        { prefs.edit().putString(first, second).commit() })

fun PreferenceOwner.preferenceStringSet(default: MutableSet<String> = mutableSetOf()) =
    PreferenceProperty(
        { prefs.getStringSet(it, default) ?: default },
        { prefs.edit().putStringSet(first, second).commit() })


class PreferenceProperty<V>(
    private val decode: (String) -> V,
    private val encode: Pair<String, V>.() -> Boolean,
) : ReadWriteProperty<PreferenceOwner, V> {

    private var cache: V? = null
    override fun getValue(thisRef: PreferenceOwner, property: KProperty<*>): V =
        cache ?: decode(property.name).also { cache = it }

    override fun setValue(thisRef: PreferenceOwner, property: KProperty<*>, value: V) {
        if (encode(property.name to value)) {
            cache = value
        }
    }


}