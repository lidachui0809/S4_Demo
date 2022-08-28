package com.lidachui.s4_demo;

import android.animation.ObjectAnimator;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.lidachui.s4_demo.Model.ListVModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class LoginActivity extends BaseActivity {

    private  EditText tep;
    private  EditText pwd;
    private Button loginBtn;
    private static final int TIME_NOW = 80;
    private static final int LOGIN_RESULT = 378;


    public LoginActivity(){
        super(false,"Login");
    }

    @Override
    protected int layoutId() {
        return R.layout.login_layout;
    }

    @Override
    protected void initData() {
        loginBtn=findViewById(R.id.start_login_btn);
        bindViewOnClick(loginBtn);
        tep=findViewById(R.id.tele_number);
        pwd=findViewById(R.id.pwd_text);
        getServer().getTimeNow(TIME_NOW);
    }

    @Override
    protected void handlerMessage(@NonNull Message msg) {
        super.handlerMessage(msg);
        if(msg.what==TIME_NOW){
            ((TextView)findViewById(R.id.hello_text)).setText(getCheapInfo(msg.obj.toString()));
        }else {
            handlerLogin(msg.obj);
        }
    }

    @Override
    protected void viewOnClick(View view) {
        super.viewOnClick(view);
        if(view.getId()==R.id.start_login_btn){
            if(!checkNull(tep)&&!checkNull(pwd)){
                doLogin();
            }
        }
    }

    @NonNull
    private String getCheapInfo(String date){
        String cheapStr="";
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date1= null;
        try {
            date1 = sdf.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance(TimeZone.getTimeZone("CH"));
        calendar.setTime(date1);
        int nowH=calendar.get(Calendar.HOUR_OF_DAY);
        if(nowH>=6&&nowH<=12){
            cheapStr="Good Morning";
        }else {
            if(nowH>12&&nowH<=18){
                cheapStr="Good Afternoon";
            }else {
                cheapStr="Good Evening";
            }
        }
        return cheapStr;
    }

    private void transformAnim(View view){
        ObjectAnimator objectAnimator= ObjectAnimator.ofFloat(view,"translationX",0,30,-30,0);
        objectAnimator.setDuration(200);
        objectAnimator.start();
    }

    private void doLogin(){
        String str="telephone="+tep.getText().toString()+"&pwd="+pwd.getText().toString();
        getServer().checkUser(LOGIN_RESULT,str);
    }

    private void handlerLogin(@NonNull Object obj){
        List<ListVModel> result= (List<ListVModel>) obj;
        if(result.size()!=0){
            List<String> values=result.get(0).getObj();
            UserInfo.firstName=values.get(0);
            UserInfo.lasName=values.get(1);
            UserInfo.userId=values.get(2);
            UserInfo.role=values.get(3);
            UserInfo.roleId=values.get(4);
            checkRole();
        }else {
            transformAnim(tep);
            tep.setText("");
            pwd.setText("");
            showToast("Your password has error!");
        }
    }

    private void checkRole(){
        if(UserInfo.roleId.equals("4")){
            startActivity(ViewMyTaskActivity.class);
        }else {
            if(UserInfo.roleId.equals("3")){
                startActivity(TaskManagerActivity.class);
            }
        }
    }

}
