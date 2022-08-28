package com.shuoxd.charge.ui.chargesetting.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivitySettingBinding

class ChargeSettingActivity:BaseActivity() {

    companion object{
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ChargeSettingActivity::class.java))
        }

    }


    private lateinit var binding: ActivitySettingBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }



}