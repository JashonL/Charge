package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityOtherBinding

class OhterActivity:BaseActivity() {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, OhterActivity::class.java))
        }
    }



    private lateinit var bind: ActivityOtherBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityOtherBinding.inflate(layoutInflater)
        setContentView(bind.root)

    }


}