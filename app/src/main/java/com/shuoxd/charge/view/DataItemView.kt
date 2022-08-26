package com.shuoxd.charge.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.ChargingItemBinding

class DataItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var bingding: ChargingItemBinding

    private var leftIcon: Drawable?
    private var title: String
    private var value: String

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.charging_item, this)
        bingding = ChargingItemBinding.bind(view)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.DataItemView,
            0, 0
        ).apply {
            try {
                leftIcon = getDrawable(R.styleable.DataItemView_lefticon)
                title = getString(R.styleable.DataItemView_title) ?: ""
                value=getString(R.styleable.DataItemView_value)?:""

            } finally {
                recycle()
            }
        }

        initView()

    }

    private fun initView() {
        bingding.ivIcon.setImageDrawable(leftIcon)
        bingding.tvTitle.setText(title)
    }


     fun setValue(value:String?) {
        bingding.tvValue.text = value
    }


}