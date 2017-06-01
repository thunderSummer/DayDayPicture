package com.oureda.thunder.daydaypicture.imageSlide;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import com.oureda.thunder.daydaypicture.base.MyApplication;
import com.oureda.thunder.daydaypicture.base.PictureOrder;
import com.oureda.thunder.daydaypicture.base.PictureOrderSave;
import com.oureda.thunder.daydaypicture.manager.StringManager;
import com.oureda.thunder.daydaypicture.util.FileUtil;
import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by thunder on 17-5-24.
 */

public class BitmapFactory {
    private Paint bitmapPaint;
    private Bitmap bitmap;
    private int start;
    private int end;
    private int current;
    private List<Integer> integers;
    private List<Bitmap> bitmapList;
    File [] files;

    public BitmapFactory() {
        initBitmapList();

    }
    public void setBitmapList(){
        initBitmapList();
    }
    private void initBitmapList(){
        int i =0;
        if(integers==null){
            integers=new ArrayList<>();
        }else{
            integers.clear();
        }
        if(bitmapList==null){
            bitmapList=new ArrayList<>();
        }else{
            bitmapList.clear();
        }


        File file = new File(FileUtil.getPictureHome());

        files =file.listFiles();
        List<PictureOrderSave> pictureOrderSaves = DataSupport.findAll(PictureOrderSave.class);
        if(files==null){
            Log.d("ss", "initBitmapList: ");
            return;
        }
        for(File file1:files){

            if(file1.exists()){
                Log.d("ss", "initBitmapList: "+file1.getName()+files.length);
                if (pictureOrderSaves==null||pictureOrderSaves.size()==0){
                    Log.d("ss", "initBitmapList: ");
                    Bitmap bitmapOld = android.graphics.BitmapFactory.decodeFile(file1.getAbsolutePath());
                    int widthOld = bitmapOld.getWidth();
                    int heightOld = bitmapOld.getHeight();
                    int widthNew = MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
                    int heightNew =MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
                    float scaleWidth = ((float) widthNew) / widthOld;
                    float scaleHeight = ((float) heightNew) / heightOld;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    Bitmap newbm = Bitmap.createBitmap(bitmapOld, 0, 0, widthOld, heightOld, matrix,
                            true);
                    bitmapList.add(newbm);
                    integers.add(i);
                }else{
                    String name = file1.getName();
                    Log.d("ss", "initBitmapList: "+name);

                    try {
                        for(PictureOrderSave pictureOrderSave :pictureOrderSaves){
                            Log.d(TAG, "initBitmapList: "+pictureOrderSave.getOrder()+"  "+name.substring(0,name.lastIndexOf(".")));
                            if(String.valueOf(pictureOrderSave.getOrder()).equals(name.substring(0,name.lastIndexOf(".")))){
                                Bitmap bitmapOld = android.graphics.BitmapFactory.decodeFile(file1.getAbsolutePath());
                                int widthOld = bitmapOld.getWidth();
                                int heightOld = bitmapOld.getHeight();
                                int widthNew = MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
                                int heightNew =MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
                                float scaleWidth = ((float) widthNew) / widthOld;
                                float scaleHeight = ((float) heightNew) / heightOld;
                                Matrix matrix = new Matrix();
                                matrix.postScale(scaleWidth, scaleHeight);
                                Bitmap newbm = Bitmap.createBitmap(bitmapOld, 0, 0, widthOld, heightOld, matrix,
                                        true);
                                bitmapList.add(newbm);
                                integers.add(i);
                            }
                        }

                    }catch (Exception e){
                        e.printStackTrace();
                        continue;
                    }


                }
                i++;
                for(int j = 0;j <integers.size();j++){
                    Log.d(TAG, "initBitmapList: "+j);
                }
            }
        }
        SharedPreferenceUtil.getInstance().putInt(StringManager.END_PICTURE,bitmapList.size());
        start= 0;
        end=bitmapList.size();
        this.current=SharedPreferenceUtil.getInstance().getInt(StringManager.CURRENT_PICTURE,start);
    }
    public void addBitmap(String path){
        bitmapList.add(android.graphics.BitmapFactory.decodeFile(path));
        end++;
        SharedPreferenceUtil.getInstance().putInt(StringManager.END_PICTURE,end);
    }
    private void createBitmap(){
        if(bitmapList.size()>0)
        bitmap=bitmapList.get(current);

    }
    public void onDraw(Canvas canvas){
        createBitmap();
        Log.d("ss", "onDraw: "+bitmapList.size());
        canvas.drawBitmap(bitmap,0,0,bitmapPaint);
        SharedPreferenceUtil.getInstance().putInt(StringManager.CURRENT_PICTURE,current);
    }
    public void setCurrent(int current){
        Log.d("", "setCurrent: "+current);
        if(current>=bitmapList.size()){

            current=0;
        }
        this.current=current;
    }

    public void setStart(int start) {
        this.start = start;
        SharedPreferenceUtil.getInstance().putInt(StringManager.START_PICTURE,start);
    }

    public void setEnd(int end) {
        this.end = end;
        SharedPreferenceUtil.getInstance().putInt(StringManager.END_PICTURE,end);
    }

    public int getEnd() {
        return end;
    }

    public int getCurrent() {
        return current;
    }
    public String getCurrentId(){
        if(current>=bitmapList.size()){
            current=0;
        }
        String name = files[integers.get(current)].getName();
        Log.d("ss", "ssssssssss "+name.substring(0,name.lastIndexOf("."))+current);
        return name.substring(0,name.lastIndexOf("."));
        
    }
    /**
     * 获取虚拟按键高度
     *
     * @param context
     * @return
     */
    public static int getNavigationBarHeight(Context context) {
    int navigationBarHeight = 0;
    Resources rs = context.getResources();
    int id = rs.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar(context)) {
        navigationBarHeight = rs.getDimensionPixelSize(id);
    }
        return navigationBarHeight;
}

    /**
     * 判断有没有虚拟按键
     *
     * @param context
     * @return
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            Log.w("", e);
        }

        return hasNavigationBar;

    }

}
