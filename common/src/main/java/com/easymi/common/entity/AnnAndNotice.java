package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liuzihao on 2018/4/20.
 */

public class AnnAndNotice implements MultiItemEntity, Serializable {

    public long id;

    public int type;//0公告 1通知

    @SerializedName("Affiche_title")
    public String annMessage;//公告的标题

    @SerializedName("notice_title")
    public String noticeTitle;//通知的标题

    @SerializedName("notice_content")
    public String noticeContent;//通知的内容

    @SerializedName("created")
    public long time;

    public static final int ITEM_HEADER = 1;
    public static final int ITEM_POSTER = 2;
    public static final int ITEM_DESC = 3;

    public int viewType;

    public AnnAndNotice() {
    }

    public AnnAndNotice(int type) {
        this.viewType = type;
    }

    @Override
    public int getItemType() {
        return viewType;
    }
}
