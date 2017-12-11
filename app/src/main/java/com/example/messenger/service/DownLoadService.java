package com.example.messenger.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.example.messenger.listener.DownloadListener;

import java.util.List;


public class DownLoadService extends Service {
    public DownloadTask downloadTask;
    private String downloadURL;
    private List<String> downloadURLS;
    public void setPause(boolean isPause){
        if(downloadTask!=null){
            downloadTask.setPause(isPause);
        }

    }

//    public void setDownloadURL(String downloadURL) {
//        this.downloadURL = downloadURL;
//    }

    private DownloadListener downloadListener;
    private DownloadBinder downloadBinder=new DownloadBinder();

    public void setDownloadListener(DownloadListener downloadListener) {
        this.downloadListener = downloadListener;
    }
    public void setDownloadURLS(List<String> downloadURLS){
        this.downloadURLS = downloadURLS;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return downloadBinder;
    }
    public class DownloadBinder extends Binder{
        public DownloadBinder(){}
        public DownLoadService getService(){
            return DownLoadService.this;
        }
        public void startDownload(){
            downloadTask = new DownloadTask(downloadListener,downloadURLS);
            downloadTask.execute(downloadURLS.size());
        }


    }
}
