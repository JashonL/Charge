package com.shuoxd.charge.model.charge


data class RecordModel(
    val id: String,
    val chargerId: String,
    val userId: String,
    val connectorId: String,
    val connectorText: String,
    val startTimeLocalDetail: String,
    val startDate: String,
    val stopTimeLocalDetail: String,
    val stopReason: String,
    val status: String,
    val energyKWH: String,
    val charingTimeText: String,
    val cost: String,
    val power: String,
    val current: String
)


