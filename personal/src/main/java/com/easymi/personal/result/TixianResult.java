package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.Detail;
import com.easymi.personal.entity.TixianRecord;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developerLzh on 2017/12/1 0001.
 */

public class TixianResult extends EmResult {
    @SerializedName("enchashments")
    public List<TixianRecord> tixianRecords;
    public int total;
}
