package com.easymi.personal.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: BusinessType
 * @Author: shine
 * Date: 2018/12/20 下午9:03
 * Description:
 * History:
 */
public class CarInfo extends EmResult {

    @SerializedName("vehicle")
    public Vehicle vehicle;

    @SerializedName("employees")
    public List<Employee> employees;

    public static class Vehicle {
        @SerializedName("brand")
        public String brand;

        @SerializedName("vehicle_no")
        public String vehicleNo;

        @SerializedName("vehicle_color")
        public String vehicleColor;

        @SerializedName("vehicle_type")
        public String vehicleType;

        @SerializedName("model")
        public String model;

        @SerializedName("photo")
        public String photo;

    }


    public static class Employee {
        @SerializedName("employ_id")
        public long employId;

        @SerializedName("employ_name")
        public String name;

        @SerializedName("employ_phone")
        public String phone;

    }

}

