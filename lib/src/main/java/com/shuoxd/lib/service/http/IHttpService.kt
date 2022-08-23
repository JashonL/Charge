package com.shuoxd.lib.service.http

import com.shuoxd.lib.LibApplication
import com.shuoxd.lib.service.Service
import java.io.File

/**
 * 网络请求接口
 */
abstract class IHttpService : Service {

    abstract fun host(): String

    abstract fun setHost(host: String)

    protected fun generateHeads(): Map<String, String> {
        return hashMapOf<String, String>().apply {
            put("deviceType", "Android")
            put("token", LibApplication.instance().accountService().token().orEmpty())
            put("lang", LibApplication.instance().deviceService().getAppLang())
            put("appVersion", LibApplication.instance().deviceService().getAppVersionName())
        }
    }

    /**
     * 根据传进来的路径生成完整的请求url
     * @param urlOrApi 完整请求路径或者接口
     */
    protected fun generateUrl(urlOrApi: String): String {
        return if (urlOrApi.startsWith("http")) urlOrApi else host() + urlOrApi
    }

    /**
     * post请求方法，无参
     */
    fun post(urlOrApi: String, callback: IHttpCallback) {
        postForm(urlOrApi, hashMapOf(), callback)
    }

    /**
     * post表单
     */
    abstract fun postForm(urlOrApi: String, params: Map<String, String>, callback: IHttpCallback)

    /**
     * postJson
     */
    abstract fun postJson(urlOrApi: String, params: Map<String, String>, callback: IHttpCallback)

    /**
     * 上传文件
     */
    abstract fun postFile(
        urlOrApi: String,
        params: Map<String, String>,
        file: File?,
        callback: IHttpCallback
    )

    /**
     * get请求方法，无参
     */
    fun httpGet(urlOrApi: String, callback: IHttpCallback) {
        httpGet(urlOrApi, hashMapOf(), callback)
    }

    /**
     * get请求方法，有参
     */
    abstract fun httpGet(urlOrApi: String, params: Map<String, String>, callback: IHttpCallback)

}