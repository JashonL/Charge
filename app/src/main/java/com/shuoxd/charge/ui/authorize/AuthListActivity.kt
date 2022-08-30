package com.shuoxd.charge.ui.authorize

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.base.BasePageListAdapter
import com.shuoxd.charge.base.BaseViewHolder
import com.shuoxd.charge.base.OnItemClickListener
import com.shuoxd.charge.databinding.ActivityAuthListBinding
import com.shuoxd.charge.databinding.ItemRecordBinding
import com.shuoxd.charge.model.charge.AuthModel

class AuthListActivity:BaseActivity() {


    companion object{
        fun start(context: Context?) {
            context?.startActivity(Intent(context, AuthListActivity::class.java))
        }

    }


    private lateinit var authListBinding: ActivityAuthListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authListBinding= ActivityAuthListBinding.inflate(layoutInflater)
        setContentView(authListBinding.root)
        initViews()

    }

    private fun initViews() {


    }




    inner class Adapter : BasePageListAdapter<AuthModel>(), OnItemClickListener {

        override fun onCreateItemViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
            /*    return RecordViewHolder.create(parent) { view, position ->
                    AlertDialog.showDialog(
                        supportFragmentManager,
                        getString(R.string.delete_message_or_not)
                    ) {
                        showDialog()

                    }
                }*/

            return AuthViewHolder.create(parent, this)
        }

        override fun onBindItemViewHolder(holder: BaseViewHolder, position: Int) {
            if (holder is AuthViewHolder) {
                holder.bindData(dataList[position])
            }
        }

        override fun onLoadNext() {
        }

        override fun onRefresh() {
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
                onItemClickListener: OnItemClickListener?
            ): AuthViewHolder {
                val binding = ItemRecordBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                val holder = AuthViewHolder(binding.root)
                holder.binding = binding
                holder.binding.root.setOnClickListener(holder)
                return holder
            }
        }

        private lateinit var binding: ItemRecordBinding

        fun bindData(authModel: AuthModel) {


        }
    }







}