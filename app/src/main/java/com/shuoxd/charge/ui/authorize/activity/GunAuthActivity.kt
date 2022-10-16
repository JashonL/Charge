package com.shuoxd.charge.ui.authorize.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityGunAuthBinding
import com.shuoxd.charge.databinding.ActivityScheduledChargingBinding
import com.shuoxd.charge.ui.smartcharge.ActivityOffpeak

class GunAuthActivity:BaseActivity() {
    companion object{
        fun start(context: Context?) {
            context?.startActivity(Intent(context, GunAuthActivity::class.java))
        }

    }



    private lateinit var binding: ActivityGunAuthBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityGunAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}