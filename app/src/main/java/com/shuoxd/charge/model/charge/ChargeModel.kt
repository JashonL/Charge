package com.shuoxd.charge.model.charge

class ChargeModel(
    val id: Int?,//设备类型
    val userId: Int?,//设备型号
    val agentId: Int?,//设备SN
    val chargerId: String?,//剩余电量
    val alias: String?,// 系统状态 -1充电 0 无数据 +1 放电
    val timezone: Int?,//今日发电量
    val lastHeartbeatTime: String?,//累计发电量
    val chargePointVendor: String?,//1.(true-离线、false-在线),2.(true-未连接、false-已连接)
    val chargePointModel: String?,//采集器-更新间隔，"0"
    val chargePointSerialNumber: String?,//采集器-最后更新时间，"2020-06-01 07:00:48"
    val firmwareVersion: String?,//Combiner汇流箱-功率1.0
    val iccid: String?,//Combiner汇流箱-电压1.0
    val imei: String?,//Combiner汇流箱-电流2.0
    val meterType: String?,//采集器-良
    val meterSerialNumber: String?,//采集器-良
    val charingRate:Int,
    val connectorNum: Int?,//采集器-良
) {


}