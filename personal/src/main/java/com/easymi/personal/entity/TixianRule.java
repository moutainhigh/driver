package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TixianRule
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class TixianRule {
    /**
     * 提现基数
     */
    @SerializedName("withdrawalsBase")
    public double withdrawals_base;
    /**
     * 提现最小值
     */
    @SerializedName("withdrawalsMin")
    public double withdrawals_min;
    /**
     * 提现最大值
     */
    @SerializedName("withdrawalsMax")
    public double withdrawals_max;
    /**
     * 提现备注
     */
    @SerializedName("withdrawalsMemo")
    public String withdrawals_memo;
}
