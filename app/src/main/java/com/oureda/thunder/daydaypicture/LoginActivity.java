package com.oureda.thunder.daydaypicture;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.oureda.thunder.daydaypicture.base.Installation;
import com.oureda.thunder.daydaypicture.base.User;
import com.oureda.thunder.daydaypicture.imageSlide.MyTextView;
import com.oureda.thunder.daydaypicture.manager.CacheManager;
import com.oureda.thunder.daydaypicture.manager.StringManager;
import com.oureda.thunder.daydaypicture.util.HttpUtils;
import com.oureda.thunder.daydaypicture.util.SharedPreferenceUtil;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{
private MyTextView emailTV;
private MyTextView passwordTV;
private TextView loginTV;
private TextView register;
private User.Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        emailTV= (MyTextView) findViewById(R.id.email);
        passwordTV= (MyTextView) findViewById(R.id.password);
        loginTV= (TextView) findViewById(R.id.login);
        register= (TextView) findViewById(R.id.register);
        register.setOnClickListener(this);
        loginTV.setOnClickListener(this);

        if(CacheManager.getInstance().getCookie()!=""){
            autoLogin();
        }

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login:
                final String password = passwordTV.getText().toString();
                final String account1 = emailTV.getText().toString();
                Log.d("ss", "onClick: "+password+account1);
                if ((account1.contains("@")) && (password.trim()!= "")) {
                    new Thread(new Runnable() {
                        public void run() {
                            HttpUtils.postAsyn(StringManager.URL+"login",
                                    new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(LoginActivity.this,"网络暂时出现了问题",Toast.LENGTH_LONG);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (response != null) {
                                                String content = response.body().string();
                                                Log.d("ss", "onResponse: " + content);
                                                if (content.contains("登录成功")) {
                                                    String str = response.headers().values("Set-Cookie").get(0);
                                                    String cookie = str.substring(0, str.indexOf(";"));
                                                    Log.d("ss", "onResponse: "+cookie);
                                                    CacheManager.getInstance().saveCookie(cookie);
                                                    data = new Gson().fromJson(content, User.class).data;
                                                    CacheManager.getInstance().saveAccount(account1);
                                                    SharedPreferenceUtil.getInstance().putInt("screen_id", data.getScreen_id());
                                                    SharedPreferenceUtil.getInstance().putString("username", data.getUsername());
                                                    Log.d("ss", "onResponse: "+data.getUsername()+data.getScreen_id());
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG);
                                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(LoginActivity.this, "登录失败", Toast.LENGTH_LONG);
                                                        }
                                                    });
//
//
//                                                }

                                                }
                                            }
                                        }
                                    }, new HttpUtils.Param("email", account1), new HttpUtils.Param("password", password),new HttpUtils.Param("id",1),new HttpUtils.Param("uuid",Installation.id(LoginActivity.this)+md5(account1)));
                        }
                    }).start();
                }else{
                   // Toast.makeText(registerActivity.this,"账号不符合要求",Toast.LENGTH_LONG);

                }
                break;
//            case R.id.password:
//                break;
            case R.id.register:
                startActivity(new Intent(this,registerActivity.class));
                break;
//            case R.id.email:
//                break;
        }
    }
    private void autoLogin(){
        HttpUtils.postAsynWithCookie(StringManager.URL + "login", new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this,"自动登录失败,服务器暂时出现了问题",Toast.LENGTH_LONG).show();
                    }
                });

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response!=null){
                    String content = response.body().string();
                    Log.d("sssssssssssssssss", "onResponse: "+content);
                    if (content.contains("使用cookies成功")){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "自动登录成功", Toast.LENGTH_LONG);
                                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }else{
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "自动登录失败，密码可能已经被修改", Toast.LENGTH_LONG);
                            }
                        });

                    }
                }
            }
        },new HttpUtils.Param("id",1),new HttpUtils.Param("screen_id",SharedPreferenceUtil.getInstance().getInt("screen_id",0)));
    }
    public static String md5(String string) {
        if (TextUtils.isEmpty(string)) {
            return "";
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            byte[] bytes = md5.digest(string.getBytes());
            String result = "";
            for (byte b : bytes) {
                String temp = Integer.toHexString(b & 0xff);
                if (temp.length() == 1) {
                    temp = "0" + temp;
                }
                result += temp;
            }
            return result;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }
}
