package com.easymi.common.entity;

import java.util.List;

/**
 *
 * @author liuzihao
 * @date 2017/12/18
 *
 * MQTT推送实体类
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
