package com.shuoxd.charge.model.charge

data  class AuthModel(
    val id:String,
    val userId:String,
    val agentId:String,
    val chargerId:String,
    val alias:String,
    val timezone:String,
    val addTime:String,
    val chargePointVendor:String,
    val chargePointModel:String,
    val chargePointSerialNumber:String,
    val firmwareVersion:String,
    val iccid:String,
    val imei:String,
    val meterType:String,
    val meterSerialNumber:String,
    val charingRate:String,
    val connectorNum:String,
    val usernameLater:String
)


