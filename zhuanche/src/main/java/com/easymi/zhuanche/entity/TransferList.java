package com.easymi.zhuanche.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:TransferList
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 未使用
 * History:
 */
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

        @SerializedName("vehicle_no")
        public String carNo;

        @SerializedName("model")
        public String carName;

    }


}
