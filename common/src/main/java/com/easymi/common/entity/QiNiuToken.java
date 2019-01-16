package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * @author hufeng
 */
public class QiNiuToken extends EmResult{

    /**
     * 七牛云token
     */
    @SerializedName("qiniuyun")
    public String qiNiu;
}
