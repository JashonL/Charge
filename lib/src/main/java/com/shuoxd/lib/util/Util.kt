package com.shuoxd.lib.util

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.icu.text.TimeZoneFormat
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Process
import android.text.TextUtils
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.shuoxd.lib.LibApplication
import org.json.JSONObject
import java.io.File
import java.math.BigDecimal
import java.util.*
import kotlin.system.exitProcess

object Util {

    /**
     * 获取包名（进程名）
     */
    fun getProcessNameByPID(context: Context, pid: Int): String? {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        for (processInfo in manager.runningAppProcesses) {
            if (processInfo.pid == pid) {
                return processInfo.processName
            }
        }
        return ""
    }

    /**
     * 重启app
     */
    fun restartApp(context: Context) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)
        Process.killProcess(Process.myPid())
        exitProcess(0)
    }

    /**
     * 返回桌面
     */
    fun backToDesktop(context: Context?) {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.addCategory(Intent.CATEGORY_HOME)
        context?.startActivity(intent)
    }

    /**
     * 判断系统定位功能是否开启
     * @return true 表示开启
     */
    fun isSystemLocationEnable(): Boolean {
        return try {
            val locationManager = LibApplication.instance()
                .getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val network = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            gps || network
        } catch (e: Exception) {
            false
        }
    }

    /**
     * 将图片添加到手机相册
     */
    fun galleryAddPic(currentPhotoPath: String) {
        Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).also { mediaScanIntent ->
            val f = File(currentPhotoPath)
            mediaScanIntent.data = Uri.fromFile(f)
            LibApplication.instance().sendBroadcast(mediaScanIntent)
        }
    }

    /**
     * 通过key去获取json中的value
     * @param json json字符串
     * @param key key
     */
    fun getValueForJsonKey(json: String?, key: String?): String? {
        if (TextUtils.isEmpty(json) || TextUtils.isEmpty(key)) {
            return null
        }
        return try {
            val jsonObject = JSONObject(json!!)
            jsonObject.getString(key!!)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Double转为字符串，四舍五入后保留newScale位小数点，去掉末尾的0
     * @param newScale 小数点后面保留的位数
     * 添加JvmOverloads注解，可以在java代码中调用这个有默认参数的方法
     */
    @JvmOverloads
    fun getDoubleText(value: Double?, newScale: Int = 1): String {
        if (value == null) {
            return "0"
        }
        return BigDecimal(value).setScale(newScale, BigDecimal.ROUND_HALF_UP).stripTrailingZeros()
            .toPlainString()
    }

    fun getFragmentManagerForContext(context: Context?): FragmentManager? {
        if (context is FragmentActivity) {
            return context.supportFragmentManager
        }
        return null
    }


    /**
     * 获取手机型号
     */
    fun getPhoneModel(): String {
        return Build.MODEL
    }


    fun getVersion(context: Context): String? {
        try {
            return context.packageManager.getPackageInfo(
                context.packageName, 0
            ).versionName
        } catch (e: NameNotFoundException) {
            e.printStackTrace()
        }
        return "1.0"
    }


    fun getTimeZone(): String {

        var tz: String
        val timezone: TimeZone = TimeZone.getDefault()
        val displayName = timezone.getDisplayName(false, TimeZone.SHORT)
        val substring = displayName.substring(4)
        val split = substring.split(":")
        tz = split[0].toInt().toString()
        if (split[1] == "30") {
            tz += "0.5"
        }
        return tz

    }


}