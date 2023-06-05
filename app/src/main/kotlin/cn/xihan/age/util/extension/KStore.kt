package cn.xihan.age.util.extension


import io.ktor.serialization.kotlinx.json.DefaultJson
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import okio.FileNotFoundException
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import okio.buffer
import okio.use

import kotlinx.serialization.json.okio.decodeFromBufferedSource as decode
import kotlinx.serialization.json.okio.encodeToBufferedSink as encode

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/3/25 1:30
 * @介绍 : https://github.com/xxfast/KStore 0.6.0
 */

val FILE_SYSTEM: FileSystem = FileSystem.SYSTEM

internal val StoreDispatcher: CoroutineDispatcher = Dispatchers.IO

class KStore<T : @Serializable Any>(
    private val default: T? = null,
    private val enableCache: Boolean = true,
    private val codec: Codec<T>,
) {
    private val lock: Mutex = Mutex()
    internal val cache: MutableStateFlow<T?> = MutableStateFlow(default)

    /** Observe store for updates */
    public val updates: Flow<T?>
        get() = this.cache
            .onStart { read(fromCache = false) } // updates will always start with a fresh read

    private suspend fun write(value: T?): Unit = withContext(StoreDispatcher) {
        codec.encode(value)
        cache.emit(value)
    }

    private suspend fun read(fromCache: Boolean): T? = withContext(StoreDispatcher) {
        if (fromCache && cache.value != default) return@withContext cache.value
        val decoded: T? = codec.decode()
        val emitted: T? = decoded ?: default
        cache.emit(emitted)
        return@withContext emitted
    }

    /**
     * Set a value to the store
     *
     * @param value to set
     */
    suspend fun set(value: T?): Unit = lock.withLock { write(value) }

    /**
     * Get a value from the store
     *
     * @return value stored/cached (if enabled)
     */
    suspend fun get(): T? = lock.withLock { read(enableCache) }

    /**
     * Update a value in a store.
     * Note: This maintains a single mutex lock for both get and set
     *
     * @param operation lambda to update a given value of type [T]
     */
    suspend fun update(operation: (T?) -> T?): Unit = lock.withLock {
        val previous: T? = read(enableCache)
        val updated: T? = operation(previous)
        write(updated)
    }

    /**
     * Set the value of the store to null
     */
    suspend fun delete() {
        set(null)
        cache.emit(null)
    }

    /**
     * Set the value of the store to the default
     */
    suspend fun reset() {
        set(default)
        cache.emit(default)
    }
}

interface Codec<T: @Serializable Any> {
    suspend fun encode(value: T?)
    suspend fun decode(): T?
}

inline fun <reified T : @Serializable Any> FileCodec(
    filePath: String,
    json: Json = DefaultJson,
): FileCodec<T> = FileCodec(
    filePath = filePath,
    json = json,
    serializer = json.serializersModule.serializer(),
)

@OptIn(ExperimentalSerializationApi::class)
class FileCodec<T : @Serializable Any>(
    filePath: String,
    private val json: Json,
    private val serializer: KSerializer<T>,
) : Codec<T> {
    private val path: Path = filePath.toPath()

    override suspend fun decode(): T? =
        try { json.decode(serializer, FILE_SYSTEM.source(path).buffer()) }
        catch (e: FileNotFoundException) { null }

    override suspend fun encode(value: T?) {
        val parentFolder: Path? = path.parent
        if (parentFolder != null && !FILE_SYSTEM.exists(parentFolder))
            FILE_SYSTEM.createDirectories(parentFolder, mustCreate = false)
        if (value != null) FILE_SYSTEM.sink(path).buffer().use { json.encode(serializer, value, it) }
        else FILE_SYSTEM.delete(path)
    }
}

inline fun <reified T : @Serializable Any> storeOf(
    filePath: String,
    default: T? = null,
    enableCache: Boolean = true,
    json: Json = DefaultJson,
): KStore<T> = KStore(
    default = default,
    enableCache = enableCache,
    codec = FileCodec(filePath, json)
)

inline fun <reified T : @Serializable Any> listStoreOf(
    filePath: String,
    default: List<T> = emptyList(),
    enableCache: Boolean = true,
    json: Json = Json { ignoreUnknownKeys = true; encodeDefaults = true },
): KStore<List<T>> =
    storeOf(filePath, default, enableCache, json)

/**
 * Get a list of type [T] from the store, or empty list if the store is empty
 *
 * @return stored list of type [T]
 */
suspend fun <T : @Serializable Any> KStore<List<T>>.getOrEmpty(): List<T> =
    get() ?: emptyList()

/**
 * Get an item from list of type [T] from the store, or null if the store is empty
 *
 * @param index index of the item from the list
 */
suspend fun <T : @Serializable Any> KStore<List<T>>.get(index: Int): T? =
    get()?.get(index)

/**
 * Add item(s) to the end of the list of type [T] to the store
 *
 * @param value item(s) to be added to the end of the list
 */
suspend fun <T : @Serializable Any> KStore<List<T>>.plus(vararg value: T) {
    update { list -> list?.plus(value) ?: listOf(*value) }
}

/**
 * Remove item(s) of type [T] from the list in the store
 *
 * @param value item(s) to be removed from the list
 */
suspend fun <T : @Serializable Any> KStore<List<T>>.minus(vararg value: T) {
    update { list -> list?.minus(value.toSet()) ?: emptyList() }
}

/**
 * Updates the list by applying the given [operation] lambda to each element in the stored list.
 *
 * @param operation lambda to update each list item
 */
suspend fun <T : @Serializable Any> KStore<List<T>>.map(operation: (T) -> T) {
    update { list -> list?.map { t -> operation(t) } }
}

/**
 * Updates the list by applying the given [operation] lambda to each element in the stored list and
 * its index in the stored collection.
 *
 * @param operation lambda to update each list item
 */
suspend fun <T : @Serializable Any> KStore<List<T>>.mapIndexed(operation: (Int, T) -> T) {
    update { list -> list?.mapIndexed { index, t -> operation(index, t) } }
}

/** Observe a list store for updates */
val <T : @Serializable Any> KStore<List<T>>.updatesOrEmpty: Flow<List<T>> get() =
    updates.filterNotNull()