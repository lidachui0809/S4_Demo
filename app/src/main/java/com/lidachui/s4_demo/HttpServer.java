package com.lidachui.s4_demo;

import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class HttpServer {


    private static HttpServer sHttpServer;
    public static Handler mainHandler;

    private static final String BASE_HOST = "http://10.0.2.2:5000/api/skyward/";

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
            httpURLConnection.setRequestProperty("Content-Type","application/json");
            httpURLConnection.setDoOutput(false);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setConnectTimeout(3000);
            if(inputInfo.length!=0){
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                //指定写入类型房子中文乱码
                OutputStreamWriter writer=new OutputStreamWriter(httpURLConnection.getOutputStream(),"utf-8");
                writer.write(inputInfo[0].toString());
                writer.close();
            }
            httpURLConnection.connect();
            int code= httpURLConnection.getResponseCode();
            if(code==HttpURLConnection.HTTP_OK){
                inputStream= httpURLConnection.getInputStream();
            }
        } catch (IOException e) {
            requestError(e.getMessage());
            return null;
        }
        return inputStream;
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

    //K-V的另一种哈希表格式
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
                    sendMsg(which,hashMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    //将返回的数据字符串分割处理成List集合
    private List<String> changeResultToListStr(String str){
        List<String> stringList=new ArrayList<>();
        try {
            str.replaceAll("\"","");
            str.replaceAll("[{]","");
            str.replaceAll("[{]","");
            JSONObject jsonObject=new JSONObject(str);
            JSONArray jsonArray= jsonObject.getJSONArray("body");
            for(int i=0;i< jsonArray.length();i++){
                stringList.add(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringList;
    }

    //封装changeResultToListStr方法，开启线程
    private void helpChangerResultToListStr(int which,String host,Object...objects){
        new Thread(){
            @Override
            public void run() {
                String str=changeResultToStr(getRequestResult(host,objects));
                sendMsg(which,changeResultToListStr(str));
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
                List<String> stringList=changeResultToListStr(changeResultToStr(inputStream));
                for(String str:stringList){
                    String[] strings=str.split(":",2);
                    hashMap.put(strings[0],strings[1]);
                }
                //将消息发送给主线程
                sendMsg(which,hashMap);
            }
        }.start();
    }

    public void requestError(String errorMsg){
        mainHandler.obtainMessage(1000,errorMsg).sendToTarget();
    }


    public void getTaskStatus(int which){
        changeResultToListKV("task/status",which);
    }

    public void getTaskInfo(int which,int taskStatus){
        helpChangerResultToListStr(which,"task?taskStatus="+taskStatus);
    }


    private void sendMsg(int which,Object obj){
        mainHandler.obtainMessage(which,obj).sendToTarget();
    }
}
