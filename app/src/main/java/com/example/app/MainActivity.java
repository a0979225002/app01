package com.example.app;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity  {
    private boolean isrunning ;//賦予是否正在執行判斷
    private Button leftbtn,rightbtn;//左右按鈕實作
    private TextView textView;//時鐘
    private Timer timer = new Timer();//實作計時器
    private  counter counter ;//計時器宣告
    private UIHandler uiHandler = new UIHandler();//給予執行序讓他在後台跑時間
    private  int hs;//總時間未換算
    private ListView listView;//紀錄時間的view
    private SimpleAdapter adapter;//
    private LinkedList<HashMap<String,String>> data;
    private String[] from = {"lap","time1","time2"};//key
    private int[] to = {R.id.lap_rank,R.id.time1,R.id.time2};//value
    private int lapCounter;//重置lap次數用
    private int dHs;//算出紀錄的相差時間用

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        leftbtn = findViewById(R.id.leftbtn);
        rightbtn = findViewById(R.id.rightbtn);
        textView = findViewById(R.id.textview);
        listView = findViewById(R.id.listview);
        textView.setText(pash(hs));//目的給予一開始未按開始時的值,重置用
        initLap();
    }
    private  void initLap(){
        data = new LinkedList<>();
        adapter = new SimpleAdapter(this,data,R.layout.layout_lap,from,to);
        listView.setAdapter(adapter);
    }

    public void dorightbtn(View view) {//右邊按鈕 暫停/開始
        isrunning=!isrunning;//點擊右邊按鈕會自動轉換true或false
        if (isrunning){//true
            leftbtn.setText("紀錄");
            rightbtn.setText("暫停");
            counter = new counter();//實作計時器
            timer.schedule(counter,10,10);
        }else {//false
            leftbtn.setText("重置");
            rightbtn.setText("開始");
            counter.cancel();
        }


    }
    public void doleftbtn(View view) {//左邊按鈕  紀錄/重置
        if (isrunning){
            doLap();
        }else {
            doReset();//讓他重置
        }
    }
    //紀錄方法
    public void doLap(){
        if (dHs == 0){
            dHs = hs;
        }else {
           dHs = hs-dHs;
        }
        HashMap<String,String>row = new HashMap<>();
        row.put(from[0],"lap"+ ++lapCounter);
        row.put(from[1],""+pash(dHs));
        row.put(from[2],pash(hs));
        data.add(0,row);
        adapter.notifyDataSetChanged();//更新畫面
    }
    //重置方法
    public void doReset(){
        hs = 0;
        dHs =0;
        lapCounter = 0;//重置lap
        data.clear();
        adapter.notifyDataSetChanged();
        textView.setText(pash(hs));
    }
    private class counter extends TimerTask{
        @Override
        public void run() {
            hs++;
            uiHandler.sendEmptyMessage(0);
        }
    }
    private class UIHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            textView.setText(pash(hs));
        }
    }
    public static String pash(int hs){
        int phs = hs%100;//毫秒
        String phs01 = phs<10?"0"+phs:""+phs;
        int ts = hs/100;//總時數
        int hh = ts/(60*60);//時
        String hh01 = hh<10?"0"+hh:""+hh;
        int tt = (ts-(hh*60*60))/60;//分
        String tt01 = tt<10?"0"+tt:""+tt;
        int dd = ts%60;//秒
        String dd01 = dd<10?"0"+dd:""+dd;
        return String.format("%s:%s:%s:%s",hh01,tt01,dd01,phs01);
    }

    //讓他死掉
    @Override
    public void finish() {
        timer.cancel();
        timer.purge();
        timer =null;
        super.finish();
    }
}
