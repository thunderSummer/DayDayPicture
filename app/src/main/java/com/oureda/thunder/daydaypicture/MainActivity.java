package com.oureda.thunder.daydaypicture;

import android.app.VoiceInteractor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.oureda.thunder.daydaypicture.base.Picture;
import com.oureda.thunder.daydaypicture.base.PictureData;
import com.oureda.thunder.daydaypicture.base.PictureOrder;
import com.oureda.thunder.daydaypicture.base.PictureOrderSave;
import com.oureda.thunder.daydaypicture.imageSlide.BaseImage;
import com.oureda.thunder.daydaypicture.listener.ImageChangeListener;
import com.oureda.thunder.daydaypicture.manager.CacheManager;
import com.oureda.thunder.daydaypicture.manager.StringManager;
import com.oureda.thunder.daydaypicture.util.FileUtil;
import com.oureda.thunder.daydaypicture.util.HttpUtils;
import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private FrameLayout frameLayout;
    private List<Bitmap> bitmapList;
    private BaseImage baseImage;
    private Button addTimeButton;
    private Button subTimeButton;
    private Button addPictureButton;
    private Button subPictureButton;
    private Button startAnamotion;
    private Socket socket;
    private List<Integer> pictureInt;
    private MainHandler mainHandler;
    private final static int ON_READY=0;
    private final static int NEXT_PICTURE=1;
    private final static int PREVIOUS_PICTURE=2;
    private final static int PICTURE_ORDER=3;
    private final static int GET_PICTURE_NOW=4;
    private final static int LEAVE=5;
    private final static int CHANGE_PICTURE=6;
    private final static int ON_RESUME=7;
    private PictureData pictureData;
    private List<PictureData.Data.Picture> pictures;
    private List<Picture> pictureList;
    private Button button;
    private ImageListener imageListener;
    private class ImageListener implements ImageChangeListener{

        @Override
        public void onBitmapChange(final int currentId) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("picture_id",currentId);
                        jsonObject.put("email",CacheManager.getInstance().getAccount());
                        jsonObject.put("screen_id",SharedPreferenceUtil.getInstance().getInt("screen_id",0));
                        Log.d("ssssssssssss", "run: "+jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    socket.emit("changePicture",jsonObject);
                }
            }).start();

        }
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferenceUtil.getInstance().putInt("virtualHeight",getHasVirtualKey());
        View decorView = getWindow().getDecorView();
        int option = View.SYSTEM_UI_FLAG_FULLSCREEN|View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(option);
        final ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        mainHandler=new MainHandler(this);
        final Picture picture2 = new Picture();
        getURl();



//        button= (Button) findViewById(R.id.button);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                button.setText(baseImage.getCurrentId());
//            }
//        });
//        picture2.setUrl("http://www.baidu.com/img/bd_logo1.png");
//        picture2.save();
//        Picture picture3 = new Picture();
//        picture3.setUrl("http://118.89.197.156:8000/picture-1495890128848.png");
//        picture3.save();
//        Picture picture4 = new Picture();
//        picture4.setUrl("http://118.89.197.156:8000/picture-1495890128848.png");
//        picture4.save();



//        frameLayout= (FrameLayout) findViewById(R.id.container);
//        bitmapList=new ArrayList<>();
//        initBitmapList();
//        baseImage=new BaseImage(this,bitmapList);
//        addTimeButton= (Button) findViewById(R.id.add_time);
//        subTimeButton= (Button) findViewById(R.id.sub_time);
//        addTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                baseImage.setTime(baseImage.getTime()+100);

