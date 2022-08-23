package com.shuoxd.lib.service.location

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LocationInfo @JvmOverloads constructor(
    //纬度
    val latitude: Double? = null,
    //经度
    val longitude: Double? = null,
    //地址描述
    val address: String? = "",
    //国家
    val country: String? = "",
    //省
    val province: String? = "",
    //城市
    val city: String? = "",
    //城市编码
    val cityCode: String? = "",
) : Parcelable {

    fun latitudeStr(): String {
        return String.format("%.2f", latitude)
    }

    fun longitudeStr(): String {
        return String.format("%.2f", longitude)
    }

}