package com.lidachui.s4_demo;

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

    public BaseViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public  void onBind(int position,T data){

    }


    private View getView(int resId){
        if(itemView!=null){
            View view=itemView.findViewById(resId);
            mSparseArray.put(resId,view);
            return view;
        }
        return null;
    }

    public void setText(int resId,String value){
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

}
