package com.shuoxd.charge.ui.chargesetting.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityRateBinding
import com.shuoxd.charge.ui.chargesetting.viewmodel.RateViewModel
import com.shuoxd.charge.util.ChargeSetKey
import com.shuoxd.lib.util.ToastUtil

class RateActivity : BaseActivity(),View.OnClickListener {


    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, RateActivity::class.java))
        }

    }


    private lateinit var binding: ActivityRateBinding
    private val rateViewModel:RateViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onclickListeneres()

        initData()



    }

    private fun onclickListeneres() {
        binding.btAdd.setOnClickListener(this)
    }

    private fun initData() {
        rateViewModel.rateLiveData.observe(this){
            dismissDialog()
            ToastUtil.show(it.second)
            if (it.first) {
                finish()
            }
        }

    }


    override fun onClick(p0: View?) {
        when{
            p0===binding.btAdd->{
                val rate = binding.etRate.text.toString()
                if (TextUtils.isEmpty(rate)){
                    ToastUtil.show(getString(R.string.m133_rate_not_null))
                }else{
                    val chargerId = getCurrentChargeModel()?.chargerId?:""
                    rateViewModel.setRate(chargerId,ChargeSetKey.chargerRate, rate)
                }


            }
        }

    }
}