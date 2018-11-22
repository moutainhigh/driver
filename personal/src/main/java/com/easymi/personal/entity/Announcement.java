package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/5 0005.
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
