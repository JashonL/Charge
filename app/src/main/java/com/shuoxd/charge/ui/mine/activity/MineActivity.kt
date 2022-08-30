package com.shuoxd.charge.ui.mine.activity

import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.base.BaseViewHolder
import com.shuoxd.charge.base.OnItemClickListener
import com.shuoxd.charge.databinding.ActivityUserBinding
import com.shuoxd.charge.databinding.ItemChargeInMineBinding
import com.shuoxd.charge.model.charge.ChargeModel
import com.shuoxd.charge.ui.authorize.AuthListActivity
import com.shuoxd.charge.ui.charge.activity.AddYourChargeActivity
import com.shuoxd.charge.ui.mine.viewmodel.SettingViewModel
import com.shuoxd.charge.view.itemdecoration.DividerItemDecoration
import com.shuoxd.lib.util.ToastUtil

class MineActivity : BaseActivity(),View.OnClickListener {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, MineActivity::class.java))
        }
    }


    private lateinit var bind: ActivityUserBinding;

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityUserBinding.inflate(layoutInflater)
        setContentView(bind.root)
        initViews()
        initData()
        setlisteners()

    }

    private fun setlisteners() {
        bind.itemAddCharger.setOnClickListener(this)
        bind.ivAvatar.setOnClickListener(this)
        bind.itemEmail.setOnClickListener(this)
        bind.itemAuth.setOnClickListener(this)
        bind.itemModifyPassword.setOnClickListener(this)
        bind.itemPhone.setOnClickListener(this)
        bind.btLogout.setOnClickListener(this)
    }


    private fun initViews() {
        val user = accountService().user()
        user?.let {
            bind.tvUserName.text = user.email
        }

        bind.rlvCharge.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.HORIZONTAL,
                resources.getColor(R.color.transparent),
                10f
            )
        )

        bind.rlvCharge.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        bind.rlvCharge.adapter = Adapter()


    }




    override fun onClick(v: View?) {
        when {
            v === bind.btLogout -> {
                showDialog()
                viewModel.logout(accountService().user()?.email)
            }
            v===bind.itemAddCharger-> AddYourChargeActivity.start(this)

            v===bind.itemAuth->AuthListActivity.start(this)
        }
    }



    private fun initData() {
        val chargeList = getChargeList()
        (bind.rlvCharge.adapter as Adapter).refresh(chargeList)
        viewModel.logoutLiveData.observe(this) {
            dismissDialog()
            if (it == null) {
                accountService().logout()
                accountService().login(this)
                finish()
            } else {
                ToastUtil.show(it)
            }
        }

    }

    inner class Adapter(var chargeList: MutableList<ChargeModel> = mutableListOf()) :
        RecyclerView.Adapter<ChargeViewHolder>(), OnItemClickListener {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ChargeViewHolder {
            return ChargeViewHolder.create(parent, this)
        }

        override fun onBindViewHolder(holder: ChargeViewHolder, position: Int) {
            holder.bindData(chargeList[position],position)
        }

        override fun getItemCount(): Int {
            return chargeList.size
        }

        @SuppressLint("NotifyDataSetChanged")
        fun refresh(serviceList: List<ChargeModel>) {
            this.chargeList.clear()
            this.chargeList.addAll(serviceList)
            notifyDataSetChanged()
        }

        override fun onItemClick(v: View?, position: Int) {
            val tag = v?.tag
            if (tag is ChargeModel) {
                openWebPage(position)
            }
        }

        private fun openWebPage(position: Int) {
            setCurrenChargeModel(chargeList[position])
            finish()
        }
    }


    class ChargeViewHolder(itemView: View, onItemClickListener: OnItemClickListener? = null) :
        BaseViewHolder(itemView,onItemClickListener) {

        private lateinit var binding: ItemChargeInMineBinding

        companion object {
            fun create(
                parent: ViewGroup,
                onItemClickListener: OnItemClickListener?
            ): ChargeViewHolder {
                val binding = ItemChargeInMineBinding.inflate(LayoutInflater.from(parent.context))
                val holder = ChargeViewHolder(binding.root, onItemClickListener)
                holder.binding = binding
                holder.binding.root.setOnClickListener(holder)
                return holder
            }
        }


        fun bindData(chargeModel: ChargeModel,position: Int) {
            binding.tvChageName.text=chargeModel.chargerId
            binding.root.tag = chargeModel
        }


    }


}