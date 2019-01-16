package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

public class PushFeeEmploy {
    @SerializedName("id")
    public long id;

    /**
     * 司机状态
     */
    @SerializedName("status")
    public int status;

    /**
     * 真实姓名
     */
    @SerializedName("realName")
    public String realName;

    @SerializedName("companyId")
    public long companyId;

    @SerializedName("phone")
    public String phone;

    /**
     * 服务子类型
     */
    @SerializedName("childType")
    public String childType;

    @SerializedName("business")
    public String business;

    @SerializedName("modelId")
    public long modelId;
}
