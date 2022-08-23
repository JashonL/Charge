package com.shuoxd.charge.service.account

import android.content.Context
import com.shuoxd.charge.application.Foreground
import com.shuoxd.lib.service.account.BaseAccountService

class DefaultAccountService : BaseAccountService() {

    override fun login(context: Context) {
//        LoginActivity.startClearTask(context)
    }

    override fun tokenExpired() {
        logout()
        Foreground.instance().getTopActivity()?.also {
            login(it)
        }
    }
}