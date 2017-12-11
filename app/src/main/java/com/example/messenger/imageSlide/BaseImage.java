package com.example.messenger.imageSlide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.util.Random;

import com.example.messenger.base.MyApplication;
import com.example.messenger.listener.ImageChangeListener;
import com.example.messenger.util.FileUtil;

import static android.content.ContentValues.TAG;

/**
 * Created by thunder on 17-5-24.
 */

public class BaseImage extends View {
    private final static int START_ANIMOTION=2;
    private ImageChangeListener imageChangeListener;
    private Random random;

    private BitmapFactory bitmapFactory;
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
    private int flag;
    private Camera camera;
    private Matrix matrix;
    private float centerX;
    private float centerY;
    private float extra;
    private int max=4;
    float scale =1;
    private boolean isNeedNext;


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
        init();



    }
    public void setBitmapFactory(){
        bitmapFactory.setBitmapList();
    }

    public BaseImage(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);

    }

    public BaseImage(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private static void fresh(){

    }

    @Override
    protected void onDraw(Canvas canvas) {
        // super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        if (new File(FileUtil.getPictureHome() + "/images/").listFiles() != null) {

            if (flag == 0) {
                drawRotateCanvas(canvas);

            } else if (flag == 1) {
                drawUpCanvas(canvas);
            } else if (flag == 2) {
                drawCanvas1(canvas);
            } else if (flag == 3) {
                drawScaleCanvas(canvas);
            }


        }
    }
    private void drawCanvas1(Canvas canvas){
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
            flag=random.nextInt(max);
            handler.sendMessageDelayed(message,bitmapFactory.getTime()*1000);
        }
        else if(count1>current1){
            Message message = new Message();
            message.what=START_ANIMOTION;
            handler.sendMessageDelayed(message,10);
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
//    public void setCurrent(int current){
//        this.current=current;
//        current1=0;
//        if(this.current<0||current>=bitmapFactory.getEnd()){
//            current=0;
//        }
//        handler.removeMessages(START_ANIMOTION);
//        bitmapFactory.setCurrent(current);
//        bitmapFactory.onDraw(currentCanvas);
//        postInvalidate();
//        imageChangeListener.onBitmapChange(Integer.parseInt(bitmapFactory.getCurrentId()));
//    }
    public int getCurrent() {
        return current;
    }

    public void setImageChangeListener(ImageChangeListener imageChangeListener) {
        this.imageChangeListener = imageChangeListener;
    }
    public void restart(){
        handler.removeMessages(START_ANIMOTION);
        init();
    }
    private void init(){
        currentBitmap=Bitmap.createBitmap(MyApplication.getContext().getResources().getDisplayMetrics().widthPixels,MyApplication.getContext().getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        nextBitmap=Bitmap.createBitmap(MyApplication.getContext().getResources().getDisplayMetrics().widthPixels,MyApplication.getContext().getResources().getDisplayMetrics().heightPixels, Bitmap.Config.ARGB_8888);
        currentCanvas=new Canvas(currentBitmap);
        nextCanvas = new Canvas(nextBitmap);
        bitmapFactory =new BitmapFactory();
        handler=new BaseImageHandle();
        current=0;
        start=0;
        random = new Random();
        camera = new Camera();
        centerX=currentBitmap.getWidth()/2;
        centerY =currentBitmap.getHeight()/2;
        matrix = new Matrix();
        extra=0;
        isNeedNext =true;
        if(new File(FileUtil.getPictureHome()+"/images/").listFiles()!=null){
            bitmapFactory.setCurrent(current);
            bitmapFactory.onDraw(currentCanvas);
            bitmapFactory.setCurrent(current+1);
            bitmapFactory.onDraw(nextCanvas);
            if(bitmapFactory!=null){
                Log.d(TAG, "BaseImage: not null");
            }
            postInvalidate();
        }
    }
    private void drawUpCanvas(Canvas canvas){
        canvas.drawBitmap(currentBitmap,0,0,new Paint());
        int height = canvas.getHeight()/count1;
        int width =canvas.getWidth();
        if(current1!=0){
            canvas.save();
            bitmapFactory.setCurrent(current+1);
            bitmapFactory.onDraw(nextCanvas);
            canvas.clipRect(0,0, width,height*current1);
            canvas.drawBitmap(nextBitmap,0,0,new Paint());
            canvas.restore();
        }

        if(current1==0){

            Message message = new Message();
            message.what=START_ANIMOTION;
            flag=random.nextInt(max);
            handler.sendMessageDelayed(message,bitmapFactory.getTime()*1000);
        }
        else if(count1>current1){
            Message message = new Message();
            message.what=START_ANIMOTION;
            handler.sendMessageDelayed(message,10);
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
    private void drawRotateCanvas(Canvas canvas){
        canvas.drawColor(Color.BLACK);
        matrix.reset();
        camera.save();
        float rotate = 90/count1;
        camera.rotateX(rotate*current1+extra);
        camera.getMatrix(matrix);
        camera.restore();
        float[] mValues = new float[9];
//        matrix.getValues(mValues);                //获取数值
//        mValues[6] = mValues[6]/scale;            //数值修正
//        mValues[7] = mValues[7]/scale;            //数值修正
//        matrix.setValues(mValues);                //重新赋值
        matrix.preTranslate(-this.centerX, -this.centerY);
        matrix.postTranslate(this.centerX, this.centerY);
        canvas.concat(matrix);
        if(current1==0){
            if(extra==0){
                Message message = new Message();
                message.what=START_ANIMOTION;
                flag=random.nextInt(max);
                handler.sendMessageDelayed(message,bitmapFactory.getTime()*1000);
            }else{
                Message message = new Message();
                message.what=START_ANIMOTION;
                handler.sendMessageDelayed(message,200);
            }

        } else if(current1<count1){
            Message message = new Message();
            message.what=START_ANIMOTION;
            handler.sendMessageDelayed(message,50);
        }else if(current1==count1){

            current1=0;

            if(extra==0){
                extra = 270;
                current++;
                if(current>=bitmapFactory.getEnd()||current<start){
                    current=start;
                }
                bitmapFactory.setCurrent(current);
                bitmapFactory.onDraw(currentCanvas);
            }else{
                extra = 0;
            }
            handler.removeMessages(START_ANIMOTION);
            postInvalidate();
        }
        canvas.drawBitmap(currentBitmap,0,0,new Paint());


    }
    private void drawScaleCanvas(Canvas canvas){
        canvas.save();
        canvas.translate(centerX,centerY);
        float scale = (float) (1.0/count1);
        if(isNeedNext){
            canvas.scale(1-scale*current1,1-scale*current1);
        }else{
            canvas.scale(scale*current1,scale*current1);
        }

        if(current1==0){
            if(isNeedNext){
                Message message = new Message();
                message.what=START_ANIMOTION;
                flag=random.nextInt(max);
                handler.sendMessageDelayed(message,bitmapFactory.getTime()*1000);
            }else{
                Message message = new Message();
                message.what=START_ANIMOTION;
                handler.sendMessageDelayed(message,200);
            }

        }else if(current1==count1){
            current1=0;

            if(isNeedNext){
                isNeedNext =false;
                current++;
                if(current>=bitmapFactory.getEnd()||current<start){
                    current=start;
                }
                bitmapFactory.setCurrent(current);
                bitmapFactory.onDraw(currentCanvas);
            }else{
                isNeedNext=true;
            }
            handler.removeMessages(START_ANIMOTION);
            postInvalidate();
        }else if(current1<count1){
            Message message = new Message();
            message.what=START_ANIMOTION;
            handler.sendMessageDelayed(message,50);
        }
        canvas.drawBitmap(currentBitmap,-centerX,-centerY,new Paint());
    }
}
