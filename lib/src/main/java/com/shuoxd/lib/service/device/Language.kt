package com.shuoxd.lib.service.device

import android.content.res.Resources
import android.os.Build
import java.util.*

/**
 * 语言
 */
enum class Language(val code: Int, val languageName: String, val locale: Locale?) {

    //跟随系统
    FOLLOW_SYSTEM(-1, "SYSTEM_DEFAULT", Locale.ENGLISH),

    //简体中文
    SIMPLIFIED_CHINESE(0, "中文", Locale.SIMPLIFIED_CHINESE),

    //繁体中文
    TRADITIONAL_CHINESE(1, "繁體中文", Locale("zh")),

    //英语
    ENGLISH(2, "English", Locale.ENGLISH);

//    //法语
//    FRENCH(3, "Français", Locale.FRENCH),
//
//    //希腊语
//    GREEK(4, "Greek", Locale("el")),
//
//    //德语
//    GERMAN(5, "German", Locale.GERMAN),
//
//    //荷兰
//    NEDERLAND(6, "Nederland", Locale("nl")),
//
//    //意大利语
//    ITALIANO(7, "Italiano", Locale.ITALIAN),
//
//    //日本语
//    JAPANESE(8, "日本語", Locale.JAPANESE),
//
//    //波兰
//    POLISH(9, "Polish", Locale("pl")),
//
//    //葡萄牙
//    PORTUGUESE(10, "Português", Locale("pt")),
//
//    //西班牙
//    SPANISH(11, "Español", Locale("es")),
//
//    //土耳其
//    TURKISH(12, "Türkçe", Locale("tr")),
//
//    //越南语
//    VIETNAMESE(13, "Vietnamese", Locale("vi")),
//
//    //韩文
//    KOREAN(14, "한국어", Locale.KOREAN),
//
//    //泰语
//    THAI(15, "ไทย", Locale("th"));

    companion object {
        fun languageNames(): Array<String> {
            val list = ArrayList<String>()
            for (language in values()) {
                list.add(language.languageName)
            }
            return list.toTypedArray()
        }

        fun fromCode(code: Int): Language {
            for (language in values()) {
                if (language.code == code) {
                    return language
                }
            }
            return FOLLOW_SYSTEM
        }

        fun fromLocale(locale: Locale): Language {
            for (language in values()) {
                if (language.locale == locale) {
                    return language
                }
            }
            return FOLLOW_SYSTEM
        }

        /**
         * 获取系统默认的语言
         */
        fun getSystemDefaultLocale(): Locale {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) Resources.getSystem().configuration.locales[0] else Locale.getDefault()
        }
    }

}