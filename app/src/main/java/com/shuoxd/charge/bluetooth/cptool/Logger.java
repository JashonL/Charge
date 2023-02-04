package com.shuoxd.charge.bluetooth.cptool;

import android.util.Log;

import com.shuoxd.charge.BuildConfig;

public class Logger {
    public static boolean enabled = BuildConfig.DEBUG;

    public static void d(String tag, String msg) {
        if (enabled) {
            Log.d(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (enabled) {
            Log.e(tag, msg);
        }
    }

    public static void e(String tag, String msg, Throwable e) {
        if (enabled) {
            Log.e(tag, msg, e);
        }
    }
}
