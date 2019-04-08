package com.easymi.common.result;

import com.easymi.component.entity.SystemConfig;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/6/8
 * @since 5.0.0.000
 */
public class SystemResult extends EmResult {

    /**
     * 系统配置
     */
    public SystemConfig system;
    /**
     * 支付渠道
     */
    @SerializedName("driver_pay_channel")
    public String driverPayType;

}
