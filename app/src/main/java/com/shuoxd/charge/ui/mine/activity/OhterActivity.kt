package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.shuoxd.charge.BuildConfig
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityOtherBinding
import com.shuoxd.charge.ui.common.activity.WebActivity
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.Util

class OhterActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, OhterActivity::class.java))
        }
    }


    private lateinit var binding: ActivityOtherBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setOnclickListeners()
    }

    private fun setOnclickListeners() {
        binding.tvDeleteAccount.setOnClickListener(this)
        binding.tvPrivacyAgreement.setOnClickListener(this)
    }

    private fun initViews() {
        val version = getString(R.string.m147_version_information) + ":" + Util.getVersion(this)
        binding.tvVersionInformation.text = version

    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.tvDeleteAccount -> {
                ToastUtil.show("用户已经注销")
                accountService().logout()
                accountService().login(this)
                finish()
            }

            p0===binding.tvPrivacyAgreement->{
                WebActivity.start(this@OhterActivity, BuildConfig.userAgreementUrl)
            }

        }

    }
}