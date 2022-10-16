package com.shuoxd.charge.view.popuwindow

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.method.Touch
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.shuoxd.charge.R

class CustomPopuwindow(context: Context, layoutRes: Int, initView: ViewInit? = null) :
    PopupWindow() {


    init {
        val inflater = LayoutInflater.from(context)
        this.contentView = inflater.inflate(layoutRes, null) //布局xml
        this.width = LinearLayout.LayoutParams.MATCH_PARENT //父布局减去padding
        this.height = LinearLayout.LayoutParams.WRAP_CONTENT
        this.animationStyle = R.style.Popup_Anim  //进入和退出动画效果
        this.isOutsideTouchable = true //是否可以
        this.isClippingEnabled = false //背景透明化可以铺满全屏
        // 设置最终的背景,也可以通过context.resources.getColor(resId)设置自己的颜色
        val colorDrawable = ColorDrawable(Color.parseColor("#00000000"))
        this.setBackgroundDrawable(colorDrawable) //设置背景
        initView?.viewInitListener(contentView)
        this.setTouchInterceptor { view, _ -> false };
    }


    fun setTouchAble(touch: Boolean): CustomPopuwindow {
        this.setTouchAble(touch)
        return this
    }


    interface ViewInit {
        fun viewInitListener(view: View);
    }


}