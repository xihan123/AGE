@file:Suppress("unused")

package cn.xihan.age.util.extension

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment


fun toast(message: CharSequence?): Toast =
    mainThreadForResult { application.toast(message) }

fun toast(@StringRes message: Int): Toast =
    mainThreadForResult { application.toast(message) }

fun longToast(message: CharSequence?): Toast =
    mainThreadForResult { application.longToast(message) }

fun Context.toast(message: CharSequence?): Toast =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).fixBadTokenException().apply { show() }

fun Context.toast(@StringRes message: Int): Toast =
    Toast.makeText(this, message, Toast.LENGTH_SHORT).fixBadTokenException().apply { show() }

fun Fragment.longToast(message: CharSequence?): Toast =
    requireActivity().longToast(message)

fun Fragment.longToast(@StringRes message: Int): Toast =
    requireActivity().longToast(message)

fun Context.longToast(message: CharSequence?): Toast =
    Toast.makeText(this, message, Toast.LENGTH_LONG).fixBadTokenException().apply { show() }

fun Context.longToast(@StringRes message: Int): Toast =
    Toast.makeText(this, message, Toast.LENGTH_LONG).fixBadTokenException().apply { show() }

fun Toast.fixBadTokenException(): Toast = apply {
    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
        try {
            @SuppressLint("DiscouragedPrivateApi")
            val tnField = Toast::class.java.getDeclaredField("mTN")
            tnField.isAccessible = true
            val tn = tnField.get(this)

            val handlerField = tnField.type.getDeclaredField("mHandler")
            handlerField.isAccessible = true
            val handler = handlerField.get(tn) as Handler

            val looper = checkNotNull(Looper.myLooper()) {
                "Can't toast on a thread that has not called Looper.prepare()"
            }
            handlerField.set(tn, object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    try {
                        handler.handleMessage(msg)
                    } catch (ignored: WindowManager.BadTokenException) {
                    }
                }
            })
        } catch (ignored: IllegalAccessException) {
        } catch (ignored: NoSuchFieldException) {
        }
    }
}
