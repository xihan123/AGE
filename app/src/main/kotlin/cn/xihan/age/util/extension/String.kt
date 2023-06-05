package cn.xihan.age.util.extension

import android.text.format.Formatter
import androidx.core.util.PatternsCompat
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.math.RoundingMode
import java.security.MessageDigest
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/5/5 20:56
 * @介绍 :
 */
/**
 * 统一时间格式
 */
const val DATE_FORMAT = "yyyy-MM-dd HH:mm:ss"

inline val randomUUIDString: String
    get() = UUID.randomUUID().toString()

fun Long.toFileSizeString(): String =
    Formatter.formatFileSize(application, this)

fun Long.toShortFileSizeString(): String =
    Formatter.formatShortFileSize(application, this)

fun String.limitLength(length: Int): String =
    if (this.length <= length) this else substring(0, length)

fun String.isDomainName(): Boolean =
    PatternsCompat.DOMAIN_NAME.matcher(this).matches()

fun String.isEmail(): Boolean =
    PatternsCompat.EMAIL_ADDRESS.matcher(this).matches()

fun String.isIP(): Boolean =
    PatternsCompat.IP_ADDRESS.matcher(this).matches()

/**
 *  Regular expression pattern to match most part of RFC 3987
 *  Internationalized URLs, aka IRIs.
 */
fun String.isWebUrl(): Boolean =
    PatternsCompat.WEB_URL.matcher(this).matches()

fun String.isNotWebUrl(): Boolean =
    !isWebUrl() && this.startsWith("age_")

fun String.isJson(): Boolean =
    try {
        JSONObject(this)
        true
    } catch (e: Exception) {
        false
    }

/**
 * 输入纯数字不为空
 */
fun String?.isNotNullOrBlankAndNumber(): Boolean {
    return !this.isNullOrBlank() && this.matches(Regex("\\d+"))
}

/**
 * 输入长度1位纯数字不为空
 */
fun String?.isNotNullOrBlankAndNumberAndLength(): Boolean {
    return !this.isNullOrBlank() && this.matches(Regex("[0-5]+")) && this.length == 1
}

/**
 * 字符串不为Null 和空
 */
fun CharSequence?.isNotNullOrBlank(): Boolean = !this.isNullOrBlank()


fun Float.toNumberString(
    fractionDigits: Int = 2,
    minIntDigits: Int = 1,
    isGrouping: Boolean = false,
    isHalfUp: Boolean = true,
): String =
    toDouble().toNumberString(fractionDigits, minIntDigits, isGrouping, isHalfUp)

fun Double.toNumberString(
    fractionDigits: Int = 2,
    minIntDigits: Int = 1,
    isGrouping: Boolean = false,
    isHalfUp: Boolean = true,
): String =
    (NumberFormat.getInstance() as DecimalFormat).apply {
        isGroupingUsed = isGrouping
        roundingMode = if (isHalfUp) RoundingMode.HALF_UP else RoundingMode.DOWN
        minimumIntegerDigits = minIntDigits
        minimumFractionDigits = fractionDigits
        maximumFractionDigits = fractionDigits
    }.format(this)

/**
 * 获取文件的MD5
 */
fun File.md5(): String {
    val digest = MessageDigest.getInstance("MD5")
    val inputStream = FileInputStream(this)
    val buffer = ByteArray(8192)
    var length = inputStream.read(buffer)
    while (length != -1) {
        digest.update(buffer, 0, length)
        length = inputStream.read(buffer)
    }
    val md5Bytes = digest.digest()
    return md5Bytes.bytesToHex()
}

/**
 * 将字节数组转换为16进制字符串
 */
fun ByteArray.bytesToHex(): String {
    val sb = StringBuilder()
    for (b in this) {
        val hex = Integer.toHexString(0xFF and b.toInt())
        if (hex.length == 1) {
            sb.append('0')
        }
        sb.append(hex)
    }
    return sb.toString()
}

/**
 * 获取最新时间
 */
val nowTime: String
    get() = System.currentTimeMillis().toDate()

/**
 * 时间戳 转为 yyyy-MM-dd HH:mm:ss
 */
fun Long.toDate(): String =
    SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).format(Date(this))

