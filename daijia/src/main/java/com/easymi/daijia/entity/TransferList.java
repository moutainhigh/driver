package com.easymi.daijia.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransferList extends EmResult {

    @SerializedName("emploies")
    public List<Emploie> emploies;

    public static class Emploie {
        @SerializedName("employ_id")
        public long employId;

        @SerializedName("employ_name")
        public String employName;

        @SerializedName("employ_phone")
        public String employPhone;

        @SerializedName("employ_company_id")
        public String employCompanyId;

        @SerializedName("distance")
        public float distance;

        @SerializedName("lng")
        public double lng;

        @SerializedName("lat")
        public double lat;

        @SerializedName("status")
        public int status;

        @SerializedName("business")
        public String business;

        @SerializedName("model_id")
        public long modelId;

        @SerializedName("score")
        public float score;

        @SerializedName("photo")
        public String photo;

    }


}
