package cn.xihan.age.util.extension


import android.nfc.Tag
import cn.xihan.age.model.AlertDialog


/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/11/6 23:14
 * @介绍 :
 */
/**
 * Clear exception from Throwable
 * @param SNACK 是通过 Snack bar 显示消息的类型
 * @param TOAST 是通过 Toast 显示消息的类型
 * @param INLINE 是显示或隐藏视图警告的类型，例如：密码字段的正确提示中的密码
 * @param ALERT_DIALOG 是显示警报对话框的类型，具有多个属性：标题、消息、肯定、否定和操作
 * @param REDIRECT 是带有视图、动作或完成的自动重定向类型，...
 * @param ON_PAGE 是在中心屏幕上显示消息的类型，可能显示重试按钮
 */
enum class ExceptionType {
    SNACK, TOAST, INLINE, ALERT_DIALOG, REDIRECT, ON_PAGE,
}

enum class RedirectType

sealed class AgeException(
    open val code: Int = -1,
    val type: ExceptionType,
    override val message: String?,
    var retryAction: () -> Unit,
) : Throwable(message) {

    data class AlertException(
        val alertDialog: AlertDialog,
        override val code: Int = -1,
    ) : AgeException(code, ExceptionType.ALERT_DIALOG, null, {})

    data class InlineException(
        override val code: Int = -1,
        val tags: List<Tag>? = null,
    ) : AgeException(code, ExceptionType.INLINE, null, {})

    data class RedirectException(
        val redirect: RedirectType,
        override val code: Int = -1,
    ) : AgeException(code, ExceptionType.REDIRECT, null, {})

    data class SnackBarException(
        override val message: String,
        override val code: Int = -1
    ) : AgeException(code, ExceptionType.SNACK, message, {})

    data class ToastException(
        override val message: String,
        override val code: Int = -1
    ) : AgeException(code, ExceptionType.TOAST, message, {})

    data class OnPageException(
        override val message: String,
        override val code: Int = -1
    ) : AgeException(code, ExceptionType.ON_PAGE, message, {})

}