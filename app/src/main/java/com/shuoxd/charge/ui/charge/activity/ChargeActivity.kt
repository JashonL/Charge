package com.shuoxd.charge.ui.charge.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityChargeBinding
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.ui.charge.monitor.ChargeAactivityMonitor
import com.shuoxd.charge.ui.charge.viewmodel.ChargeViewModel
import com.shuoxd.charge.ui.mine.activity.LoginActivity
import com.shuoxd.charge.ui.mine.activity.MineActivity
import com.shuoxd.charge.util.StatusUtil
import com.shuoxd.charge.util.ValueUtil
import com.shuoxd.charge.view.dialog.AlertDialog
import com.shuoxd.charge.view.dialog.OptionsDialog
import com.shuoxd.lib.util.ToastUtil
import kotlinx.coroutines.delay

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
        initViews()
        freshChage()
        initData()
        setOnclickListeners()
        timerStart()
        setOnResume()
    }

    /**
     * 监听从我的点击返回事件
     */
    var listener1 = fun() {
        val currentChargeModel = getCurrentChargeModel()
        if (currentChargeModel != null) {
            chargeViewModel.chargerId = currentChargeModel.chargerId
            binding.tvChargeChoose.text = chargeViewModel.chargerId
            chargeViewModel.getChargeInfo()
        }

    }

    /**
     * 监听添加事件
     */
    var lisener2 = fun(_: ChargeAactivityMonitor) {
        freshChage()
    }

    private fun setOnResume() {
        ChargeAactivityMonitor.watch(lifecycle, listener1, lisener2)


/*        ChargeAactivityMonitor.watch(lifecycle) {
            val currentChargeModel = getCurrentChargeModel()
            if (currentChargeModel != null) {
                chargeViewModel.chargerId = currentChargeModel.chargerId
                binding.tvChargeChoose.text = chargeViewModel.chargerId
                chargeViewModel.getChargeInfo()
            }
        }*/
    }


    //启动定时任务
    private fun timerStart() {
        lifecycleScope.launchWhenResumed {
            repeat(Int.MAX_VALUE) {
                if (!TextUtils.isEmpty(chargeViewModel.chargerId) &&
                    !TextUtils.isEmpty(chargeViewModel.connectorId)
                ) {
                    chargeViewModel.getChargeInfo()
                }
                delay(10 * 1000)
            }
        }
    }


    private fun initViews() {
        binding.smartRefresh.setOnRefreshListener {
            freshChage()
        }
    }

    private fun setOnclickListeners() {
        binding.tvChargeChoose.setOnClickListener(this)
        binding.ivLock.setOnClickListener(this)
        binding.ivStart.setOnClickListener(this)
        binding.ivAvatar.setOnClickListener(this)
    }


    private fun freshChage() {
        //请求充电桩列表
        val user = accountService().user()
        if (user != null) {
            chargeViewModel.getChargeList(user.email)
        }
    }


    private fun initData() {
        //充电桩列表
        chargeViewModel.chargeListLiveData.observe(this) {
            dismissDialog()
            if (it.second == null) {
                val first = it.first
                setChargeList(first.toMutableList())
                updataChargeList(first)
            } else {
                ToastUtil.show(it.second)
            }
        }


        //充电桩详情
        chargeViewModel.chargeInfoLiveData.observe(this) {
            dismissDialog()
            if (it.second == null) {
                //获取单个充电桩详情数据
                binding.smartRefresh.finishRefresh()
                binding.chargeInfo = it.first
                it.first?.let { it ->

                    chargeViewModel.status = it.status
                    StatusUtil.setImageStatus(this, it.status, binding.ivChargeStatus, 50)
                    val valueFromKWh = ValueUtil.valueFromKWh(it.transaction.energyKWH)
                    val valueFromA = ValueUtil.valueFromA(it.transaction.current)
                    val valueFromV = ValueUtil.valueFromV(it.transaction.power)
                    val valueFromCost = ValueUtil.valueFromCost(it.transaction.cost)

                    binding.dataViewCapacity.setValue(valueFromKWh.first + valueFromKWh.second)
                    binding.dataViewCurrent.setValue(valueFromA.first + valueFromA.second)
                    binding.dataViewVoltage.setValue(valueFromV.first + valueFromV.second)
                    binding.dataViewConsumption.setValue(valueFromCost)
                    binding.dataViewTime.setValue(it.transaction.charingTimeText)

                }

            } else {
                ToastUtil.show(it.second)
            }


        }


        chargeViewModel.chargeUnlockLiveData.observe(this) {
            dismissDialog()
            ToastUtil.show(it)
        }

        chargeViewModel.chargeTransactionLiveData.observe(this) {
            dismissDialog()
            //成功或者失败都立刻刷新一次
            chargeViewModel.getChargeInfo()
            ToastUtil.show(it.second)
        }


    }


    private fun updataChargeList(first: List<ChargeModel>) {
        if (first.isNotEmpty()) {
            //取第一个充电桩
            val chargeModel = first[0]
            setCurrenChargeModel(chargeModel)
            chargeViewModel.chargerId = chargeModel.chargerId
            chargeViewModel.connectorId = "1"

            chargeViewModel.chargeids.clear()
            for (i in first.indices) {
                val charge = first.get(i)
                charge.chargerId?.let {
                    chargeViewModel.chargeids.add(it)
                }
            }

            chargeViewModel.chargerId?.let {
                binding.tvChargeChoose.text = it
                chargeViewModel.getChargeInfo()
            }
        } else {
            //没有充电桩
            binding.ivChargeStatus.setImageResource(R.drawable.button_gray_background)
            binding.tvChargeExcption.setText(R.string.m97_please_add_charge)
            binding.ivStart.setImageResource(R.drawable.start)
        }
    }


    override fun onClick(p0: View?) {
        when {
            p0 === binding.tvChargeChoose -> showSelectChartType()
            p0 === binding.ivLock -> unlock()
            p0 === binding.ivStart -> chargeAction()
            p0 === binding.ivAvatar -> MineActivity.start(this)

        }
    }


    private fun showSelectChartType() {
        OptionsDialog.show(supportFragmentManager, chargeViewModel.chargeids.toTypedArray()) {
            chargeViewModel.chargerId = chargeViewModel.chargeids[it]
            binding.tvChargeChoose.text = chargeViewModel.chargerId
            setCurrenChargeModel(chargeViewModel.chargeList[it])
            showDialog()
            chargeViewModel.getChargeInfo()
        }
    }


    private fun unlock() {
        AlertDialog.showDialog(
            supportFragmentManager,
            getString(R.string.m104_unlock_gun_tips),
            getString(R.string.m18_confirm),
            getString(R.string.m16_cancel),
            getString(R.string.m103_unlock_gun)
        ) {
            showDialog()
            chargeViewModel.unlockConnector()
        }
    }


    private fun chargeAction() {
        val actionUrl = StatusUtil.getActionUrl(chargeViewModel.status)
        if (actionUrl.isNotEmpty()) {
            chargeViewModel.unStartOrStopCharge(actionUrl)
        }

    }


}