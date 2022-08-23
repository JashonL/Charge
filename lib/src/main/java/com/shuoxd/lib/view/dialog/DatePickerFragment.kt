package com.shuoxd.lib.view.dialog

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import java.util.*

/**
 * 日期选择器
 */
class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            maxDateLong: Long? = null,
            listener: OnDateSetListener
        ) {
            DatePickerFragment().also {
                it.listener = listener
                it.maxDateLong = maxDateLong
            }.show(fragmentManager, DatePickerFragment::class.java.name)
        }

        fun show(
            fragmentManager: FragmentManager,
            listener: OnDateSetListener
        ) {
            DatePickerFragment().also {
                it.listener = listener
                it.maxDateLong = null
            }.show(fragmentManager, DatePickerFragment::class.java.name)
        }

    }

    private var listener: OnDateSetListener? = null

    //最大时间戳
    private var maxDateLong: Long? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)
        return DatePickerDialog(requireActivity(), this, year, month, day).also {
            if (maxDateLong != null) {
                it.datePicker.maxDate = maxDateLong!!
            }
        }
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(year, month, dayOfMonth)
        listener?.onDateSet(calendar.time)
    }
}

interface OnDateSetListener {
    fun onDateSet(date: Date)
}