package com.easymi.common.entity;

import com.easymi.component.entity.PushEmploy;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class PushFee {

    @SerializedName("calc")
    public PushFeeLoc calc;

    @SerializedName("employ")
    public PushEmploy employ;
}
