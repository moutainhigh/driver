package com.easymi.component.trace;

/**
 * Created by developerLzh on 2018/1/3 0003.
 */

public class TraceLocation {
    private long time;

    private double lat;

    private double lng;

    private String locType;

    private double speed;

    private float bearing;

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

    public String getLocType() {
        return locType;
    }

    public void setLocType(String locType) {
        this.locType = locType;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }
}
