package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class Notifity {
    public long id;

    @SerializedName("created")
    public long time;

    @SerializedName("notice_content")
    public String message;

    public int state;//1是未读 2是已读
}
