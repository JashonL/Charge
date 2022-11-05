package com.shuoxd.lib.util

import android.view.Gravity
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.shuoxd.lib.LibApplication
import com.shuoxd.lib.R
import java.lang.ref.WeakReference

object ToastUtil {
    private var toast: WeakReference<Toast>? = null

    private fun cancelExist() {
        toast?.get()?.cancel()
    }

    fun show(content: String?) {
        content?.let {
            cancelExist()
            val showToast = Toast.makeText(LibApplication.instance(), content, Toast.LENGTH_LONG)
            showToast.setGravity(Gravity.CENTER, 0, 0)
            showToast.view?.setBackgroundResource(
                R.drawable.shape_toast_bg
            )
            showToast.view?.findViewById<TextView>(android.R.id.message)
                ?.setTextColor(ContextCompat.getColor(LibApplication.instance(), R.color.white))

            showToast.show()
            toast = WeakReference(showToast)
        }



    }


    fun showShortToast(content: String?) {
        content?.let {
            cancelExist()
            val showToast = Toast.makeText(LibApplication.instance(), content, Toast.LENGTH_SHORT)
            showToast.setGravity(Gravity.CENTER,0,0)
            showToast.show()
            toast = WeakReference(showToast)
        }
    }



}