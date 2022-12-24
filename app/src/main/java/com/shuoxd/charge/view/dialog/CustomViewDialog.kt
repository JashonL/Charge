package com.shuoxd.charge.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.shuoxd.charge.base.BaseDialogFragment
import com.shuoxd.charge.databinding.DialogCurrentVolBinding

class CustomViewDialog : BaseDialogFragment() {

    companion object {

        fun show(
            fragmentManager: FragmentManager,
            current: Pair<String, String>?,
            voltage: Pair<String, String>?,
            power: Pair<String, String>?
        ) {
            val dialog = CustomViewDialog()
            dialog.valueCurrent = current
            dialog.valueVoltage = voltage
            dialog.valuePower = power
            dialog.show(fragmentManager, CustomViewDialog::class.java.name)
        }


    }

    private lateinit var binding: DialogCurrentVolBinding

    var valueCurrent: Pair<String, String>? = null
    var valueVoltage: Pair<String, String>? = null
    var valuePower: Pair<String, String>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogCurrentVolBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        val current = valueCurrent?.first ?: "0.0"
        val currentUnit = valueCurrent?.second ?: "A"


        binding.dataViewCurrent.setValue(current +currentUnit)


        val vol = valueVoltage?.first ?: "0.0"
        val volUnit = valueVoltage?.second ?: "A"

        binding.dataViewVol.setValue(vol + volUnit)
    }


}