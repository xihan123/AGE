package cn.xihan.age.util.extension

import android.app.Activity
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.view.View
import java.util.concurrent.TimeUnit

/**
 * @项目名 : AGE动漫
 * @作者 : MissYang
 * @创建时间 : 2023/5/27 9:14
 * @介绍 :
 */
fun View.gone() {
    visibility = View.GONE
}

fun View.isGone(): Boolean {
    return visibility == View.GONE
}

fun View.showIfGone() {
    if (this.isGone()) {
        this.visible()
    }
}

fun View.hideIfVisible() {
    if (!this.isGone()) {
        this.gone()
    }
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun Activity.appGraying() {
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setSaturation(0f)
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    window.decorView.setLayerType(View.LAYER_TYPE_HARDWARE, paint)
}

fun View.throttleClick(
    interval: Long = 500,
    unit: TimeUnit = TimeUnit.MILLISECONDS,
    block: View.() -> Unit,
) {
    setOnClickListener(ThrottleClickListener(interval, unit, block))
}

class ThrottleClickListener(
    private val interval: Long = 500,
    private val unit: TimeUnit = TimeUnit.MILLISECONDS,
    private var block: View.() -> Unit,
) : View.OnClickListener {
    private var lastTime: Long = 0

    override fun onClick(v: View) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime > unit.toMillis(interval)) {
            lastTime = currentTime
            block(v)
        }
    }
}