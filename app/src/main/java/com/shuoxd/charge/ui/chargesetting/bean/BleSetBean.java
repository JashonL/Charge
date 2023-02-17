package com.shuoxd.charge.ui.chargesetting.bean;

import android.view.View;

import com.chad.library.adapter.base.entity.MultiItemEntity;

public class BleSetBean implements MultiItemEntity {

    public int itemType;
    public String title;
    public String key;
    public boolean isCheck;


    public static class ItemType {
        public static final int ONE_SELECT_ITEM_NEXT = 1;
        public static final int ONE_SELECT_ITEM_CHOOSE = 2;
        public static final int ONE_SELECT_ITEM_CHECK = 3;
    }


    public static class ItemKey {
        public static final String KEY_WIFI_SSID = "KEY_WIFI_SSID";
        public static final String KEY_WIFI_PASSWORD = "KEY_WIFI_PASSWORD";
        public static final String KEY_4G_APN = "KEY_4G_APN";
        public static final String KEY_4G_ACCOUNT = "KEY_4G_ACCOUNT";
        public static final String KEY_4G_PASSWORD = "KEY_4G_PASSWORD";
        public static final String KEY_SERVER_URL = "KEY_SERVER_URL";
        public static final String KEY_CP_NAME = "KEY_CP_NAME";
        public static final String KEY_AUTH_KEY = "KEY_AUTH_KEY";
        public static final String KEY_OUT_PUT_CURRENT = "KEY_OUT_PUT_CURRENT";
        public static final String KEY_CHARGE_MODE = "KEY_CHARGE_MODE";
        public static final String KEY_POWER_DISTRIBUTION_ENABLE = "KEY_POWER_DISTRIBUTION_ENABLE";
        public static final String KEY_SAMPLING_METHOD= "KEY_SAMPLING_METHOD";
        public static final String KEY_HOME_POWER_CURRENT = "KEY_HOME_POWER_CURRENT";
        public static final String KEY_POWER_METER_ADDR = "KEY_POWER_METER_ADDR";

    }


    @Override
    public int getItemType() {
        return itemType;
    }
}
