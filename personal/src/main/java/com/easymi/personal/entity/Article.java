package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Article {
    public long id;

    @SerializedName("company_id")
    public long companyId;

    public String content;

    @SerializedName("company_phone")
    public String phone;

    @SerializedName("company_web_address")
    public String url;

    @SerializedName("company_logo")
    public String logo;
}
