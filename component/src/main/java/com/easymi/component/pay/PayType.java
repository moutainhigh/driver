package com.easymi.component.pay;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/6/8
 * @since 5.0.0.000
 */
public enum PayType {
    /**
     * 微信APP支付
     */
    CHANNEL_APP_WECHAT("CHANNEL_APP_WECHAT"),
    /**
     * 支付宝APP支付
     */
    CHANNEL_APP_ALI("CHANNEL_APP_ALI"),
    /**
     * 银联APP支付
     */
    CHANNEL_APP_UNION("CHANNEL_APP_UNION"),
    /**
     * 翼支付APP支付
     */
    CHANNEL_APP_BEST("CHANNEL_APP_BEST");

    private String payType;

    PayType(String payType) {
        this.payType = payType;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }
}
