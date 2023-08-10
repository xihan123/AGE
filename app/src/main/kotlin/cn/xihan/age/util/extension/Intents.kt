@file:Suppress("unused")

package cn.xihan.age.util.extension

import android.Manifest.permission.CALL_PHONE
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresPermission
import androidx.core.os.bundleOf

inline fun <reified T> Context.intentOf(vararg pairs: Pair<String, *>) =
    intentOf<T>(bundleOf(*pairs))

inline fun <reified T> Context.intentOf(bundle: Bundle) =
    Intent(this, T::class.java).putExtras(bundle)

fun <T> Activity.intentExtras(name: String) = lazy {
    intent.extras?.get(name) as T
}

fun <T> Activity.intentExtras(name: String, default: T) = lazy {
    intent.extras?.get(name) ?: default
}

fun <T> Activity.safeIntentExtras(name: String) = lazy<T> {
    intent.extras?.get(name) as? T ?: throw IllegalStateException("Intent extras $name not found")
}

fun dial(phoneNumber: String): Boolean =
    Intent(Intent.ACTION_DIAL, Uri.parse("tel:${Uri.encode(phoneNumber)}"))
        .startForActivity()

@RequiresPermission(CALL_PHONE)
fun makeCall(phoneNumber: String): Boolean =
    Intent(Intent.ACTION_CALL, Uri.parse("tel:${Uri.encode(phoneNumber)}"))
        .startForActivity()

fun sendSMS(phoneNumber: String, content: String): Boolean =
    Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${Uri.encode(phoneNumber)}"))
        .putExtra("sms_body", content)
        .startForActivity()

fun browse(url: String, newTask: Boolean = false): Boolean =
    Intent(Intent.ACTION_VIEW, Uri.parse(url))
        .apply { if (newTask) newTask() }
        .startForActivity()

fun email(email: String, subject: String? = null, text: String? = null): Boolean =
    Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:"))
        .putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        .putExtra(Intent.EXTRA_SUBJECT, subject)
        .putExtra(Intent.EXTRA_TEXT, text)
        .startForActivity()

fun installAPK(uri: Uri): Boolean =
    Intent(Intent.ACTION_VIEW)
        .setDataAndType(uri, "application/vnd.android.package-archive")
        .newTask()
        .grantReadUriPermission()
        .startForActivity()

fun Intent.startForActivity(): Boolean =
    try {
        topActivity.startActivity(this)
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

fun Intent.clearTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)

fun Intent.clearTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

fun Intent.newDocument(): Intent = addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

fun Intent.excludeFromRecents(): Intent = addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)

fun Intent.multipleTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)

fun Intent.newTask(): Intent = addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

fun Intent.noAnimation(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)

fun Intent.noHistory(): Intent = addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)

fun Intent.singleTop(): Intent = addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)

fun Intent.grantReadUriPermission(): Intent = apply {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    }
}
