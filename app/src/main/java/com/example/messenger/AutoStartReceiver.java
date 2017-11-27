package com.oureda.thunder.daydaypicture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;

import com.oureda.thunder.daydaypicture.manager.CacheManager;

public class AutoStartReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(ACTION)&& CacheManager.getInstance().isAutoStart()) {
            Intent mainActivityIntent = new Intent(context, HelloActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("ss", "onReceive: sssssss");
            context.startActivity(mainActivityIntent);
        }
    }
}
