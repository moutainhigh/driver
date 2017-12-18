package com.easymi.component.entity;

/**
 * Created by liuzihao on 2017/12/18.
 * MQTT推送实体类
 */

public class PushBean<T> {
    public String msg;
    public T data;

    public PushBean(String msg, T data) {
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
