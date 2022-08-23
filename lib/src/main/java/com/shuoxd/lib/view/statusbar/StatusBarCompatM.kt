package com.shuoxd.lib.view.statusbar

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.View

/**
 * After M use system method.
 */
@TargetApi(Build.VERSION_CODES.M)
internal class StatusBarCompatM : StatusBarCompatLollipop() {
    override fun setWindowLightStatusBar(activity: Activity, light: Boolean) {
        val decorView = activity.window.decorView
        if (light) {
            var flags = decorView.systemUiVisibility
            flags = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            decorView.systemUiVisibility = flags
        } else {
            var flags = decorView.systemUiVisibility
            flags = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
            decorView.systemUiVisibility = flags
        }
    }
}