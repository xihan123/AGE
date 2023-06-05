package cn.xihan.age.util.extension

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import cn.xihan.age.util.DataStorePreference
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/21 16:10
 * @介绍 : https://github.com/DylanCaiCoding/DataStoreKTX
 */
open class DataStoreOwner(name: String) : IDataStoreOwner {
    private val Context.dataStore by preferencesDataStore(name)
    override val dataStore get() = context.dataStore
}

interface IDataStoreOwner {
    val context: Context get() = application

    val dataStore: DataStore<Preferences>

    fun dataStoreInt(default: Int? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Int>> =
        PreferenceProperty(::intPreferencesKey, default)

    fun dataStoreDouble(default: Double? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Double>> =
        PreferenceProperty(::doublePreferencesKey, default)

    fun dataStoreLong(default: Long? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Long>> =
        PreferenceProperty(::longPreferencesKey, default)

    fun dataStoreFloat(default: Float? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Float>> =
        PreferenceProperty(::floatPreferencesKey, default)

    fun dataStoreBool(default: Boolean? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Boolean>> =
        PreferenceProperty(::booleanPreferencesKey, default)

    fun dataStoreString(default: String? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<String>> =
        PreferenceProperty(::stringPreferencesKey, default)

    fun dataStoreStringSet(default: Set<String>? = null): ReadOnlyProperty<IDataStoreOwner, DataStorePreference<Set<String>>> =
        PreferenceProperty(::stringSetPreferencesKey, default)

    class PreferenceProperty<V>(
        private val key: (String) -> Preferences.Key<V>,
        private val default: V? = null,
    ) : ReadOnlyProperty<IDataStoreOwner, DataStorePreference<V>> {
        private var cache: DataStorePreference<V>? = null

        override fun getValue(thisRef: IDataStoreOwner, property: KProperty<*>): DataStorePreference<V> =
            cache ?: DataStorePreference(thisRef.dataStore, key(property.name), default).also { cache = it }
    }
}