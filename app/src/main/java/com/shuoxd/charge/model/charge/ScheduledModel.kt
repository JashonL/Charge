package com.shuoxd.charge.model.charge

data class ScheduledModel(
    val status: String,
    val keepAwakeStatus: String,
    val scList: List<Period>
) {
    data class Period(
        var id: Int,
        var chargerId: String?,
        var userId: Int?,
        var startTimeText: String,
        var endTimeText: String,
        var limitNum: String)
}



