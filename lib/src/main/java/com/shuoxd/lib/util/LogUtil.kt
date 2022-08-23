package com.growatt.lib.util

import android.util.Log
import com.growatt.lib.BuildConfig

object LogUtil {

    fun i(tag: String, log: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, log)
        }
    }
}