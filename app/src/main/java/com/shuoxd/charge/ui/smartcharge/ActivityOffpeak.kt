package com.shuoxd.charge.ui.smartcharge

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.base.BaseViewHolder
import com.shuoxd.charge.base.OnItemClickListener
import com.shuoxd.charge.databinding.ActivityOffpeakBinding
import com.shuoxd.charge.databinding.ItemOffpeakBinding
import com.shuoxd.charge.model.charge.OffPeakModel
import com.shuoxd.charge.ui.smartcharge.viewmodel.OffpeakViewModel
import com.shuoxd.charge.view.itemdecoration.DividerItemDecoration
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible
import com.shuoxd.lib.view.dialog.OnTimeSetListener
import com.shuoxd.lib.view.dialog.TimePickerFragment

class ActivityOffpeak:BaseActivity() , View.OnClickListener{

    companion object{
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ActivityOffpeak::class.java))
        }

    }

    private lateinit var binding: ActivityOffpeakBinding

    private val offpeakViewModel: OffpeakViewModel by viewModels()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOffpeakBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setlisteners()
        initData()
        fresh()

    }


    private fun setlisteners() {
        binding.btAdd.setOnClickListener(this)
    }

    private fun initViews() {
        binding.rvTimeList.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL,
                resources.getColor(R.color.nocolor),
                2f
            )
        )
        binding.rvTimeList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvTimeList.adapter = Adapter()



        binding.titleBar.setOnRightTextClickListener {
            save()
        }

    }


    private fun save() {
        val offpeakList = (binding.rvTimeList.adapter as Adapter).offpeakList

        var timeList = ""
        val chargerId = getCurrentChargeModel()?.chargerId
        val connectorId = "1"
        val status = if (binding.enable.isCheck()) "1" else "0"

        for (i in offpeakList.indices) {
            val scheduled = offpeakList.get(i)
            val startTimeText = scheduled.startTimeText
            val endtIimeText = scheduled.endTimeText

            if ("--".equals(startTimeText) || "--".equals(endtIimeText) ||
                TextUtils.isEmpty(startTimeText) || TextUtils.isEmpty(endtIimeText)
            ) {
                ToastUtil.show(getString(R.string.m170_not_empty))
                break
            }

            if (i != 0) {
                timeList += ",$startTimeText-$endtIimeText"
            } else {
                timeList = "$startTimeText-$endtIimeText"
            }
        }
        offpeakViewModel.setOffPeakCharging(timeList,chargerId,connectorId,status)

    }


    private fun fresh() {
        //请求充电桩列表
        val user = accountService().user()
        if (user != null) {
            val currentChargeModel = getCurrentChargeModel()
            offpeakViewModel.getOffPeakChargingByUserId(
                user.id,
                currentChargeModel?.chargerId,
                "1"
            )
        }
    }


    private fun initData() {
        offpeakViewModel.offpeakLiveData.observe(this) {
            dismissDialog()
            if (it.first) {
                val scList = it.second?.ocList
                scList?.let { it1 -> (binding.rvTimeList.adapter as Adapter).refresh(it1) }
            }
        }

        offpeakViewModel.setOffpeakLiveData.observe(this){
            dismissDialog()
            ToastUtil.show(it.second)
        }


    }





    inner class Adapter(var offpeakList: MutableList<OffPeakModel.Period> = mutableListOf()) :
        RecyclerView.Adapter<OffpeakViewHolder>(), OnItemClickListener,
        OffpeakViewHolder.OnItemChildClickListener {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): OffpeakViewHolder {
            val holder = OffpeakViewHolder.create(parent, this, this)
            return holder;
        }

        override fun onBindViewHolder(holder: OffpeakViewHolder, position: Int) {
            holder.bindData(offpeakList[position], position)
        }

        override fun getItemCount(): Int {
            return offpeakList.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refresh(serviceList: List<OffPeakModel.Period>) {
            this.offpeakList.clear()
            this.offpeakList.addAll(serviceList)
            notifyDataSetChanged()
        }


        @SuppressLint("NotifyDataSetChanged")
        fun addData(period: OffPeakModel.Period) {
            this.offpeakList.add(period)
            if (offpeakList.size >= 3) {
                binding.btAdd.gone()
            }
            notifyDataSetChanged()
        }


        override fun onItemClick(v: View?, position: Int) {

        }


        override fun onItemLongClick(v: View?, position: Int) {

        }


        override fun onItemChildClick(v: View?, position: Int, binding: ItemOffpeakBinding) {
            when {
                v === binding.ivDelete -> deleteItem(position)
                v === binding.tvEndTime -> setItemTime(false, position)
                v === binding.tvStartTime -> setItemTime(true, position)
            }
        }


        private fun setItemTime(startOrEnd: Boolean, position: Int) {
            TimePickerFragment.show(
                supportFragmentManager,
                -1,
                -1,
                object : OnTimeSetListener {
                    override fun onTimeSelected(hour: Int, min: Int) {
                        val time = String.format("%02d:%02d", hour, min)
                        if (startOrEnd) {
                            setItemStartTime(position, time)
                        } else {
                            setItemEndTime(position, time)
                        }
                    }

                })
        }


        @SuppressLint("NotifyDataSetChanged")
        fun setItemStartTime(position: Int, time: String) {
            offpeakList.get(position).startTimeText = time
            notifyDataSetChanged()
        }


        @SuppressLint("NotifyDataSetChanged")
        fun setItemEndTime(position: Int, time: String) {
            offpeakList.get(position).endTimeText = time
            notifyDataSetChanged()
        }


        @SuppressLint("NotifyDataSetChanged")
        fun deleteItem(position: Int) {
            this.offpeakList.removeAt(position)
            if (offpeakList.size < 3) {
                binding.btAdd.visible()
            }
            notifyDataSetChanged()
        }


    }


    class OffpeakViewHolder(itemView: View, onItemClickListener: OnItemClickListener? = null) :
        BaseViewHolder(itemView, onItemClickListener) {

        lateinit var binding: ItemOffpeakBinding

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener?,
                onitemChildClick: OnItemChildClickListener
            ): OffpeakViewHolder {
                val binding = ItemOffpeakBinding.inflate(LayoutInflater.from(parent.context),  parent,
                    false)
                val holder = OffpeakViewHolder(binding.root, onItemClickListener)
                holder.binding = binding
                holder.binding.root.setOnLongClickListener(holder)

                holder.binding.ivDelete.setOnClickListener {
                    onitemChildClick.onItemChildClick(it, holder.bindingAdapterPosition, binding)
                }

                holder.binding.tvStartTime.setOnClickListener {
                    onitemChildClick.onItemChildClick(it, holder.bindingAdapterPosition, binding)
                }

                holder.binding.tvEndTime.setOnClickListener {
                    onitemChildClick.onItemChildClick(it, holder.bindingAdapterPosition, binding)
                }
                return holder
            }
        }


        fun bindData(period: OffPeakModel.Period, position: Int) {
            binding.tvStartTime.text = period.startTimeText
            binding.tvEndTime.text = period.endTimeText
        }


        interface OnItemChildClickListener {

            fun onItemChildClick(v: View?, position: Int, binding: ItemOffpeakBinding) {}

        }


    }


    override fun onClick(p0: View?) {
        when {
            p0 === binding.btAdd -> {
                addPeriod()
            }
        }

    }


    private fun addPeriod() {
        val currentChargeModel = getCurrentChargeModel()
        val user = accountService().user()
        val period: OffPeakModel.Period = OffPeakModel.Period(
            0,
            currentChargeModel?.chargerId,
            user?.id?.toInt(),
            "--:--",
            "--:--",
        )

        (binding.rvTimeList.adapter as Adapter).addData(period)

    }


}