package com.shuoxd.charge.model.charge

data class ScheduledModel(
    val status: String,
    val keepAwakeStatus: String,
    val scList: List<Period>
) {
    data class Period(
        val id: Int,
        val chargerId: String?,
        val userId: Int?,
        val startTimeText: String,
        val endTimeText: String,
        val limitNum: Int)
}



