package com.shuoxd.charge.bluetooth.cptool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;


import com.shuoxd.charge.R;
import com.shuoxd.charge.application.MainApplication;
import com.shuoxd.charge.databinding.ActivityConfigBinding;
import com.timxon.cplib.BleCPClient;
import com.timxon.cplib.protocol.ChangePasswordRequest;
import com.timxon.cplib.protocol.ChangePasswordResponse;
import com.timxon.cplib.protocol.DeviceInfo;
import com.timxon.cplib.protocol.Get4GParametersRequest;
import com.timxon.cplib.protocol.Get4GParametersResponse;
import com.timxon.cplib.protocol.GetChargeModeRequest;
import com.timxon.cplib.protocol.GetChargeModeResponse;
import com.timxon.cplib.protocol.GetChargerStatusRequest;
import com.timxon.cplib.protocol.GetChargerStatusResponse;
import com.timxon.cplib.protocol.GetChargingInfoResponse;
import com.timxon.cplib.protocol.GetEthernetParametersRequest;
import com.timxon.cplib.protocol.GetEthernetParametersResponse;
import com.timxon.cplib.protocol.GetHomeLoadBalancingRequest;
import com.timxon.cplib.protocol.GetHomeLoadBalancingResponse;
import com.timxon.cplib.protocol.GetNetInterfaceSwitchRequest;
import com.timxon.cplib.protocol.GetNetInterfaceSwitchResponse;
import com.timxon.cplib.protocol.GetRatedCurrentRequest;
import com.timxon.cplib.protocol.GetRatedCurrentResponse;
import com.timxon.cplib.protocol.GetServerInfoRequest;
import com.timxon.cplib.protocol.GetServerInfoResponse;
import com.timxon.cplib.protocol.GetWifiInfoRequest;
import com.timxon.cplib.protocol.GetWifiInfoResponse;
import com.timxon.cplib.protocol.Set4GParametersRequest;
import com.timxon.cplib.protocol.Set4GParametersResponse;
import com.timxon.cplib.protocol.SetChargeModeRequest;
import com.timxon.cplib.protocol.SetChargeModeResponse;
import com.timxon.cplib.protocol.SetEthernetParametersRequest;
import com.timxon.cplib.protocol.SetEthernetParametersResponse;
import com.timxon.cplib.protocol.SetHomeLoadBalancingRequest;
import com.timxon.cplib.protocol.SetHomeLoadBalancingResponse;
import com.timxon.cplib.protocol.SetNetInterfaceSwitchRequest;
import com.timxon.cplib.protocol.SetNetInterfaceSwitchResponse;
import com.timxon.cplib.protocol.SetRatedCurrentRequest;
import com.timxon.cplib.protocol.SetRatedCurrentResponse;
import com.timxon.cplib.protocol.SetServerInfoRequest;
import com.timxon.cplib.protocol.SetServerInfoResponse;
import com.timxon.cplib.protocol.SetWifiInfoRequest;
import com.timxon.cplib.protocol.SetWifiInfoResponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;







public class BleConfigActivity extends AppCompatActivity implements View.OnClickListener {


    public static final String TAG = "ConfigActivity";

    private BleCPClient cpClient;
    private GetWifiInfoResponse getWifiInfoResponse;
    private GetServerInfoResponse getServerInfoResponse;
    private GetRatedCurrentResponse getRatedCurrentResponse;
    private Get4GParametersResponse get4GParametersResponse;
    private GetChargeModeResponse getChargeModeResponse;
    private GetHomeLoadBalancingResponse getHomeLoadBalancingResponse;
    private GetEthernetParametersResponse getEthernetParametersResponse;
    private GetNetInterfaceSwitchResponse getNetInterfaceSwitchResponse;




    private ProgressDialog progressDialog;

    private final AtomicInteger counter = new AtomicInteger();
    private final AtomicInteger setCounter = new AtomicInteger();
    private boolean chargerIdChanged;
    private String chargeMode = "";
    private String samplingMethod;
    private String serverUrl = "";

