package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: BusinessType
 * @Author: shine
 * Date: 2018/12/20 下午9:03
 * Description:
 * History:
 */
public class BusinessType implements Serializable{

    @SerializedName("label")
    public String name;
    @SerializedName("value")
    public String type;

}
