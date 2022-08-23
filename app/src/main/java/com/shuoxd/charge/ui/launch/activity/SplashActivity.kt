package com.shuoxd.charge.ui.launch.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.shuoxd.charge.R
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.databinding.ActivitySplashBinding
import com.shuoxd.charge.ui.guide.activity.GuideActivity
import com.shuoxd.lib.view.statusbar.StatusBarCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var splashBinding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding= ActivitySplashBinding.inflate(layoutInflater)
        setContentView(splashBinding.root)

        StatusBarCompat.translucentStatusBar(this,true)
        StatusBarCompat.setWindowLightStatusBar(this, true)

        lifecycleScope.launch {
            enterApp()
        }


    }


    private suspend fun enterApp(){
        delay(2000)

        val boolean = MainApplication.instance().storageService()
            .getBoolean(MainApplication.instance().APP_FIRST, true)

        if (boolean){
            //跳转到引导页
            GuideActivity.start(this)
        }else{
            //跳转到登录页

        }
        finish()

    }



}