    private WifiManager wifiManager;
    private final BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            onScanResults();
        }
    };

    private ActivityConfigBinding binding;
    private String pwd;
    private boolean defaultPasswordPrompted;
    private DeviceInfo deviceInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfigBinding.inflate(getLayoutInflater());
        cpClient = MainApplication.Companion.instance().getBleCPClient();
        setContentView(binding.getRoot());
        initView();
        initData();
    }

    private void initView() {
        addInputFilter();

        binding.toolbar.setNavigationOnClickListener(v -> onBackPressed());

        binding.btnSet.setOnClickListener(this);
        binding.btnGet.setOnClickListener(this);
        binding.btnChangePwd.setOnClickListener(this);
        binding.btnUpdateFirmware.setOnClickListener(this);


        if (enableEthernetConfig()) {
            binding.layNetSwitchs.setVisibility(View.VISIBLE);
            binding.layEthernetParams.setVisibility(View.VISIBLE);
            initEthernetConfigListeners();
        }


        initSpChargeMode();

        initEtWifiSSID();

        binding.etWifiPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                checkDefaultPassword();
                defaultPasswordPrompted = true;
            }
        });

        initSpSamplingMethod();
    }

    private void initEthernetConfigListeners() {
        binding.swLAN.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.swWiFi.setChecked(false);
                binding.sw4G.setChecked(false);
            }
        });
        binding.sw4G.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.swWiFi.setChecked(false);
                binding.swLAN.setChecked(false);
            }
        });
        binding.swWiFi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                binding.swLAN.setChecked(false);
                binding.sw4G.setChecked(false);
            }
        });
        binding.swDHCP.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.etIpAddress.setEnabled(!isChecked);
            binding.etSubnetMask.setEnabled(!isChecked);
            binding.etDefaultGateway.setEnabled(!isChecked);
            binding.etDNS.setEnabled(!isChecked);
        });
    }

    private boolean enableEthernetConfig() {
        return supportEthernetConfig();
    }

    private void showDropDownDelayed(AutoCompleteTextView view) {
        view.postDelayed(view::showDropDown, 100);
    }

    private void initSpServerUrl() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.server_url_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spServerUrl.setAdapter(adapter);
        binding.spServerUrl.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] keys = getResources().getStringArray(R.array.server_url_keys);
                serverUrl = keys[position];
                String[] values = getResources().getStringArray(R.array.server_url_values);
                if ("Manual Entry".equals(values[position])) {
                    binding.etServerUrl.setVisibility(View.VISIBLE);
                } else {
                    binding.etServerUrl.setText("");
                    binding.etServerUrl.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                serverUrl = "";
            }
        });
    }

    private void initEtWifiSSID() {
        binding.etWifiSSID.setThreshold(1);
        binding.etWifiSSID.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.etWifiSSID.getText().toString().isEmpty()) {
                binding.etWifiSSID.showDropDown();
            }
        });
        binding.etWifiSSID.addTextChangedListener(new SimpleTextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().isEmpty()) {
                    showDropDownDelayed(binding.etWifiSSID);
                }
            }
        });
        binding.etWifiSSID.setOnClickListener(v -> {
            if (binding.etWifiSSID.getText().toString().isEmpty()) {
                showDropDownDelayed(binding.etWifiSSID);
            }
        });
    }

    private void initSpSamplingMethod() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sampling_method_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spSamplingMethod.setAdapter(adapter);
        binding.spSamplingMethod.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] keys = getResources().getStringArray(R.array.sampling_method_keys);
                samplingMethod = keys[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                samplingMethod = "";
            }
        });
    }

    private void initSpChargeMode() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.charge_mode_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spChargeMode.setAdapter(adapter);
        binding.spChargeMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String[] keys = getResources().getStringArray(R.array.charge_mode_keys);
                chargeMode = keys[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chargeMode = "";
            }
        });
    }

    private void initWiFiScan() {
        wifiManager = (WifiManager) getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);

        startWiFiScan();
    }

    private void startWiFiScan() {
        boolean success = wifiManager.startScan();
        if (!success) {
            onScanResults();
        }
    }

    private void onScanResults() {
        List<ScanResult> results = wifiManager.getScanResults();
        if (results == null || results.isEmpty()) {
            return;
        }
        List<String> ssidList = new ArrayList<>();
        for (ScanResult result : results) {
            Logger.d(TAG, String.format("ssid: %s, frequency: %s", result.SSID, result.frequency));
            if (result.frequency < 2500 && !ssidList.contains(result.SSID)) {
                // 只支持2.4G
                ssidList.add(result.SSID);
            }

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ssidList);
        binding.etWifiSSID.setAdapter(adapter);
    }


    private void addInputFilter() {
        MyUtils.addAsciiFilter(binding.etWifiSSID);
        MyUtils.addAsciiFilter(binding.etWifiPassword);
        MyUtils.addAsciiFilter(binding.et4gAPN);
        MyUtils.addAsciiFilter(binding.et4gAccount);
        MyUtils.addAsciiFilter(binding.et4gPassword);
        MyUtils.addAsciiFilter(binding.etServerUrl);
        MyUtils.addAsciiFilter(binding.etCpName);
        MyUtils.addAsciiFilter(binding.etAuthKey);
        MyUtils.addAsciiFilter(binding.etOutputCurrent);
        MyUtils.addAsciiFilter(binding.etEnterPwd);
        MyUtils.addAsciiFilter(binding.etReenterPwd);
        MyUtils.addAsciiFilter(binding.etIpAddress);
        MyUtils.addAsciiFilter(binding.etSubnetMask);
        MyUtils.addAsciiFilter(binding.etDefaultGateway);
        MyUtils.addAsciiFilter(binding.etDNS);
    }

    @SuppressLint("MissingPermission")
    private void initData() {
        BluetoothDevice connectedDevice = cpClient.getConnectedDevice();
        if (connectedDevice != null) {
            binding.tvBluetoothName.setText(connectedDevice.getName());
        }
        pwd = getIntent().getStringExtra("pwd");
//        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("DeviceInfo");
        checkDefaultPassword();
        initWiFiScan();
        getChargerConfigInfo();
        getNetStatus();
        if (deviceInfo != null) {
            binding.layCurrentVersion.setVisibility(View.VISIBLE);
            binding.tvChargerVersion.setText(deviceInfo.version);
        }
    }

    private void getNetStatus() {
        GetChargerStatusRequest request = new GetChargerStatusRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetChargerStatusResponse>(GetChargerStatusResponse.class) {
            @Override
            public void onResponse2(GetChargerStatusResponse response) {
                String netStatus = response.getNetStatus();
                binding.tvNetStatus.setVisibility(View.VISIBLE);
                if (Arrays.asList("D", "M", "N", "O", "P").contains(netStatus)) {
                    binding.tvNetStatus.setText("WiFi connection OK");
                } else {
                    binding.tvNetStatus.setText("WiFi not connected");
                }
            }

            @Override
            public void onError(Throwable error) {
                Logger.e(TAG, "getNetStatus error:", error);
            }
        });
    }

    private void checkDefaultPassword() {
        if (!"12345678".equals(pwd) || defaultPasswordPrompted) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("The current password is the default password. Do you want to change The password now?")
                .setPositiveButton("Ok", (dialog, which) -> binding.etEnterPwd.requestFocus())
                .setNegativeButton("cancel", null)
                .show();
    }

    private SharedPreferences getPreferences() {
        return getSharedPreferences("preferences", MODE_PRIVATE);
    }

    private void getChargerConfigInfo() {
        counter.set(0);
        getWifiInfo();
        get4GParameters();
        getServerInfo();
        getRatedCurrent();
        getChargeMode();
        getHomeLoadBalancing();
        if (enableEthernetConfig()) {
            getNetInterfaceSwitch();
            getEthernetParameters();
            counter.set(8);
        } else {
            counter.set(6);
        }
        showProgress("Fetching");
    }

    private boolean supportEthernetConfig() {
        String protocolVersion = cpClient.getProtocolVersion();
        return !TextUtils.isEmpty(protocolVersion) && protocolVersion.compareToIgnoreCase("1") >= 0;
    }

    private void getEthernetParameters() {
        GetEthernetParametersRequest request = new GetEthernetParametersRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetEthernetParametersResponse>(GetEthernetParametersResponse.class) {
            @Override
            public void onResponse2(GetEthernetParametersResponse response) {
                getEthernetParametersResponse = response;
                binding.etIpAddress.setText(getEthernetParametersResponse.getIp());
                binding.etSubnetMask.setText(getEthernetParametersResponse.getSubnetMask());
                binding.etDefaultGateway.setText(getEthernetParametersResponse.getGateway());
                binding.etDNS.setText(getEthernetParametersResponse.getDns());
                binding.swDHCP.setChecked(getEthernetParametersResponse.isEnableDHCP());
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private void getNetInterfaceSwitch() {
        GetNetInterfaceSwitchRequest request = new GetNetInterfaceSwitchRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetNetInterfaceSwitchResponse>(GetNetInterfaceSwitchResponse.class) {
            @Override
            public void onResponse2(GetNetInterfaceSwitchResponse response) {
                getNetInterfaceSwitchResponse = response;
                binding.sw4G.setChecked(getNetInterfaceSwitchResponse.is4GEnabled());
                binding.swWiFi.setChecked(getNetInterfaceSwitchResponse.isWiFiEnabled());
                binding.swLAN.setChecked(getNetInterfaceSwitchResponse.isLANEnabled());
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private void getHomeLoadBalancing() {
        GetHomeLoadBalancingRequest request = new GetHomeLoadBalancingRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetHomeLoadBalancingResponse>(GetHomeLoadBalancingResponse.class) {
            @Override
            public void onResponse2(GetHomeLoadBalancingResponse response) {
                getHomeLoadBalancingResponse = response;
                binding.swPowerDistributionEnable.setChecked(getHomeLoadBalancingResponse.getPowerDistributionEnable() == 1);
                setSpinnerSelection(R.array.sampling_method_keys, String.valueOf(getHomeLoadBalancingResponse.getSamplingMethod()), binding.spSamplingMethod);
                binding.etHomePowerCurrent.setText(String.valueOf(getHomeLoadBalancingResponse.getHomePowerCurrent()));
                binding.etPowerMeterAddress.setText(String.valueOf(getHomeLoadBalancingResponse.getPowerMeterAddress()));
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private int setSpinnerSelection(int keysResId, String key, Spinner spinner) {
        String[] keys = getResources().getStringArray(keysResId);
        int pos = -1;
        for (int i = 0; i < keys.length; i++) {
            if (keys[i].equals(key)) {
                pos = i;
                break;
            }
        }
        if (pos != -1) {
            spinner.setSelection(pos);
        }
        return pos;
    }

    private void getChargeMode() {
        GetChargeModeRequest request = new GetChargeModeRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetChargeModeResponse>(GetChargeModeResponse.class) {
            @Override
            public void onResponse2(GetChargeModeResponse response) {
                getChargeModeResponse = response;
                setSpinnerSelection(R.array.charge_mode_keys, getChargeModeResponse.getChargeMode(), binding.spChargeMode);
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private void get4GParameters() {
        Get4GParametersRequest request = new Get4GParametersRequest();
        cpClient.enqueue(request, new SimpleCPCallback<Get4GParametersResponse>(Get4GParametersResponse.class) {
            @Override
            public void onResponse2(Get4GParametersResponse response) {
                get4GParametersResponse = response;
                binding.et4gAPN.setText(get4GParametersResponse.getApn());
                binding.et4gAccount.setText(get4GParametersResponse.getAccount());
                binding.et4gPassword.setText(get4GParametersResponse.getPassword());
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private void getRatedCurrent() {
        GetRatedCurrentRequest request = new GetRatedCurrentRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetRatedCurrentResponse>(GetRatedCurrentResponse.class) {
            @Override
            public void onResponse2(GetRatedCurrentResponse response) {
                getRatedCurrentResponse = response;
                binding.etOutputCurrent.setText(getRatedCurrentResponse.getRatedCurrent());
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private void dismissProgressIfNeeded() {
        if (counter.decrementAndGet() <= 0) {
            dismissProgress();
        }
    }

    private void getServerInfo() {
        GetServerInfoRequest request = new GetServerInfoRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetServerInfoResponse>(GetServerInfoResponse.class) {
            @Override
            public void onResponse2(GetServerInfoResponse response) {
                getServerInfoResponse = response;
                String url = getServerInfoResponse.getUrl();
                binding.etServerUrl.setText(url);
                binding.etCpName.setText(getServerInfoResponse.getChargerId());
                binding.etAuthKey.setText(getServerInfoResponse.getAuthKey());
                dismissProgressIfNeeded();
//                if (IS_BG && !getString(R.string.bg_server_url).equals(url)) {
//                    setServerInfo(true);
//                }
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    private void getWifiInfo() {
        GetWifiInfoRequest request = new GetWifiInfoRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetWifiInfoResponse>(GetWifiInfoResponse.class) {
            @Override
            public void onResponse2(GetWifiInfoResponse response) {
                getWifiInfoResponse = response;
                binding.etWifiSSID.setText(getWifiInfoResponse.getWifiSSID());
                binding.etWifiPassword.setText(getWifiInfoResponse.getWifiPassword());
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();
        if (id == R.id.btnSet) {
            setChargerConfigInfo();
        } else if (id == R.id.btnGet) {
            getChargerConfigInfo();
        } else if (id == R.id.btnChangePwd) {
            changePassword();
        } else if (id == R.id.btnUpdateFirmware) {
            updateFirmware();
        }
    }

    private void updateFirmware() {
        String fileName = binding.etFileName.getText().toString().trim();
        if (TextUtils.isEmpty(fileName)) {
            MyUtils.showToast("Please enter file name");
            return;
        }
        FirmwareUpdater firmwareUpdater = new FirmwareUpdater();
        firmwareUpdater.setFirmwareUpdateListener(new FirmwareUpdater.FirmwareUpdateListener() {
            @Override
            public void onStateChanged(int state) {
                if (state == FirmwareUpdater.DOWNLOADING) {
                    dismissProgress();
                    showProgress("Downloading firmware", 0);
                } else if (state == FirmwareUpdater.REQUESTING) {
                    showProgress("Requesting update");
                } else if (state == FirmwareUpdater.UPLOADING) {
                    dismissProgress();
                    showProgress("Uploading firmware to the charger", 0);
                } else if (state == FirmwareUpdater.VERIFYING) {
                    showProgress("Verifying firmware");
                } else if (state == FirmwareUpdater.DONE) {
                    MyUtils.showToast("Firmware update done");
                    dismissProgress();
                    setResult();
                } else if (state == FirmwareUpdater.FAILED) {
                    dismissProgress();
                }
            }

            @Override
            public void onUploadProgress(float progress) {
                showProgress("Uploading firmware to the charger", (int) (100 * progress));
            }

            @Override
            public void onDownloadProgress(float progress) {
                showProgress("Downloading firmware", (int) (100 * progress));
            }
        });
        firmwareUpdater.update(fileName);
    }

    private void changePassword() {
        String p1 = binding.etEnterPwd.getText().toString().trim();
        String p2 = binding.etReenterPwd.getText().toString().trim();
        if (TextUtils.isEmpty(p1)) {
            MyUtils.showToast("Please enter password");
            return;
        }
        if (TextUtils.isEmpty(p2)) {
            MyUtils.showToast("Please Re-enter password");
            return;
        }
        if (!TextUtils.equals(p1, p2)) {
            MyUtils.showToast("The two passwords are not equal");
            return;
        }
        showProgress("Submitting");
        ChangePasswordRequest request = new ChangePasswordRequest(p1);
        cpClient.enqueue(request, new SimpleCPCallback<ChangePasswordResponse>(ChangePasswordResponse.class) {
            @Override
            public void onResponse2(ChangePasswordResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("change password successfully");
                    setResult();
                    finish();
                } else {
                    MyUtils.showToast("change password unsuccessfully");
                }
                dismissProgress();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgress();
            }
        });
    }

    private void setChargerConfigInfo() {
        counter.set(0);
        setCounter.set(0);
        setWifiInfo();
        set4GParameters();
        setServerInfo(false);
        setRatedCurrent();
        setChargeMode();
        setHomeLoadBalancing();
        if (enableEthernetConfig()) {
            setNetInterfaceSwitch();
            setEthernetParameters();
        }
        if (counter.get() > 0) {
            showProgress("Submitting");
        }
    }

    private void setEthernetParameters() {
        if (getEthernetParametersResponse == null) {
            return;
        }
        String ip = binding.etIpAddress.getText().toString().trim();
        String subnetMask = binding.etSubnetMask.getText().toString().trim();
        String gateway = binding.etDefaultGateway.getText().toString().trim();
        String dns = binding.etDNS.getText().toString().trim();
        boolean enableDHCP = binding.swDHCP.isChecked();
        if (TextUtils.isEmpty(ip)) {
            MyUtils.showToast("The IP address cannot be empty");
            return;
        }
        if (!CheckUtils.isValidIpV4Address(ip)) {
            MyUtils.showToast("The IP address is invalid");
            return;
        }
        if (TextUtils.isEmpty(subnetMask)) {
            MyUtils.showToast("The subnet mask cannot be empty");
            return;
        }
        if (!CheckUtils.isValidIpV4Address(ip)) {
            MyUtils.showToast("The subnet mask is invalid");
            return;
        }
        if (TextUtils.isEmpty(gateway)) {
            MyUtils.showToast("The default gateway cannot be empty");
            return;
        }
        if (!CheckUtils.isValidIpV4Address(gateway)) {
            MyUtils.showToast("The default gateway is invalid");
            return;
        }
        if (TextUtils.isEmpty(dns)) {
            MyUtils.showToast("The DNS cannot be empty");
            return;
        }
        if (!CheckUtils.isValidIpV4Address(dns)) {
            MyUtils.showToast("The DNS is invalid");
            return;
        }
        if (getEthernetParametersResponse.isEnableDHCP() == enableDHCP
                && TextUtils.equals(getEthernetParametersResponse.getIp(), ip)
                && TextUtils.equals(getEthernetParametersResponse.getSubnetMask(), subnetMask)
                && TextUtils.equals(getEthernetParametersResponse.getGateway(), gateway)
                && TextUtils.equals(getEthernetParametersResponse.getDns(), dns)) {
            return;
        }
        SetEthernetParametersRequest request = new SetEthernetParametersRequest();
        request.setIp(ip);
        request.setSubnetMask(subnetMask);
        request.setGateway(gateway);
        request.setDns(dns);
        request.setEnableDHCP(enableDHCP);
        cpClient.enqueue(request, new SimpleCPCallback<SetEthernetParametersResponse>(SetEthernetParametersResponse.class) {
            @Override
            public void onResponse2(SetEthernetParametersResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    getEthernetParametersResponse.setIp(ip);
                    getEthernetParametersResponse.setSubnetMask(subnetMask);
                    getEthernetParametersResponse.setGateway(gateway);
                    getEthernetParametersResponse.setDns(dns);
                    getEthernetParametersResponse.setEnableDHCP(enableDHCP);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    private void setNetInterfaceSwitch() {
        if (getNetInterfaceSwitchResponse == null) {
            return;
        }
        boolean is4GEnabled = binding.sw4G.isChecked();
        boolean isWiFiEnabled = binding.swWiFi.isChecked();
        boolean isLANEnabled = binding.swLAN.isChecked();
        if (getNetInterfaceSwitchResponse.is4GEnabled() == is4GEnabled
                && getNetInterfaceSwitchResponse.isWiFiEnabled() == isWiFiEnabled
                && getNetInterfaceSwitchResponse.isLANEnabled() == isLANEnabled) {
            return;
        }
        SetNetInterfaceSwitchRequest request = new SetNetInterfaceSwitchRequest();
        request.set4GEnabled(is4GEnabled);
        request.setWiFiEnabled(isWiFiEnabled);
        request.setLANEnabled(isLANEnabled);
        cpClient.enqueue(request, new SimpleCPCallback<SetNetInterfaceSwitchResponse>(SetNetInterfaceSwitchResponse.class) {
            @Override
            public void onResponse2(SetNetInterfaceSwitchResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    getNetInterfaceSwitchResponse.set4GEnabled(is4GEnabled);
                    getNetInterfaceSwitchResponse.setWiFiEnabled(isWiFiEnabled);
                    getNetInterfaceSwitchResponse.setLANEnabled(isLANEnabled);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    private void exitOnSuccess() {
//        if (setCounter.decrementAndGet() <= 0) {
//            finish();
//        }
    }

    private void setHomeLoadBalancing() {
        if (getHomeLoadBalancingResponse == null) {
            return;
        }
        final String homePowerCurrent = binding.etHomePowerCurrent.getText().toString().trim();
        final String powerMeterAddress = binding.etPowerMeterAddress.getText().toString().trim();
        final int powerDistributionEnable = binding.swPowerDistributionEnable.isChecked() ? 1 : 0;
        if (TextUtils.isEmpty(samplingMethod)
                || TextUtils.isEmpty(homePowerCurrent)
                || TextUtils.isEmpty(powerMeterAddress)) {
            return;
        }
        final int iCurrent = Integer.parseInt(homePowerCurrent);
        final int iAddress = Integer.parseInt(powerMeterAddress);
        final int iMethod = Integer.parseInt(samplingMethod);
        if (getHomeLoadBalancingResponse.getHomePowerCurrent() == iCurrent
                && getHomeLoadBalancingResponse.getPowerMeterAddress() == iAddress
                && getHomeLoadBalancingResponse.getSamplingMethod() == iMethod
                && getHomeLoadBalancingResponse.getPowerDistributionEnable() == powerDistributionEnable) {
            return;
        }
        if (iCurrent > 120 || iCurrent < 0) {
            MyUtils.showToast("The home power current value must be between 0 and 120");
            return;
        }
        if (iAddress > 254 || iAddress < 0) {
            MyUtils.showToast("The power meter address value must be between 0 and 254");
            return;
        }
        SetHomeLoadBalancingRequest request = new SetHomeLoadBalancingRequest();
        request.setPowerDistributionEnable(powerDistributionEnable);
        request.setSamplingMethod(iMethod);
        request.setHomePowerCurrent(iCurrent);
        request.setPowerMeterAddress(iAddress);


        cpClient.enqueue(request, new SimpleCPCallback<SetHomeLoadBalancingResponse>(SetHomeLoadBalancingResponse.class) {
            @Override
            public void onResponse2(SetHomeLoadBalancingResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    getHomeLoadBalancingResponse.setPowerDistributionEnable(powerDistributionEnable);
                    getHomeLoadBalancingResponse.setSamplingMethod(iMethod);
                    getHomeLoadBalancingResponse.setHomePowerCurrent(iCurrent);
                    getHomeLoadBalancingResponse.setPowerMeterAddress(iAddress);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    private void setChargeMode() {
        if (getChargeModeResponse == null) {
            return;
        }
        if (TextUtils.isEmpty(chargeMode)) {
            return;
        }
        if (TextUtils.equals(chargeMode, getChargeModeResponse.getChargeMode())) {
            return;
        }
        SetChargeModeRequest request = new SetChargeModeRequest();
        request.setChargeMode(chargeMode);
        cpClient.enqueue(request, new SimpleCPCallback<SetChargeModeResponse>(SetChargeModeResponse.class) {
            @Override
            public void onResponse2(SetChargeModeResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    getChargeModeResponse.setChargeMode(chargeMode);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    private void set4GParameters() {
        if (get4GParametersResponse == null) {
            return;
        }
        final String apn = binding.et4gAPN.getText().toString().trim();
        final String account = binding.et4gAccount.getText().toString().trim();
        final String password = binding.et4gPassword.getText().toString().trim();
        if (TextUtils.equals(get4GParametersResponse.getApn(), apn)
                && TextUtils.equals(get4GParametersResponse.getAccount(), account)
                && TextUtils.equals(get4GParametersResponse.getPassword(), password)) {
            return;
        }
        Set4GParametersRequest request = new Set4GParametersRequest();
        request.setApn(apn);
        request.setAccount(account);
        request.setPassword(password);
        cpClient.enqueue(request, new SimpleCPCallback<Set4GParametersResponse>(Set4GParametersResponse.class) {
            @Override
            public void onResponse2(Set4GParametersResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    get4GParametersResponse.setApn(apn);
                    get4GParametersResponse.setAccount(account);
                    get4GParametersResponse.setPassword(password);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    private void setRatedCurrent() {
        if (getRatedCurrentResponse == null) {
            return;
        }
        String current = binding.etOutputCurrent.getText().toString().trim();
        if (TextUtils.isEmpty(current)) {
            return;
        }
        if (current.length() == 1) {
            current = "0" + current;
        }
        if (TextUtils.equals(getRatedCurrentResponse.getRatedCurrent(), current)) {
            return;
        }
        String ratedCurrent = current;
        SetRatedCurrentRequest request = new SetRatedCurrentRequest();
        request.setRatedCurrent(ratedCurrent);
        cpClient.enqueue(request, new SimpleCPCallback<SetRatedCurrentResponse>(SetRatedCurrentResponse.class) {
            @Override
            public void onResponse2(SetRatedCurrentResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    getRatedCurrentResponse.setRatedCurrent(ratedCurrent);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    private void setServerInfo(final boolean inBackground) {
        if (getServerInfoResponse == null) {
            return;
        }
        final String url = binding.etServerUrl.getText().toString().trim();
        final String chargerId = binding.etCpName.getText().toString().trim();
        final String authKey = binding.etAuthKey.getText().toString().trim();
        if (TextUtils.isEmpty(chargerId) || TextUtils.isEmpty(authKey)) {
            return;
        }
        if (TextUtils.equals(getServerInfoResponse.getUrl(), url)
                && TextUtils.equals(getServerInfoResponse.getChargerId(), chargerId)
                && TextUtils.equals(getServerInfoResponse.getAuthKey(), authKey)) {
            return;
        }
        SetServerInfoRequest request = new SetServerInfoRequest();
        request.setUrl(url);
        request.setChargerId(chargerId);
        request.setAuthKey(authKey);
        cpClient.enqueue(request, new SimpleCPCallback<SetServerInfoResponse>(SetServerInfoResponse.class) {
            @Override
            public void onResponse2(SetServerInfoResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    if (!TextUtils.equals(chargerId, getServerInfoResponse.getChargerId())) {
                        chargerIdChanged = true;
                    }
                    getServerInfoResponse.setUrl(url);
                    getServerInfoResponse.setChargerId(chargerId);
                    getServerInfoResponse.setAuthKey(authKey);
                    setResult();
                    if (!inBackground) {
                        exitOnSuccess();
                    }
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                if (!inBackground) {
                    dismissProgressIfNeeded();
                }
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                if (!inBackground) {
                    dismissProgressIfNeeded();
                }
            }
        });
        if (!inBackground) {
            counter.addAndGet(1);
            setCounter.addAndGet(1);
        }
    }

    private void setWifiInfo() {
        if (getWifiInfoResponse == null) {
            return;
        }
        final String ssid = binding.etWifiSSID.getText().toString().trim();
        final String password = binding.etWifiPassword.getText().toString().trim();
        if (TextUtils.isEmpty(ssid) || (TextUtils.isEmpty(password) && false)) {
            return;
        }
        if (TextUtils.equals(getWifiInfoResponse.getWifiSSID(), ssid)
                && TextUtils.equals(getWifiInfoResponse.getWifiPassword(), password)) {
            return;
        }
        SetWifiInfoRequest request = new SetWifiInfoRequest();
        request.setWifiSSID(ssid);
        request.setWifiPassword(password);
        cpClient.enqueue(request, new SimpleCPCallback<SetWifiInfoResponse>(SetWifiInfoResponse.class) {
            @Override
            public void onResponse2(SetWifiInfoResponse response) {
                if (response.isSuccessful()) {
                    MyUtils.showToast("set successfully");
                    getWifiInfoResponse.setWifiSSID(ssid);
                    getWifiInfoResponse.setWifiPassword(password);
                    setResult();
                    exitOnSuccess();
                } else {
                    MyUtils.showToast("set unsuccessfully");
                }
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
        counter.addAndGet(1);
        setCounter.addAndGet(1);
    }

    public void showProgress(String strMsg) {
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

    public void showProgress(String strMsg, int progress) {
        if (isFinishing() || isDestroyed()) {
            return;
        }
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(strMsg);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(progress);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }

    }

    public void dismissProgress() {
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

    private void setResult() {
        setResult(RESULT_OK, new Intent().putExtra("chargerIdChanged", chargerIdChanged));
    }

}
