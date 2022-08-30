package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityPersonalInfoBinding

class PersonalInfoActivity :BaseActivity(){


    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, PersonalInfoActivity::class.java))
        }
    }


    private lateinit var binding: ActivityPersonalInfoBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
    }

    private fun initViews() {
        val user = accountService().user()
        user?.let {
            binding.personName.setValue(user.email)
            binding.personMailbox.setValue(user.email)
            binding.personCity.setValue(user.city)
            binding.personCountry.setValue(user.country)
            binding.personPhone.setValue(user.phoneNum)


        }

    }

}