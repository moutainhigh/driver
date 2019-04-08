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
