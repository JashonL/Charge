package com.shuoxd.charge.bluetooth.cptool;


import com.timxon.cplib.protocol.Response;
import com.timxon.cplib.protocol.TypedCPCallback;


public abstract class SimpleCPCallback<T extends Response> extends TypedCPCallback<T> {


    public SimpleCPCallback(Class<T> tClass) {
        super(tClass);
    }

    @Override
    public void onError(Throwable error) {
        MyUtils.showToast(error.getMessage());
    }
}
