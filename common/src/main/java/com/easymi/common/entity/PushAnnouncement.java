package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class PushAnnouncement extends EmResult {

    @SerializedName("data")
    public AnnounceRequest request;

    public static class AnnounceRequest {

        @SerializedName("id")
        public long id;

        /**
         * 公告标题
         */
        @SerializedName("afficheTitle")
        public String title;

        /**
         * 公告内容
         */
        @SerializedName("content")
        public String content;

    }

}
