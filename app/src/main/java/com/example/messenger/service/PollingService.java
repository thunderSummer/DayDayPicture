package com.example.messenger.service;

import android.os.IBinder;

import android.app.Service;
import android.content.Intent;
import android.util.Log;

import com.example.messenger.util.PollingUtils;
import com.google.gson.Gson;
import com.example.messenger.base.MD5;
import com.example.messenger.base.PictureControl;
import com.example.messenger.base.PictureData;
import com.example.messenger.base.PollingData;
import com.example.messenger.base.ScreenInfo;
import com.example.messenger.listener.DownloadListener;
import com.example.messenger.manager.CacheManager;
import com.example.messenger.manager.StringManager;
import com.example.messenger.util.FileUtil;
import com.example.messenger.util.HttpUtils;
import com.example.messenger.util.SharedPreferenceUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Response;


public class PollingService extends Service {
    private DownloadTask downloadTask;
    public static final String ACTION  = "com.oureda.thunder.daydaypicture.service.PollingService";
    public static boolean needUpdate=false;
    private NewDownloadListener downloadListener=new NewDownloadListener();
    private String resourceUrl;
    private String jsonUrl;


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onCreate() {
    }

    @Override
    public void onStart(Intent intent, int startId) {
        new PollingThread().start();
    }
    class PollingThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                File file = new File(CacheManager.getInstance().getJSONPostion());
                if(file.exists()){
                }
                Response r =HttpUtils.post(StringManager.URL+"request",new HttpUtils.Param("uuid",SharedPreferenceUtil.getInstance().getString("uuid")));
                String content = r.body().string();
                Log.d("content ====", "run: "+content);
                PollingData pollingData = new Gson().fromJson(content,PollingData.class);

                    CacheManager.getInstance().setPullTime(Integer.valueOf(pollingData.data.getTime()));
                    CacheManager.getInstance().setScreenMD5(pollingData.data.getMd5());
                    if(!CacheManager.getInstance().getScreenMD5().equalsIgnoreCase(MD5.getFileMD5String(file))){
                        Response response =HttpUtils.post(StringManager.URL+"request_resource",new HttpUtils.Param("uuid",SharedPreferenceUtil.getInstance().getString("uuid")));
                        String jsonContent = response.body().string();
                        Log.d("sssssss", "run: "+jsonContent);
                        ScreenInfo screenInfo =null;
                        try {
                           screenInfo = new Gson().fromJson(jsonContent,ScreenInfo.class);
                        }catch (Exception e){
                            Intent intent = new Intent("com.oureda.thunder.daydaypicture.RECEIVER");
                            intent.putExtra("type",2);
                            sendBroadcast(intent);
                        }

                        if(screenInfo==null||screenInfo.data==null){
                            Intent intent = new Intent("com.oureda.thunder.daydaypicture.RECEIVER");
                            intent.putExtra("type",2);
                            sendBroadcast(intent);
                        }else{
                            List<String> stringList = new ArrayList<>();
                            stringList.add(screenInfo.data.getJson_url());
                            jsonUrl = screenInfo.data.getJson_url();
                            downloadTask = new DownloadTask(downloadListener,stringList);
                            downloadTask.execute(stringList.size());
                        }

                    }

            } catch (IOException e) {
                e.printStackTrace();
            }


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("Service:onDestroy");
    }
    class NewDownloadListener implements DownloadListener{

        @Override
        public void onProgress(int... progresses ) {

        }

        @Override
        public void onSuccess(int type) {
            if (type == 1) {
                try {
                    FileInputStream fileInputStream = new FileInputStream(FileUtil.getPictureHome() + jsonUrl.substring(jsonUrl.lastIndexOf("/")));
                    byte[] bytes = new byte[1024];
                    int b = 0;
                    StringBuilder result = new StringBuilder();
                    try {
                        while ((b = fileInputStream.read(bytes)) > 0) {
                            result.append(new String(bytes, 0, b));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    PictureData pictureData;
                    pictureData = new Gson().fromJson(result.toString(), PictureData.class);
                    List<String> pictureList = new ArrayList<>();
                    Map<String, String> originMap = new HashMap<>();
                    for (PictureData.Picture list : pictureData.picture) {
                        pictureList.add(list.getUrl());
                        originMap.put(list.getUrl(), list.getMd5());
                        SharedPreferenceUtil.getInstance().putInt(list.getName(), list.getTime());
                    }
                    pictureList = PictureControl.setPicture(originMap);
                     DownloadTask downloadTaskah = new DownloadTask(downloadListener,pictureList);
                    downloadTaskah.execute(pictureList.size());


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Intent intent = new Intent("com.example.thunder.messenger");
                intent.putExtra("type",1);
                sendBroadcast(intent);
            }
        }

        @Override
        public void onFailed() {

        }

        @Override
        public void after() {

        }


    }
}
