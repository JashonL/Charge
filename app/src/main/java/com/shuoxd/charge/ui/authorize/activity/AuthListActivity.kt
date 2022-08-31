package com.shuoxd.charge.ui.authorize.activity

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
import com.shuoxd.charge.databinding.ActivityAuthListBinding
import com.shuoxd.charge.databinding.ItemAuthBinding
import com.shuoxd.charge.model.charge.AuthModel
import com.shuoxd.charge.ui.authorize.monitor.AuthMonitor
import com.shuoxd.charge.ui.authorize.viewmodel.AuthViewModel
import com.shuoxd.charge.view.itemdecoration.DividerItemDecoration
import com.shuoxd.lib.util.ToastUtil

class AuthListActivity : BaseActivity() {


    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, AuthListActivity::class.java))
        }

    }


    private lateinit var binding: ActivityAuthListBinding

    private val authModel: AuthViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initData()
        requestData()
    }


    private fun requestData() {
        (binding.rlAuthlist.adapter as Adapter).refresh()
    }


    private fun initData() {
        AuthMonitor.watch(lifecycle) {
            getAdapter().currentPage=1
            (binding.rlAuthlist.adapter as Adapter).refresh()
        }

        authModel.authListLiveData.observe(this){
            binding.srlPull.finishRefresh()
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
        return binding.rlAuthlist.adapter as Adapter
    }

    private fun getRecordlist(curentPage: Int) {
        val email = accountService().user()?.email
        authModel.getAuthlist(email, curentPage)
    }


    private fun initViews() {

        binding.srlPull.setOnRefreshListener {
            getAdapter().currentPage=1
            (binding.rlAuthlist.adapter as Adapter).refresh()
        }


        binding.rlAuthlist.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL,
                resources.getColor(R.color.transparent),
                1f
            )
        )
        binding.rlAuthlist.adapter = Adapter()
        binding.titleBar.setOnRightImageClickListener {
            AddAuthActivity.start(this)
        }
    }


    inner class Adapter : BasePageListAdapter<AuthModel>(), OnItemClickListener {

        override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            return AuthViewHolder.create(parent) { _, position ->
                run {
                    //请求删除授权

                }
            }
        }

        override fun onBindItemViewHolder(holder: BaseViewHolder, position: Int) {
            if (holder is AuthViewHolder) {
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
        }

        override fun hideEmptyView() {
        }


    }


    class AuthViewHolder(
        itemView: View,
    ) : BaseViewHolder(itemView) {

        companion object {
            fun create(
                parent: ViewGroup,
                ondelete: (view: View, position: Int) -> Unit
            ): AuthViewHolder {
                val binding = ItemAuthBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                val holder = AuthViewHolder(binding.root)
                holder.binding = binding
                holder.binding.root.setOnClickListener(holder)
                holder.binding.btnDelete.setOnClickListener {
                    ondelete.invoke(it, holder.bindingAdapterPosition)
                }
                return holder
            }
        }

        private lateinit var binding: ItemAuthBinding

        fun bindData(authModel: AuthModel) {
            binding.tvChargeId.text = authModel.chargerId
            binding.tvTime.text = authModel.addTime
            binding.tvUsername.text = authModel.usernameLater
        }


    }


}