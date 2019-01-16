package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author hufeng
 * 已废弃
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
