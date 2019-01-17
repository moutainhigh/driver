package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.Detail;
import com.easymi.personal.entity.TixianRecord;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */


public class TixianResult extends EmResult {
    @SerializedName("data")
    public List<TixianRecord> tixianRecords;
    public int total;
}
