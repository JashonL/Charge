package com.shuoxd.charge.bluetooth.cptool;

import android.os.Handler;
import android.os.Looper;


import com.timxon.cplib.BleCPClient;
import com.timxon.cplib.protocol.FirmwareVerifyRequest;
import com.timxon.cplib.protocol.FirmwareVerifyResponse;
import com.timxon.cplib.protocol.RequestFirmwareUpdateRequest;
import com.timxon.cplib.protocol.RequestFirmwareUpdateResponse;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FirmwareUpdater {

    private int state = INITIAL;

    public static final int INITIAL = 0;
    public static final int DOWNLOADING = 1;
    public static final int REQUESTING = 2;
    public static final int UPLOADING = 3;
    public static final int VERIFYING = 4;
    public static final int DONE = 5;
    public static final int FAILED = 6;

    private final FirmwareDownloader downloader;
    private final FirmwareUploader uploader = new FirmwareUploader();
    private final BleCPClient cpClient = MyApplication.getInstance().getBleCPClient();

    private final Handler handler = new Handler(Looper.getMainLooper());

    private final FirmwareDownloader.ProgressListener downloadProgressListener = new FirmwareDownloader.ProgressListener() {
        @Override
        public void update(long bytesRead, long contentLength, boolean done) {
            if (firmwareUpdateListener != null && contentLength > 0) {
                firmwareUpdateListener.onDownloadProgress(bytesRead * 1f / contentLength);
            }
        }
    };

    private FirmwareUpdateListener firmwareUpdateListener;

    public FirmwareUpdater() {
        downloader = new FirmwareDownloader(downloadProgressListener);
    }

    public int getState() {
        return state;
    }

    public void setFirmwareUpdateListener(FirmwareUpdateListener firmwareUpdateListener) {
        this.firmwareUpdateListener = firmwareUpdateListener;
        uploader.setUploadProgressCallback(firmwareUpdateListener::onUploadProgress);
    }

    public void changeState(int state) {
        this.state = state;
        if (firmwareUpdateListener != null) {
            handler.post(() -> firmwareUpdateListener.onStateChanged(state));
        }
    }

    public void update(final String fileName) {
        changeState(INITIAL);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            changeState(DOWNLOADING);
            try {
                byte[] bin = downloader.download(fileName);
                if (bin != null) {
                    requestUpdate(bin);
                } else {
                    handler.post(() -> MyUtils.showToast("Downloading firmware failed"));
                    changeState(FAILED);
                }
            } catch (IOException e) {
                e.printStackTrace();
                handler.post(() -> MyUtils.showToast("Downloading firmware failed"));
                changeState(FAILED);
            }
        });
    }

    private void requestUpdate(final byte[] bin) {
        changeState(REQUESTING);
        RequestFirmwareUpdateRequest request = new RequestFirmwareUpdateRequest();
        request.setFirmwareSize(bin.length);
        cpClient.enqueue(request, new SimpleCPCallback<RequestFirmwareUpdateResponse>(RequestFirmwareUpdateResponse.class) {
            @Override
            public void onResponse2(RequestFirmwareUpdateResponse response) {
                if (response.isSuccessful()) {
                    changeState(UPLOADING);
                    uploader.setUploadCompleteCallback(() -> verifyFirmware(bin));
                    uploader.upload(bin);
                } else {
                    MyUtils.showToast("The charger refused to update firmware");
                    changeState(FAILED);
                }
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                changeState(FAILED);
            }
        });
    }

    private void verifyFirmware(final byte[] bin) {
        changeState(VERIFYING);
        FirmwareVerifyRequest request = new FirmwareVerifyRequest();
        byte[] data = getVerifyingData(bin);
        request.setData(data);
        cpClient.enqueue(request, new SimpleCPCallback<FirmwareVerifyResponse>(FirmwareVerifyResponse.class) {
            @Override
            public void onResponse2(FirmwareVerifyResponse response) {
                if (response.isSuccessful()) {
                    changeState(DONE);
                } else {
                    MyUtils.showToast("Firmware verification failed");
                    changeState(FAILED);
                }
            }

            @Override
            public void onError(Throwable error) {
                super.onError(error);
                changeState(FAILED);
            }
        });
    }

    private byte[] getVerifyingData(byte[] bin) {
        try {
            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            sha1.update(bin);
            return sha1.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public interface FirmwareUpdateListener {
        void onStateChanged(int state);
        void onUploadProgress(float progress);
        void onDownloadProgress(float progress);
    }

}
