package com.shuoxd.charge.ui.chargesetting.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.bluetooth.cptool.BleSetActivity
import com.shuoxd.charge.databinding.ActivitySettingBinding
import com.shuoxd.charge.ui.common.fragment.RequestPermissionHub
import com.shuoxd.lib.util.ToastUtil

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
        initLiseners()

    }

    private fun initLiseners() {
     /*   binding.itemSetRate.setOnClickListener {
            RateActivity.start(this)
        }*/



        binding.itemSetServer.setOnClickListener {
            ToastUtil.show(getString(R.string.m194_not_yet_open))
        }


        binding.itemSetBluetooth.setOnClickListener {
//            ToastUtil.show(getString(R.string.m194_not_yet_open))
            val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                arrayOf(
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_ADVERTISE,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            }

            RequestPermissionHub.requestPermission(
                supportFragmentManager,
                permissions
            ) {
                if (it) {
                    BleSetActivity.start(this)
                }
            }

        }

    }


}