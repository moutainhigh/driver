package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.Announcement;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class AnnResult extends EmResult {
    @SerializedName("employ_affiche_request")
    public Announcement ann;
}