//            }
//        });
//        subTimeButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                baseImage.setTime(baseImage.getTime()-100);
//            }
//        });
//        addPictureButton= (Button) findViewById(R.id.add_picture);
//        subPictureButton= (Button) findViewById(R.id.sub_picture);
//        addPictureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bitmapList.add(BitmapFactory.decodeResource(getResources(),R.drawable.oneplus2));
//            }
//        });
//        subPictureButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bitmapList.remove(bitmapList.size()-1);
//            }
//        });
//        startAnamotion= (Button) findViewById(R.id.star_Animation);
//        startAnamotion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                baseImage.startPlay();
//            }
//        });
//
//        frameLayout.addView(baseImage);
////        baseImage.changePicture();
//
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = IO.socket("http://118.89.197.156:3000");
                    socket.on("setTime", new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            Log.d("setTime", "call: "+args[0]);
                        }

                    }).on("getPictureNow", new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {
                            Log.d("getPictureNow", "call: "+args[0]);
//                            JSONObject jsonObject1= (JSONObject) args[0];
//                            Log.d("ss", "call: "+jsonObject1);
                            //接受到消息之后，emit一个图片编号
                            //email picture_id screen_id 发送
                            Message message = new Message();
                            message.what=GET_PICTURE_NOW;
                            mainHandler.sendMessage(message);
                        }

                    }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

                        @Override
                        public void call(Object... args) {}

                    }).on("nextPicture", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d("run", "call: ");
                            baseImage.setCurrent(baseImage.getCurrent()+1);
                            //更换下一张图片
                        }
                    }).on("previousPicture", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            Log.d("run", "call: ");
                            baseImage.setCurrent(baseImage.getCurrent()-1);
                            //更换上一张图片
                        }
                    }).on("pictureOrder", new Emitter.Listener() {
                        @Override
                        public void call(Object... args) {
                            String content = args[0].toString();
                            DataSupport.deleteAll(PictureOrderSave.class);
                            pictureInt=new Gson().fromJson(content, PictureOrder.class).picture;
                            for(Integer integer :pictureInt){
                                PictureOrderSave pictureOrderSave =new PictureOrderSave(integer);
                                pictureOrderSave.save();
                                Log.d("ssssss", "call: "+pictureOrderSave.getOrder());
                            }
                            Message message1 = new Message();
                            message1.what=PICTURE_ORDER;
                            mainHandler.sendMessage(message1);
                        }
                    });
                    socket.connect();
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("screen_id", CacheManager.getInstance().getScreenId());
                        jsonObject.put("id",1);
                        jsonObject.put("email",CacheManager.getInstance().getAccount());
                        socket.emit("join",jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
//    private void initBitmapList(){
//        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(),R.drawable.oneplus1);
//        bitmapList.add(bitmap3);
//        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(),R.drawable.oneplus2);
//        bitmapList.add(bitmap2);
//        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.drawable.google);
//        bitmapList.add(bitmap1);
//    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus&& Build.VERSION.SDK_INT>=19){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    |View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    |View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }
    public static class MainHandler extends Handler{
        private final WeakReference<MainActivity> mActivity;
        public MainHandler(MainActivity activity) {
            mActivity = new WeakReference<MainActivity>(activity);
        }
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final MainActivity mainActivity = mActivity.get();
            switch (msg.what){
                case PICTURE_ORDER:
                    mainActivity.getURl();
                    break;
                case ON_READY:
                    mainActivity.pictureList= DataSupport.findAll(Picture.class);
                    if(mainActivity.frameLayout!=null)
                    mainActivity.frameLayout.setVisibility(View.GONE);
                                for(final Picture picture:mainActivity.pictureList) {
                                    HttpUtils.getAsny(picture.getUrl(), new okhttp3.Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {

                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            InputStream is = response.body().byteStream();
                                                 Log.d("ss", "onResponse: "+response.body().contentLength());
                                            File file = FileUtil.getPictureFile(picture.getPictureId() + picture.getUrl().substring(picture.getUrl().lastIndexOf(".")));
                                            if (file != null) {
                                                FileOutputStream fos = new FileOutputStream(file);
                                                byte b[] = new byte[1024];
                                                int len;
                                                while ((len = is.read(b)) != -1) {
                                                    fos.write(b, 0, len);
                                                }
                                                fos.close();
                                            }
                                            if (mainActivity.pictureList.get(mainActivity.pictureList.size() - 1).getPictureId() .equals(picture.getPictureId()) ) {
                                                Message message = new Message();
                                                message.what = ON_RESUME;
                                                mainActivity.mainHandler.sendMessageDelayed(message, 3000);
                                                           Log.d("sssssss", "onResponse: "+picture.getUrl());
                                            }

                                        }
//
                                    });
                                }
                    break;
                case ON_RESUME:
                    if(mainActivity.baseImage==null){
                        mainActivity.initBaseImage();
                        mainActivity.frameLayout.setVisibility(View.VISIBLE);
                    }else{
                        mainActivity.frameLayout.setVisibility(View.VISIBLE);
                        mainActivity.baseImage.setBitmapFactory();
                    }


                    break;

                case GET_PICTURE_NOW:
                    if(mainActivity!=null){
                        JSONObject jsonObject = new JSONObject();
                        if(mainActivity.baseImage==null){

//                            try {
//                                jsonObject.put("email",CacheManager.getInstance().getAccount());
//                                jsonObject.put("screen_id",SharedPreferenceUtil.getInstance().getInt("screen_id",0));
//                                jsonObject.put("picture_id",Integer.parseInt(mainActivity.baseImage.getCurrentId()));
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
                            return;

                        }

                        try {
                            jsonObject.put("email",CacheManager.getInstance().getAccount());
                            jsonObject.put("picture_id",Integer.parseInt(mainActivity.baseImage.getCurrentId()));
                            jsonObject.put("screen_id",SharedPreferenceUtil.getInstance().getInt("screen_id",0));
                            mainActivity.socket.emit("sendPictureNow",jsonObject);
                            Log.d("ss", "handleMessage: "+jsonObject);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case CHANGE_PICTURE:
                    if(mainActivity.baseImage==null){
                        return;
                    }
                    if(mainActivity!=null){

                    }
                    break;
                case LEAVE:
                    if(mainActivity!=null){
                        JSONObject jsonObject = new JSONObject();

                        try {
                            jsonObject.put("email",SharedPreferenceUtil.getInstance().getString("username"));
                            jsonObject.put("screen_id",SharedPreferenceUtil.getInstance().getInt("screen_id",0));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mainActivity.finish();
                    }
                    break;
                case NEXT_PICTURE:
                    if(mainActivity!=null){

                    }
                    break;
                case PREVIOUS_PICTURE:
                    if(mainActivity!=null){
                        mainActivity.getURl();

                    }
                    break;
            }
        }
    }
    private void initBaseImage(){
        imageListener=new ImageListener();
        frameLayout= (FrameLayout) findViewById(R.id.contain);
        baseImage=new BaseImage(MainActivity.this);
        frameLayout.addView(baseImage);
        baseImage.setImageChangeListener(imageListener);
    }
    private int getHasVirtualKey() {
        int dpi = 0;
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        @SuppressWarnings("rawtypes")
        Class c;
        try {
            c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            dpi = dm.heightPixels;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dpi;
    }
    private void getURl(){
        Log.d("ss", "onResponse: ");
        DataSupport.deleteAll(Picture.class);
        HttpUtils.postAsynWithCookie(StringManager.URL + "getScreenPicture", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content =response.body().string();
                Log.d("ss", "onResponse: "+content);
                try{
                    pictureData=new Gson().fromJson(content,PictureData.class);
                    pictures = pictureData.data.picture;
//                    Picture picture2 = new Picture();
//                    picture2.setUrl("http://118.89.197.156:8000/picture-1495454545405.jpg");
//                    picture2.save();
//                    Picture picture3 = new Picture();
//                    picture3.setUrl("http://118.89.197.156:8000/picture-1495454618289.jpg");
//                    picture3.save();

                    for(PictureData.Data.Picture picture:pictures){
                        Picture picture1 = new Picture();
                        picture1.setPictureId(picture.getPicture_id());
                        picture1.setUrl(picture.getUrl());
                        picture1.save();
                    }
                    Message message = new Message();
                    message.what=ON_READY;
                    mainHandler.sendMessage(message);
                }catch (Exception e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this,"数据获取失败",Toast.LENGTH_LONG);
                        }
                    });
                }


            }
        },new HttpUtils.Param("id",1),new HttpUtils.Param("screen_id",CacheManager.getInstance().getScreenId()));
        final Message message = new Message();
        message.what=ON_READY;
        mainHandler.sendMessage(message);

    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Message message = new Message();
        message.what=LEAVE;
        mainHandler.sendMessage(message);
    }
    //changePicture email picture_id screen_id
    // leave email screen_id
}

