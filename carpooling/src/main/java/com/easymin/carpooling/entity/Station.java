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

    /**
     * 站点对应的序号
     */
    public int sequence;


//订单流程中的字段
    /**
     * 当前执行到的订单下标
     */
    public int current;

    /**
     * 当前站点是否已经通过
     */
    public boolean isPass;


    protected Station(Parcel in) {
        id = in.readLong();
        name = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        coordinate = in.createTypedArrayList(MapPositionModel.CREATOR);
        sequence = in.readInt();
        current = in.readInt();
        isPass = in.readByte() != 0;
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeTypedList(coordinate);
        dest.writeInt(sequence);
        dest.writeInt(current);
        dest.writeByte((byte) (isPass ? 1 : 0));
    }
}
