package com.easymi.common.result;

import com.easymi.common.entity.AnnAndNotice;
import com.easymi.component.result.EmResult;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 *
 * @author developerLzh
 * @date 2017/11/28 0028
 */

public class AnnouncementResult extends EmResult{

    /**
     * 司机公告
     */
    @SerializedName("employ_affiches")
    public List<AnnAndNotice> employAffiches;
    public int total;

    @SerializedName("EmployAfficheRequest")
    public AnnAndNotice employAfficheRequest;
}
