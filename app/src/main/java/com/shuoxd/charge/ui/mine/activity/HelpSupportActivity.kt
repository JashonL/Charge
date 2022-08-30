package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityHelpSupportBinding

class HelpSupportActivity :BaseActivity(){

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, HelpSupportActivity::class.java))
        }
    }



    private lateinit var bind: ActivityHelpSupportBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityHelpSupportBinding.inflate(layoutInflater)
        setContentView(bind.root)

    }

}