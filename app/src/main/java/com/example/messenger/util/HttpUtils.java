package com.oureda.thunder.daydaypicture.util;


import android.util.Log;

import com.google.gson.Gson;
import com.oureda.thunder.daydaypicture.manager.CacheManager;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import static android.content.ContentValues.TAG;

/**
 * Created by thunder on 17-5-26.
 */

public class HttpUtils {
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private static HttpUtils httpUtils;
    private Gson gson = new Gson();
    private OkHttpClient okHttpClient = new OkHttpClient();

    private Response _get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        return this.okHttpClient.newCall(request).execute();
    }

    private void _getBook(String url, Callback paramCallback) {
        Request request = new Request.Builder().url(url).build();
        this.okHttpClient.newCall(request).enqueue(paramCallback);
    }

    private Response _post(String paramString, Param... paramVarArgs)
            throws IOException {
        CacheManager.getInstance().getCookie();
        RequestBody requestBody = RequestBody.create(JSON, paramsTOJson(paramVarArgs));
        Request request = new Request.Builder().url(paramString).post(requestBody).build();
        return new OkHttpClient().newCall(request).execute();
    }

    private void _postAsyn(int flag ,String url, Callback paramCallback, Param... paramVarArgs) {
        if (flag==1){
            String str = CacheManager.getInstance().getCookie();
            Log.d(TAG, "_postAsyn: "+str);
            RequestBody requestBody = RequestBody.create(JSON, paramsTOJson(paramVarArgs));
            Request request = new Request.Builder().url(url).addHeader("cookie", str).post(requestBody).build();
            this.okHttpClient.newCall(request).enqueue(paramCallback);
        }else{
            RequestBody requestBody = RequestBody.create(JSON, paramsTOJson(paramVarArgs));
            Request request = new Request.Builder().url(url).post(requestBody).build();
            this.okHttpClient.newCall(request).enqueue(paramCallback);
        }

    }


    public static Response get(String paramString)
            throws IOException {
        return getInstance()._get(paramString);
    }
    private  void _getAsny(String url,Callback callback){
        final Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(callback);
    }
    public static void getAsny(String url ,Callback callback){
        getInstance()._getAsny(url,callback);
    }
    public static void getBook(String paramString, Callback paramCallback) {
        getInstance()._getBook(paramString, paramCallback);
    }

    public static HttpUtils getInstance() {
        if (httpUtils == null) {
            synchronized (HttpUtils.class) {
                httpUtils = new HttpUtils();
            }

        }
        return httpUtils;
    }

    private String paramsTOJson(Param[] paramArrayOfParam) {
        HashMap localHashMap = new HashMap();
        int j = paramArrayOfParam.length;
        for (int i = 0; i < j; i++) {
            Param localParam = paramArrayOfParam[i];
            localHashMap.put(localParam.key, localParam.value);
        }
        String json = this.gson.toJson(localHashMap);
        Log.d(TAG, "paramsTOJson: "+json);
        return json;
    }

    public static Response post(String paramString, Param... paramVarArgs)
            throws IOException {
        return getInstance()._post(paramString, paramVarArgs);
    }

    public static void postAsyn(String paramString, Callback paramCallback, Param... paramVarArgs) {
        getInstance()._postAsyn(0,paramString, paramCallback, paramVarArgs);
    }
    public static void postAsynWithCookie(String paramString, Callback paramCallback, Param... paramVarArgs) {
        getInstance()._postAsyn(1,paramString, paramCallback, paramVarArgs);
    }

    public static class Param<T> {
        String key;
        T value;

        public Param() {
        }

        public Param(String key, T value) {
            this.key = key;
            this.value = value;
        }
    }
}
