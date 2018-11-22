package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.TixianRule;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/2/23.
 */

public class TixianRuleResult extends EmResult{
//    @SerializedName("system")
//    public TixianRule tixianRule;

    @SerializedName("object")
    public TixianRule tixianRule;
}
