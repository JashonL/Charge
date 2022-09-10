package com.shuoxd.charge.ui.launch.fragment

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.shuoxd.charge.BuildConfig
import com.shuoxd.charge.application.MainApplication
import com.shuoxd.charge.ui.launch.monitor.UserAgreementMonitor
import com.shuoxd.lib.util.gone
import com.shuoxd.lib.util.visible
import com.shuoxd.charge.R
import com.shuoxd.charge.base.BaseDialogFragment
import com.shuoxd.charge.databinding.DialogUserAgreementBinding
import com.shuoxd.charge.ui.common.activity.WebActivity
import com.shuoxd.charge.util.AppUtil
import com.shuoxd.lib.LibApplication

/**
 * 用户隐私协议弹框
 */
class UserAgreementDialog : BaseDialogFragment(), View.OnClickListener {

    companion object {

        const val KEY_IS_AGREE_AGREEMENT = "key_is_agree_agreement"

        fun showDialog(fm: FragmentManager) {
            val dialog = UserAgreementDialog()
            dialog.show(fm, UserAgreementDialog::class.java.name)
        }
    }

    private lateinit var binding: DialogUserAgreementBinding
    private var isShowTipView = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUserAgreementBinding.inflate(inflater, container, false)
        //点击返回键dialog不消失
        requireDialog().setCancelable(false)
        //点击外面dialog不消失
        requireDialog().setCanceledOnTouchOutside(false)
        initView()
        updateView()
        return binding.root
    }

    private fun updateView() {
        if (isShowTipView) {
            binding.title.text = getString(R.string.m22_privacy_dialog_tip)
            binding.llContent.gone()
            binding.disagreeOrConfirm.text = getString(R.string.m18_confirm)
            binding.agreeOrCancel.text = getString(R.string.m16_cancel)
        } else {
            binding.title.text = getString(R.string.m21_user_agreement_and_privacy_policy)
            binding.llContent.visible()
            binding.disagreeOrConfirm.text = getString(R.string.m19_disagree)
            binding.agreeOrCancel.text = getString(R.string.m20_agree)
        }
    }

    private fun initView() {
        binding.tvSpan.run {
            highlightColor = resources.getColor(android.R.color.transparent)
            movementMethod = LinkMovementMethod.getInstance()
            text = getTvSpan()
        }
        binding.disagreeOrConfirm.setOnClickListener(this)
        binding.agreeOrCancel.setOnClickListener(this)
    }

    private fun getTvSpan(): SpannableString {
        val userAgreement = getString(R.string.m23_user_agreement)
        val privacyPolicy = getString(R.string.m24_privacy_policy)
        val content = getString(R.string.m25_click_agree, userAgreement, privacyPolicy)
        return SpannableString(content).apply {
            addColorSpan(this, userAgreement)
            addClickSpan(this, userAgreement) {
                WebActivity.start(requireActivity(), AppUtil.getUserAgreement())
            }
            addColorSpan(this, privacyPolicy)
            addClickSpan(this, privacyPolicy) {
                WebActivity.start(requireActivity(), AppUtil.getPrivacy())
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.disagree_or_confirm -> {
                if (isShowTipView) {
                    dismissAllowingStateLoss()
                    UserAgreementMonitor.notify(false)
                } else {
                    isShowTipView = true
                    updateView()
                }

            }
            R.id.agree_or_cancel -> {
                if (isShowTipView) {
                    isShowTipView = false
                    updateView()
                } else {
                    dismissAllowingStateLoss()
                    UserAgreementMonitor.notify(true)
                    MainApplication.instance().storageService().put(KEY_IS_AGREE_AGREEMENT, true)
                }
            }
        }
    }
}