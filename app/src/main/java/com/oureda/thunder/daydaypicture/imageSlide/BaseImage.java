package com.oureda.thunder.daydaypicture.imageSlide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.List;

import com.oureda.thunder.daydaypicture.base.MyApplication;
import com.oureda.thunder.daydaypicture.listener.ImageChangeListener;
import com.oureda.thunder.daydaypicture.util.FileUtil;

import static android.content.ContentValues.TAG;

/**
 * Created by thunder on 17-5-24.
 */

public class BaseImage extends View {
    private final static int START_PLAY=1;
    private final static int FINISH_PLAY=0;
    private final static int START_ANIMOTION=2;
    private ImageChangeListener imageChangeListener;

    private BitmapFactory bitmapFactory;
    private List<Bitmap> bitmapList;
    private Canvas currentCanvas;
    private Bitmap currentBitmap;
    private Bitmap nextBitmap;
    private Canvas nextCanvas;
    private int current;
    private BaseImageHandle handler;
    private int start;
    private int count1=10;
    private int current1=0;

    private long time=10000;
    private List<Long> timeList;

    public void setTimeList(List<Long> timeList) {
        this.timeList = timeList;
    }

    public void setTime(long time){
        this.time=time;
    }

    public long getTime() {
        return time;
    }
    public String getCurrentId(){
        return bitmapFactory.getCurrentId();
    }
    public  class BaseImageHandle extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
//                case START_PLAY:
//                    if(flag!=START_ANIMOTION){
//                        postInvalidate();
//                        if (current == end) {
//                            current = start;
//                        }
//                        end = bitmapList.size();
//                        bitmapFactory.setCurrent(current);
//                        bitmapFactory.onDraw(currentCanvas);
//                    }
//                    if (timeList == null)
//                        sendEmptyMessageDelayed(1, time);
//                    else {
//                        sendEmptyMessageDelayed(1, timeList.get(current));
//                    }
//                    break;
                case START_ANIMOTION:
                    current1++;
                    if(current1>count1){
                        current1=0;
                }postInvalidate();

                    break;
                case 3:
                    postInvalidate();
                    break;
            }
        }
    }

    public BaseImage(Context context) {
        super(context);
        currentBitmap=Bitmap.createBitmap(MyApplication.getContext().getResources().getDisplayMetrics().widthPixels,MyApplication.getContext().getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        nextBitmap=Bitmap.createBitmap(MyApplication.getContext().getResources().getDisplayMetrics().widthPixels,MyApplication.getContext().getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        currentCanvas=new Canvas(currentBitmap);
        nextCanvas = new Canvas(nextBitmap);
        bitmapFactory =new BitmapFactory();
        handler=new BaseImageHandle();
        current=0;
        start=0;
        if(new File(FileUtil.getPictureHome()).listFiles()!=null){
            bitmapFactory.setCurrent(current);
            bitmapFactory.onDraw(currentCanvas);
            bitmapFactory.setCurrent(current+1);
            bitmapFactory.onDraw(nextCanvas);
            postInvalidate();
        }



    }
    public void setBitmapFactory(){
        bitmapFactory.setBitmapList();
    }

    public BaseImage(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

    }

    public BaseImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private static void fresh(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(new File(FileUtil.getPictureHome()).listFiles()!=null){
            drawCanvas1(canvas,1);
        }



    }
    private void drawCanvas1(Canvas canvas,int id){
        canvas.drawBitmap(currentBitmap,0,0,new Paint());
        int height = canvas.getHeight();
        int width =canvas.getWidth()/count1;
        if(current1!=0){
            canvas.save();
            bitmapFactory.setCurrent(current+1);
            bitmapFactory.onDraw(nextCanvas);
            canvas.clipRect(0,0, width*current1,height);
            canvas.drawBitmap(nextBitmap,0,0,new Paint());
            canvas.restore();
        }

        if(current1==0){
            Message message = new Message();
            message.what=START_ANIMOTION;
            handler.sendMessageDelayed(message,time);
            imageChangeListener.onBitmapChange(Integer.parseInt(bitmapFactory.getCurrentId()));
            Log.d(TAG, "drawCanvas1:run "+bitmapFactory.getCurrentId());
        }
        else if(count1>current1){
            Message message = new Message();
            message.what=START_ANIMOTION;
            handler.sendMessageDelayed(message,50);
        }else if(count1<=current1){
            current++;
            if(current>=bitmapFactory.getEnd()||current<start){
                current=start;
            }
            current1=0;
            bitmapFactory.setCurrent(current);
            bitmapFactory.onDraw(currentCanvas);

            handler.removeMessages(START_ANIMOTION);
            postInvalidate();
        }

    }
    public synchronized void startPlay(){
        Message message = new Message();
        message.what=START_PLAY;
        handler.sendMessage(message);
    }
    public synchronized void finishPlay(){

    }
    public void setCurrent(int current){
        this.current=current;
        current1=0;
        if(this.current<0||current>=bitmapFactory.getEnd()){
            current=0;
        }
        handler.removeMessages(START_ANIMOTION);
        bitmapFactory.setCurrent(current);
        bitmapFactory.onDraw(currentCanvas);
        Log.d(TAG, "setCurrent: "+"run");
        postInvalidate();
        imageChangeListener.onBitmapChange(Integer.parseInt(bitmapFactory.getCurrentId()));
    }
    public int getCurrent() {
        return current;
    }

    public void setImageChangeListener(ImageChangeListener imageChangeListener) {
        this.imageChangeListener = imageChangeListener;
    }
}
