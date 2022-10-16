package com.shuoxd.charge.ui.smartcharge

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityOffpeakBinding

class ActivityOffpeak:BaseActivity() {

    companion object{
        fun start(context: Context?) {
            context?.startActivity(Intent(context, ActivityOffpeak::class.java))
        }

    }

    private lateinit var binding: ActivityOffpeakBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityOffpeakBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


}