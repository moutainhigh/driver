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

public class Notifity {
    public long id;

    @SerializedName("created")
    public long time;

    @SerializedName("noticeContent")
    public String message;

    public int state;//1是未读 2是已读

    public String noticeTitle;

    public long employId;

    public String employName;

    public String employPhone;

    public String sendWay;

}
