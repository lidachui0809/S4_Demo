package com.lidachui.s4_demo;

import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lidachui.s4_demo.Model.ListVModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class TravelRecordActivity extends BaseActivity implements EventBus {

    private ListVModel taskInfo;
    private static final int TASK_VEH = 433;
    private static final int TASK_TR_RECORD = 636;
    private static final int TASK_REMARK = 61;
    private Boolean showVeh=false;
    private RecyclerView trReList,vehList;
    private EditText address,remark;
    private List<ListVModel> selectVm;
    private static final int SAVE_RESULT = 388;
    private static final int LOAD_IMAGE = 763;
    private static final int UPDATE_VIEW = 995;


    public TravelRecordActivity(){
        super(true,"Travelling Record");
    }



    @Override
    protected int layoutId() {
        return R.layout.travel_record_layout;
    }

    @Override
    protected void initData() {

        bindViewOnClick(getView(R.id.show_veh_btn));
        bindViewOnClick(getView(R.id.add_remark_btn));
        address=findViewById(R.id.re_in_address);
        remark=findViewById(R.id.re_in_remark);
        trReList=findViewById(R.id.tr_record_list);
        vehList=findViewById(R.id.tr_veh_list_view);
        selectVm=new ArrayList<>();
        loadTaskInfo();
        loadTaskVehicle();
        loadRemarkInfo();
    }

    @Override
    protected void handlerMessage(@NonNull Message msg) {
        super.handlerMessage(msg);
        switch (msg.what){
            case TASK_VEH:
                handleTaskVeh(msg.obj);
                break;
            case TASK_TR_RECORD:

                break;
            case SAVE_RESULT:
                break;
            case LOAD_IMAGE:
                handImage(msg.obj);
                break;
            case TASK_REMARK:
                handleRemarkInfo(msg.obj);
                break;
            case UPDATE_VIEW:
                View view= (View) msg.obj;
                view.setVisibility(View.GONE);
                break;
        }
    }

    public void loadTaskInfo(){
        ListVModel vModel= (ListVModel) getIntent().getBundleExtra("bundle").getSerializable("alertTask");
        if(vModel!=null){
            taskInfo=vModel;
            List<String> strings=vModel.getObj();
            setText(R.id.task_name,strings.get(0));
            setText(R.id.task_address,strings.get(1));
            setText(R.id.task_req_date,strings.get(2));
            setText(R.id.task_weight,strings.get(3));
            setText(R.id.task_status,strings.get(4));
        }else {
            showToast("初始化任务信息失败！");
        }
    }

    public void loadRemarkInfo(){
        if(taskInfo!=null){
            getServer().getTaskRemark(TASK_REMARK,taskInfo.getObj().get(5));
        }
    }

    public void loadTaskVehicle(){
        if(taskInfo==null)
            return;
        getServer().getTaskVehById(TASK_VEH,taskInfo.getObj().get(5));
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

    private void handleTaskVeh(Object obj){
        List<ListVModel> list= (List<ListVModel>) obj;
        if(list.size()==0){
            return;
        }
        vehList=findViewById(R.id.tr_veh_list_view);
        vehList.setLayoutManager(new LinearLayoutManager(this));
        BaseAdapter baseAdapter=new BaseAdapter<ListVModel,VehicleViewHolder>(R.layout.vehicle_list_item_view,list,this){};
        baseAdapter.otherBool=false;
        baseAdapter.eventBus=this::onEventBusRunning;
        vehList.setAdapter(baseAdapter);
    }

    @Override
    protected void viewOnClick(View view) {
        super.viewOnClick(view);
        if(view.getId()==R.id.show_veh_btn){
            handleShowVehClick(view);
        }else {
            if(view.getId()==R.id.add_remark_btn){
                addRemark();
            }
        }
    }

    private void handleShowVehClick(View view){
        RecyclerView recyclerView=findViewById(R.id.tr_veh_list_view);
        if(!showVeh){
            recyclerView.setVisibility(View.VISIBLE);
            ObjectAnimator anim= ObjectAnimator.ofFloat(recyclerView,"alpha",0f,1f);
            anim.setDuration(300);
            anim.start();
            ObjectAnimator anim2=ObjectAnimator.ofFloat(view,"rotation",0f,120f);
            anim2.setDuration(300);
            anim2.start();
        }else {
            ObjectAnimator anim= ObjectAnimator.ofFloat(recyclerView,"alpha",1f,0f);
            anim.setDuration(300);
            anim.start();
            ObjectAnimator anim2=ObjectAnimator.ofFloat(view,"rotation",120f,0f);
            anim2.setDuration(300);
            anim2.start();
            recyclerView.setVisibility(View.GONE);
        }
        showVeh=!showVeh;
    }

    private void handleRemarkInfo(Object obj){
        List<ListVModel> list= (List<ListVModel>) obj;
        if(list.size()==0)
            return;
        BaseAdapter<ListVModel,TravelReViewHolder> baseViewHolder=
                new BaseAdapter<ListVModel,TravelReViewHolder>(R.layout.trav_re_item_view,list,this){};
        trReList.setLayoutManager(new LinearLayoutManager(this));
        baseViewHolder.eventBus=this::onEventBusRunning;
        trReList.setAdapter(baseViewHolder);
    }

    private void addRemark(){
        if(!checkNull(address)&&!checkNull(remark))
            return;
        Calendar calendar=Calendar.getInstance();
        DateFormat df=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        df.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        String remarkDate=df.format(calendar.getTime());
        ListVModel listVModel=new ListVModel();
        List<String> strings=new ArrayList<>();
        strings.add(remarkDate.replace(" ","T"));
        strings.add(address.getText().toString());
        strings.add(remark.getText().toString());
        listVModel.setObj(strings);
        addReViewData(listVModel);
        selectVm.add(listVModel);
        address.setText("");
        remark.setText("");
    }

    @Override
    public void onEventBusRunning(int position, View view, @NonNull Object... objects) {
        if(TravelReViewHolder.IS_OVER_TIME==Integer.valueOf(objects[0].toString())){
            //已经过了可以修改的时间，直接将数据提交给数据库,发送主线程更新视图
            mainHandler.obtainMessage(UPDATE_VIEW,view).sendToTarget();
            //将已经存在得数据移除
            autoSaveData(objects[1]);
        }
        if(TravelReViewHolder.DELETE_TR==Integer.valueOf(objects[0].toString())){
            removeReViewData(position);
        }else {
            if(VehicleViewHolder.LOAD_IMAGE==Integer.valueOf(objects[0].toString())){
                getServer().getPhoto(objects[1].toString(),LOAD_IMAGE,view);
            }else {
                if(TravelReViewHolder.IS_HAD_DATA==Integer.valueOf(objects[0].toString())){
                    //判断数据是否为用户添加的数据
                    if(selectVm.contains(objects[1])){
                        view.setVisibility(View.VISIBLE);
                    }else {
                        view.setVisibility(View.GONE);
                    }
                }
            }
        }
    }

    private void addReViewData(ListVModel listVModel){
        BaseAdapter baseAdapter= (BaseAdapter) trReList.getAdapter();
        baseAdapter.otherBool=true;
        baseAdapter.addDate(listVModel);

    }

    private void removeReViewData(int position){
        BaseAdapter<ListVModel,TravelReViewHolder> baseAdapter= (BaseAdapter) trReList.getAdapter();
        ListVModel listVModel=baseAdapter.getData(position);
        baseAdapter.removeData(position);
        selectVm.remove(listVModel);
    }


    public BaseAdapter getAdapter(RecyclerView recyclerView){
        return (BaseAdapter) recyclerView.getAdapter();
    }

    public void autoSaveData(Object obj){
//        BaseAdapter<ListVModel,TravelReViewHolder> baseAdapter= (BaseAdapter) trReList.getAdapter();
//        ListVModel listVModel=baseAdapter.getData(position);
        //已经自动保存的数据将移除
        if(!selectVm.contains(obj))
            return;
        selectVm.remove(obj);
        String json=createJson((ListVModel) obj);
        getServer().addTaskRemark(SAVE_RESULT,json);
    }

    public void handSave(){
        String json=createJson(selectVm.toArray(new ListVModel[selectVm.size()]));
        getServer().addTaskRemark(SAVE_RESULT,json);
    }


    @NonNull
    private String createJson(@NonNull ListVModel...listVModel){
        JSONArray ja=new JSONArray();
        try {
            for(ListVModel listVModel1:listVModel){
                JSONObject jb=new JSONObject();
                List<String> strings=listVModel1.getObj();
                jb.put("taskId",taskInfo.getObj().get(5));
                jb.put("recordTime",strings.get(0).replace(" ","T"));
                jb.put("address",strings.get(1));
                jb.put("remark",strings.get(2));
                ja.put(jb);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            return ja.toString();
        }
    }

    @Override
    public void onBackPressed() {
        if(selectVm.size()!=0){
            showToast("数据提交中！");
            handSave();
            return;
        }
        super.onBackPressed();

    }
}
