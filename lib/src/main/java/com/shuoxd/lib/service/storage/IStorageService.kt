package com.shuoxd.lib.service.storage

import com.shuoxd.lib.service.Service

interface IStorageService : Service {

    fun put(key: String, value: String?)

    fun put(key: String, value: Boolean)

    fun put(key: String, value: Long)

    fun put(key: String, value: Int)

    fun put(key: String, value: Float)

    fun getString(key: String, defValue: String?): String?

    fun getBoolean(key: String, defValue: Boolean): Boolean

    fun getLong(key: String, defValue: Long): Long

    fun getInt(key: String, defValue: Int): Int

    fun getFloat(key: String, defValue: Float): Float

    fun clear()
}