package com.shuoxd.charge.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.shuoxd.lib.util.ViewUtil
import com.shuoxd.charge.databinding.DialogPickerBinding

/**
 * 选择器dialog
 */
class PickerDialog : BottomSheetDialogFragment(), View.OnClickListener {

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            items: Array<out ItemName>,
            callback: ((selectPosition: Int) -> Unit)? = null
        ) {
            val dialog = PickerDialog()
            dialog.options = items
            dialog.callback = callback
            dialog.show(fragmentManager, PickerDialog::class.java.name)
        }
    }

    private lateinit var options: Array<out ItemName>
    private lateinit var binding: DialogPickerBinding
    private var callback: ((selectPosition: Int) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        binding = DialogPickerBinding.inflate(layoutInflater, null, false)
        initView()
        dialog.setContentView(binding.root)
        //设置不能手势滑动关闭
        (dialog as BottomSheetDialog).behavior.isHideable = false
        //设置dialog背景透明
        (binding.root.parent as? View)?.setBackgroundColor(resources.getColor(android.R.color.transparent))
        return dialog
    }

    private fun initView() {
        binding.btConfirm.setOnClickListener(this)
        binding.tvCancel.setOnClickListener(this)
        binding.npPicker.apply {
            minValue = 0
            maxValue = options.size - 1
            value = 0
            setFormatter { index -> options[index].itemName() }
        }
        ViewUtil.fixNumberPicker(binding.npPicker)
    }

    override fun onClick(v: View?) {
        when {
            v === binding.tvCancel -> dismissAllowingStateLoss()
            v === binding.btConfirm -> {
                dismissAllowingStateLoss()
                callback?.invoke(binding.npPicker.value)
            }
        }
    }
}

interface ItemName {
    fun itemName(): String
}