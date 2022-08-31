package com.shuoxd.charge.view.dialog

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import com.shuoxd.charge.base.BaseDialogFragment
import com.shuoxd.charge.databinding.DialogInputBinding
import com.shuoxd.lib.util.gone

class InputDialog :BaseDialogFragment(),View.OnClickListener{


    companion object {

        fun showDialog(
            fm: FragmentManager,
            content: String?,
            grayButtonText: String? = null,
            redButtonText: String? = null,
            title: String? = null,
            onRedButtonClick: (() -> Unit)? = null,
            onGrayButtonClick: ((content:String) -> Unit)?

        ) {
            val dialog = InputDialog()
            dialog.content = content
            dialog.title = title
            dialog.grayButtonText = grayButtonText
            dialog.redButtonText = redButtonText
            dialog.onGrayButtonClick = onGrayButtonClick
            dialog.onRedButtonClick = onRedButtonClick
            dialog.show(fm, AlertDialog::class.java.name)
        }
    }


    private lateinit var binding: DialogInputBinding
    private var content: String? = null
    private var subName: String? = null
    private var grayButtonText: String? = null
    private var redButtonText: String? = null
    private var onGrayButtonClick: ((content:String) -> Unit)? = null
    private var onRedButtonClick: (() -> Unit)? = null
    private var title: String? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogInputBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }



    private fun initView() {
        binding.btGray.setOnClickListener(this)
        binding.btRed.setOnClickListener(this)
        if (TextUtils.isEmpty(title)) {
            binding.tvTitle.gone()
        } else {
            binding.tvTitle.text = title
        }

        if (!TextUtils.isEmpty(subName)) {
            binding.tvSubname.text = subName
        }else{
            binding.tvSubname.gone()
        }

        if (!TextUtils.isEmpty(content)) {
            binding.etContent.setText(content)
        }


        if (!grayButtonText.isNullOrEmpty()) {
            binding.btGray.text = grayButtonText
        }
        if (!redButtonText.isNullOrEmpty()) {
            binding.btRed.text = redButtonText
        }
    }

    override fun onClick(p0: View?) {
        when {
            p0 === binding.btGray -> {
                dismissAllowingStateLoss()
                val value = binding.etContent.text.toString()
                onGrayButtonClick?.invoke(value)
            }
            p0 === binding.btRed -> {
                dismissAllowingStateLoss()
                onRedButtonClick?.invoke()
            }
        }
    }


}