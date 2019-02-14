package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class CompanyList extends EmResult{

    @SerializedName("data")
    public List<Company> companies;


    public static class Company  implements Serializable{

        @SerializedName("id")
        public long id;

        @SerializedName("companyName")
        public String companyName;

        @SerializedName("companyShortName")
        public String companyShortName;

        @SerializedName("is_main_company")
        public int isMain;

        @SerializedName("latitude")
        public double latitude;

        @SerializedName("longitude")
        public double longitude;

        @SerializedName("citycode")
        public String citycode;

        @SerializedName("adcode")
        public String adcode;

    }


}
