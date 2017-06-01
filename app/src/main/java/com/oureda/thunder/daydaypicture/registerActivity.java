package com.oureda.thunder.daydaypicture;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.oureda.thunder.daydaypicture.imageSlide.MyTextView;
import com.oureda.thunder.daydaypicture.imageSlide.RoundButton;
import com.oureda.thunder.daydaypicture.manager.CacheManager;
import com.oureda.thunder.daydaypicture.manager.StringManager;
import com.oureda.thunder.daydaypicture.util.HttpUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.Response;

public class registerActivity extends AppCompatActivity {
    private MyTextView passwordTV;
    private TextView finish;
    private MyTextView email;
    private MyTextView verifyTV;
    private MyTextView user;
    private RoundButton roundButton;
    private String cookie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);
        passwordTV= (MyTextView) findViewById(R.id.password_register);
        finish= (TextView) findViewById(R.id.finish);
        email= (MyTextView) findViewById(R.id.email_register);
        user= (MyTextView) findViewById(R.id.account_register);
        verifyTV= (MyTextView) findViewById(R.id.verify_register);
        roundButton= (RoundButton) findViewById(R.id.identifying_count_register);
        roundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email.getText().toString().trim()!=""){
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Response response = HttpUtils.post(StringManager.URL + "signup", new HttpUtils.Param("email", email.getText().toString()));
                                Headers headers = response.headers();
                                final String content = response.body().string();
                                Log.d( "ss" , "onResponse: "+content);
                            Log.d("info_headers", "header " + headers);
                            List<String> cookies = headers.values("Set-Cookie");
                            String str = cookies.get(0);
                            Log.d("info_cookies", "onResponse-size: " + str);
                            String cookie = str.substring(0, str.indexOf(";"));
                            CacheManager.getInstance().saveCookie(cookie);
                                if (content.contains("已下发验证码")) {

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(registerActivity.this,"验证码已发送，请注意查收",Toast.LENGTH_LONG);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(registerActivity.this,"验证码发送失败",Toast.LENGTH_LONG);

                                        }
                                    });

                                }
                            } catch (IOException localIOException) {
                                localIOException.printStackTrace();
                            }
                        }
                    }).start();
                    new CountDownTimer(60000L, 1000L) {
                        public void onFinish() {
                            roundButton.setEnabled(true);
                            roundButton.setContent("发送验证码");
                        }

                        public void onTick(long paramAnonymous2Long) {
                            roundButton.setEnabled(false);
                            roundButton.setContent(paramAnonymous2Long / 1000L + "后发送验证码");
                        }
                    }.start();
                }else{
                    Toast.makeText(registerActivity.this,"邮箱不能为空",Toast.LENGTH_LONG);

                }

            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String password = passwordTV.getText().toString();
                final String account1 = email.getText().toString();
                final String verify = verifyTV.getText().toString();
                final String userName = user.getText().toString();
                if ((account1.contains("@")) && (password.trim()!= "")&&userName.trim()!="") {
                    new Thread(new Runnable() {
                        public void run() {
                            HttpUtils.postAsynWithCookie(StringManager.URL+"verify",
                                    new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(registerActivity.this,"网络暂时出现了问题",Toast.LENGTH_LONG);
                                                }
                                            });
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if (response != null) {
                                                String content = response.body().string();
                                                Log.d( "ss" , "onResponse: "+content);
                                                if (content.contains("注册成功")) {
//                                                    String str = response.headers().values("Set-Cookie").get(0);
//                                                    String cookie = str.substring(0, str.indexOf(";"));
//                                                    CacheManager.getInstance().saveCookie(cookie);
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(registerActivity.this,"注册成功，即将返回登录界面",Toast.LENGTH_LONG);
                                                            startActivity(new Intent(registerActivity.this,LoginActivity.class) );
                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(registerActivity.this,"注册失败",Toast.LENGTH_LONG);
                                                        }
                                                    });


                                                }

                                            }
                                        }
                                    }, new HttpUtils.Param("email", account1), new HttpUtils.Param("password", password), new HttpUtils.Param("verify", verify),new HttpUtils.Param("id",1),new HttpUtils.Param("username",user.getText().toString()));
                        }
                    }).start();
                }else{
                    Toast.makeText(registerActivity.this,"账号不符合要求",Toast.LENGTH_LONG);

                }
            }
        });

    }
}
