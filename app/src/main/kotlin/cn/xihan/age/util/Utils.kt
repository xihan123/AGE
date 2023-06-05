package cn.xihan.age.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process.killProcess
import android.os.Process.myPid
import android.os.StrictMode
import android.os.Vibrator
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.annotation.ColorInt
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import cn.xihan.age.BuildConfig
import cn.xihan.age.R
import cn.xihan.age.base.BaseActivity
import cn.xihan.age.model.AnimeDetailModel
import cn.xihan.age.model.MainConfigModel
import cn.xihan.age.network.Api
import cn.xihan.age.network.SPSettings
import cn.xihan.age.ui.main.MainActivity
import cn.xihan.age.util.extension.application
import cn.xihan.age.util.extension.asActivity
import cn.xihan.age.util.extension.finishAllActivities
import cn.xihan.age.util.extension.isNotNullOrBlankAndNumber
import cn.xihan.age.util.extension.logError
import cn.xihan.age.util.extension.startActivity
import cn.xihan.age.util.extension.toast
import com.hadiyarajesh.flower_core.ApiEmptyResponse
import com.hadiyarajesh.flower_core.ApiErrorResponse
import com.hadiyarajesh.flower_core.ApiResponse
import com.hadiyarajesh.flower_core.ApiSuccessResponse
import com.hadiyarajesh.flower_core.Resource
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.InputDialog
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.DialogLifecycleCallback
import com.skydoves.whatif.whatIfNotNullOrEmpty
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URISyntaxException
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Formatter
import java.util.Locale
import java.util.concurrent.TimeUnit


@Composable
fun <T> rememberMutableStateOf(value: T): MutableState<T> = remember { mutableStateOf(value) }

@Composable
fun <T> rememberSavableMutableStateOf(value: T): MutableState<T> =
    rememberSaveable { mutableStateOf(value) }

@Composable
fun LockScreenOrientation(orientation: Int, isHideSystemUi: Boolean = true) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        if (isHideSystemUi) {
            activity.hideSystemUI()
        }
        onDispose {
            // restore original orientation when view disappears
            activity.requestedOrientation = originalOrientation
            if (isHideSystemUi) {
                activity.showSystemUI()
            }
        }
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

fun Context.findActivity(): BaseActivity? = when (this) {
    is BaseActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}

val LazyListState.lastVisibleItemIndex
    get() = layoutInfo.visibleItemsInfo.lastOrNull()?.index

val LazyListState.lastItemIndex
    get() = layoutInfo.totalItemsCount.let { if (it == 0) null else it }

val LazyListState.isScrolledToEnd
    get() = lastVisibleItemIndex == lastItemIndex

val LazyListState.isScrolledToStart
    get() = firstVisibleItemIndex == 0

//@Composable
//fun isPortrait(): Boolean {
//    val configuration = LocalConfiguration.current
//    return configuration.orientation == android.content.res.Configuration.ORIENTATION_PORTRAIT
//}
//
//@Composable
//fun parentSize(): Float{
//   return if (isPortrait()) .4f else .2f
//}

object Utils {

    /**
     * 保存分类列表数据
     */
    fun saveLabel(labelsDTO: MainConfigModel.AGEEntityModel.LabelsModel) {
        SpUtil("Labels").clearAll()
        val 地区arrList: List<String> = labelsDTO.labelsRegion
        val 版本arrList: List<String> = labelsDTO.labelsGenre
        val 首字母arrList: List<String> = labelsDTO.labelsLetter
        val 年份arrList: List<String> = labelsDTO.labelsYear
        val 季度arrList: List<String> = labelsDTO.labelsSeason
        val 状态arrList: List<String> = labelsDTO.labelsStatus
        val 类型arrList: List<String> = labelsDTO.labelsLabel
        val 资源arrList: List<String> = labelsDTO.labelsResource
        val 排序arrList: List<String> = labelsDTO.labelsOrder

        SpUtil("Labels").encodes {
            encode("地区", 地区arrList)
            encode("版本", 版本arrList)
            encode("首字母", 首字母arrList)
            encode("年份", 年份arrList)
            encode("季度", 季度arrList)
            encode("状态", 状态arrList)
            encode("类型", 类型arrList)
            encode("资源", 资源arrList)
            encode("排序", 排序arrList)
        }

    }

