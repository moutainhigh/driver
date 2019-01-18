package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.HelpMenu;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class HelpMenuResult extends EmResult {
    /**
     * 帮助菜单数据
     */
    @SerializedName("articles")
    public List<HelpMenu> menus;
}
