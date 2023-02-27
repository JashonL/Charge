package com.shuoxd.charge.ui.chargesetting.adapter;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseMultiItemQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.shuoxd.charge.R;
import com.shuoxd.charge.ui.chargesetting.bean.BleSetBean;
import com.shuoxd.charge.ui.chargesetting.settype.OneCheckItem;
import com.shuoxd.charge.ui.chargesetting.settype.OneInputItem;
import com.shuoxd.charge.ui.chargesetting.settype.OneSelectItem;
import com.shuoxd.charge.view.SettingChooseView;
import com.shuoxd.charge.view.SettingInputView;


import java.util.List;

public class ItemSettingAdapter extends BaseMultiItemQuickAdapter<BleSetBean, BaseViewHolder> {


    private OnchooseListener onchooseListener;
    private OnInputLisener onInputLisener;
    private OnCheckListener onCheckListener;


    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param data A new list is created out of this one to avoid mutable list
     */
    public ItemSettingAdapter(List<BleSetBean> data, OnchooseListener onchooseListener, OnInputLisener inputLisener, OnCheckListener checkListener) {
        super(data);
        addItemType(BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT, R.layout.item_setting_input_view);
        addItemType(BleSetBean.ItemType.ONE_SELECT_ITEM_CHOOSE, R.layout.item_setting_oneselect);
        addItemType(BleSetBean.ItemType.ONE_SELECT_ITEM_CHECK, R.layout.item_setting_check);
        this.onchooseListener = onchooseListener;
        this.onInputLisener = inputLisener;
        this.onCheckListener = checkListener;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BleSetBean item) {


        switch (item.itemType) {
            case BleSetBean.ItemType.ONE_SELECT_ITEM_CHOOSE:
                SettingChooseView chooseView = helper.getView(R.id.view_choose_setting);
                String[] items = ((OneSelectItem) item).getSelectItemTitles();
                chooseView.setmItems(items);
                chooseView.setTitle(item.title);
                item.isCheck = true;
                chooseView.setOnitemChooseLiseners(index -> {
                    ((OneSelectItem) item).oneChooseValue = ((OneSelectItem) item).selectItems.get(index).value;
                    ((OneSelectItem) item).dataValue  = ((OneSelectItem) item).selectItems.get(index).title;
                    onchooseListener.choose(item);
                });

                //选中项初始化
                chooseView.setValue(((OneSelectItem) item).dataValue);


                break;
            case BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT:
                SettingInputView inputView = helper.getView(R.id.view_input_setting);
                OneInputItem item1 = (OneInputItem) item;
                inputView.setTitle(item1.title);
                inputView.setEnable(item1.enable);
                inputView.setOnitemChooseLiseners(value -> {
                    ((OneInputItem) item).value = value;
                    onInputLisener.input(item);
                });
                //初始化
                inputView.setValue(item1.value);
                break;

            case BleSetBean.ItemType.ONE_SELECT_ITEM_CHECK:
                OneCheckItem checkItem = (OneCheckItem) item;
                TextView tvTitle = helper.getView(R.id.tv_item_name);
                CheckBox cb = helper.getView(R.id.iv_check);
                tvTitle.setText(checkItem.title);
                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (cb.isPressed()){
                        ((OneCheckItem) item).isCheck = isChecked;
                        onCheckListener.check(item);
                    }
                });

                //初始化
                cb.setChecked(checkItem.isCheck);
                break;

        }

    }


    public interface OnchooseListener {
        void choose(BleSetBean item);
    }


    public interface OnInputLisener {
        void input(BleSetBean item);
    }

    public interface OnCheckListener {
        void check(BleSetBean item);
    }


}
