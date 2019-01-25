package com.easymi.common.entity;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class PushBean {

    public String msg;
    public PushData data;

    public PushBean(String msg, PushData data) {
        this.msg = msg;
        this.data = data;
    }

    @Override
    public String toString() {
        return "PushBean{" +
                "msg='" + msg + '\'' +
                ", data='" + data + '\'' +
                '}';
    }
}
