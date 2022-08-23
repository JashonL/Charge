package com.shuoxd.charge.base

import android.app.ProgressDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.shuoxd.charge.R
import com.shuoxd.charge.databinding.DefaultActivityErrorPageBinding
import java.lang.ref.WeakReference

/**
 * 加载与错误状态封装
 */
class AndroidDisplay(activity: BaseActivity) : IDisplay {

    private var progressDialog: ProgressDialog? = null

    private val wrActivity = WeakReference(activity)

    private var pageErrorView: View? = null

    override fun showDialog() {
        val activity = wrActivity.get() ?: return
        if (progressDialog == null) {
            progressDialog = ProgressDialog(activity).apply {
                setCanceledOnTouchOutside(false)
                setMessage(activity.getString(R.string.m12_loading))
            }
        }
        if (progressDialog?.isShowing == false) {
            progressDialog?.show()
        }
    }

    override fun dismissDialog() {
        if (progressDialog?.isShowing == true) {
            progressDialog?.dismiss()
        }
    }

    override fun showPageLoadingView() {
        val activity = wrActivity.get() ?: return
    }

    override fun hidePageLoadingView() {

    }

    override fun showPageErrorView(onRetry: ((view: View) -> Unit)) {
        val activity = wrActivity.get() ?: return
        if (!isPageErrorViewShow()) {
            val parent = findPlaceholderContainerView(activity)
            pageErrorView = generateErrorView(activity)
            parent.addView(pageErrorView)
            pageErrorView?.setOnClickListener {
                onRetry.invoke(it)
            }
        }
    }

    private fun isPageErrorViewShow(): Boolean {
        return pageErrorView != null
    }

    override fun hidePageErrorView() {
        val activity = wrActivity.get() ?: return
        findPlaceholderContainerView(activity).removeView(pageErrorView)
        pageErrorView = null
    }

    private fun findPlaceholderContainerView(activity: BaseActivity): ViewGroup {
        val viewGroup = activity.findViewById<ViewGroup>(R.id.activity_placeholder_page)
        return viewGroup ?: return activity.findViewById(Window.ID_ANDROID_CONTENT)
    }

    private fun generateErrorView(activity: BaseActivity): View {
        val binding = DefaultActivityErrorPageBinding.inflate(LayoutInflater.from(activity))
        return binding.root
    }

}