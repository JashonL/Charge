package com.shuoxd.lib.util

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView


fun View.gone() {
    visibility = View.GONE
}

fun View.visible() {
    visibility = View.VISIBLE
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

fun TextView.textLength(): Int {
    return length()
}

fun View.setViewHeight(dpFloat: Float) {
    val newLayoutParams = layoutParams
    newLayoutParams.height = ViewUtil.dp2px(context, dpFloat)
    layoutParams = newLayoutParams
}

fun View.setViewWidth(dpFloat: Float) {
    val newLayoutParams = layoutParams
    newLayoutParams.width = ViewUtil.dp2px(context, dpFloat)
    layoutParams = newLayoutParams
}

/**
 * 设置TextView左边Icon
 */
fun TextView.setDrawableStart(drawable: Drawable) {
    setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
}

fun TextView.setDrawableEnd(drawable: Drawable) {
    setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
}

fun TextView.setDrawableNull() {
    setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
}