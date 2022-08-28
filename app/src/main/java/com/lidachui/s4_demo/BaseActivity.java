package com.lidachui.s4_demo;

import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public abstract class BaseActivity extends AppCompatActivity {

    private Toolbar toolBar;
    protected ImageView menuBtn1,menuBtn2;
    private boolean neededBack;
    private String toolBarName;
    protected Handler mainHandler;
    private static final int ERROR_MSG = 1000;
    private static final int UPDATE_VIEW = 46;
    private View childView;
    private SparseArray<View> childViews;
    private Dialog dialog;
    public ActivityResultLauncher<Intent> lunch;


    public BaseActivity(){

    }

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

        lunch=registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result
                ->{
                    activityResultCallBack(result.getResultCode(),result.getData());
                });

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
            menuBtn1.setVisibility(View.VISIBLE);
            menuBtn1.setImageResource(resId);
            menuBtn1.setOnClickListener(this::viewOnClick);
        }else {
            menuBtn2.setVisibility(View.VISIBLE);
            menuBtn2.setImageResource(resId);
            menuBtn2.setOnClickListener(this::viewOnClick);
        }
    }

    protected void activityResultCallBack(int code,Intent result){

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

    public void showLoadDialog(){
        dialog=new Dialog(this,R.style.MyDialogStyle);
        dialog.setContentView(R.layout.loading_layout);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public void closeDialog(){
        if(dialog.isShowing()){
            dialog.dismiss();
        }
    }

    protected void bindViewOnClick(@NonNull View view){
        view.setOnClickListener(this::viewOnClick);
    }

    protected void showToast(String content ){
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    protected void startActivity(Class<?> className){
        Intent intent=new Intent(this,className);
        startActivity(intent);
    }

    protected void startActivity(Class<?> className,Bundle bundle){
        Intent intent=new Intent(this,className);
        intent.putExtra("bundle",bundle);
        startActivity(intent);
    }

    protected void startActivityForResult(Class<?> className,Bundle bundle){
        Intent intent=new Intent(this,className);
        intent.putExtra("bundle",bundle);
        lunch.launch(intent);
    }



    public DatePickerDialog showDatePickerDialog(String title,int which){
        DatePickerDialog dialog=new DatePickerDialog(this);
        dialog.setTitle(title);
        dialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
            Calendar calendar=Calendar.getInstance();
            calendar.set(year,month,dayOfMonth);
            DateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            String str=simpleDateFormat.format(calendar.getTime());
            mainHandler.obtainMessage(which,str).sendToTarget();
        });
        dialog.show();
        return dialog;
    }

    protected void viewOnClick(View view){

    }

    protected boolean checkNull(@NonNull TextView textView){
        if(textView.getText().length()==0){
            showToast("Please send info!");
            playTransAnim(textView);
            return true;
        }
        return false;
    }

    private void playTransAnim(View view){
        ObjectAnimator objectAnimator=ObjectAnimator.ofFloat(view,"translationX",30,0,-30,0);
        objectAnimator.setDuration(250);
        objectAnimator.start();
    }

    protected void handlerMessage(@NonNull Message msg){
        if(msg.what==ERROR_MSG){
            showToast(msg.obj.toString());
        }
    }

    public void setText(int resId, String value){
        TextView textView=getView(resId);
        textView.setText(value);
    }


    public  <T extends View> T getView(int resId){
        if(childView!=null){
            View view=childView.findViewById(resId);
            return (T) view;
        }
        return null;
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
