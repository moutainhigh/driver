package com.easymi.cityline.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2018/11/16.
 */

public class Order {

    List<Type> zhuanche;
    List<Type> paotui;
    List<Type> daijia;
    
    void fun(){
        List<Type> newType = new ArrayList<>();
        List<Type> runType = new ArrayList<>();
        List<Type> finishType = new ArrayList<>();

        for (Type type : daijia) {
            type.carNameAndCarType = "酒后代驾";
            if(type.equals("新单")){
                newType.add(type);
            } else if(newType.equals("执行中")){
                runType.add(type);
            } else {
                finishType.add(type);
            }

        }

        for (Type type : zhuanche) {
            type.orderName = "专车";
            if(type.equals("新单")){
                newType.add(type);
            } else if(newType.equals("执行中")){
                runType.add(type);
            } else {
                finishType.add(type);
            }

        }

        for (Type type : paotui) {
            type.orderName = "跑腿";
            if(type.equals("新单")){
                newType.add(type);
            } else if(newType.equals("执行中")){
                runType.add(type);
            } else {
                finishType.add(type);
            }

        }
    }



    class Type{
        String startAddress;
        String status;
        String orderName;//大类型
        String carNameAndCarType;//小类型
        //....
    }
}
