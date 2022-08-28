package com.lidachui.s4_demo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.lidachui.s4_demo.Model.ListVModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HttpServer {


    private static HttpServer sHttpServer;
    public static Handler mainHandler;

    private static final String BASE_HOST = "http://10.0.2.2:5000/api/skyward/";
    private static final String FORM_TYPE  = "application/x-www-form-urlencoded";
    private static final String JSON_TYPE = "application/json";

    public static HttpServer getHttpServer(Handler handler) {
        if (sHttpServer==null){
            sHttpServer=new HttpServer();
        }
        mainHandler=handler;
        return sHttpServer;
    }

    private HttpServer(){

    }



    //发起网络请求，并返回请求结果
    @Nullable
    private InputStream getRequestResult(String host, @NonNull Object...inputInfo){
        String hostP=BASE_HOST+host;
        InputStream inputStream = null;
        try {
            URL uri=new URL(hostP);
            HttpURLConnection httpURLConnection= (HttpURLConnection) uri.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("Content-Type",JSON_TYPE);
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(3000);
            if(inputInfo.length!=0){
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
            }

            if(inputInfo.length!=0){
                if(inputInfo[0].toString().equals(FORM_TYPE)){
                    //表单格式传输
                    httpURLConnection.setRequestProperty("Content-Type",FORM_TYPE);
                }
                //指定写入类型房子中文乱码
                OutputStreamWriter writer=new OutputStreamWriter(httpURLConnection.getOutputStream(), StandardCharsets.UTF_8);
                writer.write(inputInfo[1].toString());
                writer.close();
            }
            httpURLConnection.connect();
            int code= httpURLConnection.getResponseCode();
            if(code==HttpURLConnection.HTTP_OK){
                inputStream= httpURLConnection.getInputStream();
            }else {
                requestError("请求码:"+code);
            }
        } catch (IOException e) {
            requestError("服务器连接失败！");
            return null;
        }
        return inputStream;
    }


    //这个方法用来处理只用来查看到字符串
    private void changeResultToListKVM(int which,String host,Object...objects){
        new Thread(){
            @Override
            public void run() {
                String str=changeResultToStr(getRequestResult(host,objects));
                ArrayList<ListVModel> list=new ArrayList<>();
                try {
                    JSONObject jsonObject=new JSONObject(str);
                    JSONArray jsonArray= jsonObject.getJSONArray("body");
                    for(int i=0;i<jsonArray.length();i++){
                        ListVModel listVModel=new ListVModel();
                        JSONObject jsonObject1=jsonArray.getJSONObject(i);
                        for(int j=0;j<jsonObject1.length();j++){
                            listVModel.getObj().add(jsonObject1.getString("value"+j));
                        }
                        list.add(listVModel);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    sendMsg(which,list);
                }
            }
        }.start();
    }


    //请求结果序列化为字符串
    private String changeResultToStr(InputStream inputStream){
        String result="";
        if(inputStream==null){
            return  result;
        }
        BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
        String line="";
        try{
            while ((line=reader.readLine())!=null) {
                result+=line;
            }
            reader.close();
        }catch (Exception exception){
            result="";
        }
        return result;
    }

    //K-V的另一种哈希表格式,将数据解析成k-v的hasMap
    private void changeResultToListKV(String host,int which,Object...objects){

        new Thread(){
            @Override
            public void run() {
                HashMap<String,Integer> hashMap=new LinkedHashMap<>();
                try {
                    String result=changeResultToStr(getRequestResult(host,objects));
                    JSONObject jsonObject=new JSONObject(result);
                    JSONArray jsonArray= jsonObject.getJSONArray("body");
                    for(int i=0;i< jsonArray.length();i++){
                        hashMap.put (jsonArray.getJSONObject(i).getString("name"),jsonArray.getJSONObject(i).getInt("id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    sendMsg(which,hashMap);
                }
            }
        }.start();
    }


    //将返回的数据字符串分割处理成List集合
    private void changeResultToListStr(String host,int witch,Object...postInfo){

        new Thread(){
            @Override
            public void run() {
                String str=changeResultToStr(getRequestResult(host,postInfo));
                str=replaceStr(str);
                List<String> stringList=new ArrayList<>();
                try {
                    str=replaceStr(str);
                    JSONObject jsonObject=new JSONObject(str);
                    JSONArray jsonArray= jsonObject.getJSONArray("body");
                    for(int i=0;i< jsonArray.length();i++){
                        stringList.add(jsonArray.getString(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }finally {
                    sendMsg(witch,stringList);
                }
            }
        }.start();


    }

    private String replaceStr(String str){
        str.replaceAll("\"","");
        str.replaceAll("[{]","");
        str.replaceAll("[{]","");
        return str;
    }

    private void getPhotoByHost(String host,int which,Object obj){
        new Thread(){
            @Override
            public void run() {
                Bitmap bitmap= BitmapFactory.decodeStream(getRequestResult(host));
                ArrayList arrayList= new ArrayList();
                arrayList.add(obj);
                arrayList.add(bitmap);
                sendMsg(which,arrayList);
            }
        }.start();
    }

    private void changeResultToStr(int which,String host,Object...objects){
        new Thread(){
            @Override
            public void run() {
                String result=  changeResultToStr(getRequestResult(host, objects));
                sendMsg(which,result);
            }
        }.start();
    }


    //封装changeResultToListStr方法，开启线程
    private void helpChangerResultToListStr(int which,String host,Object...objects){
        new Thread(){
            @Override
            public void run() {
                String str=changeResultToStr(getRequestResult(host,objects));
//                sendMsg(which,changeResultToListStr(str));
            }
        }.start();
    }

    //将结果转换为K-V哈希表格式
    private void changeResultToMap(String host,int which,Object...postInfo){
        new Thread(){
            @Override
            public void run() {
                HashMap<String,String> hashMap=new LinkedHashMap<>();
                InputStream inputStream=getRequestResult(host,postInfo);
//                List<String> stringList=changeResultToListStr(changeResultToStr(inputStream));
//                for(String str:stringList){
//                    String[] strings=str.split(":",2);
//                    hashMap.put(strings[0],strings[1]);
//                }
//                //将消息发送给主线程
//                sendMsg(which,hashMap);
            }
        }.start();
    }

    public void requestError(String errorMsg){
        mainHandler.obtainMessage(1000,errorMsg).sendToTarget();
    }

    public void getTaskInfo(int which,String userId){
        changeResultToListKVM(which,"task/staff?staffId="+userId);
    }

    public void updateTaskStatus(int which,String obj){
        changeResultToStr(which,"task/update/status",FORM_TYPE,obj);
    }


    public void checkUser(int which,String obj){
        changeResultToListKVM(which,"staff/login",FORM_TYPE,obj);
    }

    public void getTimeNow(int which){
        changeResultToStr(which,"time/now");
    }


    public void addTask(int which,String obj){
        new Thread(){
            @Override
            public void run() {
                String str= changeResultToStr(getRequestResult("task/add",JSON_TYPE,obj));
                mainHandler.obtainMessage(which,str).sendToTarget();
            }
        }.start();
    }

    public void addTaskRemark(int which,String obj){
        new Thread(){
            @Override
            public void run() {
                String str=changeResultToStr(getRequestResult("task/add/recard",JSON_TYPE,obj));
                mainHandler.obtainMessage(which,str);
            }
        }.start();
    }


    public void getTaskRemark(int which,String taskId){
        changeResultToListKVM(which,"task/record?taskId="+taskId);
    }


    public void getTaskStatus(int which){
        changeResultToListKV("task/status",which);
    }

    public void getTaskInfo(int which,int taskStatus){
        changeResultToListKVM(which,"task?status="+taskStatus);
    }

    public void getProvince(int which){
         changeResultToListKV("province",which);
    }

    public void getCity(int which,int provinceId){
        changeResultToListKV("city?provinceId="+provinceId,which);
    }

    public void getDistinct(int which,int cityId){
        changeResultToListKV("district?cityId="+cityId,which);
    }

    public void getVehStaff(int which){
        changeResultToListKV("vehicle/teamAnm",which);
    }

    public void getTaskVehicle(int which,int requestTon){
        changeResultToListKVM(which,"task/vehicle?requestTon="+requestTon);
    }

    public void getPhoto(String name,int which,Object target){
        getPhotoByHost("photo?photoName="+name,which,target);
    }

    public void getTaskTotal(int which){
        changeResultToListKVM(which,"task/total");
    }

    public void getTaskVehById(int which,String taskId){
        changeResultToListKVM(which,"task/belong/vehicle?taskId="+taskId);
    }


    private void sendMsg(int which,Object obj){
        mainHandler.obtainMessage(which,obj).sendToTarget();
    }
}
