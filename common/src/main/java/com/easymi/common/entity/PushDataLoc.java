package com.easymi.common.entity;

import java.util.List;

/**
 * Created by developerLzh on 2017/12/21 0021.
 */

public class PushDataLoc {
    public double lat;
    public double lng;
    public String appKey;
    public int serialCode;
    public double darkCost;
    public int darkMileage;
    public long positionTime;
    public float accuracy;
    public List<PushDataOrder> orderInfo;
}
