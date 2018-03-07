package com.easymi.common.result;

import com.easymi.common.entity.Announcement;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/28 0028.
 */

public class AnnouncementResult extends EmResult{
    @SerializedName("EmployAfficheRequest")
    public Announcement employAfficheRequest;
}
