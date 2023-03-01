package com.shuoxd.charge.view.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomDialog(val viewId: Int, lisener: OnviewListener) : BottomSheetDialogFragment() {


    companion object {

        fun show(
            fragmentManager: FragmentManager,
            viewId: Int,
            lisener: OnviewListener
        ) {
            val dialog = BottomDialog(viewId, lisener)
            dialog.onviewLiteners = lisener
            dialog.show(fragmentManager, PickerDialog::class.java.name)
        }
    }


    private var onviewLiteners: OnviewListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(viewId, null, false)
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setContentView(view)
        onviewLiteners?.onViewLisener(view,this)
        //设置不能手势滑动关闭
        (dialog as BottomSheetDialog).behavior.isHideable = false
        //设置dialog背景透明
        (view.parent as? View)?.setBackgroundColor(resources.getColor(android.R.color.transparent))
        return dialog
    }





    interface OnviewListener {
        fun onViewLisener(view: View,dialog:BottomDialog)
    }


}