package com.shuoxd.charge.ui.charge.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityChargeBinding
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.service.charge.ChargeSettingManager
import com.shuoxd.charge.ui.authorize.activity.GunAuthActivity
import com.shuoxd.charge.ui.charge.ChargeStatus
import com.shuoxd.charge.ui.charge.monitor.ChargeAactivityMonitor
import com.shuoxd.charge.ui.charge.viewmodel.ChargeViewModel
import com.shuoxd.charge.ui.chargesetting.activity.ChargeSettingActivity
import com.shuoxd.charge.ui.mine.activity.LoginActivity
import com.shuoxd.charge.ui.mine.activity.MineActivity
import com.shuoxd.charge.ui.smartcharge.ActivityOffpeak
import com.shuoxd.charge.ui.smartcharge.ScheduledChargeActivity
import com.shuoxd.charge.util.StatusUtil
import com.shuoxd.charge.util.ValueUtil
import com.shuoxd.charge.view.dialog.AlertDialog
import com.shuoxd.charge.view.dialog.CustomViewDialog
import com.shuoxd.charge.view.dialog.OptionsDialog
import com.shuoxd.charge.view.popuwindow.CustomPopuwindow
import com.shuoxd.lib.service.account.IAccountService
import com.shuoxd.lib.service.storage.DefaultStorageService.Companion.CURRENT_CHARGE
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ChargeActivity : BaseActivity(), View.OnClickListener,
    IAccountService.OnUserProfileChangeListener, CompoundButton.OnCheckedChangeListener {


    //枪的授权状态
    var authorizeStatus = 0

    var popShow = false

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
        if (chargeViewModel.isListCallback && currentChargeModel != null) {
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
                delay(15 * 1000)
            }
        }
    }


    private fun initViews() {
        binding.smartRefresh.setOnRefreshListener {
            if (!TextUtils.isEmpty(chargeViewModel.chargerId) &&
                !TextUtils.isEmpty(chargeViewModel.connectorId)
            ) {
                chargeViewModel.getChargeInfo()
            }
        }

        binding.itemCheckbox.setOnCheckedChangeListener(this)

        refreshUserProfile()

    }

    private fun setOnclickListeners() {
        binding.tvChargeChoose.setOnClickListener(this)
        binding.ivLock.setOnClickListener(this)
        binding.ivStart.setOnClickListener(this)
        binding.ivAvatar.setOnClickListener(this)
        binding.ivMenu.setOnClickListener(this)
        binding.tvChargingSetting.setOnClickListener(this)
        binding.ivSet.setOnClickListener(this)
        binding.ivOffpeakTips.setOnClickListener(this)
        binding.dataViewPower.setOnClickListener(this)
        binding.llSchedule.setOnClickListener(this)
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
                upView(first.isNotEmpty())

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

                    StatusUtil.setImageStatus(this, it.status, binding.ivChargeStatus, 50)
                    val valueFromKWh = ValueUtil.valueFromKWh(it.transaction.energyKWH)
                    val valueFromA = ValueUtil.valueFromA(it.transaction.current)
                    val valueFromW = ValueUtil.valueFromW(it.transaction.powerKW)
                    val valueFromCost = ValueUtil.valueFromCost(it.transaction.cost)

                    val valueFromV = ValueUtil.valueFromV(it.transaction.voltage)


                    /*     if (it.transaction.powerKW == 0.0 || it.transaction.current == 0.0) {
                             chargeViewModel.valueVoltage = ValueUtil.valueFromV(0.0)
                         } else {
                             chargeViewModel.valueVoltage =
                                 ValueUtil.valueFromV(it.transaction.powerKW / it.transaction.current)
                         }*/


                    chargeViewModel.valueCurrent = valueFromA
                    chargeViewModel.valuePower = valueFromW
                    chargeViewModel.valueVoltage = valueFromV



                    binding.dataViewCapacity.setValue(valueFromKWh.first + valueFromKWh.second)
                    binding.dataViewPower.setValue(valueFromW.first + valueFromW.second)
//                    binding.dataViewVoltage.setValue(valueFromW.first + valueFromW.second)
                    binding.dataViewConsumption.setValue(valueFromCost)
                    binding.dataViewTime.setValue(it.transaction.charingTimeText)
                    if (it.transaction.offPeakStatus == 1) {
                        binding.ivOffpeakTips.visible()
                        if (!popShow)showPopoffpeak()
                    } else binding.ivOffpeakTips.gone()


                    val scList = it.scList
                    if (scList.isNotEmpty()) {
                        var schedule = scList.get(0)
                        val startTimeText = schedule.startTimeText
                        schedule = scList.get(scList.size - 1)
                        val endTimeText = schedule.endTimeText
                        val s = String.format("%s-%s", startTimeText, endTimeText)
                        binding.tvSchdule.text = s
                        binding.itemCheckbox.visible()
                    } else {
                        binding.tvSchdule.text = getString(R.string.m191_no_schedule)
                        binding.itemCheckbox.gone()
                    }
                    binding.itemCheckbox.isChecked = it.transaction.scheduledStatus == 1
                    authorizeStatus = it.transaction.authorizeStatus
                    if (it.status == ChargeStatus.PREPEAR && chargeViewModel.status == ChargeStatus.AVAILABLE) {
                        if (authorizeStatus == 1) {
                            //授权开启时 检测到插枪 弹框提示去充电
                            showDialogCharge()
                        } else {
                            //授权关闭时 检测到插枪 直接去充电
                            chargeAction()
                        }
                    }
                    chargeViewModel.status = it.status
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
            ToastUtil.show(it.second)

            //成功或者失败都立刻刷新一次(延迟3秒)
            lifecycleScope.launch {
                delay(3000)
                chargeViewModel.getChargeInfo()
            }
        }

        chargeViewModel.chargeSechLiveData.observe(this) {
            dismissDialog()
            ToastUtil.show(it)
        }


    }

    private fun upView(isListEmpty: Boolean) {
        binding.smartRefresh.setEnableRefresh(isListEmpty)
    }


    private fun updataChargeList(first: List<ChargeModel>) {
        if (first.isNotEmpty()) {
            //取第一个充电桩
//            MainApplication.instance().storageService().put(CURRENT_CHARGE,chargeViewModel.chargerId);
            //上一个充电桩
            val storageCharge =
                MainApplication.instance().storageService().getString(CURRENT_CHARGE, "")
            var chargeModel = first[0]
            if (!TextUtils.isEmpty(storageCharge)) {
                for (i in first.indices) {
                    val charge = first.get(i)
                    charge.chargerId?.let {
                        if (storageCharge.equals(it)) {
                            chargeModel = first[i]
                        }
                    }
                }
            }

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
            binding.ivChargeStatus.setImageResource(R.drawable.unavailable)
            binding.tvChargeExcption.setText(R.string.m97_please_add_charge)
            binding.ivStart.setImageResource(R.drawable.start)


            //跳转添加页面
            AddYourChargeActivity.start(this)

        }
    }


    override fun onClick(p0: View?) {
        when {
            p0 === binding.tvChargeChoose -> showSelectChartType()
            p0 === binding.ivLock -> unlock()
            p0 === binding.ivStart -> chargeAction()
            p0 === binding.ivAvatar -> MineActivity.start(this)
            p0 === binding.ivMenu -> RecordActivity.start(this)
            p0 === binding.tvChargingSetting -> showSelectSetting()
            p0 === binding.ivSet -> ChargeSettingActivity.start(this)
            p0 === binding.ivOffpeakTips -> showPopoffpeak()
            p0 === binding.dataViewPower -> showCurrentVoltage()
            p0 === binding.llSchedule -> ScheduledChargeActivity.start(this)
        }
    }


    private fun showCurrentVoltage() {


        CustomViewDialog.show(
            supportFragmentManager,
            chargeViewModel.valueCurrent,
            chargeViewModel.valueVoltage,
            chargeViewModel.valuePower
        )
    }


    private fun showPopoffpeak() {
        popShow=true
        val popuwindow = CustomPopuwindow(this, R.layout.offpeak_tips)
        popuwindow.showAsDropDown(binding.ivOffpeakTips)
    }


    private fun showSelectSetting() {
        OptionsDialog.show(supportFragmentManager, ChargeSettingManager.List.toTypedArray()) {
            when (ChargeSettingManager.List[it]) {
                "Schedule Charging" -> ScheduledChargeActivity.start(this)
                "Off-peak charging" -> ActivityOffpeak.start(this)
                "Authorization management" -> GunAuthActivity.start(this)

            }
        }
    }


    private fun showSelectChartType() {
        OptionsDialog.show(supportFragmentManager, chargeViewModel.chargeids.toTypedArray()) {
            chargeViewModel.status = ChargeStatus.NONE

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
            getString(R.string.m16_cancel),
            getString(R.string.m18_confirm),
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


    private fun showDialogCharge() {
        AlertDialog.showDialog(
            supportFragmentManager,
            getString(R.string.m164_start_charging),
            getString(R.string.m16_cancel),
            getString(R.string.m18_confirm),
            ""
        ) {
            val actionUrl = StatusUtil.getActionUrl(ChargeStatus.PREPEAR)
            if (actionUrl.isNotEmpty()) {
                chargeViewModel.unStartOrStopCharge(actionUrl)
            }
        }
    }


    private fun refreshUserProfile() {
        Glide.with(this).load(accountService().user()?.address)
            .placeholder(R.drawable.big_user)
            .into(binding.ivAvatar)
    }

    override fun onUserProfileChange(account: IAccountService) {
        refreshUserProfile()
    }

    override fun onCheckedChanged(p0: CompoundButton?, p1: Boolean) {
        if (p0?.isPressed == true) {
            chargeViewModel.setScheduledChargingStatus(if (p1) "1" else "0")
        }
    }


}