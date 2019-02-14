package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class Vehicles extends EmResult{

    @SerializedName("content")
    public List<Vehicle> vehicleList;

    public static class Vehicle {
        @SerializedName("id")
        public long id;

        @SerializedName("vehicle_model")
        public String chinese;

        @SerializedName("version")
        public String version;

    }

}
