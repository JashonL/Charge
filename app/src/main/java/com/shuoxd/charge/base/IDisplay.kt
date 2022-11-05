package com.shuoxd.charge.base

import android.view.View

interface IDisplay {


    fun showDialog()

    fun dismissDialog()

    fun showPageLoadingView()

    fun hidePageLoadingView()

    fun showPageErrorView(onRetry: ((view: View) -> Unit))

    fun hidePageErrorView()

    fun showResultDialog(result:String?,  onCancelClick: (() -> Unit)? = null,
                         onComfirClick: (() -> Unit)?)


}