package com.shuoxd.charge.model.charge

data class TransactionModel(
    val status: Int,
    val transaction: Transaction,
    val charge: Charge,
    val scList: List<ScheduledModel.Period>
) {

    data class Transaction(
        val id: Int,
        val chargerId: String,
        val userId: Int,
        val connectorId: Int,
        val connectorText: String,
        val startTimeLocalDetail: String,
        val startDate: String,
        val stopTimeLocalDetail: String,
        val stopReason: String,
        val status: Int,
        val energyKWH: Double?,
        val charingTimeText: String,
        val cost: Double,
        val powerKW: Double,
        val current: Double,
        val offPeakStatus:Int
    )


    data class Charge(
        val id: Int,
        val userId: Int,
        val agentId: Int,
        val chargerId: String,
        val alias: String,
        val timezone: Int,
        val lastHeartbeatTime: String,
        val chargePointVendor: String,
        val chargePointModel: String,
        val chargePointSerialNumber: String,
        val firmwareVersion: String,
        val iccid: String,
        val imei: String,
        val meterType: String,
        val meterSerialNumber: String,
        val charingRate: Double,
        val connectorNum: Int,
    )


}



