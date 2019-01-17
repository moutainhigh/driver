package com.easymi.common.result;

import com.easymi.common.entity.WorkStatistics;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 *
 * @author developerLzh
 * @date 2017/12/15 0015
 */

public class WorkStatisticsResult extends EmResult {

    /**
     * 工作台统计
     */
    @SerializedName("data")
    public WorkStatistics workStatistics;
}
