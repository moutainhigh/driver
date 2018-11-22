package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class Detail {
    @SerializedName("created")
    public Long time;

    @SerializedName("memo")
    public String purpose;

    @SerializedName("money")
    public double money;


//        "id":40,
//            "companyId":1,
//            "created":1542800458,
//            "driverId":43,
//            "driverName":"老胡",
//            "driverPhone":"18180635910",
//            "money":0,
//            "balance":930,
//            "memo":"司机充值，充值金额：0.01",
//            "type":"DRIVER_RECHARGE",
//            "operating":null

}
