package com.easymi.common.result;

import com.easymi.common.entity.WorkStatistics;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/15 0015.
 */

public class WorkStatisticsResult extends EmResult {

    @SerializedName("data")
    public WorkStatistics workStatistics;
}
