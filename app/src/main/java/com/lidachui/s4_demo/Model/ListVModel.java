package com.lidachui.s4_demo.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ListVModel implements Serializable {

    public ListVModel(){
        obj=new ArrayList<>();
    }

    private List<String> obj;

    public List<String> getObj() {
        return obj;
    }

    public void setObj(List<String> obj) {
        this.obj = obj;
    }
}
