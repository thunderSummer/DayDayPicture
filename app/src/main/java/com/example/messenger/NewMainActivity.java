package com.oureda.thunder.daydaypicture;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.oureda.thunder.daydaypicture.imageSlide.BaseImage;
import com.oureda.thunder.daydaypicture.listener.DownloadListener;
import com.oureda.thunder.daydaypicture.service.PollingService;
import com.oureda.thunder.daydaypicture.util.PollingUtils;

public class NewMainActivity extends AppCompatActivity {
    private BaseImage baseImage;
    private FrameLayout frameLayout;
    private BroadcastReceiver broadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        broadcastReceiver =new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.oureda.thunder.daydaypicture.RECEIVER");
        registerReceiver(broadcastReceiver, intentFilter);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_main);
        baseImage = new BaseImage(NewMainActivity.this);
        frameLayout= (FrameLayout) findViewById(R.id.container);
        frameLayout.addView(baseImage);
        PollingUtils.startPollingService(this, 10, PollingService.class, PollingService.ACTION);




    }
    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //更新UI
            boolean  needUpdate = intent.getBooleanExtra("needUpdate",true);
            Log.d("ss", "onReceive: sssss");
            Toast.makeText(NewMainActivity.this,"正在更新资源包",Toast.LENGTH_LONG).show();
            baseImage.restart();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PollingUtils.stopPollingService(this, PollingUtils.class, PollingService.ACTION);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus&& Build.VERSION.SDK_INT>=19){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
