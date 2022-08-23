package com.shuoxd.lib.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 日期格式化工具类
 */
object DateUtils {
    val yyyy_format = SimpleDateFormat("yyyy", Locale.US)
    val yyyy_MM_format = SimpleDateFormat("yyyy-MM", Locale.US)
    val yyyy_MM_dd_format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    val yyyy_MM_dd_HH_mm_ss_format = SimpleDateFormat("yyyy-MM-dd HH-mm-ss", Locale.US)
    val yyyy_MM_dd_HH_mm_ss_format_2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
    val yyyy_MM_dd_HH_mm_format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US)
    val HH_mm_format = SimpleDateFormat("HH:mm", Locale.US)
    val HH_mm_ss_format = SimpleDateFormat("HH:mm:ss", Locale.US)
    val MM_dd_HH_mm_format = SimpleDateFormat("MM-dd HH:mm", Locale.US)
    val HH_mm_ss_SSS_format = SimpleDateFormat("HH:mm:ss::SSS", Locale.US)

    fun yyyy_format(date: Date): String {

        return yyyy_format.format(date)
    }

    fun yyyy_MM_format(date: Date): String {
        return yyyy_MM_format.format(date)
    }

    fun yyyy_MM_dd_format(date: Date): String {
        return yyyy_MM_dd_format.format(date)
    }

    fun yyyy_MM_dd_format(time: Long): String {
        return yyyy_MM_dd_format.format(Date(time))
    }

    fun yyyy_MM_dd_hh_mm_ss_format(date: Date): String {
        return yyyy_MM_dd_HH_mm_ss_format.format(date)
    }

    fun yyyy_MM_dd_hh_mm_ss_format(time: Long): String {
        return yyyy_MM_dd_hh_mm_ss_format(Date(time))
    }

    fun yyyy_MM_dd_HH_mm_ss_format_2(time: Long): String {
        return yyyy_MM_dd_HH_mm_ss_format_2.format(Date(time))
    }

    fun HH_mm_format(date: Date): String {
        return HH_mm_format.format(date)
    }

    fun from_HH_mm_format(dateString: String): Date {
        return try {
            HH_mm_format.parse(dateString)
        } catch (e: ParseException) {
            Date()
        }
    }

    fun MM_dd_HH_mm_format(date: Date): String {
        return MM_dd_HH_mm_format.format(date)
    }

    fun HH_mm_ss_format(date: Date): String {
        return HH_mm_ss_format.format(date)
    }

    fun HH_mm_ss_SSS_format(date: Date): String {
        return HH_mm_ss_SSS_format.format(date)
    }

    fun from_yyyy_MM_dd_HH_mm_ss_format_2(date: String): Date {
        return try {
            yyyy_MM_dd_HH_mm_ss_format_2.parse(date)
        } catch (e: ParseException) {
            Date()
        }
    }

    fun from_yyyy_MM_dd_format(date: String): Date {
        return try {
            yyyy_MM_dd_format.parse(date)
        } catch (e: ParseException) {
            Date()
        }
    }

    /**
     * 当前日期
     * @return yyyy-MM-dd
     */
    fun getCurrentDate(): String {
        return yyyy_MM_dd_format.format(Date())
    }

    /**
     * 获取昨天的日期
     * @return yyyy-MM-dd
     */
    fun getYesterdayDate(): String {
        return yyyy_MM_dd_format.format(addDateDays(Date(), -1))
    }

    /**
     * 增加或者减少日期
     */
    fun addDateDays(date: Date, days: Int): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.add(Calendar.DAY_OF_YEAR, days)
        return cal.time
    }

    fun convertDate(date: Long, format: SimpleDateFormat): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = date
        return format.format(calendar.time)
    }
}