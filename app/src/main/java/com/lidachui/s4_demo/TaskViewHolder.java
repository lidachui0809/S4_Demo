package com.lidachui.s4_demo;

import android.view.View;

import androidx.annotation.NonNull;

import com.lidachui.s4_demo.Model.ListVModel;

import java.util.List;

public class TaskViewHolder extends BaseViewHolder<ListVModel>{

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setLongClickable(true);
    }

    @Override
    public void onBind(int position, @NonNull ListVModel data) {
        List<String> strings=data.getObj();
        setText(R.id.task_name,strings.get(0));
        setText(R.id.task_address,strings.get(1));
        setText(R.id.task_req_date,strings.get(2));
        setText(R.id.task_weight,strings.get(3));
        setText(R.id.task_status,strings.get(4));
    }
}
