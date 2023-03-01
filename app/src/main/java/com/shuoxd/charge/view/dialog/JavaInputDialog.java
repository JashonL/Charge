package com.shuoxd.charge.view.dialog;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.shuoxd.charge.R;
import com.shuoxd.charge.base.BaseDialogFragment;
import com.shuoxd.charge.databinding.DialogInputBinding;


public class JavaInputDialog extends BaseDialogFragment implements View.OnClickListener {

    public static void showDialog(FragmentManager fm, String title,String subName,
                                  String content, String btnLeft, String btnRight, OnButtonClickListener listener) {
        JavaInputDialog javaInputDialog = new JavaInputDialog();
        javaInputDialog.content = content;
        javaInputDialog.title = title;
        javaInputDialog.subName=subName;
        javaInputDialog.grayButtonText = btnLeft;
        javaInputDialog.redButtonText = btnRight;
        javaInputDialog.onButtonClickListener = listener;
        javaInputDialog.show(fm, JavaInputDialog.class.getSimpleName());

    }


    private DialogInputBinding binding;
    private String content;
    private String subName;
    private String grayButtonText;
    private String redButtonText;

    private OnButtonClickListener onButtonClickListener;

    private String title;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogInputBinding.inflate(inflater, container, false);
        initView();
        return binding.getRoot();
    }


    private void initView() {
        binding.btGray.setOnClickListener(this);
        binding.btRed.setOnClickListener(this);
        if (!TextUtils.isEmpty(title)) {
            binding.tvTitle.setText(title);
        }

        if (!TextUtils.isEmpty(subName)) {
            binding.tvSubname.setText(subName);
        } else {
            binding.tvSubname.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(content)) {
            binding.etContent.setText(content);
        }


        if (!TextUtils.isEmpty(grayButtonText)) {
            binding.btGray.setText(grayButtonText);
        }
        if (!TextUtils.isEmpty(redButtonText)) {
            binding.btRed.setText(redButtonText);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_gray:
                dismissAllowingStateLoss();
                if (onButtonClickListener != null) {
                    onButtonClickListener.onCancel();
                }
                break;
            case R.id.bt_red:
                dismissAllowingStateLoss();
                String value = binding.etContent.getText().toString();

                if (onButtonClickListener != null) {
                    onButtonClickListener.onComfir(value);
                }
                break;
        }
    }


   public interface OnButtonClickListener {
        void onCancel();

        void onComfir(String value);
    }


}
