package com.growatt.lib.service.location

import android.app.Application
import com.growatt.lib.service.Service

interface ILocationService : Service {

    fun init(application: Application)

    /**
     * 请求位置信息
     */
    fun requestLocation()

    /**
     * 停止定位
     */
    fun stopLocation()

    fun addLocationListener(listener: OnLocationListener)

    fun removeLocationListener(listener: OnLocationListener)

}


interface OnLocationListener {

    fun locationSuccess(locationInfo: LocationInfo)

    fun locationFailure(errorMsg: String)

}