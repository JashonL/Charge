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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.shuoxd.charge.R;
import com.shuoxd.charge.application.MainApplication;
import com.shuoxd.charge.databinding.ActivityBluetoothMainBinding;
import com.timxon.cplib.BleCPClient;
import com.timxon.cplib.ConnectCallback;
import com.timxon.cplib.protocol.CPClient;
import com.timxon.cplib.protocol.CPUtils;
import com.timxon.cplib.protocol.Response;
import com.timxon.cplib.protocol.VerifyPasswordRequest;
import com.timxon.cplib.protocol.VerifyPasswordResponse;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;


import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;



public class BleSetActivity extends AppCompatActivity {


    public static void start(Context context){
        Intent intent=new Intent(context,BleSetActivity.class);
        context.startActivity(intent);
    }


    private static final int REQUEST_CODE_BLUETOOTH_ENABLE = 1;
    private static final int REQUEST_CODE_PERMISSION = 2;
    private static final String TAG = "MainActivity";

    private List<BluetoothDevice> devices = new ArrayList<>();

    private ProgressDialog progressDialog;
    private CommonAdapter<BluetoothDevice> adapter;

    private BluetoothAdapter bluetoothAdapter;
    private boolean mScanning;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private BleCPClient cpClient;

    private ActivityBluetoothMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBluetoothMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cpClient = MainApplication.Companion.instance().getBleCPClient();
        initView();
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
            ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), REQUEST_CODE_PERMISSION);
        }
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void initView() {
        binding.refreshLayout.setOnRefreshListener(() -> {
            if (!mScanning) {
                devices.clear();
                adapter.notifyDataSetChanged();
                checkPermissionAndSearchDevices();
            } else {
                binding.refreshLayout.setRefreshing(false);
            }
        });
        binding.rvBluetoothList.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommonAdapter<BluetoothDevice>(this, R.layout.item_device_list, devices) {
            @SuppressLint("MissingPermission")
            @Override
            protected void convert(ViewHolder holder, final BluetoothDevice device, int position) {
                holder.setText(R.id.name, device.getName());
                holder.setText(R.id.mac, device.getAddress());
                holder.setVisible(R.id.tvDisconnect, device.equals(cpClient.getConnectedDevice())
                        && cpClient.isConnected());
                holder.setOnClickListener(R.id.tvDisconnect, v -> {
                    sendExitCmd();
                });
                holder.itemView.setOnClickListener(v -> {
                    if (checkBluetoothState()) return;
                    connect(device);
                });
            }
        };
        binding.rvBluetoothList.setAdapter(adapter);

        binding.llBottomButtons.setVisibility(View.GONE);

        binding.tvVersion.setVisibility(View.VISIBLE);
        binding.tvVersion.setText(String.format("version: %s", MyUtils.getVersionName(this)));

    }

    public void openWebPage(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            MyUtils.showToast("The System has no browser");
            Logger.e(TAG, "openWebPage error:", e);
        }
    }

    @SuppressLint("MissingPermission")
    private void connect(final BluetoothDevice device) {
        cpClient.setDeviceName(device.getName());
        boolean started = cpClient.connect(device, new ConnectCallback() {
            @Override
            public void onSuccess() {
                dismissProgress();
                MyUtils.showToast("Connected to charger successfully");
                adapter.notifyDataSetChanged();
                showVerifyPasswordDialog();
            }

            @Override
            public void onError(Throwable e) {
                dismissProgress();
                Log.e(TAG, "onError: ", e);
                MyUtils.showToast(e.getMessage());
            }
        });
        if (started) {
            adapter.notifyDataSetChanged();
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


    private void showVerifyPasswordDialog() {
        View view = View.inflate(this, R.layout.dialog_verify_password, null);
        final AlertDialog dialog = new AlertDialog.Builder(this)
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
                    MyUtils.showToast("wrong password");
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
        Intent intent = new Intent(this, ConfigActivity.class);
        intent.putExtra("pwd", pwd);
        intent.putExtra("DeviceInfo", cpClient.getDeviceInfo());
        startActivityForResult(intent, 100);
    }

    private void startScanDevice() {
        scanBleDevice(true);
    }

    private void stopScanDevice() {
        scanBleDevice(false);
    }

    @SuppressLint("MissingPermission")
    private void scanBleDevice(final boolean enable) {
        if (enable) {
            handler.postDelayed(this::stopScanDevice, SCAN_PERIOD);
            mScanning = true;
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.startScan(scanCallback);
            binding.refreshLayout.setRefreshing(true);
        } else {
            mScanning = false;
            BluetoothLeScanner bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            bluetoothLeScanner.stopScan(scanCallback);
            binding.refreshLayout.setRefreshing(false);
        }
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @SuppressLint("MissingPermission")
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            BluetoothDevice device = result.getDevice();
            if (!devices.contains(device) && !TextUtils.isEmpty(device.getName())) {
                devices.add(device);
                adapter.notifyDataSetChanged();
            }
        }
    };

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

    private void showProgress(String strMsg) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        dismissProgress();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(strMsg);
        progressDialog.show();
    }

    private void dismissProgress() {
        if (isFinishing() || isDestroyed()) {
            return;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_BLUETOOTH_ENABLE && resultCode == Activity.RESULT_OK) {
            searchDevice();
        } else if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            sendExitCmd();
            if (data != null && data.getBooleanExtra("chargerIdChanged", false)) {
                devices.clear();
                adapter.notifyDataSetChanged();
                searchDevice();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mScanning) {
            stopScanDevice();
        }
        if (cpClient.isConnected()) {
            sendExitCmd();
        } else {
            cpClient.close();
        }
    }

    private void sendExitCmd() {
        showProgress("Disconnecting");
        cpClient.sendExitCommand(new CPClient.Callback() {
            @Override
            public void onResponse(Response response) {
                Logger.d(TAG, "exit");
                adapter.notifyDataSetChanged();
                dismissProgress();
            }



            @Override
            public void onError(Throwable error) {
                Logger.e(TAG, "exit", error);
                MyUtils.showToast(error.getMessage());
                adapter.notifyDataSetChanged();
                dismissProgress();
            }
        });
    }
}
