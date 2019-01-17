package com.easymi.personal.entity;

import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Detail {
    @SerializedName("time")
    public Long time;


    //driver_recharge 司机充值
    //PAY_DRIVER_BALANCE 余额支付
    //ADMIN_DRIVER_RECHARGE 后台充值
    //ALIPAY_DRIVER_RECHARGE  支付宝充值
    //WECHAT_DRIVER_RECHARGE  微信充值
    //PAY_DRIVER_ROYALTY  提成
    //DRIVER_PUT_FORWARD  提现申請
    //REJECT_PUT_FORWARD 提现拒絕
    //ACCEPT_PUT_FORWARD 提现同意
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
