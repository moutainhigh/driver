package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.Announcement;
import com.easymi.personal.entity.Notifity;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by developerLzh on 2017/12/5 0005.
 */

public class AnnouncementResult extends EmResult {
    @SerializedName("employ_affiches")
    public List<Announcement> employAffiches;
    public int total;
}
