package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.HelpMenu;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuzihao on 2018/3/1.
 */

public class HelpMenuResult extends EmResult {
    @SerializedName("articles")
    public List<HelpMenu> menus;
}
