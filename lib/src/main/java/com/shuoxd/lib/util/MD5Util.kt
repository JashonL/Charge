package com.shuoxd.lib.util

import java.security.MessageDigest

object MD5Util {

    private const val ENCRYPT_KEY = 0xFF

    /**
     * MD5加密
     */
    fun md5(password: String): String? {
        return try {
            val cacheChar = StringBuilder()
            val defaultByte = password.toByteArray()
            val md5 = MessageDigest.getInstance("MD5")
            md5.reset()
            md5.update(defaultByte)
            val resultByte = md5.digest()
            for (i in resultByte.indices) {
                val hex = Integer.toHexString(ENCRYPT_KEY and resultByte[i].toInt())
                if (hex.length == 1) {
                    cacheChar.append("0")
                }
                cacheChar.append(hex)
            }
            cacheChar.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}