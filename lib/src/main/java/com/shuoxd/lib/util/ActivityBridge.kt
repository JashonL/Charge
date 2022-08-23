package com.shuoxd.lib.util

import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager

/**
 * startActivityForResult帮助类
 */
object ActivityBridge {
    /**
     *
     * @param sourceActivity
     * @param openActivityIntent
     * @param onActivityForResult
     */
    fun startActivity(
        sourceActivity: FragmentActivity,
        openActivityIntent: Intent,
        onActivityForResult: OnActivityForResult
    ) {
        InternalSourceFragment.attachToActivity(
            sourceActivity.supportFragmentManager,
            openActivityIntent,
            onActivityForResult
        )
    }

    class InternalSourceFragment : Fragment() {
        private lateinit var onActivityForResult: OnActivityForResult
        private lateinit var openActivityIntent: Intent
        private val internalRequestCode = 1231
        override fun onAttach(context: Context) {
            super.onAttach(context)
            startActivityForResult(openActivityIntent, internalRequestCode)
            retainInstance = true
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            if (requestCode == internalRequestCode) {
                onActivityForResult.onActivityForResult(context, resultCode, data)
            }
            detachSelf()
        }

        private fun detachSelf() {
            try {
                parentFragmentManager.beginTransaction().remove(this).commitAllowingStateLoss()
            } catch (e: Exception) {
            }
        }

        companion object {
            fun attachToActivity(
                fragmentManager: FragmentManager,
                openActivityIntent: Intent,
                callback: OnActivityForResult
            ) {
                val internal = InternalSourceFragment()
                internal.openActivityIntent = openActivityIntent
                internal.onActivityForResult = callback
                val tag = String.format(
                    "InternalBridgeFragment-%s",
                    System.currentTimeMillis().toString()
                )
                fragmentManager.beginTransaction().add(internal, tag).commit()
            }
        }
    }

    interface OnActivityForResult {
        fun onActivityForResult(context: Context?, resultCode: Int, data: Intent?)
    }
}