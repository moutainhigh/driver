package com.easymi.common.result;

import com.easymi.common.entity.AnnAndNotice;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developerLzh on 2017/11/28 0028.
 */

public class NotitfyResult extends EmResult{
    public List<AnnAndNotice> employNoticeRecords;
    public int total;

    public AnnAndNotice data;

}
