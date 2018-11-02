package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CompanyList extends EmResult {

    @SerializedName("companyinfos")
    public List<Company> companies;


    public static class Company {

        @SerializedName("id")
        public long id;

        @SerializedName("company_name")
        public String companyName;

        @SerializedName("company_short_name")
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
