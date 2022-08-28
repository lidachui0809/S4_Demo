package com.lidachui.s4_demo;

import android.view.View;

import androidx.annotation.NonNull;

import com.lidachui.s4_demo.Model.ListVModel;

public class TaskTotalViewHolder extends BaseViewHolder<ListVModel> {

    public TaskTotalViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(int position, ListVModel data) {
        super.onBind(position, data);
        setText(R.id.month_v,data.getObj().get(0));
        setText(R.id.total_count_v,data.getObj().get(1));
        setText(R.id.advance_V,data.getObj().get(2));
        setText(R.id.onTime_v,data.getObj().get(3));
        setText(R.id.over_v,data.getObj().get(4));
    }


}
