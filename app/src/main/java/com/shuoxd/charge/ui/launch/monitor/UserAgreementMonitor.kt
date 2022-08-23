package com.shuoxd.charge.ui.launch.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shuoxd.charge.application.MainApplication

/**
 * 隐私政策同意与否监听
 */
class UserAgreementMonitor : BroadcastReceiver(), LifecycleObserver {

    private var listener: ((isAgree: Boolean, monitor: UserAgreementMonitor) -> Unit)? = null

    companion object {

        private const val ACTION_AGREEMENT = "action_agreement"
        private const val KEY_IS_AGREE = "key_is_agree"

        /**
         * 使用高阶函数，将函数作为参数传进去，减少创建接口
         */
        fun watch(
            lifecycle: Lifecycle? = null,
            listener: (isAgree: Boolean, monitor: UserAgreementMonitor) -> Unit
        ): UserAgreementMonitor {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_AGREEMENT)
            val monitor = UserAgreementMonitor()
            monitor.listener = listener
            lifecycle?.addObserver(monitor)
            LocalBroadcastManager.getInstance(MainApplication.instance())
                .registerReceiver(monitor, intentFilter)
            return monitor
        }

        fun notify(isAgree: Boolean) {
            LocalBroadcastManager.getInstance(MainApplication.instance())
                .sendBroadcast(Intent().apply {
                    action = ACTION_AGREEMENT
                    putExtra(KEY_IS_AGREE, isAgree)
                })
        }
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        intent?.let {
            if (it.action == ACTION_AGREEMENT) {
                listener?.invoke(it.getBooleanExtra(KEY_IS_AGREE, false), this)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unWatch() {
        listener = null
        LocalBroadcastManager.getInstance(MainApplication.instance()).unregisterReceiver(this)
    }
}