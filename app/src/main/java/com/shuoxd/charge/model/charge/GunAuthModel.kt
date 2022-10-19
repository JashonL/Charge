package com.shuoxd.charge.model.charge

data class GunAuthModel(
    val id: Int?,//
    val chargerId: String?,//
    val connectorId: Int?,//
    val time: String?,//
    val status: Int?,//
    val errorCode: Int?,//
    val authorize: Int?,//
    val vendorErrorCode: String?,//
    val info: String?,//
    val vendorId: String?,//
    val connectEV: Int?,//
    val disconnectEV: Int?,//
)

