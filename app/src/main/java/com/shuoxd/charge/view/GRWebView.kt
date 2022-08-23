package com.shuoxd.charge.view

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.webkit.WebSettings
import android.webkit.WebView

/**
 * WebView配置
 */
class GRWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : WebView(context, attrs, defStyleAttr) {

    //WebView的基本配置
    init {
        settings.defaultFontSize = 13
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
        }
        settings.allowFileAccessFromFileURLs = false
        settings.allowUniversalAccessFromFileURLs = false
        settings.domStorageEnabled = true
        settings.savePassword = false
        settings.saveFormData = false
        settings.javaScriptEnabled = true
    }

}