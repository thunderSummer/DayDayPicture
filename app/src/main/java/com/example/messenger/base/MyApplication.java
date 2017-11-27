package com.oureda.thunder.daydaypicture.base;

import android.app.Application;
import android.content.Context;

import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import org.litepal.LitePal;

/**
 * Created by thunder on 17-5-24.
 */

public class MyApplication extends Application {
    private static Context context;
    protected void initPrefs() {
        SharedPreferenceUtil.init(context, getPackageName() + "_preference");
    }

    public static Context getContext() {
        return context;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
        initPrefs();
        LitePal.initialize(this);
    }
}
