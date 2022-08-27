package com.shuoxd.charge.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.service.http.ApiPath
import com.shuoxd.charge.ui.charge.ChargeStatus
import com.shuoxd.lib.util.GlideUtil

object StatusUtil {

    fun getChargeStatus(@ChargeStatus status: Int): String {
        return MainApplication.instance().getString(
            when (status) {
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


    fun setImageStatus(
        context: Context,
        @ChargeStatus status: Int,
        imageView: ImageView,
        energy: Int
    ) {
        when (status) {
            ChargeStatus.UNAVAILABLE -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            ChargeStatus.AVAILABLE -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            ChargeStatus.PREPEAR -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)

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

            ChargeStatus.DEVICE_STOP -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)

            ChargeStatus.CAR_STOP -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)

            ChargeStatus.CHARGE_FINISH -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            ChargeStatus.RESERVCE -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            ChargeStatus.UNAVAILABLE1 -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
            else -> GlideUtil.showGif(context, R.drawable.pre_75, imageView)
        }
    }


    fun getActionImageStatus(context: Context, @ChargeStatus status: Int): Drawable? {
        return ContextCompat.getDrawable(
            context, when (status) {
                ChargeStatus.UNAVAILABLE -> R.drawable.start
                ChargeStatus.AVAILABLE -> R.drawable.start
                ChargeStatus.PREPEAR -> R.drawable.start
                ChargeStatus.CHARGING -> R.drawable.start
                ChargeStatus.DEVICE_STOP -> R.drawable.start
                ChargeStatus.CAR_STOP -> R.drawable.start
                ChargeStatus.CHARGE_FINISH -> R.drawable.start
                ChargeStatus.RESERVCE -> R.drawable.start
                ChargeStatus.UNAVAILABLE1 -> R.drawable.start
                else -> R.drawable.start
            }
        )
    }


    fun getActionUrl(@ChargeStatus status: Int): String {
        return when (status) {
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


}