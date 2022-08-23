package com.growatt.lib

import android.app.Application
import com.growatt.lib.service.ServiceManager

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