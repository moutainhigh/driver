package com.easymin.carpooling.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class MapPositionModel implements Parcelable {

    private long id;
    private String address;
    private double latitude;//纬度
    private double longitude;//经度
    private int sort;//顺序
    private int type;//类型1代表出发地，3代表目的地

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public MapPositionModel() {
        super();
    }

    protected MapPositionModel(Parcel in) {
        id = in.readLong();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        sort = in.readInt();
        type = in.readInt();
    }

    public static final Creator<MapPositionModel> CREATOR = new Creator<MapPositionModel>() {
        @Override
        public MapPositionModel createFromParcel(Parcel in) {
            return new MapPositionModel(in);
        }

        @Override
        public MapPositionModel[] newArray(int size) {
            return new MapPositionModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeInt(sort);
        parcel.writeInt(type);
    }
}
