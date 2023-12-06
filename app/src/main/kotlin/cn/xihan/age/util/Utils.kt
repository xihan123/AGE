package cn.xihan.age.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import android.provider.Settings
import android.util.Base64
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemSpanScope
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import cn.xihan.age.model.WeekItem
import com.kongzue.dialogx.DialogX
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Formatter
import java.util.Locale
import kotlin.system.exitProcess

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/9/17 20:01
 * @介绍 :
 */
@Composable
fun <T> rememberMutableStateOf(value: T): MutableState<T> = remember { mutableStateOf(value) }

@Composable
fun <T> rememberMutableStateOf(key1: Any?, value: T): MutableState<T> =
    remember(key1) { mutableStateOf(value) }

@Composable
fun <T> rememberMutableStateOf(key1: Any?, key2: Any?, value: T): MutableState<T> =
    remember(key1, key2) { mutableStateOf(value) }

@Composable
fun rememberFocusRequester() = remember { FocusRequester() }

@Composable
fun rememberMutableInteractionSource() = remember { MutableInteractionSource() }

@Composable
fun <T> rememberDerivedStateOf(calculation: () -> T) = remember { derivedStateOf(calculation) }

@Composable
fun <T> rememberSavableMutableStateOf(value: T): MutableState<T> =
    rememberSaveable { mutableStateOf(value) }

inline fun <T : Any> LazyGridScope.items(
    items: LazyPagingItems<T>,
    noinline key: ((item: T) -> Any)? = null,
    noinline span: (LazyGridItemSpanScope.(item: T?) -> GridItemSpan)? = null,
    noinline contentType: (item: T?) -> Any? = { null },
    crossinline itemContent: @Composable LazyGridItemScope.(item: T?) -> Unit
) {
    items(count = items.itemCount, key = if (key == null) null else { index ->
        val item = items.peek(index)
        if (item == null) {
            MyPagingPlaceholderKey(index)
        } else {
            key(item)
        }
    }, span = if (span != null) {
        { span(items[it]) }
    } else null, contentType = { index: Int -> contentType(items[index]) }) { index ->
        itemContent(items[index])
    }
}

@SuppressLint("BanParcelableUsage")
data class MyPagingPlaceholderKey(private val index: Int) : Parcelable {
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(index)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @Suppress("unused")
        @JvmField
        val CREATOR: Parcelable.Creator<MyPagingPlaceholderKey> =
            object : Parcelable.Creator<MyPagingPlaceholderKey> {
                override fun createFromParcel(parcel: Parcel) =
                    MyPagingPlaceholderKey(parcel.readInt())

                override fun newArray(size: Int) = arrayOfNulls<MyPagingPlaceholderKey?>(size)
            }
    }
}

fun <T> nullList(size: Int): List<T?> = List(size) { null }

fun nullWeekMap(size: Int): Map<Int, List<WeekItem?>> = (0..6).associateWith { nullList(size) }

fun <K, V> Map<K, V>.indexOfKey(key: K): Int {
    val index = this.keys.toList().indexOf(key)
    return if (index != -1) index + 1 else -1
}

@Composable
fun getAspectRadio(): Float {
    val configuration = LocalConfiguration.current
    return remember(configuration) {
        configuration.screenHeightDp.toFloat() / configuration.screenWidthDp.toFloat()
    }
}

@Composable
fun isTablet(): Boolean {
    val configuration = LocalConfiguration.current
    return if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        configuration.screenHeightDp > 600
    } else {
        configuration.screenWidthDp > 600
    }
}

/**
 * 设置竖向间隔dp
 * Spacer with vertical
 */
@Composable
fun VerticalSpace(dp: Int) {
    VerticalSpace(dp.dp)
}

@Composable
fun VerticalSpace(dp: Dp) {
    Spacer(Modifier.height(dp))
}

/**
 * 统一时间格式
 */
const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

/**
 * 获取最新时间
 */
val nowTime: String
    get() = System.currentTimeMillis().toDate()

fun String.aid(): Int = this.split("/").last().toInt()

/**
 * 时间戳 转为 yyyy-MM-dd HH:mm:ss
 */
fun Long.toDate(): String =
    SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date(this))

fun Context.setScreenOrientation(orientation: Int) {
    val activity = this as? Activity ?: return
    activity.requestedOrientation = orientation
}

fun Context.isOrientationLocked() = Settings.System.getInt(
    contentResolver, Settings.System.ACCELEROMETER_ROTATION, 1
) == 0

/**
 * 设置主题模式
 */
