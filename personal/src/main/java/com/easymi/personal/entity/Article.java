package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/5 0005.
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
