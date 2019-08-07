package com.easymin.custombus.entity;

import java.io.Serializable;

public class StationBean implements Serializable {
    public long id;
    public long lineId;
    public long stationId;
    public String name;
    public String address;
    public int onOff;
    public int isCurrentStation;
    public int chooseStatus;
    public int sequence;
    public double ticket;

    @Override
    public String toString() {
        return "StationBean{" +
                "id=" + id +
                ", lineId=" + lineId +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", onOff=" + onOff +
                ", isCurrentStation=" + isCurrentStation +
                ", chooseStatus=" + chooseStatus +
                ", sequence=" + sequence +
                '}';
    }
}