package com.shuoxd.charge.ui.charge.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.AddYourChargeBinding
import com.shuoxd.charge.ui.charge.monitor.ChargeAactivityMonitor
import com.shuoxd.charge.ui.charge.viewmodel.AddChargeViewModel
import com.shuoxd.charge.ui.common.activity.ScanActivity
import com.shuoxd.charge.ui.common.fragment.RequestPermissionHub
import com.shuoxd.lib.util.ActivityBridge
import com.shuoxd.lib.util.ToastUtil

class AddYourChargeActivity : BaseActivity(), View.OnClickListener {


    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, AddYourChargeActivity::class.java))
        }
    }


    private lateinit var binding: AddYourChargeBinding

    private val addChargeViewModel: AddChargeViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = AddYourChargeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initViews()
        initListeners()
    }

    private fun initListeners() {
        binding.btAdd.setOnClickListener(this)
        binding.btScan.setOnClickListener(this)

    }

    private fun initViews() {


    }


    private fun initData() {
        //充电桩列表
        addChargeViewModel.addchargeLiveData.observe(this) {
            dismissDialog()
            if (it == null) {
                ChargeAactivityMonitor.notifyPlant()
                finish()
            } else {
                ToastUtil.show(it)
            }
        }

    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.btAdd -> {
                val text = binding.etSn.text.toString().trim()
                when {
                    text.isEmpty() -> {
                        ToastUtil.show(getString(R.string.m116_sn_not_null))
                    }
                    else -> {
                        showDialog()
                        addChargeViewModel.addChage(text)
                    }
                }
            }

            p0===binding.btScan->{
                RequestPermissionHub.requestPermission(
                    supportFragmentManager,
                    arrayOf(Manifest.permission.CAMERA)
                ) {
                    if (it) {
                        scan()
                    }
                }
            }


        }
    }


    private fun scan() {
        ActivityBridge.startActivity(
            this,
            ScanActivity.getIntent(this),
            object : ActivityBridge.OnActivityForResult {
                override fun onActivityForResult(
                    context: Context?,
                    resultCode: Int,
                    data: Intent?
                ) {
                    if (resultCode == RESULT_OK && data?.hasExtra(ScanActivity.KEY_CODE_TEXT) == true) {
                        val collectionSN = data.getStringExtra(ScanActivity.KEY_CODE_TEXT)
                        binding.etSn.setText(collectionSN)
                        showDialog()
                        addChargeViewModel.addChage(collectionSN)
                    }
                }
            })
    }

}