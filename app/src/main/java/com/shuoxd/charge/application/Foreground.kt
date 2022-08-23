package com.shuoxd.charge.application

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.util.*

class Foreground private constructor() : Application.ActivityLifecycleCallbacks {

    companion object {
        private val INSTANCE by lazy(LazyThreadSafetyMode.NONE) {
            Foreground()
        }

        fun instance(): Foreground {
            return INSTANCE
        }

        fun init(app: Application) {
            app.registerActivityLifecycleCallbacks(INSTANCE)
        }
    }

    private val activityList = Stack<Activity>()
    private var isForeground = false
    private val listeners = mutableSetOf<Listener>()

    fun getTopActivity(): Activity? {
        if (activityList.isEmpty()) {
            return null
        }
        return activityList.peek()
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {

    }

    override fun onActivityStarted(activity: Activity) {
        activityList.push(activity)
        if (!isForeground) {
            isForeground = true
            notifyForeground()
        }
    }

    override fun onActivityResumed(activity: Activity) {

    }

    override fun onActivityPaused(activity: Activity) {

    }

    override fun onActivityStopped(activity: Activity) {
        activityList.remove(activity)
        if (activityList.isEmpty() && !activity.isChangingConfigurations) {
            isForeground = false
            notifyBackground()
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

    }

    override fun onActivityDestroyed(activity: Activity) {

    }

    private fun notifyForeground() {
        for (listener in listeners) {
            listener.becomeForeground()
        }
    }

    private fun notifyBackground() {
        for (listener in listeners) {
            listener.becomeBackground()
        }
    }

    private fun addListener(listener: Listener) {
        listeners.add(listener)
    }

    private fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    interface Listener {
        /**
         * 回到前台
         */
        fun becomeForeground()

        /**
         * 回到后台
         */
        fun becomeBackground()
    }

}