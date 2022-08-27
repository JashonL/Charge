package com.shuoxd.charge.view

import android.app.Activity
import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.IntDef
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.TitleBarLayoutBinding
import com.shuoxd.charge.databinding.ToolbarBinding

class ToolbarLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) :
    LinearLayout(context, attrs, defStyleAttr) {

    private val binding: ToolbarBinding
    private var leftIcon: Drawable? = null

    private var titleText: String? = null
    private var rightIcon: Drawable? = null


    init {
        val view = LayoutInflater.from(context).inflate(R.layout.toolbar, this)
        binding = ToolbarBinding.bind(view)
        context.theme.obtainStyledAttributes(attrs, R.styleable.Toolbar, 0, 0).apply {
            try {
                leftIcon = getDrawable(R.styleable.ToolbarLayout_toolbarLeftIcon)
                titleText=getString(R.styleable.ToolbarLayout_toolbarTitle)?:""
                rightIcon = getDrawable(R.styleable.ToolbarLayout_toolbarRightIcon)
            } finally {
                recycle()
            }
        }

        initViews()

    }

    private fun initViews() {
        binding.toolbar.navigationIcon=leftIcon
        binding.toolbar.title = titleText
        binding.toolbar.overflowIcon=rightIcon

        binding.toolbar.setNavigationOnClickListener {
            (context as Activity).finish()
        }

    }







}