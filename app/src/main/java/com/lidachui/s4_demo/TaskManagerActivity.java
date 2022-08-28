package com.lidachui.s4_demo;

import android.animation.ObjectAnimator;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.lidachui.s4_demo.Model.ListVModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class TaskManagerActivity extends BaseActivity {

    private              TabLayout    taskStatus;
    private static final int          TASK_STATUS = 892;
    private static final int TASK_INFO = 380;
    private              List<String> values;
    private RecyclerView taskList;

    public TaskManagerActivity() {
        super(false, "Transportation Task Management");
    }

    @Override
    protected int layoutId() {
        return R.layout.view_task_layout;
    }

    @Override
    protected void initData() {
        setMenuBtnClick(1,R.mipmap.icon_add);
        setMenuBtnClick(2,R.mipmap.icon_statistics);
        taskList=findViewById(R.id.task_list);
        taskStatus=findViewById(R.id.task_status_bar);
        taskList.setLayoutManager(new LinearLayoutManager(this));
        taskList.setAdapter(new BaseAdapter<ListVModel,TaskViewHolder>(R.layout.task_detail_item_view,new ArrayList<>(),this){});
        getServer().getTaskStatus(TASK_STATUS);
        getServer().getTaskInfo(TASK_INFO,0);
    }

    @Override
    protected void viewOnClick(View view) {
        super.viewOnClick(view);
        switch (view.getId()){
            case R.id.menu_btn_1:
                startActivity(AddTaskActivity.class);
                break;
            case R.id.menu_btn_2:
                startActivity(TransportationStatisticsActivity.class);
                break;
        }
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

    //处理生成的tabLayout的item
    private void handlerTaskStatus(Object obj){
        HashMap<String,Integer> hashMap= new LinkedHashMap<>();
        hashMap.put("All",0);
        hashMap.putAll((HashMap<String,Integer>)obj);
        for (String str:hashMap.keySet()) {
            //添加自定义的控件时需要通过tab对象setCustomView()方法
            TextView textView = new TextView(this);
            textView.setText(str);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            //如果自己创建new tab 其父控件将为null,需要自己指定改tab的parent
            taskStatus.addTab(taskStatus.newTab().setText(str));
        }
        taskStatus.addOnTabSelectedListener(new TabLayoutSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int statusId=hashMap.get(tab.getText());
                getServer().getTaskInfo(TASK_INFO,statusId);
            }
        });
    }

    //处理返回的数据任务
    private void handlerTaskInfo(Object obj){
        List<String> stringList= (List<String>) obj;
        BaseAdapter baseAdapter= (BaseAdapter) taskList.getAdapter();
        baseAdapter.setData(stringList);
        playTransAnim(taskList);
    }


    private void playTransAnim(View obj){
        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(obj,"alpha",0f,1f);
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }
}