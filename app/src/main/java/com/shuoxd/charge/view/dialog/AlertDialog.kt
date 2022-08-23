package com.shuoxd.charge.view.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.growatt.lib.util.gone
import com.growatt.lib.util.visible
import com.shuoxd.charge.base.BaseDialogFragment
import com.shuoxd.charge.databinding.DialogAlertBinding

/**
 * 通用Dialog
 */
class AlertDialog : BaseDialogFragment(), View.OnClickListener {

    companion object {

        fun showDialog(
            fm: FragmentManager,
            content: String?,
            grayButtonText: String? = null,
            redButtonText: String? = null,
            title: String? = null,
            onRedButtonClick: (() -> Unit)? = null,
            onGrayButtonClick: (() -> Unit)?

        ) {
            val dialog = AlertDialog()
            dialog.content = content
            dialog.title = title
            dialog.grayButtonText = grayButtonText
            dialog.redButtonText = redButtonText
            dialog.onGrayButtonClick = onGrayButtonClick
            dialog.onRedButtonClick = onRedButtonClick
            dialog.show(fm, AlertDialog::class.java.name)
        }
    }

    private lateinit var binding: DialogAlertBinding
    private var content: String? = null
    private var grayButtonText: String? = null
    private var redButtonText: String? = null
    private var onGrayButtonClick: (() -> Unit)? = null
    private var onRedButtonClick: (() -> Unit)? = null
    private var title: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogAlertBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        binding.btGray.setOnClickListener(this)
        binding.btRed.setOnClickListener(this)
        if (TextUtils.isEmpty(title)) {
            binding.tvTitle.text = content
            binding.tvContent.gone()
        } else {
            binding.tvTitle.text = title
            binding.tvContent.visible()
            binding.tvContent.text = content
        }
        if (!grayButtonText.isNullOrEmpty()) {
            binding.btGray.text = grayButtonText
        }
        if (!redButtonText.isNullOrEmpty()) {
            binding.btRed.text = redButtonText
        }
    }

    override fun onClick(v: View?) {
        when {
            v === binding.btGray -> {
                dismissAllowingStateLoss()
                onGrayButtonClick?.invoke()
            }
            v === binding.btRed -> {
                dismissAllowingStateLoss()
                onRedButtonClick?.invoke()
            }
        }
    }
}