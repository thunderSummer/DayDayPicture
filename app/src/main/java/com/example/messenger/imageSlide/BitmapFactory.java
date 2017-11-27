package com.oureda.thunder.daydaypicture.imageSlide;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

import com.oureda.thunder.daydaypicture.base.MyApplication;
import com.oureda.thunder.daydaypicture.manager.StringManager;
import com.oureda.thunder.daydaypicture.util.FileUtil;
import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import java.io.File;
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
    private List<Integer> timeOrder;
    File [] files;

    public BitmapFactory() {
        initBitmapList();

    }
    public void setBitmapList(){
        initBitmapList();
    }
    private void initBitmapList(){
        bitmapPaint=new Paint();
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
        }if(timeOrder==null){
            timeOrder=new ArrayList<>();
        }else{
            timeOrder.clear();
        }

        File file = new File(FileUtil.getPictureHome()+"/images/");

        files =file.listFiles();
        if(files==null){
            return;
        }
        for(File file1:files){

            if(file1.exists()){
                Bitmap bitmapOld = android.graphics.BitmapFactory.decodeFile(file1.getAbsolutePath());
                Bitmap bitmapBase=Bitmap.createBitmap(MyApplication.getContext().getResources().getDisplayMetrics().widthPixels,MyApplication.getContext().getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapBase);
                canvas.drawColor(Color.BLACK);


                    int widthOld = bitmapOld.getWidth();
                    int heightOld = bitmapOld.getHeight();
                    int widthNew = MyApplication.getContext().getResources().getDisplayMetrics().widthPixels;
                    int heightNew =MyApplication.getContext().getResources().getDisplayMetrics().heightPixels;
                    float scaleWidth = ((float) widthNew) / widthOld;
                    float scaleHeight = ((float) heightNew) / heightOld;
                    Matrix matrix = new Matrix();

                    float min = scaleHeight>scaleHeight?scaleHeight:scaleWidth;
                    matrix.postScale(min, min);
                    Bitmap newbm = Bitmap.createBitmap(bitmapOld, 0, 0, widthOld, heightOld, matrix, true);
                    canvas.drawBitmap(newbm,(widthNew-widthOld*min)/2,(heightNew-heightOld*min)/2,new Paint());
                    bitmapList.add(bitmapBase);
                    timeOrder.add(SharedPreferenceUtil.getInstance().getInt(file1.getName(),0));

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
        canvas.drawBitmap(bitmap,0,0,bitmapPaint);
        SharedPreferenceUtil.getInstance().putInt(StringManager.CURRENT_PICTURE,current);
    }
    public void setCurrent(int current){
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
    public int getTime(){
        return timeOrder.get(current);
    }

    public int getEnd() {
        return end;
    }

    public int getCurrent() {
        return current;
    }
    public String getCurrentId() {
        if (current >= bitmapList.size()) {
            current = 0;
        }
        String name = files[integers.get(current)].getName();
        return name.substring(0, name.lastIndexOf("."));
    }



}
