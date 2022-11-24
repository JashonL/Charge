package com.shuoxd.charge.ui.authorize.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.AuthOtherUsersBinding
import com.shuoxd.charge.ui.authorize.monitor.AuthMonitor
import com.shuoxd.charge.ui.authorize.viewmodel.AddAuthViewModel
import com.shuoxd.charge.view.dialog.OptionsDialog
import com.shuoxd.lib.util.ToastUtil

class AddAuthActivity:BaseActivity() ,View.OnClickListener{

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, AddAuthActivity::class.java))
        }

    }

    private lateinit var binding: AuthOtherUsersBinding

    private val authModel: AddAuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AuthOtherUsersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        setOnclickListener()
        getOrinalList()
    }

    private fun getOrinalList() {
        val user = accountService().user()
        authModel.getChargeList(user?.email)
    }


    private fun initData() {
        authModel.addAuthLiveData.observe(this){
            dismissDialog()
//            ToastUtil.show(it.second)
            if (it.first) {
                showResultDialog(it.second,null){
                    AuthMonitor.notifyPlant()
                    finish()
                }

            }else{
                showResultDialog(it.second,null,null)
            }
        }

        //充电桩列表
        authModel.chargeListLiveData.observe(this) {
            dismissDialog()
            if (it.second == null) {
//                val first = it.first
                authModel.chargeList= it.first.toMutableList()

            } else {
                ToastUtil.show(it.second)
            }
        }

    }

    private fun setOnclickListener() {
        binding.btAdd.setOnClickListener(this)
        binding.tvChargeId.setOnClickListener(this)
    }


    override fun onClick(p0: View?) {
        when{

            p0===binding.tvChargeId->{
                showSelectChartType()
            }

            p0===binding.btAdd->{
                val username = binding.etUsername.text.toString()
                val chargeId = binding.tvChargeId.text.toString()
                when{
                    TextUtils.isEmpty(username)->ToastUtil.show(getString(R.string.m74_please_input_username))
                    TextUtils.isEmpty(chargeId)->ToastUtil.show(getString(R.string.m136_choose))
                    else ->{
//                        val email = accountService().user()?.email


                        authModel.addAuthlist(chargeId,username)
                    }

                }

            }

        }
    }



    private fun showSelectChartType() {
        val chargeList = authModel.chargeList
        if (chargeList==null){
            getOrinalList()
        }else{
            val list= mutableListOf<String>()
            for (i in chargeList.indices){
                chargeList.get(i).chargerId?.let { list.add(it) }
            }
            OptionsDialog.show(supportFragmentManager, list.toTypedArray()) {
                binding.tvChargeId.text=list.get(it)
            }
        }


    }


}