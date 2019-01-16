package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * @author hufeng
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
