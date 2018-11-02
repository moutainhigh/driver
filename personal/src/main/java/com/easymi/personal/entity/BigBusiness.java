package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/15 0015.
 */

public class BigBusiness {
    @SerializedName("company_id")
    public Long companyId;

    @SerializedName("business_id")
    public Long businessId;

    @SerializedName("business")
    public String business;

    @SerializedName("business_name")
    public String businessName;

    @SerializedName("sort")
    public int sort;
}