fun setThemeMode(mode: Int) = when (mode) {
    1 -> {
        DialogX.globalTheme = DialogX.THEME.LIGHT
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
    }

    2 -> {
        DialogX.globalTheme = DialogX.THEME.DARK
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    else -> {
        DialogX.globalTheme = DialogX.THEME.AUTO
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
}

@Composable
fun KeepScreenOn() {
    val currentView = LocalView.current
    DisposableEffect(Unit) {
        currentView.keepScreenOn = true
        onDispose {
            currentView.keepScreenOn = false
        }
    }
}

fun String.toBitmap(): Bitmap {
    val decodedBytes = Base64.decode(this, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
}

/**
 * 默认浏览器打开url
 */
fun Context.openUrl(url: String) = runCatching {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    startActivity(intent)
}

/**
 * 重新启动应用程序
 * @suppress Generate Documentation
 */
fun Activity.restartApplication() = packageManager.getLaunchIntentForPackage(packageName)?.let {
    finishAffinity()
    startActivity(intent)
    exitProcess(0)
}

object Utils {

    /**
     * 获取当前是星期?  1-7
     */
    fun getWeekOfDate(): Int = runCatching {
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).dayOfWeek.value
    }.getOrElse { 1 }

    /**
     * 解析播放列表名
     * @param type 类型
     * @return 返回解析后的列表名
     */
    fun analysisPlayer(
        playerLabel: Map<String, String>,
        type: Set<String>? = null
    ): List<String> {
        val list = mutableListOf<String>()
        playerLabel.forEach { (k, v) ->
            if (type == null || type.contains(k)) {
                list.add(v)
            }
        }
        return list

    }

    /**
     * 传入播放列表 以100个分组 返回每个分组的开头和结尾集数 多余的集数放在最后一个分组
     */
    fun getGroupList(list: List<List<String>>?): ArrayList<String> =
        runCatching {
            val groupList = ArrayList<String>()
            list.whatIfNotNullOrEmpty {
                val groupSize = 100
                val groupCount = it.size / groupSize
                for (i in 0 until groupCount) {
                    groupList.add("${it[i * groupSize][0]}~${it[(i + 1) * groupSize - 1][0]}")
                }
                if (it.size % groupSize != 0) {
                    groupList.add("${it[groupCount * groupSize][0]}~${it.last()[0]}")
                }
            }
            groupList
        }.getOrElse {
            arrayListOf()
        }

    /**
     * 传入分组索引 返回切割的播放列表
     */
    fun getGroupList(
        list: List<List<String>>?,
        position: Int
    ): List<List<String>> = runCatching {
        val groupList = ArrayList<List<String>>()
        list.whatIfNotNullOrEmpty {
            val groupSize = 100
            val groupCount = it.size / groupSize
            if (position < groupCount) {
                groupList.addAll(it.subList(position * groupSize, (position + 1) * groupSize))
            } else {
                groupList.addAll(it.subList(groupCount * groupSize, it.size))
            }
        }
        groupList
    }.getOrElse {
        emptyList()
    }

    /**
     * 传入播放列表 以100个分组 返回每个分组的开头位置 返回List<Int>
     */
    fun getGroupListPosition(list: List<List<String>>?): ArrayList<Int> =
        runCatching {
            val groupList = ArrayList<Int>()
            list.whatIfNotNullOrEmpty {
                val groupSize = 100
                val groupCount = it.size / groupSize
                for (i in 0 until groupCount) {
                    groupList.add(i * groupSize)
                }
                if (it.size % groupSize != 0) {
                    groupList.add(groupCount * groupSize)
                }
            }
            groupList
        }.getOrElse { arrayListOf() }

    /**
     * 按索引获取剧集标题
     * @param [index] 指数
     * @param [isNext] 是下一个
     */
    fun List<String>.getEpisodeTitleByIndex(
        index: Int,
        isNext: Boolean = false
    ) = runCatching {
        if (isNext) {
            if (index + 1 < this.size) {
                this[index + 1]
            } else {
                this[index]
            }
        } else {
            if (index - 1 >= 0) {
                this[index - 1]
            } else {
                this[index]
            }
        }
    }.getOrNull()

    fun stringForTime(timeMs: Long): String {
        if (timeMs <= 0 || timeMs >= 24 * 60 * 60 * 1000) {
            return "00:00"
        }
        val totalSeconds = timeMs / 1000
        val seconds = (totalSeconds % 60).toInt()
        val minutes = (totalSeconds / 60 % 60).toInt()
        val hours = (totalSeconds / 3600).toInt()
        val stringBuilder = StringBuilder()
        val mFormatter = Formatter(stringBuilder, Locale.getDefault())
        return if (hours > 0) {
            mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString()
        } else {
            mFormatter.format("%02d:%02d", minutes, seconds).toString()
        }
    }
}