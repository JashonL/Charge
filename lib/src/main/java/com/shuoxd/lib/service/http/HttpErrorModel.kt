package com.shuoxd.lib.service.http

/**
 * 包含业务与非业务异常
 * 1.非业务异常是负数开头
 * 2.业务异常是正数开头
 */
data class HttpErrorModel(val errorCode: String, val errorMsg: String?) {

    companion object {
        /**
         * 解析错误
         */
        const val ERROR_CODE_PARSE = "-1"

        /**
         * 网络错误
         */
        const val ERROR_CODE_NETWORK = "-400"

        /**
         * 服务异常
         */
        const val ERROR_CODE_SERVER = "-500"

    }

}