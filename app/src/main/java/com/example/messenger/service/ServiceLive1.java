package com.example.messenger.service;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.example.messenger.manager.CacheManager;

import java.util.ArrayList;

public class ServiceLive1 extends Service {
    public ServiceLive1() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        long time = intent.getLongExtra("interval",0);
        if(time!=0){

        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (CacheManager.getInstance().getShouldLive() && !isServiceRunning(ServiceLive1.this, "com.example.messenger.service.ServiceLive1")) {
                    startService(new Intent(ServiceLive1.this, ServiceLive2.class));
                }
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public static boolean isServiceRunning(Context context, String ServiceName) {
        if (("").equals(ServiceName) || ServiceName == null)
            return false;
        ActivityManager myManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService = (ArrayList<ActivityManager.RunningServiceInfo>) myManager
                .getRunningServices(1000);
        for (int i = 0; i < runningService.size(); i++) {
            if (runningService.get(i).service.getClassName().toString()
                    .equals(ServiceName)) {
                return true;
            }
        }
        return false;
    }
}
