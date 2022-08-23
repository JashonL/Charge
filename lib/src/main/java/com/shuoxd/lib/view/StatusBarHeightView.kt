package com.shuoxd.lib.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import com.shuoxd.lib.util.ViewUtil
import com.shuoxd.lib.view.statusbar.StatusBarCompat

class StatusBarHeightView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attributeSet, defStyleAttr) {

    init {
        ViewUtil.getActivityFromContext(context)?.also {
            StatusBarCompat.translucentStatusBar(it, true)
            StatusBarCompat.setWindowLightStatusBar(it, true)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(
            widthMeasureSpec, MeasureSpec.makeMeasureSpec(
                ViewUtil.getStatusBarHeight(
                    context
                ), MeasureSpec.EXACTLY
            )
        )
    }
}