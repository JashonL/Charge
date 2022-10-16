package com.shuoxd.charge.ui.smartcharge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityScheduledChargingBinding

class ScheduledChargeActivity :BaseActivity(){
    companion object{
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ScheduledChargeActivity::class.java))
        }

    }

    private lateinit var binding:ActivityScheduledChargingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityScheduledChargingBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }



}