package com.easymi.common.entity;

import com.easymi.component.decoration.Sticky;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class Brands extends EmResult{

    @SerializedName("content")
    public List<Brand> brandList;

    public static class Brand implements Sticky{

        @SerializedName("id")
        public long id;

        @SerializedName("chinese")
        public String chinese;

        @SerializedName("initials_cn")
        public String initialsCn;

        @SerializedName("version")
        public String version;

        @Override
        public String groupTag() {
            return initialsCn;
        }
    }

}
