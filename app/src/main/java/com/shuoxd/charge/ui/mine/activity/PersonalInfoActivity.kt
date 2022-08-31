package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityPersonalInfoBinding
import com.shuoxd.charge.ui.mine.viewmodel.PersonalInfoViewModel
import com.shuoxd.charge.view.dialog.InputDialog
import com.shuoxd.lib.util.ToastUtil

class PersonalInfoActivity : BaseActivity(), View.OnClickListener {


    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, PersonalInfoActivity::class.java))
        }
    }


    private lateinit var binding: ActivityPersonalInfoBinding

    private val viewModel: PersonalInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initListeners()
        initData()
    }

    private fun initData() {
        viewModel.personalLiveData.observe(this) {
            ToastUtil.show(it.second)
            val first = it.first
            if (first != null) {
                val user = accountService().user()
                user?.let {
                    it.city = first.city
                    it.country = first.country
                    it.phoneNum = first.phoneNum
                }
            }
            if (it.third){
                finish()
            }


        }
    }

    private fun initListeners() {
        binding.password.setOnClickListener(this)
        binding.phone.setOnClickListener(this)
        binding.city.setOnClickListener(this)
        binding.country.setOnClickListener(this)
        binding.btDone.setOnClickListener(this)

    }

    private fun initViews() {
        val user = accountService().user()
        user?.let {
            binding.email.setSubName(user.email)
            binding.phone.setSubName(user.phoneNum)
            binding.city.setSubName(user.city)
            binding.country.setSubName(user.country)
        }

    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.password -> ModifyPasswordActivity.start(this)
            p0 === binding.phone -> showSelectChartType(
                p0,
                getString(R.string.m9_phone),
                binding.phone.getSubName()
            )
            p0 === binding.city -> showSelectChartType(
                p0,
                getString(R.string.m78_city),
                binding.city.getSubName()
            )
            p0 === binding.country -> showSelectChartType(
                p0,
                getString(R.string.m79_country),
                binding.country.getSubName()
            )
            p0 === binding.btDone -> {
                val phone = binding.phone.getSubName()
                val city = binding.city.getSubName()
                val country = binding.country.getSubName()
                viewModel.modifyUserInfo(phone, country, city)

            }
        }

    }


    private fun showSelectChartType(p0: View, title: String, content: String) {
        InputDialog.showDialog(
            supportFragmentManager,
            content,
            getString(R.string.m18_confirm),
            getString(R.string.m16_cancel),
            title
        ) {
            when {
                p0 === binding.phone -> binding.phone.setSubName(it)
                p0 === binding.city -> binding.city.setSubName(it)
                p0 === binding.country -> binding.country.setSubName(it)
            }
        }
    }


}