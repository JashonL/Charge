package com.growatt.lib.service.http

/**
 * 网络响应基本字段
 */
data class HttpResult<T>(var status_code: String, var msg: String?, var data: T?) {

    fun isBusinessSuccess(): Boolean {
        return status_code == "0"
    }

}