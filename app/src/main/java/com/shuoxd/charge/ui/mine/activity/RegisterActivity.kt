package com.shuoxd.charge.ui.mine.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import androidx.activity.viewModels
import com.shuoxd.charge.BuildConfig
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseActivity
import com.shuoxd.charge.databinding.ActivityRegisterBinding
import com.shuoxd.charge.ui.common.activity.WebActivity
import com.shuoxd.charge.ui.mine.viewmodel.RegisterViewModel
import com.shuoxd.lib.util.ToastUtil
import com.shuoxd.lib.util.Util

class RegisterActivity : BaseActivity(), View.OnClickListener {

    companion object {
        fun start(context: Context?) {
            context?.startActivity(Intent(context, RegisterActivity::class.java))
        }
    }


    private lateinit var binding: ActivityRegisterBinding

    private val viewModel: RegisterViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setListeners()
        initData()

    }

    private fun initData() {
        viewModel.registerLiveData.observe(this) {
            dismissDialog()
            if (it == null) {
                ToastUtil.show(getString(R.string.m90_register_success))
                //注册成功，关闭页面返回登录页面
                finish()
            } else {
                ToastUtil.show(it)
            }
        }

    }

    private fun initView() {
        val timeZone = Util.getTimeZone()
        binding.etTimeZone.setText(timeZone)

        binding.tvUserAgreement.run {
            highlightColor = resources.getColor(android.R.color.transparent)
            movementMethod = LinkMovementMethod.getInstance()
            text = getTvSpan()
        }

    }



    private fun getTvSpan(): SpannableString {
        val userAgreement = getString(R.string.m23_user_agreement)
        val privacyPolicy = getString(R.string.m24_privacy_policy)
        val content = getString(R.string.m88_agree_company_user_agreement, userAgreement, privacyPolicy)
        return SpannableString(content).apply {
            addColorSpan(this, userAgreement)
            addClickSpan(this, userAgreement) {
                WebActivity.start(this@RegisterActivity, BuildConfig.userAgreementUrl)
            }
            addColorSpan(this, privacyPolicy)
            addClickSpan(this, privacyPolicy) {
                WebActivity.start(this@RegisterActivity, BuildConfig.privacyPolicyUrl)
            }
        }
    }


    private fun addColorSpan(spannable: SpannableString, colorSpanContent: String) {
        val span = ForegroundColorSpan(resources.getColor(R.color.red))
        val startPosition = spannable.toString().indexOf(colorSpanContent)
        val endPosition = startPosition + colorSpanContent.length
        spannable.setSpan(span, startPosition, endPosition, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }



    private fun addClickSpan(
        spannable: SpannableString,
        clickSpanContent: String,
        click: (View) -> Unit
    ) {
        val span = object : ClickableSpan() {
            override fun onClick(view: View) {
                click(view)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                //去掉下划线
                ds.isUnderlineText = false
            }
        }
        val startPosition = spannable.toString().indexOf(clickSpanContent)
        val endPosition = startPosition + clickSpanContent.length
        spannable.setSpan(span, startPosition, endPosition, Spannable.SPAN_INCLUSIVE_EXCLUSIVE)
    }


    private fun setListeners() {
        binding.btnLogin.setOnClickListener(this)
        binding.ivSelect.setOnClickListener(this)


    }

    override fun onClick(p0: View?) {

        when {
            p0 === binding.btnLogin -> checkInputInfo()
            p0 === binding.ivSelect -> updateSelectView(!viewModel.isAgree)
        }
    }


    private fun updateSelectView(isAgree: Boolean) {
        binding.ivSelect.setImageResource(if (isAgree) R.drawable.ic_unselect else R.drawable.ic_unselect)
        viewModel.isAgree = isAgree
    }


    /**
     * 检查输入信息是否合规
     */
    private fun checkInputInfo() {

        val username = binding.etUsername.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etReenterPassword.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val city = binding.etCity.text.toString().trim()
        val country = binding.etCountry.text.toString().trim()
        val timeZone = binding.etTimeZone.text.toString().trim()


        if (!viewModel.isAgree) {
            ToastUtil.show(getString(R.string.m81_please_check_agree_agreement))
        } else if (TextUtils.isEmpty(username)) {
            ToastUtil.show(getString(R.string.m74_please_input_username))
        } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            ToastUtil.show(getString(R.string.m82_password_cant_empty))
        } else if (password.length < 6 || confirmPassword.length < 6) {
            ToastUtil.show(getString(R.string.m84_password_cannot_be_less_than_6_digits))
        } else if (password != confirmPassword) {
            ToastUtil.show(getString(R.string.m83_passwords_are_inconsistent))
        } else if (TextUtils.isEmpty(phone)) {
            ToastUtil.show(getString(R.string.m85_no_phone_number))
        } else if (TextUtils.isEmpty(city)) {
            ToastUtil.show(getString(R.string.m89_no_email))
        }

        else if (TextUtils.isEmpty(country)) {
            ToastUtil.show(getString(R.string.m87_no_country))
        } else if (TextUtils.isEmpty(timeZone)) {
            ToastUtil.show(getString(R.string.m86_no_timezone))
        } else {
            viewModel.register(username,password,country,city,phone,timeZone)
        }
    }


}