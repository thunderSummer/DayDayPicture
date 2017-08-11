package com.oureda.thunder.daydaypicture.listener;

/**
 * Created by thunder on 17-7-5.
 */

public interface DowloadListener {
    void onProgress(int progress);
    void onSuccess();
    void onFailed();

}
