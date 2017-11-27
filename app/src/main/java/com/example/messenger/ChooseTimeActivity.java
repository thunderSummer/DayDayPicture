package com.oureda.thunder.daydaypicture;

import android.app.ActionBar;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.oureda.thunder.daydaypicture.manager.CacheManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ChooseTimeActivity extends AppCompatActivity implements View.OnClickListener{
    private TextView timeChoose;
    private TextView timeShow;
    private TextView timeStop;
    private TextView timeStart;
    private long interval;
    private AlarmManager alarmManager;
    private PendingIntent pi;
    Date date=new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_time);
        setSupportActionBar(toolbar);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        setContentView(R.layout.activity_choose_time);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar!=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        }
        timeChoose = (TextView) findViewById(R.id.time_choose_tv_time);
        timeShow = (TextView) findViewById(R.id.time_show_tv_time);
        timeStart = (TextView) findViewById(R.id.time_start_tv_time);
        timeStop = (TextView) findViewById(R.id.time_stop_tv_time);
        timeStop.setOnClickListener(this);
        timeChoose.setOnClickListener(this);
        timeStart.setOnClickListener(this);
        timeShow.setText(CacheManager.getInstance().getAutoTime());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.time_choose_tv_time:
                showTimePicker(timeShow);
                break;
            case R.id.time_start_tv_time:
                startService();
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
        alarmManager= (AlarmManager) getSystemService(ALARM_SERVICE);
        long interval = date.getTime()-System.currentTimeMillis();
        //Intent intent = new Intent(ChooseTimeActivity.this,HelloActivity.class);
       // pi = PendingIntent.getActivity(ChooseTimeActivity.this,0,intent,0);
        Intent intent = new Intent(ChooseTimeActivity.this,MyAlarmReceiver.class);
        pi = PendingIntent.getBroadcast(ChooseTimeActivity.this,0,intent,0);
        Log.d("ss", "startService: "+interval);
        if(interval<60000){
            Toast.makeText(ChooseTimeActivity.this,"你设置的时间不符合实际要求(需大于当前时间的一分钟)",Toast.LENGTH_SHORT).show();
        }else{
            if (Build.VERSION.SDK_INT>=19){
                //API19以上使用
                alarmManager.setExact(AlarmManager.RTC_WAKEUP,date.getTime(),pi);
            }else {
                alarmManager.set(AlarmManager.RTC_WAKEUP,date.getTime(),pi);
            }
            Toast.makeText(ChooseTimeActivity.this,"自启动设置成功",Toast.LENGTH_SHORT).show();
            CacheManager.getInstance().setAutoTime(timeShow.getText().toString());
        }
    }
    private void endService(){
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(pi!=null){
            alarmManager.cancel(pi);
        }
        Toast.makeText(ChooseTimeActivity.this,"自启动取消成功",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
