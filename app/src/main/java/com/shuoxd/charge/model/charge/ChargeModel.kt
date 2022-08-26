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
    val charingRate: Double?,
    val connectorNum: Int?,//
)


/*
{"id":33,
    "userId":4,
    "agentId":1,
    "chargerId":"CP2022",
    "alias":null,
    "timezone":1.0,
    "lastHeartbeatTime":"2022-08-19 03:56:50.0",
    "chargePointVendor":"Teison Charger",
    "chargePointModel":"DC_40K_2P_CCS",
    "chargePointSerialNumber":"CP2022",
    "firmwareVersion":"TS-DC40K2P_H1S05L02",
    "iccid":null,
    "imei":null,
    "meterType":null,
    "meterSerialNumber":null,
    "charingRate":0.0,
    "connectorNum":2}*/
