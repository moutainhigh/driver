package com.easymi.personal.result;

import com.easymi.component.entity.TaxiSetting;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/3/6.
 */

public class SettingResult extends EmResult {
    @SerializedName("daijiaApp")
    public TaxiSetting taxiSetting;
}