    /**
     * 解析分类
     */
    fun analysisLabels(number: Int): List<String> {
        val stringList: List<String> = ArrayList()
        try {
            when (number) {
                0 -> return SpUtil("Labels").decodeList("地区")
                1 -> return SpUtil("Labels").decodeList("版本")
                2 -> return SpUtil("Labels").decodeList("首字母")
                3 -> return SpUtil("Labels").decodeList("年份")
                4 -> return SpUtil("Labels").decodeList("季度")
                5 -> return SpUtil("Labels").decodeList("状态")
                6 -> return SpUtil("Labels").decodeList("类型")
                7 -> return SpUtil("Labels").decodeList("资源")
                8 -> return SpUtil("Labels").decodeList("排序")
                else -> {}
            }
        } catch (e: Exception) {
            e.printStackTrace()
            logError("解析分类${e.message}")
        }
        return stringList
    }

    /**
     * 传入分类标题 返回分类内索引
     */
    fun getLabelsIndex(title: String?, titleList: List<String>): Int = runCatching {
        if (title.isNullOrEmpty()) return@runCatching 0
        titleList.indexOf(title)
    }.getOrDefault(0)

    /**
     * 根据传入的内容 修改 map 集合
     */
    fun updateMap(map: MutableMap<String, String>, key: String, value: String) {
        when (value) {
            "全部" -> {
                map[key] = "all"
            }

            else -> {
                map[key] = value
            }
        }
    }

    /**
     * 获取星期
     */
    fun getWeekOfDate(): Int {
        val weekDays = intArrayOf(6, 0, 1, 2, 3, 4, 5)
        val cal: Calendar = Calendar.getInstance()
        cal.time = Date()
        var w: Int = cal.get(Calendar.DAY_OF_WEEK) - 1
        if (w < 0) {
            w = 0
        }
        return weekDays[w]
    }

    /**
     * 获取自定义API列表
     */
    fun getCustomApiList(): List<MainConfigModel.AppEntityModel.CustomApiModel> = runCatching {
        val configModel =
            SpUtil("Settings").decodeParcelableNotNull(
                "mainConfigModel",
                MainConfigModel::class.java
            )
        configModel.appEntity.customApi
    }.getOrElse {
        emptyList()
    }

    /**
     * 获取自定义API
     */
    fun getCustomApi(number: Int = 0): MainConfigModel.AppEntityModel.CustomApiModel = runCatching {
        getCustomApiList()[number]
    }.getOrElse {
        MainConfigModel.AppEntityModel.CustomApiModel(
            apiName = "备用API",
            apiUrl = "https://jsd.cdn.zzko.cn/gh/xihan123/AGE-API@master/details/"
        )

    }

    /**
     * 返回周一到周日的列表合集
     */
    fun weekList(): ArrayList<String> =
        arrayListOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")

    /**
     * 转换数字为 xx 万
     */
    fun intChange2Str(number: Int): String {
        val str: String = if (number <= 0) {
            "0"
        } else if (number < 10000) {
            number.toString() + ""
        } else {
            //1.将数字转换成以万为单位的数字
            val num = number.toDouble() / 10000
            val b = BigDecimal(num)
            //2.转换后的数字四舍五入保留小数点后一位;
            val f1: Double = b.setScale(1, RoundingMode.HALF_UP).toDouble()
            f1.toString() + "w"
        }
        return str
    }

    /**
     * 得到播放列表的 Tablelayout 标题
     */
    fun getPlayListTitle(list: List<List<AnimeDetailModel.AniInfoModel.PlayModel>>?): ArrayList<String> =
        runCatching {
            val arrayList = ArrayList<String>()
            list.whatIfNotNullOrEmpty {
                it.forEachIndexed { index, playList ->
                    playList.whatIfNotNullOrEmpty {
                        arrayList.add("播放列表${index + 1}")
                    }
                }
            }
            arrayList
        }.getOrElse {
            arrayListOf()
        }

    /**
     * 传入标题返回对应的播放列表索引
     */
    fun getPlayListIndex(title: String): Int = runCatching {
        title.replace("播放列表", "").toInt() - 1
    }.getOrElse {
        0
    }

    /**
     * 传入播放列表 以100个分组 返回每个分组的开头和结尾集数 多余的集数放在最后一个分组
     */
    fun getGroupList(list: List<AnimeDetailModel.AniInfoModel.PlayModel>?): ArrayList<String> =
        runCatching {
            val groupList = ArrayList<String>()
            list.whatIfNotNullOrEmpty {
                val groupSize = 100
                val groupCount = it.size / groupSize
                for (i in 0 until groupCount) {
                    groupList.add("${it[i * groupSize].epName}~${it[(i + 1) * groupSize - 1].epName}")
                }
                if (it.size % groupSize != 0) {
                    groupList.add("${it[groupCount * groupSize].epName}~${it.last().epName}")
                }
            }
            groupList
        }.getOrElse {
            arrayListOf()
        }

