package com.easymi.common.entity;

import java.util.List;

/**
 * Created by liuzihao on 2017/12/18.
 *
 * MQTT推送实体类
 */

public class PushBean {
    public String msg;
//    public List<PushData> data;
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
