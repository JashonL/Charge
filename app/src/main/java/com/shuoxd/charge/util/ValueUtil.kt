package com.shuoxd.charge.util

import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.lib.util.Util

/**
 * 数值换算工具，根据规则换算出单位
 */
object ValueUtil {
    /**
     * 电量单位：数值转换，基础单位是kWh
     */
    fun valueFromKWh(kwhValue: Double?): Pair<String, String> {
        return when {
            kwhValue == null -> {
                Pair("0", MainApplication.instance().getString(R.string.m56_kwh))
            }
            kwhValue < 100000 -> {
                Pair(
                    Util.getDoubleText(kwhValue),
                    MainApplication.instance().getString(R.string.m56_kwh)
                )
            }
            else -> {
                Pair(
                    Util.getDoubleText(kwhValue / 1000),
                    MainApplication.instance().getString(R.string.m57_mwh)
                )
            }
        }
    }

    /**
     * 功率单位(平均)：数值转换，基础单位是w
     */
    fun valueFromW(wValue: Double?): Pair<String, String> {
        return when {
            wValue == null -> {
                Pair("0", MainApplication.instance().getString(R.string.m60_kw))
            }
            wValue < 1000000000 -> {
                Pair(
                    Util.getDoubleText(wValue / 1000),
                    MainApplication.instance().getString(R.string.m60_kw)
                )
            }
            else -> {
                Pair(
                    Util.getDoubleText(wValue / 1000000),
                    MainApplication.instance().getString(R.string.m61_mw)
                )
            }
        }
    }

    /**
     * 功率单位（峰值）：数值转换，基础单位是w
     * 目前只有组件总功率使用到
     */
    fun valueFromWp(wValue: Double?): Pair<String, String> {
        return when {
            wValue == null -> {
                Pair("0", MainApplication.instance().getString(R.string.m58_kwp))
            }
            wValue < 100000000 -> {
                Pair(
                    Util.getDoubleText(wValue / 1000),
                    MainApplication.instance().getString(R.string.m58_kwp)
                )
            }
            else -> {
                Pair(
                    Util.getDoubleText(wValue / 1000000),
                    MainApplication.instance().getString(R.string.m59_mwp)
                )
            }
        }
    }

    /**
     * 重量单位：数值转换，基础单位是kg
     */
    fun valueFromKG(kgValue: Double?): Pair<String, String> {
        return when {
            kgValue == null -> {
                Pair("0", MainApplication.instance().getString(R.string.m67_kg))
            }
            kgValue < 100000 -> {
                Pair(
                    Util.getDoubleText(kgValue),
                    MainApplication.instance().getString(R.string.m67_kg)
                )
            }
            else -> {
                Pair(
                    Util.getDoubleText(kgValue / 1000),
                    MainApplication.instance().getString(R.string.m68_t)
                )
            }
        }
    }
}