package com.shuoxd.charge.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.RelativeLayout
import androidx.annotation.StringDef
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.ChartTimeSelectLayoutBinding
import com.shuoxd.lib.util.DateUtils
import com.shuoxd.lib.util.Util
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible
import com.shuoxd.lib.view.dialog.DatePickerFragment
import com.shuoxd.lib.view.dialog.OnDateSetListener
import java.util.*

/**
 * 自定义图表时间选择控件
 */
class ChartTimeSelectLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {

    private val binding: ChartTimeSelectLayoutBinding

    @DateType
    var dateType: String = DateType.HOUR
    var selectDate = Date()

    private var listener: ((String, Date) -> Unit)? = null

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.chart_time_select_layout, this)
        binding = ChartTimeSelectLayoutBinding.bind(view)
        binding.tvSelectDate.text = DateUtils.yyyy_MM_dd_format(selectDate)

        binding.tvHour.setOnClickListener(this)
        binding.tvDay.setOnClickListener(this)
        binding.tvMonth.setOnClickListener(this)
        binding.tvYear.setOnClickListener(this)
        binding.tvSelectDate.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when {
            v === binding.tvHour -> setDateTypeValue(DateType.HOUR)
            v === binding.tvDay -> setDateTypeValue(DateType.DAY)
            v === binding.tvMonth -> setDateTypeValue(DateType.MONTH)
            v === binding.tvYear -> setDateTypeValue(DateType.YEAR)
            v === binding.tvSelectDate -> showDatePickDialog()
        }
    }

    private fun showDatePickDialog() {
        Util.getFragmentManagerForContext(context)?.also {
            DatePickerFragment.show(it, Date().time, object : OnDateSetListener {
                override fun onDateSet(date: Date) {
                    setDateValue(date)
                }
            })
        }
    }

    fun setDateTypeValue(@DateType dateType: String) {
        if (this.dateType == dateType) {
            return
        }
        when (this.dateType) {
            DateType.HOUR -> {
                binding.tvHour.setTextColor(resources.getColor(R.color.color_text_99))
                binding.tvHour.setBackgroundColor(Color.TRANSPARENT)
                binding.tvSelectDate.text = DateUtils.yyyy_MM_dd_format(selectDate)
                binding.tvSelectDate.visible()
            }
            DateType.DAY -> {
                binding.tvDay.setTextColor(resources.getColor(R.color.color_text_99))
                binding.tvSelectDate.text = DateUtils.yyyy_MM_format(selectDate)
                binding.tvDay.setBackgroundColor(Color.TRANSPARENT)
                binding.tvSelectDate.visible()
            }
            DateType.MONTH -> {
                binding.tvMonth.setTextColor(resources.getColor(R.color.color_text_99))
                binding.tvSelectDate.text = DateUtils.yyyy_format(selectDate)
                binding.tvMonth.setBackgroundColor(Color.TRANSPARENT)
                binding.tvSelectDate.visible()
            }
            DateType.YEAR -> {
                binding.tvYear.setTextColor(resources.getColor(R.color.color_text_99))
                binding.tvYear.setBackgroundColor(Color.TRANSPARENT)
                binding.tvSelectDate.gone()
            }
        }

        when (dateType) {
            DateType.HOUR -> {
                binding.tvHour.setTextColor(resources.getColor(R.color.white))
                binding.tvHour.setBackgroundResource(R.color.colorAccent)
                binding.tvSelectDate.text = DateUtils.yyyy_MM_dd_format(selectDate)
                binding.tvSelectDate.visible()
            }
            DateType.DAY -> {
                binding.tvDay.setTextColor(resources.getColor(R.color.white))
                binding.tvDay.setBackgroundResource(R.color.colorAccent)
                binding.tvSelectDate.text = DateUtils.yyyy_MM_format(selectDate)
                binding.tvSelectDate.visible()
            }
            DateType.MONTH -> {
                binding.tvMonth.setTextColor(resources.getColor(R.color.white))
                binding.tvMonth.setBackgroundResource(R.color.colorAccent)
                binding.tvSelectDate.text = DateUtils.yyyy_format(selectDate)
                binding.tvSelectDate.visible()
            }
            DateType.YEAR -> {
                binding.tvYear.setTextColor(resources.getColor(R.color.white))
                binding.tvYear.setBackgroundResource(R.color.colorAccent)
                binding.tvSelectDate.gone()
            }
        }

        this.dateType = dateType
        listener?.invoke(this.dateType, selectDate)
    }

    fun setDateValue(date: Date) {
        selectDate = date
        when (dateType) {
            DateType.HOUR -> {
                binding.tvSelectDate.text = DateUtils.yyyy_MM_dd_format(date)
            }
            DateType.DAY -> {
                binding.tvSelectDate.text = DateUtils.yyyy_MM_format(date)
            }
            DateType.MONTH -> {
                binding.tvSelectDate.text = DateUtils.yyyy_format(date)
            }
        }
        listener?.invoke(this.dateType, selectDate)
    }

    fun setChartTimeSelectListener(listener: ((String, Date) -> Unit)) {
        this.listener = listener
    }
}

/**
 * 设备状态
 */
@StringDef(
    DateType.HOUR,
    DateType.DAY,
    DateType.MONTH,
    DateType.YEAR,
)
@Retention(AnnotationRetention.SOURCE)
@Target(
    AnnotationTarget.VALUE_PARAMETER,
    AnnotationTarget.PROPERTY
)
annotation class DateType {
    companion object {
        const val HOUR = "1"
        const val DAY = "2"
        const val MONTH = "3"
        const val YEAR = "4"
    }
}