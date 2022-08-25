package com.shuoxd.charge.ui.charge.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityChargeBinding
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.ui.charge.viewmodel.ChargeViewModel
import com.shuoxd.charge.ui.mine.activity.LoginActivity
import com.shuoxd.charge.view.dialog.OptionsDialog
import com.shuoxd.lib.util.ToastUtil

class ChargeActivity : BaseActivity(), View.OnClickListener {

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
        freshChage()
        initData()
        setOnclickListeners()
    }

    private fun setOnclickListeners() {
        binding.tvChargeChoose.setOnClickListener(this)
    }


    private fun freshChage() {
        //请求充电桩列表
        val user = accountService().user()
        if (user != null) {
            chargeViewModel.getChargeList(user.email)
        }
    }


    private fun initData() {
        chargeViewModel.chargeListLiveData.observe(this) {
            dismissDialog()
            if (it.second == null) {
                val first = it.first
                updataChargeList(first)
            } else {
                ToastUtil.show(it.second)
            }
        }



        chargeViewModel.chargeInfoLiveData.observe(this){
            if (it.second==null){
                //
                val first=it.first
                
            }else{

            }


        }



    }


    private fun updataChargeList(first: List<ChargeModel>) {
        if (first.isNotEmpty()) {
            //取第一个充电桩
            val chargeModel = first[0]
            val chargerId = chargeModel.chargerId
            chargerId?.let {
                binding.tvChargeChoose.text = chargerId
            }
            chargeViewModel.chargeids.clear()
            for (i in 0..first.size) {
                val charge = first.get(i)
                charge.chargerId?.let {
                    chargeViewModel.chargeids.add(it)
                }
            }
        } else {
            //没有充电桩

        }
    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.tvChargeChoose -> showSelectChartType()
        }
    }


    private fun showSelectChartType() {
        OptionsDialog.show(supportFragmentManager, chargeViewModel.chargeids.toTypedArray()) {
            chargeViewModel.chargerId = chargeViewModel.chargeids[it]
            showDialog()
            chargeViewModel.getChargeInfo()
        }
    }


}