package com.shuoxd.charge.ui.common.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.text.TextUtils
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityWebBinding
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible

/**
 * 通用WebView页面
 */
class WebActivity : BaseActivity() {

    companion object {

        private const val KEY_WEB_URL = "key_web_url"
        private const val KEY_WEB_TITLE = "key_web_title"

        fun start(context: Context?, webUrl: String?, webTitle: String? = null) {
            context?.startActivity(Intent(context, WebActivity::class.java).apply {
                putExtra(KEY_WEB_URL, webUrl)
                putExtra(KEY_WEB_TITLE, webTitle)
            })
        }

    }

    private lateinit var binding: ActivityWebBinding
    private var webUrl: String? = null
    private var webTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWebBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initData()
        initView()
        setListener()
        //解决第一次打开WebView导致通过Application获取string的多语言适配失效
        MainApplication.instance().initLanguage(applicationContext)
    }

    private fun initData() {
        webUrl = intent.getStringExtra(KEY_WEB_URL)
        webTitle = intent.getStringExtra(KEY_WEB_TITLE)
    }

    private fun setListener() {
        binding.web.webViewClient = object : WebViewClient() {
            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                binding.pbProgress.visible()
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                binding.pbProgress.gone()
            }
        }

        binding.web.webChromeClient = object : WebChromeClient() {
            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
            /*    if (TextUtils.isEmpty(webTitle) && !TextUtils.isEmpty(title)) {
                    binding.title.setTitleText(title)
                }*/
            }

            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                binding.pbProgress.progress = newProgress
            }
        }
    }

    private fun initView() {
     /*   if (!TextUtils.isEmpty(webTitle)) {
            binding.title.setTitleText(webTitle)
        }*/
        if (!TextUtils.isEmpty(webUrl)) {
            binding.web.loadUrl(webUrl!!)
        }
    }

    override fun onBackPressed() {
        if (binding.web.canGoBack()) {
            binding.web.goBack()
            return
        }
        super.onBackPressed()
    }
}