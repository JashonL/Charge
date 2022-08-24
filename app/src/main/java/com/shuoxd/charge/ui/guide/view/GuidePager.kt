package com.shuoxd.charge.ui.guide.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.GuidePageBinding
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible

class GuidePager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, current: Int
) : ConstraintLayout(context, attrs), View.OnClickListener {


    private val bing: GuidePageBinding

    private var listener: (() -> Unit)? = null


    private val titles: Array<String> = arrayOf(
        context.getString(R.string.m28_guide1),
        context.getString(R.string.m29_guide2),
        context.getString(R.string.m30_guide3),
        context.getString(R.string.m31_guide4)
    )


    private val imagesRes: IntArray = intArrayOf(
        R.drawable.guide_car,
        R.drawable.guide_charge,
        R.drawable.guide_network,
        R.drawable.guide_era
    )


    private val tips: Array<String> = arrayOf(
        context.getString(R.string.m32_tips1),
        context.getString(R.string.m33_tips2),
        context.getString(R.string.m34_tips3),
        context.getString(R.string.m35_tips4)
    )


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.guide_page, this)
        bing = GuidePageBinding.bind(view)
        initView(current)

    }


    private fun initView(current: Int) {
        for (i in 0..3) {
            val textView = TextView(context)
            textView.apply {
                //设置大小
                val layoutParams = LinearLayout.LayoutParams(35, 35)
                layoutParams.setMargins(0,0,20,0)
                setLayoutParams(layoutParams)

                //设置背景
                if (i == current) {
                    background = ContextCompat.getDrawable(context, R.drawable.circle_green)
                } else {
                    background = ContextCompat.getDrawable(context, R.drawable.circle_gray)
                }
            }


            bing.llDir.addView(textView)


        }

        bing.title.text = titles[current]
        bing.car.setImageResource(imagesRes[current])
        bing.title.text = tips[current]

        bing.btUse.setOnClickListener(this)

        if (current == 3) {
            bing.btUse.visible()
        } else {
            bing.btUse.gone()
        }


    }


    fun setOnclickListeners(listener: (() -> Unit)) {
        this.listener = listener
    }

    override fun onClick(view: View?) {
        when {
            view === bing.btUse -> {
                listener?.invoke()
            }
        }
    }


}