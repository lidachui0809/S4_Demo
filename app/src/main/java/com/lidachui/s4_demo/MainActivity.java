package com.lidachui.s4_demo;

import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends BaseActivity {

    private              TabLayout    taskStatus;
    private static final int          TASK_STATUS = 892;
    private static final int TASK_INFO = 380;
    private              List<String> values;
    private RecyclerView taskList;

    public MainActivity() {
        super(false, "Transportation Task Management");
    }

    @Override
    protected int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initData() {
        setMenuBtnClick(1,R.mipmap.icon_add);
        setMenuBtnClick(2,R.mipmap.icon_statistics);
        taskList=findViewById(R.id.task_list);
        taskStatus=findViewById(R.id.task_status_bar);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        //这里传入类型
        List<String> strings=new ArrayList<>();
        for (int i=0;i<=strings.size();i++){
            strings.add("测试 "+i);
        }

        taskList.setAdapter(new BaseAdapter<String,TaskViewHolder>(R.layout.task_detail_item_view,strings,this));
        getServer().getTaskStatus(TASK_STATUS);
        getServer().getTaskInfo(TASK_INFO,0);
    }

    @Override
    protected void viewOnClick(View view) {
        super.viewOnClick(view);
    }

    @Override
    protected void handlerMessage(Message msg) {
        super.handlerMessage(msg);
        switch (msg.what){
            case TASK_STATUS:
                handlerTaskStatus(msg.obj);
                break;
            case TASK_INFO:
                handlerTaskInfo(msg.obj);
                break;
        }
    }


    private void handlerTaskStatus(Object obj){
        HashMap<String,Integer> hashMap= new LinkedHashMap<>();
        hashMap.put("All",0);
        hashMap.putAll((HashMap<String,Integer>)obj);
        for (String str:hashMap.keySet()){
            //添加自定义的控件时需要通过tab对象setCustomView()方法
            TextView textView=new TextView(this);
            textView.setText(str);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //如果自己创建new tab 其父控件将为null,需要自己指定改tab的parent
            taskStatus.addTab(taskStatus.newTab().setCustomView(textView));
        }
        taskStatus.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tab里默认有一个textView控件,注意控件id
               TextView textView= (TextView) tab.getCustomView();
               textView.setTextAppearance(R.style.TabSelected);
               int statusId=hashMap.get(textView.getText());
               getServer().getTaskInfo(TASK_INFO,statusId);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                TextView textView= (TextView) tab.getCustomView();
                textView.setTextAppearance(R.style.TabNoSelected);
//                new Intent(this,.class);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void handlerTaskInfo(Object obj){
        List<String> stringList= (List<String>) obj;
        BaseAdapter baseAdapter= (BaseAdapter) taskList.getAdapter();
        baseAdapter.setData(stringList);
    }
}