package com.shuoxd.charge.model.charge

data class OffPeakModel(
    val status: String,
    val ocList: List<Period>
) {
    data class Period(
        var id: Int,
        var chargerId: String?,
        var userId: Int?,
        var startTimeText: String,
        var endTimeText: String,
    )
}



