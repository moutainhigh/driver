package com.easymi.personal.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

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

