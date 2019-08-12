package com.easymin.custombus.entity;

import java.io.Serializable;

public class StationBean implements Serializable {
    public long id;
    public long lineId;
    public long stationId;
    public String name;
    public String address;
    public int onOff;
    public int chooseStatus;
    public int sequence;
    public double ticket;
}