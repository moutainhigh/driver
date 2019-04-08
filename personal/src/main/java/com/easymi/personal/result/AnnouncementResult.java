package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.Announcement;
import com.easymi.personal.entity.Notifity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AnnouncementResult
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */


public class AnnouncementResult extends EmResult {
    @SerializedName("data")
    public List<Announcement> employAffiches;
    public int total;
}
