package com.oureda.thunder.daydaypicture.util;

import android.content.Context;
import android.content.SharedPreferences;


public class SharedPreferenceUtil {
    private static SharedPreferenceUtil prefsUtil;
    public Context context;
    public SharedPreferences.Editor editor;
    public SharedPreferences prefs;

    public static SharedPreferenceUtil getInstance()
    {
        if(prefsUtil==null){
            prefsUtil=new SharedPreferenceUtil();
        }
        return prefsUtil;
    }

    public static void init(Context paramContext, String paramString)
    {
        prefsUtil = new SharedPreferenceUtil();
        prefsUtil.context = paramContext;
        prefsUtil.prefs = prefsUtil.context.getSharedPreferences(paramString, 0);
        prefsUtil.editor = prefsUtil.prefs.edit();
    }

    public boolean getBoolean(String paramString)
    {
        return this.prefs.getBoolean(paramString, false);
    }

    public boolean getBoolean(String paramString, boolean paramBoolean)
    {
        return this.prefs.getBoolean(paramString, paramBoolean);
    }

    public int getInt(String paramString, int paramInt)
    {
        return this.prefs.getInt(paramString, paramInt);
    }

    public String getString(String paramString)
    {
        return this.prefs.getString(paramString, "");
    }

    public String getString(String paramString1, String paramString2)
    {
        return this.prefs.getString(paramString1, paramString2);
    }

    public void putBoolean(String paramString,boolean value)
    {
        this.editor.putBoolean(paramString, value);
        this.editor.apply();
    }

    public SharedPreferenceUtil putInt(String paramString, int paramInt)
    {
        this.editor.putInt(paramString, paramInt);
        this.editor.commit();
        return this;
    }

    public SharedPreferenceUtil putString(String paramString1, String paramString2)
    {
        this.editor.putString(paramString1, paramString2);
        this.editor.commit();
        return this;
    }

    public SharedPreferenceUtil remove(String paramString)
    {
        this.editor.remove(paramString);
        this.editor.commit();
        return this;
    }

    public SharedPreferenceUtil removeAll()
    {
        this.editor.clear();
        this.editor.commit();
        return this;
    }
}
