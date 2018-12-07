package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class Detail {
    @SerializedName("time")
    public Long time;


    //driver_recharge 司机充值
    //PAY_DRIVER_BALANCE 余额支付
    //ADMIN_DRIVER_RECHARGE 后台充值
    @SerializedName("type")
    public String purpose;

    @SerializedName("cost")
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
