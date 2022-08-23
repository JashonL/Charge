package com.shuoxd.charge.service.location

import android.app.Application
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.growatt.lib.service.location.ILocationService
import com.growatt.lib.service.location.LocationInfo
import com.growatt.lib.service.location.OnLocationListener

/**
 * 高德定位解决方案
 * 文档地址如下
 * https://lbs.amap.com/api/android-location-sdk/guide/android-location/getlocation
 */
class AmapLocationService : ILocationService, AMapLocationListener {

    private var locationClient: AMapLocationClient? = null

    private val listeners = mutableSetOf<OnLocationListener>()

    init {

    }

    override fun init(application: Application) {
        //高德开发文档提示需要try catch
        try {
            locationClient = AMapLocationClient(application)
        } catch (e: Exception) {

        }

        locationClient?.setLocationListener(this)
        locationClient?.setLocationOption(AMapLocationClientOption().also {
            //高精度定位模式：会同时使用网络定位和GPS定位，优先返回最高精度的定位结果，以及对应的地址描述信息。
            it.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
            //获取一次定位结果
            it.isOnceLocation = true
            //设置是否返回地址信息（默认返回地址信息）
            it.isNeedAddress = true
            //关闭缓存机制，这样定位就不会返回缓存位置了
            it.isLocationCacheEnable = false
        })
    }

    override fun requestLocation() {
        //暂停上次的定位
        locationClient?.stopLocation()
        //重新开启定位功能
        locationClient?.startLocation()
    }

    override fun stopLocation() {
        locationClient?.stopLocation()
    }

    override fun addLocationListener(listener: OnLocationListener) {
        listeners.add(listener)
    }

    override fun removeLocationListener(listener: OnLocationListener) {
        listeners.remove(listener)
    }

    override fun onLocationChanged(location: AMapLocation?) {
        location?.also {
            if (it.errorCode == 0) {
                //定位成功
                for (listener in listeners) {
                    listener.locationSuccess(covertFormAMapLocation(it))
                }
            } else {
                for (listener in listeners) {
                    listener.locationFailure(it.errorInfo)
                }
            }
        }
    }


    private fun covertFormAMapLocation(location: AMapLocation): LocationInfo {
        val latitude = location.latitude
        val longitude = location.longitude
        val address = location.address
        val country = location.country
        val province = location.province
        val city = location.city
        val cityCode = location.cityCode
        return LocationInfo(latitude, longitude, address, country, province, city, cityCode)
    }
}