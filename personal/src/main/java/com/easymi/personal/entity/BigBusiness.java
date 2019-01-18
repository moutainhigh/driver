package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
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
