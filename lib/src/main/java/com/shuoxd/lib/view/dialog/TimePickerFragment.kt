package com.shuoxd.lib.view.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.annotation.IntDef
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.util.*


/**
 * 日期选择器
 */
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    companion object {

        /**
         * @param maxDateLong 可选择最大日期限制
         * @param selectDateType 选择日期类型
         */
        fun show(
            fragmentManager: FragmentManager,
            hour: Int,
            min: Int,
            listener: OnTimeSetListener
        ) {
            TimePickerFragment().also {
                it.listener = listener
                it.hour = hour
                it.min = min
            }.show(fragmentManager, TimePickerFragment::class.java.name)
        }

    }

    private var listener: OnTimeSetListener? = null
    private var hour: Int = -1
    private var min: Int = -1


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val min = c.get(Calendar.MINUTE)
        if (this.hour == -1) {
            this.hour = hour
        }

        if (this.min == -1) {
            this.min = min
        }
        return TimePickerDialog(requireActivity(), AlertDialog.THEME_HOLO_LIGHT,this, this.hour, this.min, true)

    }



    override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
        listener?.onTimeSelected(p1, p2)
    }

}

interface OnTimeSetListener {
    fun onTimeSelected(hour: Int, min: Int)
}

@IntDef(
    SelectDateType.DAY,
    SelectDateType.MONTH,
    SelectDateType.YEAR,
)
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class SelectDateType {
    companion object {
        const val DAY = 0
        const val MONTH = 1
        const val YEAR = 2
    }
}
