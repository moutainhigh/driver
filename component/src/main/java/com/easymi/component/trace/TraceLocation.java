package com.easymi.component.trace;

/**
 * Created by developerLzh on 2018/1/3 0003.
 */

public class TraceLocation {
    private long time;
    private double lat;
    private double lng;
    private double speed;

    public void setTime(long time) {
        this.time = time;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public long getTime() {
        return time;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }

    public double getSpeed() {
        return speed;
    }
}
