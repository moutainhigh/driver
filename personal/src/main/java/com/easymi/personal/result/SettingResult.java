package com.easymi.personal.result;

import com.easymi.component.entity.Setting;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/3/6.
 */

public class SettingResult extends EmResult {
    @SerializedName("daijiaApp")
    public Setting setting;
}
