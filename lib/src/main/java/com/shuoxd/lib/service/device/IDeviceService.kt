package com.shuoxd.lib.service.device

import com.shuoxd.lib.service.Service

interface IDeviceService : Service {

    /**
     * 获取app版本名称（版本号）
     */
    fun getAppVersionName(): String

    /**
     * 获取app版本号
     */
    fun getAppVersionCode(): Int

    /**
     * 获取当前手机系统版本号
     *
     * @return
     */
    fun deviceVersion(): String

    /**
     * 获取手机型号
     */
    fun deviceModel(): String

    /**
     * 获取手机厂商
     */
    fun deviceBrand(): String

    /**
     * 获取设备唯一标识，应用生命周期唯一，卸载后失效
     */
    fun getDeviceId(): String

    /**
     * 获取屏幕宽
     */
    fun getDeviceWidth(): Int

    /**
     * 获取屏幕高
     */
    fun getDeviceHeight(): Int

    /**
     * 获取当前设备时间
     */
    fun currentTimeMillis(): Long

    /**
     * 获取应用语言
     */
    fun getAppLanguage(): Language

    /**
     * 设置应用语言
     */
    fun setAppLanguage(language: Language)

    /**
     * 获取语言代码-对应上传给服务端的字段
     */
    fun getAppLang(): String

    /**
     * 获取屏幕密度
     */
    fun screenDensity(): Float


}