package com.easymin.custombus.entity;

public class TimeBean {
    public TimeBean(int day) {
        this.day = day;
    }

    public int day;

    public long getTimeStamp() {
        return System.currentTimeMillis() + day * 24 * 60 * 60 * 1000;
    }

    public String getDesc() {
        if (day == 0) {
            return "今天";
        } else if (day == 1) {
            return "明天";
        } else {
            return "后天";
        }
    }

}
