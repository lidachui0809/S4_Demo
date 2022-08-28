package com.lidachui.s4_demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class BaseAdapter<T,VM extends BaseViewHolder<T>> extends RecyclerView.Adapter<VM> {


    private List<T> data;
    public ItemViewInterface mItemViewInterface;
    private Context mContext;
    private int resId;
    private Class<?> vmClassName;

    public BaseAdapter(int resId,List<T> data,Context context){
        this.resId=resId;
        this.data=data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public VM onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView= LayoutInflater.from(mContext).inflate(resId,parent,false);
        VM vm=getVMInstance(itemView);
        return vm;
    }

    @Override
    public void onBindViewHolder(@NonNull VM holder, int position) {
        holder.onBind(position, data.get(position));
        holder.itemView.setOnClickListener(view -> {
            mItemViewInterface.onItemViewClick(holder.getAdapterPosition(),holder.itemView);
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public void setData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void addDate(T data){
        this.data.add(data);
        notifyItemInserted(getItemCount());
        notifyItemChanged(getItemCount());
    }

    public void removeData(int position){
        this.data.remove(position);
        notifyItemRemoved(position);
        notifyItemChanged(position);
    }

   @Nullable
   private VM getVMInstance(View view){
       Constructor constructor=null;
        //获得当前类
        Class<?> cls=getClass();
        //获得当前类的父类,通过该父类获取该父类泛型对象的类型
        Class<VM> vmClass=getClass(cls);
        //通过泛型类型获得该类的构造函数,
       try {
           constructor=vmClass.getDeclaredConstructor(View.class);
           //通过获得构造器创建实例对象
          return (VM) constructor.newInstance(view);
       } catch (NoSuchMethodException e) {
           e.printStackTrace();
       } catch (InvocationTargetException e) {
           e.printStackTrace();
       } catch (IllegalAccessException e) {
           e.printStackTrace();
       } catch (InstantiationException e) {
           e.printStackTrace();
       }
       return null;
   }


   @Nullable
   private Class getClass(Class<?> cls){
       //获得该类得父类泛型对象
       Type type= cls.getGenericSuperclass();
       //判断是否为参数类型
       if(type instanceof ParameterizedType){
           //获得父类的所有泛型对象
           Type[] types= ((ParameterizedType) type).getActualTypeArguments();
           for(Type type1:types){
               if(type1 instanceof Class){
                   Class vmClass= (Class) type1;
                   //判断该类是否为 BaseViewHolder的子类
                    if(BaseViewHolder.class.isAssignableFrom(vmClass)){
                        return  vmClass;
                    }
               }else {
                   if(type1 instanceof ParameterizedType){
                       //获取type1变量的类型
                       Type type2=((ParameterizedType) type1).getRawType();
                       if(type2 instanceof Class){
                            Class vmClass= (Class) type2;
                            if (BaseViewHolder.class.isAssignableFrom(vmClass)){
                                return vmClass;
                            }
                       }
                   }
               }
           }
       }
       return null;
   }

}
