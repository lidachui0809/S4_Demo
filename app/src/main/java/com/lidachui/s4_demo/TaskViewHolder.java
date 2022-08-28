package com.lidachui.s4_demo;

import android.view.View;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskViewHolder extends BaseViewHolder<String>{

    public TaskViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(int position, @NonNull String data) {
        List<String> strings=new ArrayList<>(Arrays.asList(data.split(",")));
        setText(R.id.task_name,strings.get(0));
    }
}
