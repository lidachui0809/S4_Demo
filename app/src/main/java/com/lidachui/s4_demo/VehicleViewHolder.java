package com.lidachui.s4_demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.lidachui.s4_demo.Model.ListVModel;

import java.util.List;

public class VehicleViewHolder extends BaseViewHolder<ListVModel> {

    public static final int LOAD_IMAGE = 41;

    public VehicleViewHolder(@NonNull View itemView) {
        super(itemView);
    }


    @Override
    public void onBind(int position, ListVModel data) {
        List<String> stringList=data.getObj();
        setText(R.id.veh_pat_numb,stringList.get(0));
        //将照片图片名称发送出去,交给act处理
        sendMes(position,getView(R.id.veh_pho),LOAD_IMAGE,stringList.get(1));
        setText(R.id.veh_weight,stringList.get(2));
        setText(R.id.veh_dri_name,stringList.get(3));
        //删除单击事件
        if(otherBool){
            ImageView imageView= getView(R.id.veh_delete_btn);
            imageView.setOnClickListener(v -> {
                playAnim(itemView).addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        sendMes(getAdapterPosition(),v,LOAD_IMAGE);
                    }
                });
            });
            imageView.setVisibility(View.VISIBLE);
        }
    }
}
