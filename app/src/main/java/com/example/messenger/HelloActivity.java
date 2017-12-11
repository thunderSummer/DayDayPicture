package com.example.messenger;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.example.messenger.base.Installation;
import com.example.messenger.base.PictureControl;
import com.example.messenger.base.PictureData;
import com.example.messenger.base.ScreenInfo;
import com.example.messenger.imageSlide.RoundButton;
import com.example.messenger.imageSlide.RoundProgressBarWidthNumber;
import com.example.messenger.listener.DownloadListener;
import com.example.messenger.manager.CacheManager;
import com.example.messenger.manager.StringManager;
import com.example.messenger.service.DownLoadService;
import com.example.messenger.service.UpdateService;
import com.example.messenger.util.FileUtil;
import com.example.messenger.util.HttpUtils;
import com.example.messenger.util.SharedPreferenceUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class HelloActivity extends AppCompatActivity {
    private static String TAG = "HelloActivity------>";
    private BroadcastReceiver broadcastReceiver;
    private TextView UUID;
    private RoundProgressBarWidthNumber roundProgressBarWidthNumber;
    private String UUIDString;
    private DownLoadService downLoadService;
    private DownLoadService.DownloadBinder downloadBinder;
    private Switch aSwitch;
    private TextView autoTime;
    private TextView update;
    private TextView enter;
    private TextView about;
    private PackageManager packageManager;
    private PackageInfo packageInfo;

    private DrawerLayout drawerLayout;
    String jsonUrl;
    private boolean busy = true;

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            downloadBinder = (DownLoadService.DownloadBinder) service;
            downLoadService = downloadBinder.getService();

            downLoadService.setDownloadListener(new DownloadListener() {
                @Override
                public void onProgress(int... progress) {
                    roundProgressBarWidthNumber.setTipTitle("正在获取资源文件");
                    for (Integer integer : progress) {
                        Log.d(TAG, "onProgress: " + integer);
                    }
                    roundProgressBarWidthNumber.setAllCount(progress[1]);
                    roundProgressBarWidthNumber.setCurrent(progress[0]);
                    roundProgressBarWidthNumber.setProgress(100 * progress[0] / progress[1]);
                }

                @Override
                public void onSuccess(int type) {
                    roundProgressBarWidthNumber.setVisibility(View.INVISIBLE);
                    String content;
                    if (type == 1) {
                        content = "获取资源目录成功";
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    downloadPicture();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    } else {
                        content = "获取资源文件成功";
                        startActivity(new Intent(HelloActivity.this, NewMainActivity.class));
                    }
                    Toast.makeText(HelloActivity.this, content, Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailed() {
                    Toast.makeText(HelloActivity.this, "资源文件获取失败，请重试", Toast.LENGTH_LONG).show();
                    roundProgressBarWidthNumber.setVisibility(View.INVISIBLE);
                }

                @Override
                public void after() {
                    roundProgressBarWidthNumber.setVisibility(View.INVISIBLE);
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        String id = Installation.id(HelloActivity.this);
        init();

        UUID.setText(id.substring(0, 8));
        SharedPreferenceUtil.getInstance().putString("uuid", UUID.getText().toString());
        UUIDString = SharedPreferenceUtil.getInstance().getString("uuid");
        Intent bindIntent = new Intent(HelloActivity.this, DownLoadService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
        broadcastReceiver = new HelloBroadcastReceive();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(broadcastReceiver, intentFilter);
        try {
            packageManager = getPackageManager();
            packageInfo = packageManager.getPackageInfo(this.getPackageName(),0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


//      new Thread(new Runnable() {
//            @Override
//            public void run() {
//                    verifyScreen();
//            }
//        }).start();


    }

    private void createScreen() {
        try {
            Response response = HttpUtils.post(StringManager.URL + "create_screen", new HttpUtils.Param("uuid", UUIDString));
            Log.d(TAG, "createScreen: response:" + response.body().string());
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HelloActivity.this, "正在创建屏幕", Toast.LENGTH_SHORT).show();
                }
            });

        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HelloActivity.this, "无法连接到服务器，请检查网络", Toast.LENGTH_LONG).show();
                }
            });
            e.printStackTrace();
        }
    }

    private void verifyScreen() {
        try {
            ScreenInfo screenInfo;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HelloActivity.this, "正在验证屏幕", Toast.LENGTH_SHORT).show();
                }
            });
            Response response = HttpUtils.post(StringManager.URL + "verify_screen", new HttpUtils.Param("uuid", UUIDString));
            String content = response.body().string();
            Log.d(TAG, "verifyScreen: response" + content);
            busy = false;
            Gson gson = new Gson();
            screenInfo = gson.fromJson(content, ScreenInfo.class);
            if (screenInfo != null) {
                final int status = screenInfo.data.getIs_user();
                if (status == 1) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            requestAll();
                        }
                    }).start();

                } else {
                    if (status == 2) {
                        createScreen();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HelloActivity.this, "该屏幕尚未被绑定", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                }
            }
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(HelloActivity.this, "无法连接到服务器，请检查网络", Toast.LENGTH_LONG).show();

                }
            });
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        unbindService(connection);
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                drawerLayout.openDrawer(Gravity.START);
                break;
        }
        return true;
    }

    private void requestAll() {
        HttpUtils.postAsyn(StringManager.URL + "all", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String content = response.body().string();
                ScreenInfo screenInfo;
                Gson gson = new Gson();
                Log.d(TAG, "requestAll: onResponse: " + content);
                screenInfo = gson.fromJson(content, ScreenInfo.class);
                CacheManager.getInstance().setPullTime(Integer.parseInt(screenInfo.data.getTime()));
                CacheManager.getInstance().setScreenMD5(screenInfo.data.getMd5());
                Log.d(TAG, "onResponse: " + CacheManager.getInstance().getScreenMD5());
                jsonUrl = screenInfo.data.getJson_url();
                if (screenInfo.data.getJson_url() == null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            roundProgressBarWidthNumber.setTipTitle("无资源包，程序即将退出");
                            roundProgressBarWidthNumber.setProgress(100);
                            roundProgressBarWidthNumber.setCancel(true);
                            roundProgressBarWidthNumber.setVisibility(View.VISIBLE);
                            CountDownTimer countDownTimer = new CountDownTimer(3000, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                }

                                @Override
                                public void onFinish() {
                                    finish();
                                }
                            };
                            countDownTimer.start();

                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            List<String> list = new ArrayList<>();
                            list.add(jsonUrl);
                            downLoadService.setPause(false);
                            downLoadService.setDownloadURLS(list);
                            downloadBinder.startDownload();
                            roundProgressBarWidthNumber.setVisibility(View.VISIBLE);


                        }
                    });
                }


            }
        }, new HttpUtils.Param("uuid", UUIDString), new HttpUtils.Param("id", 1));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roundProgressBarWidthNumber.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(1234);
        autoTime.setText(CacheManager.getInstance().getAutoTime());

    }

    private void init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_hello);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_hello);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        autoTime = (TextView) findViewById(R.id.auto_time_hello);
        autoTime.setText(CacheManager.getInstance().getAutoTime());
        autoTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HelloActivity.this, ChooseTimeActivity.class));
                drawerLayout.closeDrawer(Gravity.START);
            }
        });
        aSwitch = (Switch) findViewById(R.id.switch_hello);
        aSwitch.setChecked(CacheManager.getInstance().isAutoStart());
        aSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (aSwitch.isChecked()) {
                    CacheManager.getInstance().setAutoStart(true);
                    Toast.makeText(HelloActivity.this, "已设置开机自动启动", Toast.LENGTH_SHORT).show();
                } else {
                    CacheManager.getInstance().setAutoStart(false);
                    Toast.makeText(HelloActivity.this, "已取消开机自动启动", Toast.LENGTH_SHORT).show();
                }
            }
        });
        UUID = (TextView) findViewById(R.id.id_hello);
        roundProgressBarWidthNumber = (RoundProgressBarWidthNumber) findViewById(R.id.id_progress);
        enter = (TextView) findViewById(R.id.enter_hello);

        enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        verifyScreen();
                    }
                }).start();
            }
        });
        about = (TextView) findViewById(R.id.about_hello);
        update = (TextView) findViewById(R.id.update_hello);
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(HelloActivity.this, "当前版本：V" +packageInfo.versionName, Toast.LENGTH_SHORT).show();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkUpdate();
            }
        });
    }

    private void downloadPicture() throws Exception {
        FileInputStream fileInputStream = new FileInputStream(FileUtil.getPictureHome() + jsonUrl.substring(jsonUrl.lastIndexOf("/")));
        byte[] bytes = new byte[1024];
        int b = 0;
        StringBuilder result = new StringBuilder();
        try {
            while ((b = fileInputStream.read(bytes)) > 0) {
                result.append(new String(bytes, 0, b));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "after: " + result);
        PictureData pictureData;
        pictureData = new Gson().fromJson(result.toString(), PictureData.class);
        List<String> pictureList = new ArrayList<>();
        Map<String, String> originMap = new HashMap<>();
        for (PictureData.Picture list : pictureData.picture) {
            pictureList.add(list.getUrl());
            Log.d(TAG, "downloadPicture: " + list.getUrl());
            originMap.put(list.getUrl(), list.getMd5());
            SharedPreferenceUtil.getInstance().putInt(list.getName(), list.getTime());
        }
        pictureList = PictureControl.setPicture(originMap);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                roundProgressBarWidthNumber.setVisibility(View.VISIBLE);
            }
        });
        downLoadService.setPause(false);
        downLoadService.setDownloadURLS(pictureList);
        downloadBinder.startDownload();

    }

    class HelloBroadcastReceive extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {

            } else {
                if (downLoadService != null) {
                    downLoadService.setPause(true);
                }

            }
        }
    }

    private void checkUpdate() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int versionCode = packageInfo.versionCode;
                    Response response = null;
                    try {
                        response = HttpUtils.post(StringManager.URL + "request_update", new HttpUtils.Param("uuid", UUIDString),new HttpUtils.Param("version_code",versionCode));
                        String content = response.body().string();
                        ScreenInfo screenInfo = new Gson().fromJson(content,ScreenInfo.class);
                        final String url = screenInfo.data.getUrl();
                        if(url.equals("")){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(HelloActivity.this,"目前已是最新版本",Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else{
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    showDialog(url);
                                }
                            });
                        }
                    } catch (IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(HelloActivity.this,"无法连接到服务器，请检查网络",Toast.LENGTH_SHORT).show();
                            }
                        });
                        e.printStackTrace();
                    }

                }
            }).start();


    }

    private void showDialog(final String url) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("检查到新版本");
        builder.setMessage("是否更新？");
        builder.setNegativeButton("取消", null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(HelloActivity.this,UpdateService.class);
                        intent.putExtra("downloadUrl",url);
                        startService(intent);
                    }
                }).start();
            }
        });
        builder.show();
    }
}


