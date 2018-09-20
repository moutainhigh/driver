package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BusinessList extends EmResult {

    @SerializedName("business")
    public List<Business> businesses;

    public static class Business {

        @SerializedName("business")
        public String business;

        @SerializedName("business_name")
        public String businessName;


        @SerializedName("sort")
        public int sort;

        @SerializedName("status")
        public int status;

    }


}
