package com.easymi.common.result;

import com.easymi.common.entity.AnnAndNotice;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public class NotitfyResult extends EmResult{

    public List<AnnAndNotice> employNoticeRecords;

    public int total;

    public AnnAndNotice data;

}
