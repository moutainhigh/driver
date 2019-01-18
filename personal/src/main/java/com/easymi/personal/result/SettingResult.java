package com.easymi.personal.result;

import com.easymi.component.entity.TaxiSetting;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class SettingResult extends EmResult {

    @SerializedName("daijiaApp")
    public TaxiSetting taxiSetting;
}
