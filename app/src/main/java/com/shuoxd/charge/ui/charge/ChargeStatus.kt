package com.shuoxd.charge.ui.charge

import androidx.annotation.IntDef
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication

/**
 * 电枪状态
 */

@IntDef(
    ChargeStatus.DISCONNECTION,

    ChargeStatus.UNAVAILABLE,
    ChargeStatus.AVAILABLE,
    ChargeStatus.PREPEAR,
    ChargeStatus.CHARGING,
    ChargeStatus.DEVICE_STOP,
    ChargeStatus.CAR_STOP,
    ChargeStatus.CHARGE_FINISH,
    ChargeStatus.RESERVCE,
    ChargeStatus.UNAVAILABLE1,
    ChargeStatus.FAULT,
    ChargeStatus.NONE

)



@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class ChargeStatus {
    /*
    	状态，0不可用， 1可使用,2准备充电,3充电中,4充电设施停用,5车辆停用,6充电结束中,7预约中,8不可使用,9故障
    */
    companion object{
        const val DISCONNECTION=-1
        const val UNAVAILABLE = 0
        const val AVAILABLE = 1
        const val PREPEAR = 2
        const val CHARGING = 3
        const val DEVICE_STOP = 4
        const val CAR_STOP = 5
        const val CHARGE_FINISH = 6
        const val RESERVCE = 7
        const val UNAVAILABLE1 = 8
        const val FAULT = 9
        const val NONE=10

        fun getChargeStatus(@ChargeStatus status: Int): String {
            return MainApplication.instance().getString(
                when (status) {
                    DISCONNECTION->R.string.m195_disconnection
                    UNAVAILABLE -> R.string.m93_unavailable
                    AVAILABLE -> R.string.m94_available
                    PREPEAR -> R.string.m95_prepear
                    CHARGING -> R.string.m96_charging
                    DEVICE_STOP -> R.string.m102_device_stop
                    CAR_STOP -> R.string.m97_car_stop
                    CHARGE_FINISH -> R.string.m98_charge_finish
                    RESERVCE->R.string.m99_reservce
                    UNAVAILABLE1->R.string.m93_unavailable
                    else -> R.string.m101_fault
                }
            )
        }

    }






}