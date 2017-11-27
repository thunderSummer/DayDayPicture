package com.oureda.thunder.daydaypicture.manager;

import android.os.health.SystemHealthManager;

import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.SocketHandler;

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
    public boolean isAutoStart(){
    return     SharedPreferenceUtil.getInstance().getBoolean("autoStart",false);
    }
    public void setAutoStart(boolean value){
        SharedPreferenceUtil.getInstance().putBoolean("autoStart",value);
    }
    public void setAutoTime(String time){
        SharedPreferenceUtil.getInstance().putString("autoTime",time);
    }
    public String getAutoTime(){
        String result = SharedPreferenceUtil.getInstance().getString("autoTime");
        if(result.equals("")){
            result="自启动时间";
        }else{
            Date date=null;
            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            try {
                date =  ft.parse(result);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date!=null){
                if(date.getTime()< System.currentTimeMillis()){
                    result = "自启动时间";
                }
            }
        }
        return result;

    }
    public String getScreenMD5(){
        return SharedPreferenceUtil.getInstance().getString("screen_md5");
    }
    public void setScreenMD5(String md5){
        SharedPreferenceUtil.getInstance().putString("screen_md5",md5);
    }
    public int getPullTime(){
        return SharedPreferenceUtil.getInstance().getInt("screen_time",30);
    }
    public void setPullTime(int time){
        SharedPreferenceUtil.getInstance().putInt("screen_time",time);
    }
    public void setJOSNPosition(String position){
        SharedPreferenceUtil.getInstance().putString("json_position",position);
    }
    public String  getJSONPostion(){
        return SharedPreferenceUtil.getInstance().getString("json_position");
    }
}
