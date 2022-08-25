package com.shuoxd.charge.model.charge

data class ChargeModel(
    val id: Int?,//
    val userId: Int?,//
    val agentId: Int?,//
    val chargerId: String?,//
    val alias: String?,//
    val timezone: Int?,//
    val lastHeartbeatTime: String?,//
    val chargePointVendor: String?,//
    val chargePointModel: String?,//
    val chargePointSerialNumber: String?,//
    val firmwareVersion: String?,//
    val iccid: String?,//
    val imei: String?,//
    val meterType: String?,//
    val meterSerialNumber: String?,//
    val charingRate: Int,
    val connectorNum: Int?,//
)