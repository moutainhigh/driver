package com.easymi.common.result;

import com.easymi.component.entity.ZCSetting;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuzihao on 2018/3/6.
 */

public class SettingResult extends EmResult {

//    @SerializedName("daijiaApp")
//    public TaxiSetting setting;

//    @SerializedName("app_settings")
//    public String appSetting;

    public List<ZCSetting> data;

    public ZCSetting zcSetting;

}
