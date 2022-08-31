package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityModifyPasswordBinding
import com.shuoxd.charge.ui.mine.viewmodel.ModyfyViewModel
import com.shuoxd.lib.util.ToastUtil

class ModifyPasswordActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ModifyPasswordActivity::class.java))
        }
    }

    private lateinit var binding: ActivityModifyPasswordBinding


    private val viewModel: ModyfyViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityModifyPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setclickListeners()
        initData()
    }


    private fun initData() {
        viewModel.modyfiLiveData.observe(this){
            dismissDialog()
            ToastUtil.show(it.second)
            if (it.first) {
                accountService().logout()
                accountService().login(this)
                finish()
            }
        }
    }



    private fun setclickListeners() {
        binding.btDone.setOnClickListener(this)
        binding.btDone.setOnClickListener(this)

    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.btDone -> {
                val oldPassword = binding.etOldPassword.text.toString().trim()
                val newPassword = binding.etNewPassword.text.toString().trim()
                val confirm = binding.etPasswordConfirm.text.toString().trim()

                when {
                    TextUtils.isEmpty(oldPassword) -> ToastUtil.show(getString(R.string.m76_password_cant_empty))
                    TextUtils.isEmpty(newPassword) -> ToastUtil.show(getString(R.string.m76_password_cant_empty))
                    TextUtils.isEmpty(confirm) -> ToastUtil.show(getString(R.string.m76_password_cant_empty))
                    else-> {
                        showDialog()
                        viewModel.modifyPassword(oldPassword,newPassword)
                    }

                }
            }

        }

    }


}