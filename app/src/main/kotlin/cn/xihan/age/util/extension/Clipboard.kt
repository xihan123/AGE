package cn.xihan.age.util.extension

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2022/5/1 12:26
 * @介绍 : 剪切板相关
 */
fun CharSequence.copyToClipboard(label: CharSequence? = null) =
    (application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText(label, this))

fun Intent.copyToClipboard(label: CharSequence? = null) =
    (application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newIntent(label, this))

fun getTextFromClipboard(): CharSequence? =
    (application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .primaryClip?.takeIf { it.itemCount > 0 }?.getItemAt(0)?.coerceToText(application)

fun clearClipboard() =
    (application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .setPrimaryClip(ClipData.newPlainText(null, ""))

fun doOnClipboardChanged(listener: ClipboardManager.OnPrimaryClipChangedListener): ClipboardManager.OnPrimaryClipChangedListener =
    (application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .addPrimaryClipChangedListener(listener).let { listener }

fun ClipboardManager.OnPrimaryClipChangedListener.cancel() =
    (application.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager)
        .removePrimaryClipChangedListener(this)
