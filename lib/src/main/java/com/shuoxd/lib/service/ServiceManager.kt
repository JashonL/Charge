package com.shuoxd.lib.service

import com.shuoxd.lib.service.account.IAccountService
import com.shuoxd.lib.service.device.IDeviceService
import com.shuoxd.lib.service.http.IHttpService
import com.shuoxd.lib.service.location.ILocationService
import com.shuoxd.lib.service.storage.IStorageService

/**
 * 服务管理类
 * 隔离层:网络加载库、简单的key-value存储、设备信息、账号信息
 */
class ServiceManager private constructor() {

    private val serviceMap = hashMapOf<String, Service>()

    //静态内部类实现单例模式
    companion object {
        fun instance(): ServiceManager = Holder.instance
    }

    private object Holder {
        val instance = ServiceManager()
    }

    fun registerService(@ServiceType type: String, service: Service) {
        serviceMap[type] = service
    }

    fun getService(@ServiceType type: String): Service {
        return serviceMap[type]!!
    }

    interface ServiceInterface {

        fun apiService(): IHttpService

        fun storageService(): IStorageService

        fun deviceService(): IDeviceService

        fun accountService(): IAccountService

        fun locationService(): ILocationService

    }
}