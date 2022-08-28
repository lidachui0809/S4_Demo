package com.lidachui.s4_demo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.lidachui.s4_demo.Model.ListVModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class AddTaskActivity extends BaseActivity implements EventBus{

    private Spinner selectProv,selectCity,selectDis,selectTeamStaff;
    private TextView startPc,endPc,reqDate;
    private TabLayout placeType;
    private Button autoBtn,handBtn;
    private RecyclerView vehAssignList;
    private EditText remark,reqWeight,selectStreetAdr;
    private HashMap<String,Integer> pHm,cHm,dHm,vsHm;
    private ArrayList<ListVModel> selectedVeh;
    private int startId,endId;
    private String startPcStr,endPcStr;
    private static final int PROVINCE = 869;
    private static final int CITY = 882;
    private static final int DISTRICT = 355;
    private static final int DATE_SELECT = 982;
    private static final int VEH_TEM_STAFF = 144;
    private static final int AUTO_ASSIGN = 743;
    private static final int LOAD_IMAGE = 474;
    private static final int SUBMIT_RESULT = 263;

    public AddTaskActivity() {
        super(true, "Create Transportation Task");
    }

    @Override
    protected int layoutId() {
        return R.layout.add_task_layout;
    }

    @Override
    protected void initData() {
        setMenuBtnClick(1,R.mipmap.icon_submit);
        selectCity=findViewById(R.id.select_city);
        selectProv=findViewById(R.id.select_prov);
        selectDis=findViewById(R.id.select_Dis);
        selectTeamStaff=findViewById(R.id.veh_team_staff);
        selectStreetAdr=findViewById(R.id.select_street_adr);
        startPc=findViewById(R.id.start_place);
        endPc=findViewById(R.id.end_place);
        remark=findViewById(R.id.task_remark);
        reqWeight=findViewById(R.id.req_weight);
        reqDate=findViewById(R.id.req_date);
        placeType=findViewById(R.id.pace_type);
        autoBtn=findViewById(R.id.auto_assign);
        handBtn=findViewById(R.id.hand_assign);
        vehAssignList=findViewById(R.id.vehicle_list);
        getServer().getProvince(PROVINCE);
        getServer().getVehStaff(VEH_TEM_STAFF);
        selectStreetAdr.addTextChangedListener(new TextMatch(){
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setPc(placeType.getSelectedTabPosition());
            }
        });
        bindViewOnClick(reqDate);
        bindViewOnClick(autoBtn);
        bindViewOnClick(handBtn);
        selectedVeh=new ArrayList<>();
    }


    @Override
    protected void handlerMessage(@NonNull Message msg) {
        super.handlerMessage(msg);
        switch (msg.what){
            case DISTRICT:
                handlerDis(msg.obj);
                break;
            case CITY:
                handlerCity(msg.obj);
                break;
            case PROVINCE:
                handlerProvince(msg.obj);
                break;
            case DATE_SELECT:
                reqDate.setText(msg.obj.toString());
                break;
            case VEH_TEM_STAFF:
                HashMap<String,Integer> hashMap1= (HashMap<String, Integer>) msg.obj;
                vsHm=hashMap1;
                setAdapter(selectTeamStaff,hashMap1.keySet());
                break;
            case AUTO_ASSIGN:
                handAutoAssign(msg.obj);
                break;
            case LOAD_IMAGE:
                handImage(msg.obj);
                break;
            case SUBMIT_RESULT:
                showToast(msg.obj.toString());
                closeDialog();
                break;
        }
    }

    private void handlerDis(Object obj){
        HashMap<String,Integer> hashMap= (HashMap<String, Integer>) obj;
        int i=placeType.getSelectedTabPosition();
        dHm=hashMap;
        setAdapter(selectDis,hashMap.keySet());
        selectDis.setOnItemSelectedListener(new MyOnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int index=placeType.getSelectedTabPosition();
                if(index==0){
                    startId=dHm.get(selectDis.getSelectedItem().toString());
                }else {
                    endId=dHm.get(selectDis.getSelectedItem().toString());
                }
                setPc(index);
            }
        });
    }

    private void handlerProvince(Object obj){
        HashMap<String,Integer> hashMap= (HashMap<String, Integer>) obj;
        pHm=hashMap;
        setAdapter(selectProv,hashMap.keySet());
        selectProv.setOnItemSelectedListener(new MyOnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int provId=pHm.get(selectProv.getSelectedItem().toString());
                getServer().getCity(CITY,provId);
                setPc(placeType.getSelectedTabPosition());
            }
        });
    }

    private void handlerCity(Object object){
        HashMap<String,Integer> hashMap= (HashMap<String, Integer>) object;
        int i=placeType.getSelectedTabPosition();
        //默认都是选中第一个
        cHm=hashMap;
        setAdapter(selectCity,hashMap.keySet());
        selectCity.setOnItemSelectedListener(new MyOnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int cityId=cHm.get(selectCity.getSelectedItem().toString());
                getServer().getDistinct(DISTRICT,cityId);
                setPc(placeType.getSelectedTabPosition());
            }
        });
    }


    private void setAdapter(Spinner spinner,Object object){
        if(object==null){
            object=new String[]{};
        }
        Set<String> stringSet= (Set<String>) object;
        List<String> strings=new ArrayList<>(stringSet);
        ArrayAdapter<String> stringArrayAdapter=(ArrayAdapter<String>)spinner.getAdapter();
        if(stringArrayAdapter!=null){
            stringArrayAdapter.clear();
            stringArrayAdapter.addAll(strings);
        }else {
            stringArrayAdapter=new ArrayAdapter<>(this,R.layout.span_item_view,strings);
        }
        spinner.setAdapter(stringArrayAdapter);
    }

    private void setPc(int type){
        if(pHm==null||cHm==null||dHm==null)
            return;
        StringBuilder sb=new StringBuilder();
        sb.append(selectProv.getSelectedItem().toString()+",");
        sb.append(selectCity.getSelectedItem().toString()+",");
        sb.append(selectDis.getSelectedItem().toString()+",");
        sb.append(selectStreetAdr.getText().toString());
        String str="Start Place:";
        TextView textView=startPc;
        if(type==1){
            str="End Place:";
            textView=endPc;
            endPcStr=selectStreetAdr.getText().toString();
        }else {
            startPcStr=selectStreetAdr.getText().toString();
        }
        sb.indexOf(str,0);
        textView.setText(sb.toString());
    }

    @Override
    protected void viewOnClick(View view) {
        super.viewOnClick(view);
        switch (view.getId()){
            case R.id.req_date:
                showDatePickerDialog("RequestDate",DATE_SELECT);
                break;
            case R.id.auto_assign:
                if(!checkNull(reqWeight))
                    getServer().getTaskVehicle(AUTO_ASSIGN,Integer.valueOf(reqWeight.getText().toString()));
                break;
            case R.id.hand_assign:
                handAssign();
                break;
            case R.id.menu_btn_1:
                submitClick();
                break;
        }
    }

    private void handImage(Object obj){
        ArrayList arrayList= (ArrayList) obj;
        ImageView imageView= (ImageView) arrayList.get(0);
        Bitmap bitmap= (Bitmap) arrayList.get(1);
        if(bitmap==null){
            imageView.setVisibility(View.GONE);
        }else {
            imageView.setVisibility(View.VISIBLE);
        }
        imageView.setImageBitmap(bitmap);
    }


    private void handAutoAssign(Object obj){
        BaseAdapter baseAdapter= (BaseAdapter) vehAssignList.getAdapter();
        List<ListVModel> list= (List<ListVModel>) obj;
        selectedVeh.clear();
        selectedVeh.addAll(list);
        if(baseAdapter==null){
            baseAdapter=new BaseAdapter<ListVModel,VehicleViewHolder>(R.layout.vehicle_list_item_view,list,this){};
            baseAdapter.eventBus=this::onEventBusRunning;
            vehAssignList.setLayoutManager(new LinearLayoutManager(this));
            vehAssignList.setAdapter(baseAdapter);
        }else {
            baseAdapter.setData(list);
        }
    }

    private void handAssign(){
        Bundle bundle=new Bundle();
        bundle.putSerializable("selectVeh",selectedVeh);
        startActivityForResult(HandVehActivity.class,bundle);
    }

    //接收上一个act返回的数据
    @Override
    protected void activityResultCallBack(int code, Intent result) {
        super.activityResultCallBack(code, result);
        if(code==200){
            ArrayList<ListVModel> list= (ArrayList<ListVModel>) result.getSerializableExtra("selectVeh");
            if(list!=null){
                handAutoAssign(list);
            }
        }
    }

    @Override
    public void onEventBusRunning(int position, @NonNull View view, Object... objects) {
        if(view.getId()==R.id.veh_pho){
            getServer().getPhoto(objects[1].toString(),LOAD_IMAGE,view);
        }else {
            //删除事件，移除该项
            BaseAdapter<ListVModel,VehicleViewHolder> adapter= (BaseAdapter) vehAssignList.getAdapter();
            selectedVeh.remove(adapter.getData(position));
            adapter.removeData(position);
        }
    }

    public void submitClick(){
        if(!checkNull(startPc)&&!checkNull(endPc)
                &&!checkNull(selectStreetAdr)&&!checkNull(startPc)&&!checkNull(reqDate)){
            if(selectedVeh.size()==0){
                showToast("As least add a vehicle!");
                return;
            }
            showToast("正在提交数据库...");
            showLoadDialog();
            String result=createJson();
            getServer().addTask(SUBMIT_RESULT,result);
        }
    }


    private String createJson(){
        JSONObject jsonObject=new JSONObject();
        //C# webapi接收的日期格式默认为 yyyy-MM-ddTHH:mm:ssZ (其中T必不可少，否则读不不到数据)
        try {
            jsonObject.put("name","Transport "+reqWeight.getText().toString()+"Ton of coal to "+selectCity.getSelectedItem().toString());
            jsonObject.put("startDistrictId",startId);
            jsonObject.put("startStreetAddress",startPcStr);
            jsonObject.put("destinationDistrictId",endId);
            jsonObject.put("destinationStreetAddress",endPcStr);
            jsonObject.put("weight",Integer.valueOf(reqWeight.getText().toString()));
            jsonObject.put("requiredCompletionDate",reqDate.getText()+"T00:00:00");
            jsonObject.put("actualCompletionDate",JSONObject.NULL);
            jsonObject.put("vehicleTeamAdministrator",vsHm.get(selectTeamStaff.getSelectedItem().toString()));
            jsonObject.put("remark",remark.getText().toString());
            jsonObject.put("statusId",1);
            JSONArray jsonArray=new JSONArray();
            JSONArray jsonTrR=new JSONArray();
            for(ListVModel listVModel:selectedVeh){
                JSONObject json=new JSONObject();
                json.put("vehicleId",listVModel.getObj().get(4));
                jsonArray.put(json);
            }
//            JSONObject obj=new JSONObject();
//            Calendar calendar=Calendar.getInstance();
//            String date=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(calendar.getTime());
//            obj.put("recordTime",date.replace(" ","T"));
//            obj.put("address",JSONObject.NULL);
//            obj.put("remark",remark.getText().toString());
//            jsonTrR.put(obj);
            jsonObject.put("taskAssigns",jsonArray);
//            jsonObject.put("travellingRecords",jsonTrR);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

}
