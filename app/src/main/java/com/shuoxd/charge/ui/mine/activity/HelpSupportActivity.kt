package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.BuildConfig
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityHelpSupportBinding

class HelpSupportActivity :BaseActivity(){

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, HelpSupportActivity::class.java))
        }
    }



    private lateinit var binding: ActivityHelpSupportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHelpSupportBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initListeners()
    }


    private fun initListeners() {
    }

    private fun initViews() {
        val user = accountService().user()
        user?.let {
            binding.supportEmail.setValue(BuildConfig.serviceEmail)
            binding.supportTime.setValue(BuildConfig.serviceTime)
            binding.supportPhone.setValue(BuildConfig.companyPhone)

        }

    }

}