    /**
     * 传入播放列表 以100个分组 返回每个分组的开头位置 返回List<Int>
     */
    fun getGroupListPosition(list: List<AnimeDetailModel.AniInfoModel.PlayModel>?): ArrayList<Int> =
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
     * 传入分组索引 返回切割的播放列表
     */
    fun getGroupList(
        list: List<AnimeDetailModel.AniInfoModel.PlayModel>?,
        position: Int
    ): List<AnimeDetailModel.AniInfoModel.PlayModel> = runCatching {
        val groupList = ArrayList<AnimeDetailModel.AniInfoModel.PlayModel>()
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
     * 根据播放列表 epName 返回索引的拓展函数
     */
    fun List<AnimeDetailModel.AniInfoModel.PlayModel>.getEpNameIndex(epName: String): Int =
        runCatching {
            this.indexOfFirst { it.epName == epName }
        }.getOrElse {
            0
        }

    /**
     * 根据播放列表当前索引 返回上一集或下一集的标题
     */
    fun List<AnimeDetailModel.AniInfoModel.PlayModel>.getEpNameByIndex(
        index: Int,
        isNext: Boolean = false
    ) = runCatching {
        if (isNext) {
            if (index + 1 < this.size) {
                this[index + 1].title
            } else {
                this[index].title
            }
        } else {
            if (index - 1 >= 0) {
                this[index - 1].title
            } else {
                this[index].title
            }
        }
    }.getOrElse {
        ""
    }

    /**
     * 根据当前索引 返回上一集或下一集的索引
     */
    fun List<AnimeDetailModel.AniInfoModel.PlayModel>.getEpIndexByIndex(
        index: Int,
        isNext: Boolean = false
    ) = runCatching {
        if (isNext) {
            if (index + 1 < this.size) {
                index + 1
            } else {
                index
            }
        } else {
            if (index - 1 >= 0) {
                index - 1
            } else {
                index
            }
        }
    }.getOrElse { 0 }

    /**
     * 根据当前索引 返回上一集或下一集的模型
     */
    fun List<AnimeDetailModel.AniInfoModel.PlayModel>.getEpModelByIndex(
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

    /**
     * 根据列表传入当前索引修改是否播放值后返回新的列表
     */
    fun List<AnimeDetailModel.AniInfoModel.PlayModel>.getNewListByPlayModel(
        playModel: AnimeDetailModel.AniInfoModel.PlayModel
    ) = runCatching {
        val newList = ArrayList<AnimeDetailModel.AniInfoModel.PlayModel>()
        newList.addAll(this)
        newList.find { it.epName == playModel.epName }?.playing = true
        newList
    }.getOrElse {
        this
    }

    /**
     * 跳转去支付宝
     */
    fun startAlipayClient(activity: Activity, payCode: String): Boolean {
        return startIntentUrl(activity, Api.INTENT_URL_FORMAT.replace("{payCode}", payCode))
    }

    private fun startIntentUrl(activity: Activity, intentFullUrl: String): Boolean {
        return try {
            if (isPackageInstalled(activity, "com.eg.android.AlipayGphone")) {
                val e = Intent.parseUri(intentFullUrl, Intent.URI_INTENT_SCHEME)
                activity.startActivity(e)
            } else {
                activity.toast(
                    String.format(
                        activity.getString(R.string.not_install_tip),
                        activity.getString(R.string.alipay)
                    )
                )
            }
            true
        } catch (var3: URISyntaxException) {
            var3.printStackTrace()
            false
        } catch (var3: ActivityNotFoundException) {
            var3.printStackTrace()
            false
        }
    }

    /**
     * 跳转去微信
     */
    @SuppressLint("WrongConstant")
    fun startWeChatClient(c: Context) {
        val intent = Intent()
        intent.component = ComponentName("com.tencent.mm", "com.tencent.mm.ui.LauncherUI")
        intent.putExtra("LauncherUI.From.Scaner.Shortcut", true)
        intent.flags = 335544320
        intent.action = "android.intent.action.VIEW"
        if (isPackageInstalled(c, "com.tencent.mm")) {
            c.startActivity(intent)
        } else {
            c.toast(
                String.format(
                    c.getString(R.string.not_install_tip),
                    c.getString(R.string.wechat)
                )
            )
        }
    }

    /**
     * 判断指定包名是否安装
     */
    private fun isPackageInstalled(context: Context, packageName: String): Boolean {
        val packageManager = context.packageManager
        return try {
            packageManager.getPackageInfo(packageName, 1)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    /**
     * 保存 drawable 图片到相册
     */
    fun saveImageToGallery(context: Context, bitmap: Bitmap) {
        // 首先保存图片
        val storePath =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + "DCIM" + File.separator + "Camera"
        val appDir = File(storePath)
        if (!appDir.exists()) {
            appDir.mkdir()
        }
        val fileName = System.currentTimeMillis().toString() + ".png"
        val file = File(appDir, fileName)
        try {
            val fos = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.flush()
            fos.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(
                context.contentResolver,
                file.absolutePath,
                fileName,
                null
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        // 最后通知图库更新
        context.sendBroadcast(
            Intent(
                Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                Uri.parse("file://$storePath")
            )
        )
        TipDialog.show("保存成功~", WaitDialog.TYPE.SUCCESS)
    }

    /**
     * 弹出播放速度选择框
     */
    fun showPlaySpeedDialog(
        onSpeedChangeListener: (Int) -> Unit = {},
        onShowSpeedChangeListener: () -> Unit = {},
        onDismissSpeedChangeListener: () -> Unit = {}
    ) {
        val items = arrayOf("0.5", "1.0", "1.5", "2.0", "2.5", "3.0")
        PopMenu.show(items).setOnMenuItemClickListener { dialog, _, index ->
            onSpeedChangeListener(index)
            dialog.dismiss()
            false
        }.dialogLifecycleCallback = object : DialogLifecycleCallback<PopMenu>() {
            override fun onShow(dialog: PopMenu?) {
                super.onShow(dialog)
                onShowSpeedChangeListener()
            }

            override fun onDismiss(dialog: PopMenu?) {
                super.onDismiss(dialog)
                onDismissSpeedChangeListener()
            }

        }
    }

    /**
     * 弹出播放跳过时间选择框
     */
    fun showPlaySkipTimeDialog(
        onSkipTimeChangeListener: (String) -> Unit = {},
        onShowSkipTimeChangeListener: () -> Unit = {},
        onDismissSkipTimeChangeListener: () -> Unit = {}
    ) {
        val items = arrayOf("15s", "30s", "60s", "90s")
        PopMenu.show(items).setOnMenuItemClickListener { dialog, _, index ->
            onSkipTimeChangeListener(items[index])
            dialog.dismiss()
            false
        }.dialogLifecycleCallback = object : DialogLifecycleCallback<PopMenu>() {
            override fun onShow(dialog: PopMenu?) {
                super.onShow(dialog)
                onShowSkipTimeChangeListener()
            }

            override fun onDismiss(dialog: PopMenu?) {
                super.onDismiss(dialog)
                onDismissSkipTimeChangeListener()
            }

        }
    }

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

    /**
     * 根据总索引 除以 每页数量 获取总页数
     */
    fun getPageCount(totalIndex: Int, pageSize: Int): Int = runCatching {
        if (totalIndex % pageSize == 0) {
            totalIndex / pageSize
        } else {
            totalIndex / pageSize + 1
        }
    }.getOrElse { 1 }

    /**
     * 传入MutableState<Int> 弹出输入对话框
     */
    fun showNumberInputDialog(
        context: Context,
        title: String,
        message: String,
        input: Int,
        onOkClick: (Int) -> Unit = {}
    ) {
        InputDialog(
            title,
            message,
            context.getString(android.R.string.ok),
            context.getString(android.R.string.cancel),
            "$input"
        ).setCancelable(false).setOkButton { _, _, input1 ->
            if (input1.isNotNullOrBlankAndNumber()) {
                onOkClick(input1.toInt())
                false
            } else {
                PopTip.show("请输入数字")
                true
            }
        }.show()
    }

    internal fun getDurationString(durationMs: Long, negativePrefix: Boolean = false): String {
        val hours = TimeUnit.MILLISECONDS.toHours(durationMs)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(durationMs)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(durationMs)
        return if (hours > 0) {
            String.format(
                "%s%02d:%02d:%02d",
                if (negativePrefix) "-" else "",
                hours,
                minutes - TimeUnit.HOURS.toMinutes(hours),
                seconds - TimeUnit.MINUTES.toSeconds(minutes)
            )
        } else String.format(
            "%s%02d:%02d",
            if (negativePrefix) "-" else "",
            minutes,
            seconds - TimeUnit.MINUTES.toSeconds(minutes)
        )
    }

    /**
     * 更改主题模式
     */
    fun changeThemeMode(followSystemTheme: Boolean = false, nightMode: Boolean = true) {
        SPSettings.themeMode = when {
            followSystemTheme -> {
                DialogX.globalTheme = DialogX.THEME.AUTO
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                3
            }

            nightMode -> {
                DialogX.globalTheme = DialogX.THEME.DARK
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                1
            }

            else -> {
                DialogX.globalTheme = DialogX.THEME.LIGHT
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                0
            }
        }
    }

    /**
     * 获取当前版本号
     */
    fun getASVersionName(): String {
        return BuildConfig.VERSION_NAME
    }

    /**
     * 获取当前版本号
     */
    fun getASVersionCode(): Int {
        return BuildConfig.VERSION_CODE
    }

    /**
     * 获取编译时间
     */
    fun getBuildTimeDescription(): String {
        return SimpleDateFormat(
            Api.DATE_FORMAT, Locale.getDefault()
        ).format(BuildConfig.BUILD_TIMESTAMP)
    }

    fun Context.showDisclaimers() {
        MessageDialog.show(
            getString(R.string.disclaimers_title),
            getString(R.string.disclaimers_message),
            getString(android.R.string.ok),
            getString(android.R.string.cancel)
        ).setCancelable(false)
            .setOkButton { _, _ ->
                SharedPreferencesUtil().encode("isTheFirstTime", false)
                false
            }.setCancelButton { _, _ ->
                finishAllActivities()
                false
            }
    }

    /**
     * 获取年份
     */
    fun getYear(): Int = Calendar.getInstance().get(Calendar.YEAR)


}

/**
 * app灰白化,特殊节日使用
 */
fun Activity.appGraying() {
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
}

/**
 * 获取屏幕分辨率-高
 *
 * @return 高
 */
fun Context.getScreenHeight(): Int {
    val windowManager = this.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.bottom - insets.top
    } else {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }
}

/**
 * 判断是否2k屏幕
 */
fun Context.is2kScreen(): Boolean = getScreenHeight() > 1920

/**
 * 重启App
 */
fun Context.restartApp() {
    finishAllActivities()
    val intent = Intent(asActivity(), MainActivity::class.java)
    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    startActivity(intent)
}

/**
 * 结束进程并重启
 */
fun Context.killProcessAndRestart() {
    killProcess(myPid())
    restartApp()
}

/**
 * 默认浏览器打开url
 */
fun String.openUrl() = runCatching {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(this)
    startActivity(intent)
}

fun CharSequence.openUrl() = runCatching {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(this.toString())
    startActivity(intent)
}

/**
 * Url解码
 */
fun String.urlDecoder(): String {
    return if (hasUrlEncoded(this)) URLDecoder.decode(this, "UTF-8") else this
}

/**
 * Url编码
 */
fun String.urlEncoder(): String {
    return if (hasUrlEncoded(this)) URLEncoder.encode(this, "UTF-8") else this
}

private fun hasUrlEncoded(str: String): Boolean {
    var encode = false
    for (i in str.indices) {
        val c = str[i]
        if (c == '%' && i + 2 < str.length) {
            // 判断是否符合urlEncode规范
            val c1 = str[i + 1]
            val c2 = str[i + 2]
            if (isValidHexChar(c1) && isValidHexChar(c2)) {
                encode = true
            }
            break
        }
    }
    return encode
}

private fun isValidHexChar(c: Char): Boolean {
    return c in '0'..'9' || c in 'a'..'f' || c in 'A'..'F'
}

/**
 * 传入地址转为Uri 返回地址
 */
inline val String.url: Uri get() = this.toUri().buildUpon().scheme("https").build()

inline val Int.currentWeek: Int
    get() = if (this > 6)
    //抛出异常
    //throw IllegalArgumentException("Week must be between 0 and 6. currentWeek: $this")
        1
    else if (this == 6) 0 else this + 1

@ColorInt
fun parseColor(colorString: String): Int =
    Color.parseColor(colorString)

fun <T> allowReads(block: () -> T): T {
    val oldPolicy = StrictMode.allowThreadDiskReads()
    try {
        return block()
    } finally {
        StrictMode.setThreadPolicy(oldPolicy)
    }
}

fun <T> allowWrites(block: () -> T): T {
    val oldPolicy = StrictMode.allowThreadDiskWrites()
    try {
        return block()
    } finally {
        StrictMode.setThreadPolicy(oldPolicy)
    }
}

inline fun <DB, REMOTE> dbBoundResource(
    crossinline fetchFromLocal: suspend () -> DB,
    crossinline shouldMakeNetworkRequest: suspend (DB?) -> Boolean = { true },
    crossinline makeNetworkRequest: suspend () -> ApiResponse<REMOTE>,
    crossinline processNetworkResponse: (response: ApiSuccessResponse<REMOTE>) -> Unit = { },
    crossinline saveResponseData: suspend (REMOTE) -> Unit = { },
    crossinline onNetworkRequestFailed: (errorMessage: String, httpStatusCode: Int) -> Unit = { _: String, _: Int -> }
) = flow<Resource<DB>> {
    emit(Resource.loading(data = null))
    val localData = fetchFromLocal()

    if (shouldMakeNetworkRequest(localData)) {
        emit(Resource.loading(data = localData))

        when (val apiResponse = makeNetworkRequest()) {
            is ApiSuccessResponse -> {
                processNetworkResponse(apiResponse)
                apiResponse.body?.let { saveResponseData(it) }
                fetchFromLocal()?.let { dbData ->
                    emitAll(flowOf(Resource.success(data = dbData)))
                }
            }

            is ApiErrorResponse -> {
                onNetworkRequestFailed(
                    apiResponse.errorMessage,
                    apiResponse.httpStatusCode
                )
                emitAll(
                    flowOf(
                        Resource.error(
                            errorMessage = apiResponse.errorMessage,
                            httpStatusCode = apiResponse.httpStatusCode,
                            data = fetchFromLocal()
                        )
                    )
                )

            }

            is ApiEmptyResponse -> {
                emit(Resource.emptySuccess())
            }
        }
    } else {
        emitAll(
            flowOf(fetchFromLocal()?.let {
                Resource.success(data = it)
            } ?: Resource.emptySuccess())
        )
    }
}

inline fun <REMOTE> networkSaveResource(
    crossinline makeNetworkRequest: suspend () -> ApiResponse<REMOTE>,
    crossinline saveResponseData: suspend (REMOTE) -> Unit = { },
    crossinline onNetworkRequestFailed: (errorMessage: String, httpStatusCode: Int) -> Unit = { _: String, _: Int -> }
) = flow<Resource<REMOTE>> {
    emit(Resource.loading(data = null))

    when (val apiResponse = makeNetworkRequest()) {
        is ApiSuccessResponse -> {
            apiResponse.body?.let {
                saveResponseData(it)
                emit(Resource.success(data = it))
            }
        }

        is ApiErrorResponse -> {
            onNetworkRequestFailed(apiResponse.errorMessage, apiResponse.httpStatusCode)
            emit(
                Resource.error(
                    errorMessage = apiResponse.errorMessage,
                    httpStatusCode = apiResponse.httpStatusCode,
                    data = null
                )
            )
        }

        is ApiEmptyResponse -> {
            emit(Resource.emptySuccess())
        }
    }
}

/**
 * 获取当前是否夜间模式
 */
val isNightMode get() = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES

/**
 * 当前不是夜间模式
 */
val isNotNightMode get() = AppCompatDelegate.getDefaultNightMode() != AppCompatDelegate.MODE_NIGHT_YES

/**
 * 调试 返回 0 否则 1
 */
val isDebug get() = if (BuildConfig.DEBUG) 0 else 1

/**
 * 长按事件触发震动一下
 */
fun vibrate() {
    val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    vibrator.vibrate(100)
}

fun autoSizeDensity(context: Context, designWidthInDp: Int): Density =
    with(context.resources) {
        val isVertical = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        val scale = displayMetrics.run {
            val sizeInDp = if (isVertical) widthPixels else heightPixels
            sizeInDp.toFloat() / density / designWidthInDp
        }

        Density(
            density = displayMetrics.density * scale,
            fontScale = configuration.fontScale * scale
        )
    }

@Composable
fun AppIcon() {
    Box(
        modifier = Modifier
            .size(80.dp, 80.dp)
            .background(androidx.compose.ui.graphics.Color.White, RoundedCornerShape(6.dp))
    ) {
        Image(
            painter = painterResource(id = R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center)
        )
//        Icon(
//            painter = painterResource(id = R.mipmap.ic_launcher),
//            contentDescription = null,
//            modifier = Modifier.align(Alignment.Center)
//        )
    }
}
