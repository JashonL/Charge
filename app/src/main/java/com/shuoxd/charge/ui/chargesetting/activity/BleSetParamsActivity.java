package com.shuoxd.charge.ui.chargesetting.activity;

import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_4G_ACCOUNT;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_4G_APN;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_4G_PASSWORD;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_AUTH_KEY;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_CP_NAME;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_HOME_POWER_CURRENT;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_POWER_DISTRIBUTION_ENABLE;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_POWER_METER_ADDR;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_SAMPLING_METHOD;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_SERVER_URL;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_WIFI_PASSWORD;
import static com.shuoxd.charge.ui.chargesetting.bean.BleSetBean.ItemKey.KEY_WIFI_SSID;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Spinner;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.shuoxd.charge.R;
import com.shuoxd.charge.application.MainApplication;
import com.shuoxd.charge.base.BaseActivity;
import com.shuoxd.charge.bluetooth.cptool.MyUtils;
import com.shuoxd.charge.bluetooth.cptool.SimpleCPCallback;
import com.shuoxd.charge.databinding.ActivityBleSetParamBinding;
import com.shuoxd.charge.ui.chargesetting.adapter.ItemSettingAdapter;
import com.shuoxd.charge.ui.chargesetting.bean.BleSetBean;
import com.shuoxd.charge.ui.chargesetting.settype.OneCheckItem;
import com.shuoxd.charge.ui.chargesetting.settype.OneInputItem;
import com.shuoxd.charge.ui.chargesetting.settype.OneSelectItem;
import com.timxon.cplib.BleCPClient;
import com.timxon.cplib.protocol.CPClient;
import com.timxon.cplib.protocol.DeviceInfo;
import com.timxon.cplib.protocol.Get4GParametersRequest;
import com.timxon.cplib.protocol.Get4GParametersResponse;
import com.timxon.cplib.protocol.GetChargeModeRequest;
import com.timxon.cplib.protocol.GetChargeModeResponse;
import com.timxon.cplib.protocol.GetChargerStatusRequest;
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
import com.timxon.cplib.protocol.Response;
import com.timxon.cplib.protocol.Set4GParametersRequest;
import com.timxon.cplib.protocol.Set4GParametersResponse;
import com.timxon.cplib.protocol.SetChargeModeRequest;
import com.timxon.cplib.protocol.SetChargeModeResponse;
import com.timxon.cplib.protocol.SetHomeLoadBalancingRequest;
import com.timxon.cplib.protocol.SetHomeLoadBalancingResponse;
import com.timxon.cplib.protocol.SetRatedCurrentRequest;
import com.timxon.cplib.protocol.SetRatedCurrentResponse;
import com.timxon.cplib.protocol.SetServerInfoRequest;
import com.timxon.cplib.protocol.SetServerInfoResponse;
import com.timxon.cplib.protocol.SetWifiInfoRequest;
import com.timxon.cplib.protocol.SetWifiInfoResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BleSetParamsActivity extends BaseActivity {

    public static void start(Context context,String pwd,DeviceInfo info) {
        Intent intent = new Intent(context, BleSetParamsActivity.class);
        intent.putExtra("pwd", pwd);
        intent.putExtra("DeviceInfo", info);
        context.startActivity(intent);
    }


    private BleCPClient cpClient;
    private GetWifiInfoResponse getWifiInfoResponse;
    private GetServerInfoResponse getServerInfoResponse;
    private GetRatedCurrentResponse getRatedCurrentResponse;
    private Get4GParametersResponse get4GParametersResponse;
    private GetChargeModeResponse getChargeModeResponse;
    private GetHomeLoadBalancingResponse getHomeLoadBalancingResponse;
    private GetEthernetParametersResponse getEthernetParametersResponse;
    private GetNetInterfaceSwitchResponse getNetInterfaceSwitchResponse;


    private ActivityBleSetParamBinding binding;


    private ItemSettingAdapter adapter;
    private String[] titles;
    public List<BleSetBean> settingItems = new ArrayList<>();


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

    List<String> ssidList = new ArrayList<>();


    private void onScanResults() {
        List<ScanResult> results = wifiManager.getScanResults();
        if (results == null || results.isEmpty()) {
            return;
        }
        for (ScanResult result : results) {
            if (result.frequency < 2500 && !ssidList.contains(result.SSID)) {
                // 只支持2.4G
                ssidList.add(result.SSID);
            }

        }

    }


    private String pwd;
    private boolean defaultPasswordPrompted;
    private DeviceInfo deviceInfo;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBleSetParamBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        cpClient = MainApplication.Companion.instance().getBleCPClient();
        initSetItems();
        initViews();
        initData();
    }

    private void initData() {
        pwd = getIntent().getStringExtra("pwd");
        deviceInfo = (DeviceInfo) getIntent().getSerializableExtra("DeviceInfo");
        checkDefaultPassword();
        initWiFiScan();
        getChargerConfigInfo();
        getNetStatus();
        if (deviceInfo != null) {
     /*       binding.layCurrentVersion.setVisibility(View.VISIBLE);
            binding.tvChargerVersion.setText(deviceInfo.version);*/
        }
    }


    private void checkDefaultPassword() {
   /*     if (!"12345678".equals(pwd) || defaultPasswordPrompted) {
            return;
        }
        new AlertDialog.Builder(this)
                .setMessage("The current password is the default password. Do you want to change The password now?")
                .setPositiveButton("Ok", (dialog, which) -> binding.etEnterPwd.requestFocus())
                .setNegativeButton("cancel", null)
                .show();*/
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


    private boolean enableEthernetConfig() {
        return supportEthernetConfig();
    }


    private void getNetStatus() {
        GetChargerStatusRequest request = new GetChargerStatusRequest();
 /*       cpClient.enqueue(request, new SimpleCPCallback<GetChargerStatusResponse>(GetChargerStatusResponse.class) {
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
        });*/
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
       /*         binding.etIpAddress.setText(getEthernetParametersResponse.getIp());
                binding.etSubnetMask.setText(getEthernetParametersResponse.getSubnetMask());
                binding.etDefaultGateway.setText(getEthernetParametersResponse.getGateway());
                binding.etDNS.setText(getEthernetParametersResponse.getDns());
                binding.swDHCP.setChecked(getEthernetParametersResponse.isEnableDHCP());*/


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
/*                binding.sw4G.setChecked(getNetInterfaceSwitchResponse.is4GEnabled());
                binding.swWiFi.setChecked(getNetInterfaceSwitchResponse.isWiFiEnabled());
                binding.swLAN.setChecked(getNetInterfaceSwitchResponse.isLANEnabled());*/
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
                List<BleSetBean> data = adapter.getData();
                for (int i = 0; i < data.size(); i++) {
                    BleSetBean bleSetBean = data.get(i);
                    String key = bleSetBean.key;
                    switch (key) {
                        case KEY_POWER_DISTRIBUTION_ENABLE:
                            ((OneCheckItem) bleSetBean).isCheck = getHomeLoadBalancingResponse.getPowerDistributionEnable() == 1;
                            break;
                        case KEY_SAMPLING_METHOD:
                            OneSelectItem oneSelectItem = ((OneSelectItem) bleSetBean);
                            List<OneSelectItem.SelectItem> selectItems = oneSelectItem.selectItems;
                            for (int j = 0; j < selectItems.size(); j++) {
                                OneSelectItem.SelectItem selectItem = selectItems.get(j);
                                String value = selectItem.value;
                                if (value.equals(String.valueOf(getHomeLoadBalancingResponse.getSamplingMethod()))) {
                                    oneSelectItem.dataValue = selectItem.title;
                                    break;
                                }
                            }
                            break;
                        case KEY_HOME_POWER_CURRENT:
                            OneInputItem powercurrent = ((OneInputItem) bleSetBean);
                            powercurrent.value = String.valueOf(getHomeLoadBalancingResponse.getHomePowerCurrent());

                            break;
                        case KEY_POWER_METER_ADDR:
                            OneInputItem metteraddr = ((OneInputItem) bleSetBean);
                            metteraddr.value = String.valueOf(getHomeLoadBalancingResponse.getPowerMeterAddress());

                            break;
                    }
                }


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
                String chargeMode = getChargeModeResponse.getChargeMode();

                OneSelectItem oneSelectItem = (OneSelectItem) adapter.getData().get(9);
                List<OneSelectItem.SelectItem> selectItems = oneSelectItem.selectItems;
                for (int i = 0; i < selectItems.size(); i++) {
                    OneSelectItem.SelectItem selectItem = selectItems.get(i);
                    String value = selectItem.value;
                    if (value.equals(chargeMode)) {
                        oneSelectItem.dataValue = selectItem.title;
                        break;
                    }
                }


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

                OneInputItem input4gAPN = (OneInputItem) adapter.getData().get(2);
                input4gAPN.value = get4GParametersResponse.getApn();

                OneInputItem input4gAccount = (OneInputItem) adapter.getData().get(3);
                input4gAccount.value = get4GParametersResponse.getAccount();

                OneInputItem input4gPassword = (OneInputItem) adapter.getData().get(4);
                input4gPassword.value = get4GParametersResponse.getPassword();

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
                OneInputItem input4gPassword = (OneInputItem) adapter.getData().get(8);
                input4gPassword.value = getRatedCurrentResponse.getRatedCurrent();

                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void dismissProgressIfNeeded() {
        if (counter.decrementAndGet() <= 0) {
            dismissProgress();
            adapter.notifyDataSetChanged();
        }
    }

    private void getServerInfo() {
        GetServerInfoRequest request = new GetServerInfoRequest();
        cpClient.enqueue(request, new SimpleCPCallback<GetServerInfoResponse>(GetServerInfoResponse.class) {
            @Override
            public void onResponse2(GetServerInfoResponse response) {
                getServerInfoResponse = response;
                OneInputItem url = (OneInputItem) adapter.getData().get(5);
                url.value = getServerInfoResponse.getUrl();

                OneInputItem chargeid = (OneInputItem) adapter.getData().get(6);
                chargeid.value = getServerInfoResponse.getChargerId();

                OneInputItem authkey = (OneInputItem) adapter.getData().get(7);
                authkey.value = getServerInfoResponse.getAuthKey();

                dismissProgressIfNeeded();
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
                OneSelectItem oneSelectItem = (OneSelectItem) adapter.getData().get(0);
                oneSelectItem.dataValue = getWifiInfoResponse.getWifiSSID();
                OneInputItem oneInputItem = (OneInputItem) adapter.getData().get(1);
                oneInputItem.value = getWifiInfoResponse.getWifiPassword();
                dismissProgressIfNeeded();
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                dismissProgressIfNeeded();
            }
        });
    }


    private void initSetItems() {
        titles = new String[]{
                getString(R.string.m197_wifi_ssid),
                getString(R.string.m198_wifi_password),
                getString(R.string.m199_4g_apn),
                getString(R.string.m200_4g_account),
                getString(R.string.m201_4g_password),
                getString(R.string.m202_server_url),
                getString(R.string.m203_cp_name),
                getString(R.string.m204_authorization_key),
                getString(R.string.m205_output_current),
                getString(R.string.m206_charge_mode),
                getString(R.string.m207_power_distribution_enable),
                getString(R.string.m208_sampling_method),
                getString(R.string.m209_home_power_current),
                getString(R.string.m210_power_meter_address),

        };

        String[] key = new String[]{
                KEY_WIFI_SSID,
                KEY_WIFI_PASSWORD,
                KEY_4G_APN,
                KEY_4G_ACCOUNT,
                KEY_4G_PASSWORD,
                KEY_SERVER_URL,
                KEY_CP_NAME,
                KEY_AUTH_KEY,
                BleSetBean.ItemKey.KEY_OUT_PUT_CURRENT,
                BleSetBean.ItemKey.KEY_CHARGE_MODE,
                KEY_POWER_DISTRIBUTION_ENABLE,
                KEY_SAMPLING_METHOD,
                KEY_HOME_POWER_CURRENT,
                KEY_POWER_METER_ADDR,
        };


        int[] itemType = new int[]{
                BleSetBean.ItemType.ONE_SELECT_ITEM_CHOOSE,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_CHOOSE,
                BleSetBean.ItemType.ONE_SELECT_ITEM_CHECK,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT,
                BleSetBean.ItemType.ONE_SELECT_ITEM_NEXT

        };

        for (int i = 0; i < titles.length; i++) {
            if (i == 0) {
                OneSelectItem oneSelectItem = new OneSelectItem();
                oneSelectItem.title = titles[i];
                oneSelectItem.itemType = itemType[i];
                oneSelectItem.key = key[i];
                List<OneSelectItem.SelectItem> chooseItems = new ArrayList<>();
                for (int j = 0; j < ssidList.size(); j++) {
                    chooseItems.add(new OneSelectItem.SelectItem(
                            ssidList.get(j),
                            String.valueOf(1)
                    ));
                }

                chooseItems.add(new OneSelectItem.SelectItem(getString(R.string.m211_manual_input), "0"));
                oneSelectItem.selectItems = chooseItems;
            } else if (i == 9) {
                OneSelectItem oneSelectItem = new OneSelectItem();
                oneSelectItem.title = titles[i];
                oneSelectItem.itemType = itemType[i];
                oneSelectItem.key = key[i];

                List<OneSelectItem.SelectItem> chooseItems = new ArrayList<>();

                String[] stringArray = getResources().getStringArray(R.array.charge_mode_values);
                for (int j = 0; j < stringArray.length; j++) {
                    chooseItems.add(new OneSelectItem.SelectItem(
                            stringArray[j],
                            String.valueOf(j)
                    ));
                }
                chooseItems.add(new OneSelectItem.SelectItem(getString(R.string.m211_manual_input), "0"));
                oneSelectItem.selectItems = chooseItems;
            } else if (i == 10) {
                OneCheckItem oneCheckItem = new OneCheckItem();
                oneCheckItem.key = key[i];
                oneCheckItem.title = titles[i];
                oneCheckItem.itemType = itemType[i];
                oneCheckItem.isCheck = false;
            } else {
                OneInputItem inputItem = new OneInputItem();
                inputItem.key = key[i];
                inputItem.title = titles[i];
                inputItem.itemType = itemType[i];
                settingItems.add(inputItem);

            }


        }

    }


    private void initViews() {
        //初始化标题
        //初始化recycleView
        binding.rlvParams.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ItemSettingAdapter(settingItems,
                this::setCharge, this::setCharge, this::setCharge);
        binding.rlvParams.setAdapter(adapter);
    }


    private void setCharge(BleSetBean item) {
        String key = item.key;
        switch (key) {
            case KEY_WIFI_SSID:
                String oneChooseValue = ((OneSelectItem) item).oneChooseValue;
                setWifiInfo(oneChooseValue, getValueByKey(KEY_WIFI_PASSWORD));
                break;
            case KEY_WIFI_PASSWORD:
                String password = ((OneInputItem) item).value;
                setWifiInfo(getValueByKey(KEY_WIFI_SSID), password);
                break;
            case KEY_4G_APN:
                String vpn = ((OneInputItem) item).value;
                set4GParameters(vpn, getValueByKey(KEY_4G_ACCOUNT), getValueByKey(KEY_4G_PASSWORD));
                break;
            case KEY_4G_ACCOUNT:
                String account = ((OneInputItem) item).value;
                set4GParameters(getValueByKey(KEY_4G_APN), account, getValueByKey(KEY_4G_PASSWORD));
                break;
            case KEY_4G_PASSWORD:
                String _4gPassword = ((OneInputItem) item).value;
                set4GParameters(getValueByKey(KEY_4G_APN), getValueByKey(KEY_4G_ACCOUNT), _4gPassword);
                break;
            case KEY_SERVER_URL:
                String url = ((OneInputItem) item).value;
                setServerInfo(false, url,  getValueByKey(KEY_CP_NAME),  getValueByKey(KEY_AUTH_KEY));


                break;
            case KEY_CP_NAME:
                String cpName = ((OneInputItem) item).value;
                setServerInfo(false, getValueByKey(KEY_SERVER_URL), cpName, getValueByKey(KEY_AUTH_KEY));


                break;
            case KEY_AUTH_KEY:
                String auth = ((OneInputItem) item).value;
                setServerInfo(false, getValueByKey(KEY_SERVER_URL), getValueByKey(KEY_CP_NAME), auth);
                break;
            case BleSetBean.ItemKey.KEY_OUT_PUT_CURRENT:
                String current = ((OneInputItem) item).value;
                setRatedCurrent(current);
                break;
            case BleSetBean.ItemKey.KEY_CHARGE_MODE:
                chargeMode = ((OneSelectItem) item).oneChooseValue;
                setChargeMode();
                break;
            case KEY_POWER_DISTRIBUTION_ENABLE:
                boolean isCheck = ((OneCheckItem) item).isCheck;
                setHomeLoadBalancing(getValueByKey(KEY_HOME_POWER_CURRENT), getValueByKey(KEY_POWER_METER_ADDR), isCheck ? 1 : 0);
                break;
            case KEY_SAMPLING_METHOD:
                samplingMethod = ((OneSelectItem) item).oneChooseValue;
                setHomeLoadBalancing(getValueByKey(KEY_HOME_POWER_CURRENT),  getValueByKey(KEY_POWER_METER_ADDR), Integer.parseInt(getValueByKey(KEY_POWER_DISTRIBUTION_ENABLE)));
                break;
            case KEY_HOME_POWER_CURRENT:
                String homePowerCurrent = ((OneInputItem) item).value;
                setHomeLoadBalancing(homePowerCurrent, getValueByKey(KEY_POWER_METER_ADDR),  Integer.parseInt(getValueByKey(KEY_POWER_DISTRIBUTION_ENABLE)));
                break;
            case KEY_POWER_METER_ADDR:
                String powerMeterAddress = ((OneInputItem) item).value;
                setHomeLoadBalancing(getValueByKey(KEY_HOME_POWER_CURRENT), powerMeterAddress,  Integer.parseInt(getValueByKey(KEY_POWER_DISTRIBUTION_ENABLE)));
                break;
        }

    }


    private String getValueByKey(String key) {
        String value = "";
        BleSetBean item = null;
        for (int i = 0; i < adapter.getData().size(); i++) {
            BleSetBean bleSetBean = adapter.getData().get(i);
            String key1 = bleSetBean.key;
            if (key.equals(key1)) {
                item = bleSetBean;
            }
        }

        if (item == null) return "";
        switch (key) {
            case KEY_WIFI_SSID:
                value = ((OneSelectItem) item).oneChooseValue;
                break;
            case KEY_WIFI_PASSWORD:
            case KEY_4G_APN:
            case KEY_4G_ACCOUNT:
            case KEY_4G_PASSWORD:
            case KEY_SERVER_URL:
            case KEY_CP_NAME:
            case KEY_AUTH_KEY:
            case BleSetBean.ItemKey.KEY_OUT_PUT_CURRENT:
            case KEY_HOME_POWER_CURRENT:
            case KEY_POWER_METER_ADDR:
                value = ((OneInputItem) item).value;
                break;
            case BleSetBean.ItemKey.KEY_CHARGE_MODE:
                chargeMode = ((OneSelectItem) item).oneChooseValue;
                break;
            case KEY_POWER_DISTRIBUTION_ENABLE:
                boolean isCheck = ((OneCheckItem) item).isCheck;
                value = isCheck ? "1" : "0";
                break;
            case KEY_SAMPLING_METHOD:
                samplingMethod = ((OneSelectItem) item).oneChooseValue;
                break;
        }
        return value;
    }








 /*   private void updateFirmware() {
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
    }*/

/*    private void setChargerConfigInfo() {
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
    }*/

/*    private void setEthernetParameters() {
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
    }*/

/*    private void setNetInterfaceSwitch() {
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
    }*/

    private void exitOnSuccess() {
//        if (setCounter.decrementAndGet() <= 0) {
//            finish();
//        }
    }

    private void setHomeLoadBalancing(String homePowerCurrent, String powerMeterAddress, int powerDistributionEnable) {
        counter.set(0);
        setCounter.set(0);
        if (getHomeLoadBalancingResponse == null) {
            return;
        }
/*        final String homePowerCurrent = binding.etHomePowerCurrent.getText().toString().trim();
        final String powerMeterAddress = binding.etPowerMeterAddress.getText().toString().trim();
        final int powerDistributionEnable = binding.swPowerDistributionEnable.isChecked() ? 1 : 0;*/
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

    }

    private void setChargeMode() {
        counter.set(0);
        setCounter.set(0);
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

    }

    private void set4GParameters(String apn, String account, String password) {
        counter.set(0);
        setCounter.set(0);
        if (get4GParametersResponse == null) {
            return;
        }
  /*      final String apn = binding.et4gAPN.getText().toString().trim();
        final String account = binding.et4gAccount.getText().toString().trim();
        final String password = binding.et4gPassword.getText().toString().trim();*/
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

    }

    private void setRatedCurrent(String current) {
        counter.set(0);
        setCounter.set(0);
        if (getRatedCurrentResponse == null) {
            return;
        }
//        String current = binding.etOutputCurrent.getText().toString().trim();
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

    }

    private void setServerInfo(final boolean inBackground, String url, String chargerId, String authKey) {
        counter.set(0);
        setCounter.set(0);
        if (getServerInfoResponse == null) {
            return;
        }
  /*      final String url = binding.etServerUrl.getText().toString().trim();
        final String chargerId = binding.etCpName.getText().toString().trim();
        final String authKey = binding.etAuthKey.getText().toString().trim();*/
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

    }

    private void setWifiInfo(String ssid, String password) {
        counter.set(0);
        setCounter.set(0);
        if (getWifiInfoResponse == null) {
            return;
        }
//        final String ssid = binding.etWifiSSID.getText().toString().trim();
//        final String password = binding.etWifiPassword.getText().toString().trim();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();
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
                dismissProgress();
            }



            @Override
            public void onError(Throwable error) {
                MyUtils.showToast(error.getMessage());
                dismissProgress();
            }
        });
    }

}
