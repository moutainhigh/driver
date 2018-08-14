package com.easymi.common.entity;

import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

public class PushAnnouncement extends EmResult {

    @SerializedName("employ_affiche_request")
    public AnnounceRequest request;

    public static class AnnounceRequest {

        @SerializedName("id")
        public long id;

        @SerializedName("affiche_title")
        public String title;

        @SerializedName("affiche_content")
        public String content;

    }

}
