package com.example.messenger;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.messenger.manager.CacheManager;
import com.example.messenger.service.FourGroundService;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ChooseTimeActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView timeChoose;
    private TextView timeShow;
    private TextView timeStop;
    private LinearLayout after;
    private TextView before;
    private long interval;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    Date date=new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_choose_time);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_time);
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            Log.d("sss", "onCreate: ");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.back_icon);
        }
        timeChoose = (TextView) findViewById(R.id.time_choose_tv_time);
        timeShow = (TextView) findViewById(R.id.time_show_tv_time);
        after = (LinearLayout) findViewById(R.id.after_choose_time);
        before = (TextView) findViewById(R.id.before_choose_time);
        timeStop = (TextView) findViewById(R.id.time_stop_tv_time);
        timeStop.setOnClickListener(this);
        timeChoose.setOnClickListener(this);

        changeView();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_choose_tv_time:
                showTimePicker(timeShow);
                break;
            case R.id.time_stop_tv_time:
                endService();
                break;


        }
    }
    private void showTimePicker( final  TextView textView){
        final StringBuffer time = new StringBuffer();
        //获取Calendar对象，用于获取当前时间
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        //实例化TimePickerDialog对象
        final TimePickerDialog timePickerDialog = new TimePickerDialog(ChooseTimeActivity.this, new TimePickerDialog.OnTimeSetListener() {
            //选择完时间后会调用该回调函数
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                time.append(" "  + hourOfDay + ":" + minute);
                //设置TextView显示最终选择的时间
                textView.setText(time);

                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    date=ft.parse(time.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                startService();
            }
        }, hour, minute, true);
        //实例化DatePickerDialog对象
        DatePickerDialog datePickerDialog = new DatePickerDialog(ChooseTimeActivity.this, new DatePickerDialog.OnDateSetListener() {
            //选择完日期后会调用该回调函数
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                //因为monthOfYear会比实际月份少一月所以这边要加1
                time.append(year + "-" + (monthOfYear+1) + "-" + dayOfMonth);
                //选择完日期后弹出选择时间对话框
                timePickerDialog.show();
            }
        }, year, month, day);
        //弹出选择日期对话框
        datePickerDialog.show();

    }
    private void startService(){
        long interval = date.getTime()-System.currentTimeMillis();

        if(interval<60000){
            Toast.makeText(ChooseTimeActivity.this,"你设置的时间不符合实际要求(需大于当前时间的一分钟)",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(ChooseTimeActivity.this,"自启动设置成功",Toast.LENGTH_SHORT).show();
            CacheManager.getInstance().setAutoTime(timeShow.getText().toString());
            CacheManager.getInstance().setIntervalLong(date.getTime());
            changeView();
            Intent intent = new Intent(this,FourGroundService.class);
            startService(intent);
        }
    }
    private void endService(){
        CacheManager.getInstance().setAutoTime("");
        changeView();
        Toast.makeText(ChooseTimeActivity.this,"自启动取消成功",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent("com.example.thunder.messenger.AutoStart");
        sendBroadcast(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        long timeLong = CacheManager.getInstance().getIntervalLong();
        if(timeLong!=-1){
            startService();
        }
        finish();
    }
    private void changeView(){
        String timeSetting = CacheManager.getInstance().getAutoTime();
        if(timeSetting.equals("自启动时间")){
            after.setVisibility(View.GONE);
            before.setVisibility(View.VISIBLE);
        }else{
            before.setVisibility(View.GONE);
            after.setVisibility(View.VISIBLE);
            timeShow.setText(timeSetting);
        }
    }
}
