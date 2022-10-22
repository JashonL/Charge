package com.shuoxd.charge.ui.charge.activity

import android.R
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.base.BasePageListAdapter
import com.shuoxd.charge.base.BaseViewHolder
import com.shuoxd.charge.base.OnItemClickListener
import com.shuoxd.charge.databinding.ActivityRecordBinding
import com.shuoxd.charge.databinding.ItemRecordBinding
import com.shuoxd.charge.model.charge.RecordModel
import com.shuoxd.charge.ui.charge.viewmodel.RecordViewModel
import com.shuoxd.charge.util.ValueUtil
import com.shuoxd.charge.view.itemdecoration.DividerItemDecoration
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible

class RecordActivity : BaseActivity() {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, RecordActivity::class.java))
        }
    }


    private lateinit var binding: ActivityRecordBinding
    private val recordViewModel: RecordViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
//        getRecordlist()
        initData()

        requestData()
    }

    private fun requestData() {
        (binding.rlvRecord.adapter as Adapter).refresh()
    }

    private fun initViews() {

        binding.srlPull.setOnRefreshListener {
            (binding.rlvRecord.adapter as Adapter).refresh()
        }


        binding.rlvRecord.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL,
                resources.getColor(R.color.transparent),
                10f
            )
        )
        binding.rlvRecord.adapter = Adapter().also {
            it.refreshView = binding.srlPull
        }


    }


    private fun getRecordlist(curentPage:Int) {
        val currentChargeModel = getCurrentChargeModel()
        recordViewModel.getTransactionlist(currentChargeModel?.chargerId,curentPage)
    }

    private fun initData() {
        recordViewModel.recordLiveData.observe(this) {
            dismissDialog()
            if (it.second == null) {
                getAdapter().setResultSuccess(it.first!!)
            } else {
                ToastUtil.show(it.second)
                getAdapter().setResultError()

            }
        }

    }


    private fun getAdapter(): Adapter {
        return binding.rlvRecord.adapter as Adapter
    }

    inner class Adapter : BasePageListAdapter<RecordModel>(), OnItemClickListener {

        override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            /*    return RecordViewHolder.create(parent) { view, position ->
                    AlertDialog.showDialog(
                        supportFragmentManager,
                        getString(R.string.delete_message_or_not)
                    ) {
                        showDialog()

                    }
                }*/

            return RecordViewHolder.create(parent, this)
        }

        override fun onBindItemViewHolder(holder: BaseViewHolder, position: Int) {
            if (holder is RecordViewHolder) {
                holder.bindData(dataList[position])
            }
        }

        override fun onLoadNext() {
            getRecordlist(++currentPage)
        }

        override fun onRefresh() {
            getRecordlist(currentPage)
        }


        override fun showEmptyView() {
            binding.tvEmpty.visible()
        }

        override fun hideEmptyView() {
            binding.tvEmpty.gone()
        }

    }



    class RecordViewHolder(
        itemView: View,
    ) : BaseViewHolder(itemView) {

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener?
            ): RecordViewHolder {
                val binding = ItemRecordBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                val holder = RecordViewHolder(binding.root)
                holder.binding = binding
                holder.binding.root.setOnClickListener(holder)
                return holder
            }
        }

        private lateinit var binding: ItemRecordBinding

        fun bindData(chargeModel: RecordModel) {
            binding.tvName.text = chargeModel.chargerId
            binding.tvDate.text = chargeModel.startDate
            binding.tvGunName.text = chargeModel.connectorText
            binding.tvStartTime.text = chargeModel.startTimeLocalDetail
            binding.tvEndTime.text = chargeModel.stopTimeLocalDetail


            val valueFromKWh = ValueUtil.valueFromKWh(chargeModel.energyKWH.toDouble())
            binding.tvEnergyValue.text =
                String.format("%s%s", valueFromKWh.first, valueFromKWh.second)
            binding.tvTimeValue.text = chargeModel.charingTimeText
            binding.tvCostValue.text = chargeModel.cost

        }
    }

}


