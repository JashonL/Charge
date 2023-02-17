package com.shuoxd.charge.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.mylhyl.circledialog.CircleDialog;
import com.shuoxd.charge.R;

public class SettingChooseView extends LinearLayout implements CompoundButton.OnCheckedChangeListener{

    private TextView tvItemTitle;
    private LinearLayout llChooseContent;
    private TextView tvValue;



    private String title = "";


    //待优化  封装选项
    private String[] mItems;

    private String value;
    private int value_index = -1;

    private String unit;
    private String hint = "";
    private String key;

    private OnitemChooseLiseners onitemChooseLiseners;

    public SettingChooseView(Context context) {
        this(context, null);
    }

    public SettingChooseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SettingChooseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        bindView(context);
        initAtrr(context, attrs);
        initViews();
    }


    private void bindView(Context context) {
        View view = inflate(context, R.layout.item_ble_set, this);
        llChooseContent = view.findViewById(R.id.ll_select);
        tvItemTitle = view.findViewById(R.id.tv_item_name);
        tvValue = view.findViewById(R.id.tv_item_value);
        llChooseContent.setOnClickListener(v -> showSelect());

    }


    private void initAtrr(Context context, AttributeSet attrs) {
 /*       //获取属性值
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DeviceSetView);

        typedArray.recycle();*/
    }

    private void initViews() {
        if (!TextUtils.isEmpty(hint)) {
            tvValue.setHint(hint);
        }
    }








    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title)) {
            this.title = title;
            tvItemTitle.setText(title);
        }
    }


    public void setmItems(String[] mItems) {
        this.mItems = mItems;
    }

    private void showSelect() {
        if (mItems == null) return;
        if (TextUtils.isEmpty(title))title=getContext().getString(R.string.m136_choose);
        FragmentActivity activity = (FragmentActivity) getContext();
        new CircleDialog.Builder()
                .configDialog(params -> {
                    //增加弹出动画
                    params.animStyle = R.style.MyDialogStyle;
                })
                .setTitle(title)
                .setGravity(Gravity.CENTER)
                .setWidth(0.7f)
                .setItems(mItems, (parent, view, position, id) -> {
                    value_index = position;
                    value = mItems[position];
                    if (onitemChooseLiseners!=null){
                        onitemChooseLiseners.onItemchoose(position);
                    }
                    return true;
                })
                .setNegative(getContext().getString(R.string.m16_cancel), null)
                .show(activity.getSupportFragmentManager());
    }


    //根据值设置选中项
    public void setValue(String value) {
        this.value = value;
        if (!TextUtils.isEmpty(value)){
            tvValue.setText(value);
        }
    }

    public String getValue() {
        return value;
    }

    public int getValue_index() {
        return value_index;
    }

    public void setValue_index(int value_index) {
        this.value_index = value_index;
        if (value_index < mItems.length && value_index >= 0){
            value = mItems[value_index];
            tvValue.setText(value);
        }

    }

    public String getUnit() {
        return unit;
    }



    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }





    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }




    public OnitemChooseLiseners getOnitemChooseLiseners() {
        return onitemChooseLiseners;
    }

    public void setOnitemChooseLiseners(OnitemChooseLiseners onitemChooseLiseners) {
        this.onitemChooseLiseners = onitemChooseLiseners;
    }

    public interface OnitemChooseLiseners{
        void onItemchoose(int index);
    }

}
