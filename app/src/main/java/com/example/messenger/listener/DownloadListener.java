package com.example.messenger.listener;

/**
 * Created by thunder on 17-7-5.
 */

public interface DownloadListener {
    void onProgress(int ... progress);
    void onSuccess(int type);
    void onFailed();
    void after();
}
