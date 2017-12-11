package com.example.messenger.service;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.messenger.ChooseTimeActivity;
import com.example.messenger.MyAlarmReceiver;
import com.example.messenger.NewMainActivity;
import com.example.messenger.R;
import com.example.messenger.manager.CacheManager;

public class FourGroundService extends Service {
    AlarmManager alarmManager;
    PendingIntent pi;
    private BroadcastReceiver broadcastReceiver;
    public FourGroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        broadcastReceiver =new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        Intent intent2 = new Intent(this,MyAlarmReceiver.class);
        pi = PendingIntent.getService(this,0,intent2,0);
        intentFilter.addAction("com.example.thunder.messenger.AutoStart");
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String timeString= CacheManager.getInstance().getAutoTime();
        long timeLong = CacheManager.getInstance().getIntervalLong();
        if(timeLong!=-1){
            long temp =timeLong-System.currentTimeMillis();
            Log.d("ssssssss", "onStartCommand: "+temp+"");
            Intent intent1 = new Intent(FourGroundService.this, ChooseTimeActivity.class);
            PendingIntent pendingIntent = PendingIntent.getService(this,0,intent1,0);
            Notification notification = new NotificationCompat.Builder(this).setContentTitle("Messenger，系统消息")
                    .setContentText("Messenger将在"+timeString+"重启")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.icon)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.icon))
                    .setContentIntent(pendingIntent)
                    .build();
            startForeground(1111,notification);
            alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
            if (Build.VERSION.SDK_INT>=19){
                //API19以上使用
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,timeLong,pi);
            }else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,timeLong,pi);
            }
        }else{
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    private void cancelAlarm(){
        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(pi);
    }
    private class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            cancelAlarm();
            stopSelf();
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
