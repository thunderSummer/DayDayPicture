package com.oureda.thunder.daydaypicture.manager;

import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

/**
 * Created by thunder on 17-5-26.
 */

public class CacheManager {
    private static CacheManager manager;

    private static String _getCookie()
    {
        return SharedPreferenceUtil.getInstance().getString("cookie", "");
    }

    private static void _saveCookie(String paramString)
    {
        SharedPreferenceUtil.getInstance().putString("cookie", paramString);
    }

    public static CacheManager getInstance()
    {
        if (manager == null)
        {
            manager = new CacheManager();
        }
        return manager;
    }
    public String getCookie(){
        return manager._getCookie();
    }
    public void saveCookie(String cookie){
        manager._saveCookie(cookie);
    }
    private static int _getScreenId(){
        return SharedPreferenceUtil.getInstance().getInt("screen_id",0);
    }
    private static void _saveScreenId(int screenId){
        SharedPreferenceUtil.getInstance().putInt("screen_id",screenId);
    }
    public  int getScreenId(){
        return manager._getScreenId();
    }
    public void saveScreenId(int screenId){
        manager._saveScreenId(screenId);
    }
    private String _getAccount(){
        return SharedPreferenceUtil.getInstance().getString("account");
    }
    private void _saveAccount(String account){
        SharedPreferenceUtil.getInstance().putString("account",account);
    }
    public  String getAccount(){
        return manager._getAccount();
    }
    public  void saveAccount(String account){
        manager._saveAccount(account);
    }

}
