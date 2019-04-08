package com.easymi.common.result;

import com.easymi.common.entity.WorkStatistics;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public class WorkStatisticsResult extends EmResult {

    /**
     * 工作台统计
     */
    @SerializedName("data")
    public WorkStatistics workStatistics;
}
