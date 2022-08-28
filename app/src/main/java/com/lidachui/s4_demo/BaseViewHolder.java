package com.lidachui.s4_demo;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public  class BaseViewHolder<T> extends RecyclerView.ViewHolder {

    private SparseArray<View> mSparseArray=new SparseArray<>();
    private T data;
    public EventBus eventBus;
    public boolean otherBool=false;
    public BaseAdapter mBaseAdapter;



    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public  void onBind(int position,T data){

    }


    public  <T extends View> T getView(int resId){
        if(itemView!=null){
            View view=itemView.findViewById(resId);
            mSparseArray.put(resId,view);
            return (T) view;
        }
        return null;
    }

    public void setText(int resId, String value){
        TextView textView= (TextView) mSparseArray.get(resId);
        if(textView==null){
            textView= (TextView)getView(resId);
        }
        textView.setText(value);
    }

    public void setImage(int resId, Bitmap bitmap){
        ImageView imageView= (ImageView) mSparseArray.get(resId);
        if(imageView==null){
            imageView= (ImageView) getView(resId);
        }
        imageView.setImageBitmap(bitmap);
    }

    public void sendMes(int position,View view,Object...objects){
        if(eventBus!=null){
            eventBus.onEventBusRunning(position,view,objects);
        }
    }

    public ObjectAnimator playAnim(View view){
        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(view,"alpha",1,0);
        objectAnimator.setDuration(300);
        objectAnimator.start();
        return objectAnimator;
    }

}
