package com.example.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.messenger.manager.CacheManager;

public class AutoStartReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(ACTION)&& CacheManager.getInstance().isAutoStart()) {
            Intent mainActivityIntent = new Intent(context, HelloActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("auto start testing", "onReceive: I will be start");
            context.startActivity(mainActivityIntent);
        }
    }
}
