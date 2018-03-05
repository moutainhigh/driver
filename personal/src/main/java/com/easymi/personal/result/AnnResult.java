package com.easymi.personal.result;

import com.easymi.component.result.EmResult;
import com.easymi.personal.entity.Announcement;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/3/5.
 */

public class AnnResult extends EmResult {
    @SerializedName("EmployAfficheRequest")
    public Announcement ann;
}
