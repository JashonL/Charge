package com.shuoxd.charge.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.ChargingItemBinding
import com.shuoxd.charge.databinding.ItemCheckboxBinding

class CheckItemView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private var bingding: ItemCheckboxBinding

    private var title: String

    init {
        val view = LayoutInflater.from(context).inflate(R.layout.item_checkbox, this)
        bingding = ItemCheckboxBinding.bind(view)

        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.CheckItemView,
            0, 0
        ).apply {
            try {
                title = getString(R.styleable.CheckItemView_checkTitle) ?: ""

            } finally {
                recycle()
            }
        }

        initView()

    }

    private fun initView() {
        bingding.tvTitle.setText(title)
    }


     fun setCheck(b:Boolean) {
        bingding.itemCheckbox.isChecked=b
    }


}