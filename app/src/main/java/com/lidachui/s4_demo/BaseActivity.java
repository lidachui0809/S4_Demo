package com.lidachui.s4_demo;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolBar;
    protected ImageView menuBtn1,menuBtn2;
    private boolean neededBack;
    private String toolBarName;
    protected Handler mainHandler;
    private static final int ERROR_MSG = 1000;
    private View childView;
    private SparseArray<View> childViews;


    public BaseActivity(boolean neededBack,String toolBarName){
        this.neededBack=neededBack;
        this.toolBarName=toolBarName;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setMyContentView();
        mainHandler=new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(@NonNull Message msg) {
               handlerMessage(msg);
            }
        };
        childViews=new SparseArray<>();
        setToolBar();
        initData();
    }

    private void setMyContentView(){
        setContentView(R.layout.base_act_layout);
        ViewGroup.LayoutParams layoutParams=
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        childView=LayoutInflater.from(this).inflate(layoutId(),null);
        ((FrameLayout)findViewById(R.id.layout_contain)).addView(childView,layoutParams);
    }

    protected abstract int layoutId();
    protected abstract void initData();

    protected void setMenuBtnClick(int which,int resId){
        if(which==1){

            menuBtn1.setImageResource(resId);
            menuBtn1.setOnClickListener(this::viewOnClick);
        }else {
            menuBtn2.setImageResource(resId);
            menuBtn2.setOnClickListener(this::viewOnClick);
        }
    }


    private void setToolBar(){
        toolBar=findViewById(R.id.tool_bar);
        menuBtn1=findViewById(R.id.menu_btn_1);
        menuBtn2=findViewById(R.id.menu_btn_2);
        menuBtn2.setScaleType(ImageView.ScaleType.FIT_XY);
        menuBtn1.setScaleType(ImageView.ScaleType.FIT_XY);
        toolBar.setTitle(toolBarName);
        setSupportActionBar(toolBar);

        if(neededBack){
            toolBar.setNavigationIcon(R.mipmap.icon_left);
            toolBar.setNavigationOnClickListener(view -> {
                finish();
            });
        }
    }

    protected HttpServer getServer(){
        return HttpServer.getHttpServer(mainHandler);
    }

    protected void showToast(String content ){
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    protected void startActivity(Class<?> className){
        Intent intent=new Intent(this,className);
        startActivity(intent);

    }

    protected void viewOnClick(View view){

    }

    protected void handlerMessage(@NonNull Message msg){
        if(msg.what==ERROR_MSG){
            showToast(msg.obj.toString());
        }
    }

    protected void sendMessage(int what,Object target){
        new Thread(){
            @Override
            public void run() {
                mainHandler.obtainMessage(what, target).sendToTarget();
            }
        }.start();

    }




}
