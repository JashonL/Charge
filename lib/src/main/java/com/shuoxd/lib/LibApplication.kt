package com.shuoxd.lib

import android.app.Application
import com.shuoxd.lib.service.ServiceManager

abstract class LibApplication : Application(), ServiceManager.ServiceInterface {

    companion object {
        private lateinit var instance: LibApplication
        fun instance() = instance
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}