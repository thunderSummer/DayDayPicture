package com.example.messenger.service;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.messenger.R;
import com.example.messenger.util.FileUtil;

import java.io.File;

public class UpdateService extends Service {

    private UpdateTask downloadTask;

    private String downloadUrl;


    @Override
    public void onCreate() {
        super.onCreate();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        downloadUrl = intent.getStringExtra("downloadUrl");
        if (downloadTask == null) {
            downloadTask = new UpdateTask(listener);
            downloadTask.execute(downloadUrl);
            startForeground(1, getNotification("Downloading...", 0));
            Toast.makeText(UpdateService.this, "Downloading...", Toast.LENGTH_SHORT).show();

        }
        return super.onStartCommand(intent, flags, startId);
    }

    private UpdateTask.UpdateListener listener = new UpdateTask.UpdateListener() {
        @Override
        public void onProgress(int progress) {
            getNotificationManager().notify(1, getNotification("Downloading...", progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            // 下载成功时将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Success", -1));
            Toast.makeText(UpdateService.this, "Download Success", Toast.LENGTH_SHORT).show();
            if(installApk()!=0){
                File file1 = new File(FileUtil.getPictureHome()+downloadUrl.substring(downloadUrl.lastIndexOf("/")));
                file1.delete();
            }

        }

        @Override
        public void onFailed() {
            downloadTask = null;
            // 下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1, getNotification("Download Failed", -1));
            Toast.makeText(UpdateService.this, "Download Failed", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPaused() {
            downloadTask = null;
            Toast.makeText(UpdateService.this, "Paused", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(UpdateService.this, "Canceled", Toast.LENGTH_SHORT).show();
        }

    };


//    private DownloadBinder mBinder = new DownloadBinder();
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        return mBinder;
//    }
//
//    class DownloadBinder extends Binder {
//
//        public void startDownload(String url) {
//
//        }
//
//        public void pauseDownload() {
//            if (downloadTask != null) {
//                downloadTask.pauseDownload();
//            }
//        }
//
//        public void cancelDownload() {
//            if (downloadTask != null) {
//                downloadTask.cancelDownload();
//            } else {
//                if (downloadUrl != null) {
//                    // 取消下载时需将文件删除，并将通知关闭
//                    String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/"));
//                    String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
//                    File file = new File(directory + fileName);
//                    if (file.exists()) {
//                        file.delete();
//                    }
//                    getNotificationManager().cancel(1);
//                    stopForeground(true);
//                    Toast.makeText(UpdateService.this, "Canceled", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//
 //   }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification getNotification(String title, int progress) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        if (progress >= 0) {
            // 当progress大于或等于0时才需显示下载进度
            builder.setContentText(progress + "%");
            builder.setProgress(100, progress, false);
        }
        return builder.build();
    }
    private int  installApk() {
        File apkfile = new File(FileUtil.getPictureHome()+downloadUrl.substring(downloadUrl.lastIndexOf("/")));
        if (!apkfile.exists()) {
            return 0;
        }
        // 通过Intent安装APK文件
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        startActivity(intent);
        return 1;
    }

}
