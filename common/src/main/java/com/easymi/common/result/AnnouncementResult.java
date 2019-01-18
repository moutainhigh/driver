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

public class AnnouncementResult extends EmResult{

    /**
     * 司机公告
     */
    @SerializedName("employ_affiches")
    public List<AnnAndNotice> employAffiches;
    public int total;

    @SerializedName("EmployAfficheRequest")
    public AnnAndNotice employAfficheRequest;
}
