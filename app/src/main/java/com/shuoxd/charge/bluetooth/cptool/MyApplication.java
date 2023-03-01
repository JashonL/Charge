package com.shuoxd.charge.bluetooth.cptool;

import android.app.Application;

import com.timxon.cplib.BleCPClient;
import com.timxon.cplib.CPInitializer;


public class MyApplication extends Application {

    private static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }

    private BleCPClient bleCPClient;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CPInitializer.init();
    }

    public synchronized BleCPClient getBleCPClient() {
        if (bleCPClient == null) {
            bleCPClient = new BleCPClient(this);
        }
        return bleCPClient;
    }
}
