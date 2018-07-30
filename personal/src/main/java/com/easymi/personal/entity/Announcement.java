package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/12/5 0005.
 */

public class Announcement {
    public long id;

    @SerializedName("created")
    public long time;

    @SerializedName("affiche_title")
    public String message;

    public String url;

    @SerializedName("affiche_content")
    public String content;
}
