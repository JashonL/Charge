package com.shuoxd.lib.util

import com.google.gson.Gson
import java.lang.reflect.Type

object GsonManager {

    const val TAG = "GsonManager"

    private val gson = Gson()

    fun toJson(any: Any?): String {
        return gson.toJson(any)
    }

    fun <T> fromJson(json: String, clazz: Class<T>): T? {
        var result: T? = null
        try {
            result = gson.fromJson(json, clazz)
        } catch (e: Exception) {
            LogUtil.i(TAG, e.toString())
        }
        return result
    }

    fun <T> fromJsonType(json: String?, type: Type): T? {
        var result: T? = null
        try {
            result = gson.fromJson(json, type)
        } catch (e: Exception) {
            LogUtil.i(TAG, e.toString())
        }
        return result
    }
}