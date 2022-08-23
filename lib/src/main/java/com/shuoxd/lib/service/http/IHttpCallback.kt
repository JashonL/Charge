package com.shuoxd.lib.service.http

interface IHttpCallback {

    /**
     * 包含业务异常
     */
    fun onSuccess(response: String?)

    /**
     * 非业务异常
     */
    fun onFailure(errorCode: String, error: String?)

}