package com.shuoxd.charge.bluetooth.cptool;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.shuoxd.charge.R;
import com.shuoxd.charge.application.MainApplication;
import com.shuoxd.charge.base.BaseFragment;
import com.shuoxd.charge.ui.chargesetting.activity.BleSetParamsActivity;
import com.shuoxd.charge.ui.chargesetting.activity.ChargeSettingActivity;
import com.shuoxd.charge.ui.common.fragment.RequestPermissionHub;
import com.shuoxd.charge.view.dialog.BottomDialog;
import com.timxon.cplib.BleCPClient;
import com.timxon.cplib.ConnectCallback;
import com.timxon.cplib.protocol.CPClient;
import com.timxon.cplib.protocol.CPUtils;
import com.timxon.cplib.protocol.Response;
import com.timxon.cplib.protocol.VerifyPasswordRequest;
import com.timxon.cplib.protocol.VerifyPasswordResponse;

import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class BleConnetFragment extends BaseFragment {
    private static final String TAG = "BleConnetFragment";

    private static final int REQUEST_CODE_BLUETOOTH_ENABLE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;


    private List<BluetoothDevice> devices = new ArrayList<>();

    private ProgressDialog progressDialog;

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private BleCPClient cpClient;


    //充电桩序列号
    private String chargeSn;


    public BleConnetFragment(String chargeSn) {
        this.chargeSn = chargeSn;
    }


    public static void startBleCon(FragmentActivity activity, String chargeSn) {
        FragmentManager supportFragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        BleConnetFragment bleConnetFragment = new BleConnetFragment(chargeSn);
        fragmentTransaction.add(bleConnetFragment, BleConnetFragment.class.getSimpleName());
        fragmentTransaction.commitAllowingStateLoss();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        cpClient = MainApplication.Companion.instance().getBleCPClient();
        checkPermissionAndSearchDevices();
    }


    private void checkPermissionAndSearchDevices() {
        if (hasPermission(Manifest.permission_group.LOCATION)
                && (Build.VERSION.SDK_INT < Build.VERSION_CODES.S
                || (hasPermission(Manifest.permission.BLUETOOTH_SCAN)
                && hasPermission(Manifest.permission.BLUETOOTH_CONNECT)))) {
            searchDevice();
        } else {
            List<String> permissions = new ArrayList<>();
            permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                permissions.add(Manifest.permission.BLUETOOTH_SCAN);
                permissions.add(Manifest.permission.BLUETOOTH_CONNECT);
            }
            requestPermissions(permissions.toArray(new String[0]), REQUEST_CODE_PERMISSION);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }


    private void searchDevice() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            MyUtils.showToast("This device do not support bluetooth");
            return;
        }
        if (checkBluetoothState()) return;
        startScanDevice();
    }


    @SuppressLint("MissingPermission")
    private void requestEnableBluetooth() {
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent, REQUEST_CODE_BLUETOOTH_ENABLE);
    }


    @SuppressLint("MissingPermission")
    private void scanBleDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(this::stopScanDevice, SCAN_PERIOD);
            mScanning = true;
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(scanCallback);
            showDialog();
        } else {
            mScanning = false;
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.stopScan(scanCallback);
            dismissDialog();
        }
    }


    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!devices.contains(device) && !TextUtils.isEmpty(device.getName())) {
                Log.e(TAG, "充电桩名称："+chargeSn+"搜索中: " + device.getName());
                if (device.getName().equals(chargeSn)) {
                    devices.add(device);
                    stopScanDevice();
                    //去连接
                    connect(device);
                }
            }
        }
    };


    @Override
    public void showResultDialog(@Nullable String result, @Nullable Function0<Unit> onCancelClick, @Nullable Function0<Unit> onComfirClick) {

    }


    @SuppressLint("MissingPermission")
    private void connect(final BluetoothDevice device) {
        cpClient.setDeviceName(device.getName());
        boolean started = cpClient.connect(device, new ConnectCallback() {
            @Override
            public void onSuccess() {
                dismissProgress();
                MyUtils.showToast("Connected to charger successfully");
                setDefaultPwd();
            }

            @Override
            public void onError(Throwable e) {
                dismissProgress();
                Log.e(TAG, "onError: ", e);
                MyUtils.showToast(e.getMessage());
            }
        });
        if (started) {
            showProgress("Connecting");
        }
    }


    private boolean checkBluetoothState() {
        if (!bluetoothAdapter.isEnabled()) {
            requestEnableBluetooth();
            return true;
        }
        return false;
    }




    private void setDefaultPwd(){
        String randomHexString = CPUtils.genRandomHexString(4);
        verifyPassword("12345678", randomHexString);
    }



    private void showVerifyPasswordDialog() {
        View view = View.inflate(getActivity(), R.layout.dialog_verify_password, null);
        final AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(view)
                .show();
        dialog.setCanceledOnTouchOutside(false);
        final EditText etPwd = view.findViewById(R.id.etPwd);
        final TextView tvMagicNo = view.findViewById(R.id.tvMagicNo);
        final TextView tvConfirm = view.findViewById(R.id.tvConfirm);
        final TextView tvCancel = view.findViewById(R.id.tvCancel);
        MyUtils.addAsciiFilter(etPwd);
        String randomHexString = CPUtils.genRandomHexString(4);
        tvMagicNo.setText(randomHexString);
        tvConfirm.setOnClickListener(v -> {
            String pwd = etPwd.getText().toString().trim();
            if (TextUtils.isEmpty(pwd)) {
                return;
            }
            dialog.dismiss();
            String magicNo = tvMagicNo.getText().toString();
            verifyPassword(pwd, magicNo);
        });
        tvCancel.setOnClickListener(v -> dialog.cancel());


    }


    private void verifyPassword(String pwd, String magicNo) {
        showProgress("Verifying");
        VerifyPasswordRequest request = new VerifyPasswordRequest(pwd, magicNo);
        cpClient.enqueue(request, new SimpleCPCallback<VerifyPasswordResponse>(VerifyPasswordResponse.class) {
            @Override
            public void onResponse2(VerifyPasswordResponse response) {
                dismissProgress();
                if (response.isSuccessful()) {
                    gotoConfig(pwd);
                } else {
                    showVerifyPasswordDialog();
                }
            }

            @Override
            public void onError(Throwable error) {
                dismissProgress();
                Logger.e(TAG, "onError: ", error);
                MyUtils.showToast(error.getMessage());
            }
        });
    }


    private void gotoConfig(String pwd) {
        BleSetParamsActivity.start(getActivity(),pwd,cpClient.getDeviceInfo());

/*        Intent intent = new Intent(getActivity(), BleConfigActivity.class);
        intent.putExtra("pwd", pwd);
        intent.putExtra("DeviceInfo", cpClient.getDeviceInfo());
        startActivityForResult(intent, 100);*/
    }


    private void startScanDevice() {
        scanBleDevice(true);
    }

    private void stopScanDevice() {
        if (devices==null||devices.size()==0){
            ((ChargeSettingActivity) getActivity()).showNoBleDialog();
            cpClient.close();
            detach();
        }
        scanBleDevice(false);
    }


    private void showProgress(String strMsg) {
   /*     if (isFinishing() || isDestroyed()) {
            return;
        }*/
        dismissProgress();
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(strMsg);
        progressDialog.show();
    }

    private void dismissProgress() {
  /*      if (isFinishing() || isDestroyed()) {
            return;
        }*/
        try {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
                progressDialog = null;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0) {
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                searchDevice();
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLUETOOTH_ENABLE && resultCode == Activity.RESULT_OK) {
            searchDevice();
        } else if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            sendExitCmd();
            if (data != null && data.getBooleanExtra("chargerIdChanged", false)) {
                devices.clear();
                searchDevice();
            }
        }

    }


    private void sendExitCmd() {
        showProgress("Disconnecting");
        cpClient.sendExitCommand(new CPClient.Callback() {
            @Override
            public void onResponse(Response response) {
                Logger.d(TAG, "exit");
                dismissProgress();
            }


            @Override
            public void onError(Throwable error) {
                Logger.e(TAG, "exit", error);
                MyUtils.showToast(error.getMessage());
                dismissProgress();
            }
        });
    }


    private void detach() {
        getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
    }


}
