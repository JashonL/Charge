package com.shuoxd.lib.util

import android.widget.Toast
import com.shuoxd.lib.LibApplication
import java.lang.ref.WeakReference

object ToastUtil {
    private var toast: WeakReference<Toast>? = null

    private fun cancelExist() {
        toast?.get()?.cancel()
    }

    fun show(content: String?) {
        content?.let {
            cancelExist()
            val showToast = Toast.makeText(LibApplication.instance(), content, Toast.LENGTH_SHORT)
            showToast.show()
            toast = WeakReference(showToast)
        }
    }

}