package com.shuoxd.charge.ui.charge.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityChargeBinding
import com.shuoxd.charge.ui.charge.viewmodel.ChargeViewModel
import com.shuoxd.charge.ui.mine.activity.LoginActivity
import com.shuoxd.lib.util.ToastUtil

class ChargeActivity : BaseActivity() {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ChargeActivity::class.java))
        }


        fun startClearTask(context: Context?) {
            val intent = Intent(context, LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            context?.startActivity(intent)
        }

    }


    private lateinit var binding: ActivityChargeBinding

    private val chargeViewModel: ChargeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        GlideUtil.showGif(this, R.drawable.pre_75,binding.ivChargeStatus)
        initData()

        //请求充电桩列表
        val user = accountService().user()
        if (user!=null){
            chargeViewModel.getChargeList(user.email)
        }

    }

    private fun initData() {
        chargeViewModel.chargeListLiveData.observe(this) {
            dismissDialog()
            if (it.second == null) {
                val first = it.first

            } else {
                ToastUtil.show(it.second)
            }
        }

    }





}