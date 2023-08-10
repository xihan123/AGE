package cn.xihan.age.util

import android.app.PictureInPictureParams
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Rational
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.cache.Cache
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.ui.PlayerView
import java.io.File

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/6/3 22:54
 * @介绍 :
 */
/**
 * Manage video player cache.
 */
object VideoPlayerCacheManager {

    private lateinit var cacheInstance: Cache

    /**
     * Set the cache for video player.
     * It can only be set once in the app, and it is shared and used by multiple video players.
     *
     * @param context Current activity context.
     * @param maxCacheBytes Sets the maximum cache capacity in bytes. If the cache builds up as much as the set capacity, it is deleted from the oldest cache.
     */
    @androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
    fun initialize(context: Context, maxCacheBytes: Long) {
        if (VideoPlayerCacheManager::cacheInstance.isInitialized) {
            return
        }

        cacheInstance = SimpleCache(
            File(context.cacheDir, "video"),
            LeastRecentlyUsedCacheEvictor(maxCacheBytes),
            StandaloneDatabaseProvider(context),
        )
    }

    /**
     * Gets the ExoPlayer cache instance. If null, the cache to be disabled.
     */
    internal fun getCache(): Cache? =
        if (VideoPlayerCacheManager::cacheInstance.isInitialized) {
            cacheInstance
        } else {
            null
        }
}

/**
 * Enables PIP mode for the current activity.
 *
 * @param context Activity context.
 * @param defaultPlayerView Current video player controller.
 */
@Suppress("DEPRECATION")
internal fun enterPIPMode(context: Context, defaultPlayerView: PlayerView) {
    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
        defaultPlayerView.useController = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val params = PictureInPictureParams.Builder()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                params
                    .setTitle("Video Player")
                    .setAspectRatio(Rational(16, 9))
                    .setSeamlessResizeEnabled(true)
            }

            context.findActivity()?.enterPictureInPictureMode(params.build())
        } else {
            context.findActivity()?.enterPictureInPictureMode()
        }
    }
}


/**
 * Check that the current activity is in PIP mode.
 *
 * @return `true` if the activity is in pip mode. (PIP mode is not supported in the version below Android N, so `false` is returned unconditionally.)
 */
internal fun Context.isActivityStatePipMode(): Boolean =
    findActivity()?.isInPictureInPictureMode ?: false
