package com.shuoxd.charge.ui.charge.monitor

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.shuoxd.charge.application.MainApplication

class ChargeAactivityMonitor: BroadcastReceiver(),LifecycleObserver {


    private var listener: (() -> Unit)? = null
    private var listener2: ((monitor: ChargeAactivityMonitor) -> Unit)? = null

    companion object {

        private const val ACTION_CHARGE = "action_charge"
        /**
         * 使用高阶函数，将函数作为参数传进去，减少创建接口
         */
        fun watch(
            lifecycle: Lifecycle? = null,
            listener: () -> Unit,
            listener2: (monitor: ChargeAactivityMonitor) -> Unit
        ): ChargeAactivityMonitor {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_CHARGE)
            val monitor = ChargeAactivityMonitor()
            monitor.listener = listener
            monitor.listener2=listener2
            lifecycle?.addObserver(monitor)
            LocalBroadcastManager.getInstance(MainApplication.instance())
                .registerReceiver(monitor, intentFilter)
            return monitor
        }



        fun watchAddCharge(
            lifecycle: Lifecycle? = null,
            listener2: (monitor: ChargeAactivityMonitor) -> Unit
        ): ChargeAactivityMonitor {
            val intentFilter = IntentFilter()
            intentFilter.addAction(ACTION_CHARGE)
            val monitor = ChargeAactivityMonitor()
            monitor.listener2=listener2
            lifecycle?.addObserver(monitor)
            LocalBroadcastManager.getInstance(MainApplication.instance())
                .registerReceiver(monitor, intentFilter)
            return monitor
        }





        fun notifyPlant() {
            LocalBroadcastManager.getInstance(MainApplication.instance())
                .sendBroadcast(Intent().apply {
                    action = ACTION_CHARGE
                })
        }


    }



    override fun onReceive(p0: Context?, p1: Intent?) {
        p1?.let {
            if (it.action == ACTION_CHARGE) {
                listener2?.invoke(this)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun resume() {
        listener?.invoke()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun unWatch() {
        listener = null
        LocalBroadcastManager.getInstance(MainApplication.instance()).unregisterReceiver(this)
    }


}