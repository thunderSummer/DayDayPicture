package com.oureda.thunder.daydaypicture.service;

import android.os.AsyncTask;
import android.util.Log;

import com.oureda.thunder.daydaypicture.base.MD5;
import com.oureda.thunder.daydaypicture.listener.DownloadListener;
import com.oureda.thunder.daydaypicture.manager.CacheManager;
import com.oureda.thunder.daydaypicture.util.FileUtil;
import com.oureda.thunder.daydaypicture.util.HttpUtils;
import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.SocketTimeoutException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by thunder on 17-7-5.
 */

public class DownloadTask extends AsyncTask<Integer,Integer,Integer> {

    public static final int TYPE_SUCCESS=0;
    public static final int TYPE_FAILED=1;
    public static final int TYPE_PAUSE=2;
    private int downloadType=0;
    private int downloadSize;

    public DownloadListener downloadListener;
    private Boolean isStart=false;
    private Boolean isPause=false;

    public void setPause(Boolean pause) {
        isPause = pause;
    }

    private int lastProgress;
    private List<String> downloadUrls;

    public void setDownloadUrls(List<String> downloadUrls) {
        this.downloadUrls = downloadUrls;
    }

    public DownloadTask(DownloadListener downloadListener, List<String> downloadUrls) {
        this.downloadListener = downloadListener;
        this.downloadUrls = downloadUrls;
    }

    @Override
    protected Integer doInBackground(Integer ... integers) {
        InputStream inputStream =null;
        RandomAccessFile randomAccessFile = null;
        int i = 0;
        File file =null;
        int failNum=0;
        long downloadLength = 0;
        downloadSize = integers[0];
        File file1 = new File(FileUtil.getPictureHome()+"images/");
        File rootFile = new File(FileUtil.getPictureHome());
        if(!rootFile.exists()){
            rootFile.mkdir();
        }
        if(!file1.exists()){
            file1.mkdir();
        }
        while(i<downloadSize) {
            String downloadURL = downloadUrls.get(i);
            String fileName = downloadURL.substring(downloadURL.lastIndexOf("/"));
            if(fileName.contains(".json")){
                file = new File(FileUtil.getPictureHome() + fileName);
                downloadType=1;
            }else{
                file = new File(FileUtil.getPictureHome()+"images/"+fileName);
                downloadType=0;
            }

            if (fileName.contains(".json")) {
                try {
                    if (CacheManager.getInstance().getScreenMD5().equalsIgnoreCase(MD5.getFileMD5String(file))){
                        CacheManager.getInstance().setJOSNPosition(file.getAbsolutePath());
                        return TYPE_SUCCESS;
                    }else{
                        CacheManager.getInstance().setJOSNPosition(file.getAbsolutePath());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                long contentLength = getContentLength(downloadURL);
                if (contentLength == 0) {
                    failNum++;
                    i++;
                    publishProgress(i,downloadSize);
                    continue;
                } else if (contentLength == downloadLength) {
                    i++;
                    publishProgress(i,downloadSize);
                    continue;
                }
                Log.d("tag", "doInBackground: 正在下载"+downloadURL+"共"+downloadSize+"文件");

                OkHttpClient okHttpClient = new OkHttpClient();
                Request request = new Request.Builder()
                        .addHeader("RANGE", "bytes=" + downloadLength + "-")
                        .url(downloadURL)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                if (response != null) {
                    inputStream = response.body().byteStream();
                    randomAccessFile = new RandomAccessFile(file, "rw");
                    randomAccessFile.seek(downloadLength);
                    byte[] b = new byte[1024];
                    int total = 0;
                    int len;
                    while ((len = inputStream.read(b)) != -1) {
                        if(isPause){
                            return TYPE_PAUSE;
                        }
                        total += len;
                        randomAccessFile.write(b, 0, len);
                    }
                }
                assert response != null;
                response.body().close();
                i++;
                if(i==downloadSize){
                    if(failNum==0){
                        return TYPE_SUCCESS;
                    }else{
                        return TYPE_FAILED;
                    }
                }
                publishProgress(i,downloadSize);

            }catch (SocketTimeoutException s){
                s.printStackTrace();
                return TYPE_FAILED;
            }
            catch (IOException e) {
                e.printStackTrace();
                return TYPE_FAILED;
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return TYPE_SUCCESS;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress = values[0];
        int all =values[1];
        if(progress>lastProgress){
            if(downloadListener!=null){
                downloadListener.onProgress(progress,all);
                lastProgress=progress;
            }

        }
    }

    private long getContentLength(String url) throws IOException {
        Response response =HttpUtils.get(url);
        if(response!=null&&response.isSuccessful()){
            long contentLength = response.body().contentLength();
            response.close();
            return contentLength;
        }else{
            return 0;
        }

    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_FAILED:
                if(downloadListener!=null){
                    downloadListener.onFailed();
                   // downloadListener.after();
                }

                break;
            case TYPE_SUCCESS:
                if(downloadListener!=null){
                    downloadListener.onSuccess(downloadType);
                }
                break;
            case TYPE_PAUSE:
                if(downloadListener!=null){
                    downloadListener.after();
                }

                break;
            default:
                break;

        }
    }
}
