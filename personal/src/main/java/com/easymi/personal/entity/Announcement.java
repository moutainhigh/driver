package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Announcement {
    public long id;

    @SerializedName("created")
    public long time;

    @SerializedName("afficheTitle")
    public String message;

    public String url;

    @SerializedName("afficheContent")
    public String content;

    /**
     *公司id
     */
    private Long companyId;

}
