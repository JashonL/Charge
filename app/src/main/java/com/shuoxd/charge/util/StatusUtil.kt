package com.shuoxd.charge.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.charge.ui.charge.ChargeStatus
import com.shuoxd.lib.util.GlideUtil

object StatusUtil {

    fun getChargeStatus(@ChargeStatus status: Int): String {
        return MainApplication.instance().getString(
            when (status) {
                ChargeStatus.DISCONNECTION -> R.string.m195_disconnection
                ChargeStatus.UNAVAILABLE -> R.string.m93_unavailable
                ChargeStatus.AVAILABLE -> R.string.m94_available
                ChargeStatus.PREPEAR -> R.string.m95_prepear
                ChargeStatus.CHARGING -> R.string.m96_charging
                ChargeStatus.DEVICE_STOP -> R.string.m102_device_stop
                ChargeStatus.CAR_STOP -> R.string.m97_car_stop
                ChargeStatus.CHARGE_FINISH -> R.string.m98_charge_finish
                ChargeStatus.RESERVCE -> R.string.m99_reservce
                ChargeStatus.UNAVAILABLE1 -> R.string.m93_unavailable
                else -> R.string.m101_fault
            }
        )
    }


    fun getTextColorByStatus(@ChargeStatus status: Int): Int {

        return ContextCompat.getColor(MainApplication.instance(),when (status) {
            ChargeStatus.DISCONNECTION -> R.color.gray
            ChargeStatus.UNAVAILABLE -> R.color.red
            ChargeStatus.AVAILABLE -> R.color.black_333333
            ChargeStatus.PREPEAR -> R.color.black_333333
            ChargeStatus.CHARGING -> R.color.color_app_main_pressed
            ChargeStatus.DEVICE_STOP -> R.color.black_333333
            ChargeStatus.CAR_STOP -> R.color.black_333333
            ChargeStatus.CHARGE_FINISH -> R.color.black_333333
            ChargeStatus.RESERVCE -> R.color.black_333333
            ChargeStatus.UNAVAILABLE1 -> R.color.red
            else -> R.color.black_333333
        })
 /*       return when (status) {
            ChargeStatus.UNAVAILABLE -> R.color.red
            ChargeStatus.AVAILABLE -> R.color.black_333333
            ChargeStatus.PREPEAR -> R.color.black_333333
            ChargeStatus.CHARGING -> R.color.color_app_main_pressed
            ChargeStatus.DEVICE_STOP -> R.color.black_333333
            ChargeStatus.CAR_STOP -> R.color.black_333333
            ChargeStatus.CHARGE_FINISH -> R.color.black_333333
            ChargeStatus.RESERVCE -> R.color.black_333333
            ChargeStatus.UNAVAILABLE1 -> R.color.red
            else -> R.color.black_333333
        }*/
    }


