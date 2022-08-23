package com.shuoxd.charge.ui.common.viewmodel

import androidx.lifecycle.MutableLiveData
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseViewModel
import com.growatt.lib.service.location.LocationInfo

/**
 * 高德地图
 */
class AMapViewModel : BaseViewModel(), GeocodeSearch.OnGeocodeSearchListener {
    private val geocodeSearch = GeocodeSearch(MainApplication.instance())

    init {
        geocodeSearch.setOnGeocodeSearchListener(this)
    }

    /**
     * 逆地理编码（坐标转地址）
     */
    val regeocodeSearchedLiveData = MutableLiveData<Pair<LocationInfo?, String?>>()

    /**
     * 通过经纬度获取地址
     */
    fun fetchAddressFromLocation(latitude: Double, longitude: Double) {
        val query =
            RegeocodeQuery(
                LatLonPoint(latitude, longitude),
                100f,
                GeocodeSearch.AMAP
            )
        geocodeSearch.getFromLocationAsyn(query)
    }

    /**
     * 经纬度获取地址回调
     */
    override fun onRegeocodeSearched(result: RegeocodeResult?, rCode: Int) {
        //解析result获取地址描述信息
        if (rCode == 1000) {
            val latitude = result?.regeocodeQuery?.point?.latitude
            val longitude = result?.regeocodeQuery?.point?.longitude
            val address = result?.regeocodeAddress?.formatAddress
            val country = result?.regeocodeAddress?.country
            val province = result?.regeocodeAddress?.province
            val city = result?.regeocodeAddress?.city
            val cityCode = result?.regeocodeAddress?.cityCode
            regeocodeSearchedLiveData.value = Pair(
                LocationInfo(latitude, longitude, address, country, province, city, cityCode),
                null
            )
        } else {
            regeocodeSearchedLiveData.value = Pair(null, "")
        }
    }

    override fun onGeocodeSearched(result: GeocodeResult?, rCode: Int) {

    }
}