package com.shuoxd.lib.service.http

/**
 * 网络响应基本字段
 */
data class HttpResult<T>(var result: String, var msg: String?, var obj: T?) {

    fun isBusinessSuccess(): Boolean {
        return result == "0"
    }

}