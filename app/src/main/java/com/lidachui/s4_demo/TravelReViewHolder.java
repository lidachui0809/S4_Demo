package com.lidachui.s4_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

import androidx.annotation.NonNull;

import com.lidachui.s4_demo.Model.ListVModel;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TravelReViewHolder extends BaseViewHolder<ListVModel> {

    private Timer timer;
    private long spiltTime=10*1000;
    public static final int IS_OVER_TIME = 415;
    public static final int DELETE_TR = 572;
    public static final int IS_HAD_DATA = 125;

    public TravelReViewHolder(@NonNull View itemView) {
        super(itemView);
        timer=new Timer();
    }

    @Override
    public void onBind(int position, ListVModel data) {
        List<String> strings=data.getObj();
        setText(R.id.record_time,strings.get(0));
        setText(R.id.record_address,strings.get(1));
        setText(R.id.record_remark,strings.get(2));
        //默认原有数据是不可删除的,
        getView(R.id.tr_delete_btn).setOnClickListener(v->{
            playAnim(itemView).addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    sendMes(getAdapterPosition(),v,DELETE_TR);
                }
            });
        });
        //将控件发出，设置可见性
        //当adapte处于绘制阶段是期间，禁止控件调用notify(),刷新视图
        sendMes(getAdapterPosition(),getView(R.id.tr_delete_btn),IS_HAD_DATA,data);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if(getView(R.id.tr_delete_btn).getVisibility()!=View.VISIBLE){
                    return;
                }
                if(this==null)
                    return;
                sendMes(getAdapterPosition(),getView(R.id.tr_delete_btn),IS_OVER_TIME,data);
            }
        },spiltTime);
    }
}
