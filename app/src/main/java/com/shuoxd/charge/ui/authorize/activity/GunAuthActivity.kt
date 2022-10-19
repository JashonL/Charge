package com.shuoxd.charge.ui.authorize.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityGunAuthBinding
import com.shuoxd.charge.ui.authorize.viewmodel.GunAuthViewModel
import com.shuoxd.lib.util.ToastUtil

class GunAuthActivity : BaseActivity(), View.OnClickListener {
    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, GunAuthActivity::class.java))
        }

    }


    private lateinit var binding: ActivityGunAuthBinding

    private val gunAuthViewModel: GunAuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGunAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        getGunAuth()
        setLisenters()
    }

    private fun setLisenters() {
        binding.btAdd.setOnClickListener(this)
    }


    private fun getGunAuth() {
        val currentChargeModel = getCurrentChargeModel()
        gunAuthViewModel.connectorAuthorizeStatus(currentChargeModel?.chargerId, 1)
    }


    private fun initData() {
        gunAuthViewModel.gunAuthLiveData.observe(this) {
            dismissDialog()
            if (it.first != null) {
                val authorize = it?.first?.authorize
                binding.itemAuth.setCheck(authorize == 1)
            } else {
                ToastUtil.show(it.second)
            }
        }

    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.btAdd -> {
                setGunAuth()
            }
        }

    }



    private fun setGunAuth() {
        val currentChargeModel = getCurrentChargeModel()
        val authorize = if (binding.itemAuth.isCheck()) 1 else 0
        currentChargeModel?.chargerId?.let {
            gunAuthViewModel.setScheduledCharging(
                it,
                "1",
                authorize.toString()
            )
        }
    }


}