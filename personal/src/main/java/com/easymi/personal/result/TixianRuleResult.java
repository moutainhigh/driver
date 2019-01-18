package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.TixianRule;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TixianRuleResult
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class TixianRuleResult extends EmResult{

    /**
     *
     * 提现规则
     */
    @SerializedName("object")
    public TixianRule tixianRule;
}
