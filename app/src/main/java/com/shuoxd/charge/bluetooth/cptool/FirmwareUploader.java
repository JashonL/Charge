package com.shuoxd.charge.bluetooth.cptool;



import java.util.Arrays;

public class FirmwareUploader {

    public static final int MAX_PACKET_SIZE = 200;

    private BleCPClient cpClient = MyApplication.getInstance().getBleCPClient();
    private UploadProgressCallback uploadProgressCallback;
    private UploadCompleteCallback uploadCompleteCallback;

    private byte[] bin;
    private int num;
    private int index;

    public void upload(byte[] bin) {
        this.bin = bin;
        index = 0;
        num = bin.length / MAX_PACKET_SIZE;
        if (bin.length % MAX_PACKET_SIZE > 0) {
            num++;
        }
        if (num > 0) {
            uploadNext();
        }
    }

    private void uploadNext() {
        if (index < num) {
            int from = index * MAX_PACKET_SIZE;
            int to = Math.min((index + 1) * MAX_PACKET_SIZE, bin.length);
            byte[] data = Arrays.copyOfRange(bin, from, to);
            upload(from, data);
        }
    }

    private void upload(int offset, byte[] data) {
        FirmwareUploadRequest request = new FirmwareUploadRequest();
        request.setData(data);
        request.setOffset(offset);
        cpClient.enqueue(request, new SimpleCPCallback<FirmwareUploadResponse>(FirmwareUploadResponse.class) {
            @Override
            public void onResponse2(FirmwareUploadResponse response) {
                if (response.isSuccessful()) {
                    index++;
                    if (uploadProgressCallback != null) {
                        uploadProgressCallback.onProgress(index * 1f / num);
                    }
                    if (index == num && uploadCompleteCallback != null) {
                        uploadCompleteCallback.onComplete();
                    }
                }
                uploadNext();
            }

            @Override
            public void onError(Throwable error) {
                Logger.e("FirmwareUploader", "upload error:", error);
                uploadNext();
            }
        });
    }

    public void setUploadProgressCallback(UploadProgressCallback uploadProgressCallback) {
        this.uploadProgressCallback = uploadProgressCallback;
    }

    public void setUploadCompleteCallback(UploadCompleteCallback uploadCompleteCallback) {
        this.uploadCompleteCallback = uploadCompleteCallback;
    }

    public interface UploadProgressCallback {
        void onProgress(float progress);
    }

    public interface UploadCompleteCallback {
        void onComplete();
    }

}
