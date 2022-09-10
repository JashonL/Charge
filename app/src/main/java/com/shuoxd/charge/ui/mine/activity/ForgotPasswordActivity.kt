package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityForgotPwdBinding
import com.shuoxd.charge.ui.mine.viewmodel.ForgotPwdViewModel
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.setViewHeight
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ForgotPasswordActivity:BaseActivity() ,View.OnClickListener{

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ForgotPasswordActivity::class.java))
        }
    }

    private lateinit var binding:ActivityForgotPwdBinding

    private val verifyCodeViewModel:ForgotPwdViewModel by viewModels()
    private var focusOnPhoneOrEmail = false
    private var focusOnVerifyCode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityForgotPwdBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        setListener()
    }



    private fun setListener() {
        binding.tvSendVerifyCode.setOnClickListener(this)
        binding.btnConfirm.setOnClickListener(this)
        binding.etPhoneOrEmail.setOnFocusChangeListener { v, hasFocus ->
            focusOnPhoneOrEmail = hasFocus
            updateFocusView()
        }
        binding.etVerifyCode.setOnFocusChangeListener { v, hasFocus ->
            focusOnVerifyCode = hasFocus
            updateFocusView()
        }
    }





    private fun updateFocusView() {
        if (focusOnPhoneOrEmail) {
            binding.etPhoneOrEmail.setSelection(binding.etPhoneOrEmail.length())
            binding.viewPhoneOrEmailLine.setBackgroundResource(R.color.color_app_main_pressed)
            binding.viewPhoneOrEmailLine.setViewHeight(2f)
        } else {
            binding.viewPhoneOrEmailLine.setBackgroundResource(R.color.color_app_main)
            binding.viewPhoneOrEmailLine.setViewHeight(1f)
        }

        if (focusOnVerifyCode) {
            binding.etVerifyCode.setSelection(binding.etVerifyCode.length())
            binding.viewVerifyCodeLine.setBackgroundResource(R.color.color_app_main_pressed)
            binding.viewVerifyCodeLine.setViewHeight(2f)
        } else {
            binding.viewVerifyCodeLine.setBackgroundResource(R.color.color_app_main)
            binding.viewVerifyCodeLine.setViewHeight(1f)
        }

    }



    private fun initData() {
        verifyCodeViewModel.getVerifyCodeLiveData.observe(this) {
            dismissDialog()
            ToastUtil.show(it.second)
            if (it.first) {
                updateCountDown()
            }
        }


        verifyCodeViewModel.findPasswordLiveData.observe(this){
            dismissDialog()
            ToastUtil.show(it.second)
        }

    }




    private fun updateCountDown() {
        lifecycleScope.launch {
            binding.tvSendVerifyCode.isEnabled = false
            for (i in 180 downTo 0) {
                if (i == 0) {
                    binding.tvSendVerifyCode.isEnabled = true
                    binding.tvSendVerifyCode.text = getString(R.string.m158_send_verify_code)
                } else {
                    binding.tvSendVerifyCode.text = getString(R.string.m160_second_after_send, i)
                    delay(1000)
                }
            }
        }
    }



    override fun onClick(v: View?) {
        when {
            v === binding.tvSendVerifyCode -> requestSendVerifyCode()
            v===binding.btnConfirm->findPassword();

        }
    }



    private fun requestSendVerifyCode() {
        showDialog()
        val phoneOrEmail = binding.etPhoneOrEmail.text.toString().trim()
        verifyCodeViewModel.fetchVerifyCode(phoneOrEmail)
    }


    private fun findPassword() {
        val verifyCode = binding.etVerifyCode.text.toString().trim()
        val phoneOrEmail = binding.etPhoneOrEmail.text.toString().trim()
        if (!TextUtils.isEmpty(phoneOrEmail) && !TextUtils.isEmpty(verifyCode) && verifyCode.length == 6) {
            showDialog()
            verifyCodeViewModel.findPassword(phoneOrEmail, verifyCode)
        }
    }


}