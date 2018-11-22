package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/2/23.
 */

public class TixianRule {
    @SerializedName("withdrawalsBase")
    public double withdrawals_base;//提现基数
    @SerializedName("withdrawalsMin")
    public double withdrawals_min;//提现最小值
    @SerializedName("withdrawalsMax")
    public double withdrawals_max;//提现最大值
    @SerializedName("withdrawalsMemo")
    public String withdrawals_memo;//提现备注
}