    fun setImageStatus(
        context: Context,
        @ChargeStatus status: Int,
        imageView: ImageView,
        energy: Int
    ) {
        when (status) {
            ChargeStatus.DISCONNECTION -> GlideUtil.showImage(
                context,
                R.drawable.unavailable,
                imageView,
                R.drawable.unavailable
            )



            ChargeStatus.UNAVAILABLE -> GlideUtil.showImage(
                context,
                R.drawable.unavailable,
                imageView,
                R.drawable.unavailable
            )
            ChargeStatus.AVAILABLE -> GlideUtil.showImage(
                context,
                R.drawable.available,
                imageView,
                R.drawable.available
            )
            ChargeStatus.PREPEAR -> GlideUtil.showImage(
                context,
                R.drawable.available,
                imageView,
                R.drawable.available
            )

            ChargeStatus.CHARGING ->
                if (energy < 5) {
                    GlideUtil.showGif(context, R.drawable.pre_1, imageView)
                } else if (energy <= 10) {
                    GlideUtil.showGif(context, R.drawable.pre_10, imageView)
                } else if (energy <= 25) {
                    GlideUtil.showGif(context, R.drawable.pre_25, imageView)
                } else if (energy <= 50) {
                    GlideUtil.showGif(context, R.drawable.pre_50, imageView)
                } else if (energy <= 75) {
                    GlideUtil.showGif(context, R.drawable.pre_75, imageView)
                } else {
                    GlideUtil.showGif(context, R.drawable.precent_100, imageView)
                }

            ChargeStatus.DEVICE_STOP -> GlideUtil.showImage(
                context,
                R.drawable.unavailable,
                imageView,
                R.drawable.unavailable
            )

            ChargeStatus.CAR_STOP -> GlideUtil.showImage(
                context,
                R.drawable.unavailable,
                imageView,
                R.drawable.unavailable
            )

            ChargeStatus.CHARGE_FINISH -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            ChargeStatus.RESERVCE -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            ChargeStatus.UNAVAILABLE1 -> GlideUtil.showImage(
                context,
                R.drawable.unavailable,
                imageView,
                R.drawable.unavailable
            )
            else -> GlideUtil.showImage(
                context,
                R.drawable.unavailable,
                imageView,
                R.drawable.unavailable
            )
        }
    }


    fun getActionImageStatus(context: Context, @ChargeStatus status: Int): Drawable? {
        return ContextCompat.getDrawable(
            context, when (status) {
                ChargeStatus.DISCONNECTION -> R.drawable.cant_start
                ChargeStatus.UNAVAILABLE -> R.drawable.cant_start
                ChargeStatus.AVAILABLE -> R.drawable.start_charge
                ChargeStatus.PREPEAR -> R.drawable.start_charge
                ChargeStatus.CHARGING -> R.drawable.start
                ChargeStatus.DEVICE_STOP -> R.drawable.cant_start
                ChargeStatus.CAR_STOP -> R.drawable.cant_start
                ChargeStatus.CHARGE_FINISH -> R.drawable.start
                ChargeStatus.RESERVCE -> R.drawable.start
                ChargeStatus.UNAVAILABLE1 -> R.drawable.cant_start
                else -> R.drawable.start
            }
        )
    }







    fun getActionUrl(@ChargeStatus status: Int): String {
        return when (status) {
            ChargeStatus.DISCONNECTION -> ""
            ChargeStatus.UNAVAILABLE -> ""
            ChargeStatus.AVAILABLE -> ApiPath.Charge.REMOTESTARTTRANSACTION
            ChargeStatus.PREPEAR -> ApiPath.Charge.REMOTESTARTTRANSACTION
            ChargeStatus.CHARGING -> ApiPath.Charge.REMOTESTOPTRANSACTION
            ChargeStatus.DEVICE_STOP -> ""
            ChargeStatus.CAR_STOP -> ""
            ChargeStatus.CHARGE_FINISH -> ""
            ChargeStatus.RESERVCE -> ""
            ChargeStatus.UNAVAILABLE1 -> ""
            else -> ""
        }

    }


    fun getStartStatus(@ChargeStatus status: Int): String {
        return MainApplication.instance().getString(
            when (status) {
                ChargeStatus.DISCONNECTION -> R.string.m181_start
                ChargeStatus.UNAVAILABLE -> R.string.m181_start
                ChargeStatus.AVAILABLE -> R.string.m181_start
                ChargeStatus.PREPEAR -> R.string.m181_start
                ChargeStatus.CHARGING -> R.string.m182_stop
                ChargeStatus.DEVICE_STOP -> R.string.m181_start
                ChargeStatus.CAR_STOP -> R.string.m181_start
                ChargeStatus.CHARGE_FINISH -> R.string.m181_start
                ChargeStatus.RESERVCE -> R.string.m181_start
                ChargeStatus.UNAVAILABLE1 -> R.string.m181_start
                else -> R.string.m181_start
            }
        )

    }



}