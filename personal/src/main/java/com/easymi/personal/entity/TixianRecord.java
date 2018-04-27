package com.easymi.personal.entity;

import android.content.Context;

import com.easymi.personal.R;
import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixianRecord {
    @SerializedName("application_time")
    public long time;


    public int status;//1.待审批 2.同意 3.驳回

    @SerializedName("refuse_reason")
    public String refuseReason;

    @SerializedName("cost")
    public double money;

    public String getStatusStr(Context context) {
        if (status == 1) {
            return context.getString(R.string.tixian_shenpi);
        } else if (status == 2) {
            return context.getString(R.string.tixian_agree);
        } else if (status == 3) {
            return context.getString(R.string.tixian_refuse);
        } else {
            return "";
        }
    }
}
