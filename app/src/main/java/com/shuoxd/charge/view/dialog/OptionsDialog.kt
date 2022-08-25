package com.shuoxd.charge.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shuoxd.lib.util.ViewUtil
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.DialogOptionsBinding

/**
 * 选项dialog
 */
class OptionsDialog : BottomSheetDialogFragment() {

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            options: Array<String>,
            title: String? = null,
            callback: ((selectPosition: Int) -> Unit)? = null
        ) {
            val dialog = OptionsDialog()
            dialog.options = options
            dialog.callback = callback
            dialog.title = title
            dialog.show(fragmentManager, OptionsDialog::class.java.name)
        }


    }

    private lateinit var options: Array<String>
    private lateinit var binding: DialogOptionsBinding
    private var callback: ((selectPosition: Int) -> Unit)? = null
    private var title: String? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        binding = DialogOptionsBinding.inflate(layoutInflater, null, false)
        initView()
        dialog.setContentView(binding.root)
        //设置不能手势滑动关闭
        (dialog as BottomSheetDialog).behavior.isHideable = false
        dialog.setOnShowListener {
            //设置默认全部展开
            (getDialog() as BottomSheetDialog).findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
                ?.also {
                    BottomSheetBehavior.from(it).setState(BottomSheetBehavior.STATE_EXPANDED)
                }

        }
        return dialog
    }

    private fun initView() {
        for (position in options.indices) {
            binding.llOptions.addView(generateItemView(position))
        }
        binding.tvCancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
        if (!TextUtils.isEmpty(title)) {
            binding.tvTitle.text = title
        }
    }

    private fun generateItemView(position: Int): View {
        val textView = TextView(requireContext())
        textView.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            ViewUtil.dp2px(requireContext(), 46f)
        )
        textView.gravity = Gravity.CENTER
        textView.setTextColor(resources.getColor(R.color.black))
        textView.textSize = 14f
        textView.text = options[position]
        textView.setOnClickListener {
            callback?.invoke(position)
            dismissAllowingStateLoss()
        }
        return textView
    }
}