package com.shuoxd.lib.service.device

import android.content.Context
import android.content.pm.PackageInfo
import com.shuoxd.lib.LibApplication

class DefaultDeviceService(private val context: Context) : IDeviceService {

    companion object {
        val APP_LANGUAGE = "app_language"
    }

    private lateinit var packageInfo: PackageInfo

    init {
        DeviceId.init(LibApplication.instance())
        packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    }

    override fun getAppVersionName(): String {
        return packageInfo.versionName
    }

    override fun getAppVersionCode(): Int {
        return packageInfo.versionCode
    }

    override fun deviceVersion(): String {
        return android.os.Build.VERSION.RELEASE
    }

    override fun deviceModel(): String {
        return android.os.Build.MODEL
    }

    override fun deviceBrand(): String {
        return android.os.Build.BRAND
    }

    override fun getDeviceId(): String {
        return DeviceId.instance().id()
    }

    override fun getDeviceWidth(): Int {
        return context.resources.displayMetrics.widthPixels
    }

    override fun getDeviceHeight(): Int {
        return context.resources.displayMetrics.heightPixels
    }

    override fun currentTimeMillis(): Long {
        return System.currentTimeMillis()
    }

    override fun getAppLanguage(): Language {
        val code = LibApplication.instance().storageService()
            .getInt(APP_LANGUAGE, Language.FOLLOW_SYSTEM.code)
        return Language.fromCode(code)
    }

    override fun setAppLanguage(language: Language) {
        LibApplication.instance().storageService().put(APP_LANGUAGE, language.code)
    }

    /**
     * 目前支持中文简体（zh_CN），中文繁体（zh_TW），英文（en）
     */
    override fun getAppLang(): String {
        return when (getAppLanguage()) {
            Language.SIMPLIFIED_CHINESE -> "zh_CN"
            Language.TRADITIONAL_CHINESE -> "zh_TW"
            Language.ENGLISH -> "en"
            else -> {
                val systemDefaultLocale = Language.getSystemDefaultLocale()
                if (systemDefaultLocale.language == "zh") {
                    return when (systemDefaultLocale.country) {
                        "HK", "MO", "TW" -> "zh_TW"
                        else -> {
                            "zh_CN"
                        }
                    }
                }
                systemDefaultLocale.language
            }
        }
    }

    override fun screenDensity(): Float {
        return context.resources.displayMetrics.density
    }
}