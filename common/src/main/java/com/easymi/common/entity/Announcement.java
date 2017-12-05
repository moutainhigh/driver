package com.easymi.common.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class Announcement {
    public long id;

    @SerializedName("created")
    public long time;

    @SerializedName("Affiche_title")
    public String message;

}
