package com.easymi.zhuanche.result;

import com.easymi.component.result.EmResult;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ConsumerResult
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 客户信息实体
 * History:
 */

public class ConsumerResult extends EmResult {

    /**
     * 客户信息
     */
    @SerializedName("passengerInfo")
    public ConsumerInfo consumerInfo;
}
