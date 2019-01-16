package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 *
 * @author liuzihao
 * @date 2018/4/20
 */

public class AnnAndNotice implements MultiItemEntity, Serializable {

    public long id;

    /**
     * 0公告 1通知
     */
    public int type;

    /**
     * 公告的标题
     */
    @SerializedName("affiche_title")
    public String annMessage;

    /**
     * 通知的标题
     */
    @SerializedName("noticeTitle")
    public String noticeTitle;

    /**
     * 通知的内容
     */
    @SerializedName("noticeContent")
    public String noticeContent;

    /**
     * 通知状态,为1时表示未读消息
     */
    @SerializedName("state")
    public int state;

    @SerializedName("created")
    public long time;

    public static final int ITEM_HEADER = 1;
    public static final int ITEM_POSTER = 2;
    public static final int ITEM_DESC = 3;

    /**
     * 显示类型 1头 2子项
     */
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
