package com.shuoxd.lib.util

import android.R
import android.content.Context
import android.os.Build
import android.util.TypedValue
import androidx.annotation.ColorInt

object AttrUtils {
    @JvmStatic
    @ColorInt
    fun getThemePrimaryDarkColor(context: Context): Int {
        val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.attr.colorPrimaryDark
        } else {
            //Get colorAccent defined for AppCompat
            context.resources.getIdentifier("colorPrimaryDark", "attr", context.packageName)
        }
        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.data
    }

    @ColorInt
    fun getThemeAccentColor(context: Context): Int {
        val colorAttr: Int = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            R.attr.colorAccent
        } else {
            //Get colorAccent defined for AppCompat
            context.resources.getIdentifier("colorAccent", "attr", context.packageName)
        }
        val outValue = TypedValue()
        context.theme.resolveAttribute(colorAttr, outValue, true)
        return outValue.data
    }
}