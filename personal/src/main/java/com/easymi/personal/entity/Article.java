package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/5 0005.
 */

public class Article {
    public long id;

    @SerializedName("company_id")
    public long companyId;

    public String contents;
}
