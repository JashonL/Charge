package com.shuoxd.lib.service.storage

import android.content.Context
import android.content.Context.MODE_PRIVATE

/**
 * 默认的本地存储服务
 */
class DefaultStorageService(context: Context, name: String = "shuoxd_charge") : IStorageService {


    companion object {
        val CURRENT_CHARGE = "current_charge"
    }


    private val storage by lazy(LazyThreadSafetyMode.NONE) {
        context.getSharedPreferences(name, MODE_PRIVATE)
    }

    override fun put(key: String, value: String?) {
        storage.edit().putString(key, value).apply()
    }

    override fun put(key: String, value: Boolean) {
        storage.edit().putBoolean(key, value).apply()
    }

    override fun put(key: String, value: Long) {
        storage.edit().putLong(key, value).apply()
    }

    override fun put(key: String, value: Int) {
        storage.edit().putInt(key, value).apply()
    }

    override fun put(key: String, value: Float) {
        storage.edit().putFloat(key, value).apply()
    }

    override fun getString(key: String, defValue: String?): String? {
        return storage.getString(key, defValue)
    }

    override fun getBoolean(key: String, defValue: Boolean): Boolean {
        return storage.getBoolean(key, defValue)
    }

    override fun getLong(key: String, defValue: Long): Long {
        return storage.getLong(key, defValue)
    }

    override fun getInt(key: String, defValue: Int): Int {
        return storage.getInt(key, defValue)
    }

    override fun getFloat(key: String, defValue: Float): Float {
        return storage.getFloat(key, defValue)
    }

    override fun clear() {
        storage.edit().clear().apply()
    }
}