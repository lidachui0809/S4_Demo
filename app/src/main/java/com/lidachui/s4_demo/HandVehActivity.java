package com.lidachui.s4_demo;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lidachui.s4_demo.Model.ListVModel;

import java.util.ArrayList;

public class HandVehActivity extends BaseActivity implements ViewClickInterface,EventBus{

    private RecyclerView handVehList;
    private ArrayList<ListVModel> selectVehList;
    private static final int ALL_VEH = 252;
    private static final int LOAd_IMAGE = 543;
    private Button subBtn;


    public HandVehActivity(){
        super(true,"Assign Manual");
    }

    @Override
    protected int layoutId() {
        return R.layout.assign_manual_layout;
    }

    @Override
    protected void initData() {
        handVehList=findViewById(R.id.hand_veh_list);
        subBtn=findViewById(R.id.submit_select);
        getSelectedVeh();
        getServer().getTaskVehicle(ALL_VEH,-1);
        bindViewOnClick(subBtn);
    }

    @Override
    protected void handlerMessage(@NonNull Message msg) {
        super.handlerMessage(msg);
        switch (msg.what){
            case ALL_VEH:
                setSelectVehListAdapter(msg.obj);
                break;
            case LOAd_IMAGE:
                handImage(msg.obj);
                break;
        }
    }

    public void getSelectedVeh(){
        ArrayList<ListVModel> list= (ArrayList<ListVModel>) getIntent()
                .getBundleExtra("bundle").getSerializable("selectVeh");
        if(list!=null){
            selectVehList=list;
        }else {
            selectVehList=new ArrayList<>();
        }
    }

    public void setSelectVehListAdapter(Object obj){
        BaseAdapter<ListVModel,VehicleViewHolder> baseAdapter= (BaseAdapter<ListVModel, VehicleViewHolder>) handVehList.getAdapter();
        ArrayList<ListVModel> list= (ArrayList<ListVModel>) obj;
        removeIsSelected(list);
        if(baseAdapter==null){
            baseAdapter=new BaseAdapter<ListVModel,VehicleViewHolder>(R.layout.vehicle_list_item_view,list,this){};
            baseAdapter.mViewClickInterface =this::onItemViewClick;
            baseAdapter.otherBool=false;
            baseAdapter.eventBus=this::onEventBusRunning;
            handVehList.setItemViewCacheSize(20);
            handVehList.setLayoutManager(new LinearLayoutManager(this));
            handVehList.setAdapter(baseAdapter);
        }else {
            baseAdapter.setData(list);
        }
    }

    private void removeIsSelected(ArrayList<ListVModel> list){
        for(ListVModel listVModel:selectVehList){
            list.removeIf(a->a.getObj().get(4).equals(listVModel.getObj().get(4)));
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

    @Override
    protected void viewOnClick(View view) {
        super.viewOnClick(view);
        if(view.getId()==R.id.submit_select){
            setResult();
            finish();
        }
    }

    //处理控件点击
    @Override
    public void onItemViewClick(int position, View itemView, Object... otherObj) {
        BaseAdapter<ListVModel,VehicleViewHolder> baseAdapter= (BaseAdapter) handVehList.getAdapter();
        ListVModel listVModel=baseAdapter.getData(position);
        if(selectVehList.contains(listVModel)){
            selectVehList.remove(listVModel);
            itemView.setBackgroundTintList(null);
        }else {
            selectVehList.add(listVModel);
            itemView.setBackgroundTintList(ColorStateList.valueOf(getColor(R.color.blue)));
        }
    }



    @Override
    public void onEventBusRunning(int position, View view, @NonNull Object... objects) {
        getServer().getPhoto(objects[1].toString(),LOAd_IMAGE,view);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    public void setResult(){
        Intent intent=new Intent();
        intent.putExtra("selectVeh",selectVehList);
        setResult(200,intent);
//        setResult();
    }
}
