package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

public class PushFeeEmploy {
    @SerializedName("id")
    public long id;

    @SerializedName("status")
    public String status;

    @SerializedName("realName")
    public String realName;//真实姓名

    @SerializedName("companyId")
    public long companyId;

    @SerializedName("phone")
    public String phone;

    @SerializedName("childType")
    public String childType;//服务子类型

    @SerializedName("business")
    public String business;

    @SerializedName("modelId")
    public long modelId;
}
