package com.easymin.carpooling.entity;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class Station implements Parcelable {
    /**
     * 站点id
     */
    public long id;
    /**
     * 站点名字
     */
    public String name;

    /**
     * 站点纬度
     */
    public double latitude;

    /**
     * 站点经度
     */
    public double longitude;

    /**
     * 站点详细信息
     */
    public List<MapPositionModel> coordinate;

    protected Station(Parcel in) {
        id = in.readLong();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        coordinate = in.createTypedArrayList(MapPositionModel.CREATOR);
    }

    public static final Creator<Station> CREATOR = new Creator<Station>() {
        @Override
        public Station createFromParcel(Parcel in) {
            return new Station(in);
        }

        @Override
        public Station[] newArray(int size) {
            return new Station[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(name);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeTypedList(coordinate);
    }
}
