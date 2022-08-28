package com.lidachui.s4_demo;

import android.os.Bundle;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lidachui.s4_demo.Model.ListVModel;

import java.util.ArrayList;

public class ViewMyTaskActivity extends BaseActivity implements ViewLongClickInterface{

    private              RecyclerView     taskList;
    private static final int              TASK_INFO = 548;
    private              ArrayList<ListVModel> data;
    private int selectPosition;
    private ListVModel alertData;
    private static final int UPDATE_STATUS = 998;

    public ViewMyTaskActivity(){
        super(false,"My Transportation Task");
    }

    @Override
    protected int layoutId() {
        return R.layout.view_task_layout;
    }

    @Override
    protected void onStart() {
        super.onStart();
        registerForContextMenu(taskList);
    }

    @Override
    protected void initData() {
        findViewById(R.id.task_status_bar).setVisibility(View.GONE);
        taskList=findViewById(R.id.task_list);
        getServer().getTaskInfo(TASK_INFO,UserInfo.userId);
    }

    @Override
    protected void handlerMessage(@NonNull Message msg) {
        super.handlerMessage(msg);
        if(msg.what==TASK_INFO){
            data= (ArrayList<ListVModel>) msg.obj;
            handTaskMessage();
        }else {
            showToast(msg.obj.toString());
        }
    }

    //快捷创建一个长按弹出框
    //registerForContextMenu()方法绑定目标控件
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Set Task Status");
        menu.add(1,1,1,"Start");
        menu.add(1,2,1,"Travelling Record");
        menu.add(1,3,1,"Finish");
    }


    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case 1:
                if(!alertData.getObj().get(4).equals("NoStart")){
                    showToast("任务已经开始！");
                    break;
                }
                alertTaskStatus(2);
                break;
            case 2:
                Bundle bundle=new Bundle();
                bundle.putSerializable("alertTask",alertData);
                startActivity(TravelRecordActivity.class,bundle);
               break;
            case 3:
               alertTaskStatus(3);
               break;
        }
        return super.onContextItemSelected(item);
    }

    private void handTaskMessage(){
        BaseAdapter<ListVModel,TaskViewHolder> baseAdapter= (BaseAdapter<ListVModel, TaskViewHolder>) taskList.getAdapter();
        if(baseAdapter==null){
            baseAdapter=new BaseAdapter<ListVModel, TaskViewHolder>(R.layout.task_detail_item_view,data,this){};
            baseAdapter.mViewLongClickInterface=this::onLongClick;
            taskList.setLayoutManager(new LinearLayoutManager(this));
            taskList.setAdapter(baseAdapter);
        }else {
            baseAdapter.setData(data);
        }
        if(data.size()==0){
            showToast("你还没有未完成的任务！");
        }
    }

    @Override
    public void onLongClick(int position, View target, Object... obj) {
        selectPosition=position;
        BaseAdapter<ListVModel,TaskViewHolder> baseAdapter= (BaseAdapter<ListVModel, TaskViewHolder>) taskList.getAdapter();
        alertData=baseAdapter.getData(position);
    }

    private void alertTaskStatus(int taskStatus){
        String str="taskId="+alertData.getObj().get(5)+"&statusId="+taskStatus;
        getServer().updateTaskStatus(UPDATE_STATUS,str);
    }
}
