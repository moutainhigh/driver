package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

public class PushAnnouncement extends EmResult {

    @SerializedName("data")
    public AnnounceRequest request;

    public static class AnnounceRequest {

        @SerializedName("id")
        public long id;

        @SerializedName("afficheTitle")
        public String title;

        @SerializedName("content")
        public String content;

    }

}
