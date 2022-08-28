package com.lidachui.s4_demo;

import android.animation.ObjectAnimator;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.lidachui.s4_demo.Model.ListVModel;

import java.util.ArrayList;
import java.util.List;

public class TransportationStatisticsActivity extends BaseActivity{

    private RecyclerView taskTotalList;
    private TabLayout selectDate;
    private static final int TASK_TOTAL = 446;
    private int showPosition;
    private List<ListVModel> taskTotals;

    public TransportationStatisticsActivity(){
        super(true,"Transportation Statistics");
    }

    @Override
    protected int layoutId() {
        return R.layout.transportation_statistics_layout;
    }

    @Override
    protected void initData() {
        taskTotalList=findViewById(R.id.task_total_list);
        selectDate=findViewById(R.id.select_date);
        selectDate.addOnTabSelectedListener(new TabLayoutSelectedListener(){
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position=selectDate.getSelectedTabPosition();
                showPosition=position;
                updateTaskInfo();
            }
        });
        getServer().getTaskTotal(TASK_TOTAL);
    }

    @Override
    protected void handlerMessage(@NonNull Message msg) {
        super.handlerMessage(msg);
        if(msg.what==TASK_TOTAL){
            handleTaskTotal(msg.obj);
        }
    }

    private void handleTaskTotal(Object obj){
        List<ListVModel> list= (List<ListVModel>) obj;
        if(list.size()==0){
            showToast("未找到合适的数据信息！");
        }else {
            BaseAdapter<ListVModel,TaskTotalViewHolder> baseAdapter= (BaseAdapter<ListVModel, TaskTotalViewHolder>) taskTotalList.getAdapter();
            taskTotals=list;
            if(baseAdapter!=null){
                baseAdapter.setData(list);
            }else {
                taskTotalList.setLayoutManager(new LinearLayoutManager(this));
                baseAdapter=new BaseAdapter<ListVModel,TaskTotalViewHolder>(R.layout.task_total_item_view,list,this){};
                taskTotalList.setAdapter(baseAdapter);
            }
        }
    }

    private void updateTaskInfo(){
        BaseAdapter<ListVModel,TaskTotalViewHolder> baseAdapter= (BaseAdapter<ListVModel, TaskTotalViewHolder>) taskTotalList.getAdapter();
        if(baseAdapter!=null){
            List<ListVModel> list=new ArrayList<>(taskTotals);
            if(showPosition!=0){
                String month=selectDate.getTabAt(selectDate.getSelectedTabPosition()).getText().toString();
                list.removeIf(a->!a.getObj().get(0).equals(month));
            }
            baseAdapter.setData(list);
            baseAdapter.notifyItemRangeChanged(0,baseAdapter.getItemCount());
            playTransAnim(taskTotalList);
        }
    }

    private void playTransAnim(View obj){
        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(obj,"alpha",0f,1f);
        objectAnimator.setDuration(300);
        objectAnimator.start();
    }
}
