package com.easymin.carpooling.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class MapPositionModel implements Parcelable {

    public long id;
    public long stationId;
    public String name;
    public String polygonPathJsonStr;
    public double price;
    public String color;
    public int sequence;
    public String address;
    public double longitude;
    public double latitude;
    public int sort;
    public int type;

    public MapPositionModel(){}

    protected MapPositionModel(Parcel in) {
        id = in.readLong();
        stationId = in.readLong();
        name = in.readString();
        polygonPathJsonStr = in.readString();
        price = in.readDouble();
        color = in.readString();
        sequence = in.readInt();
        address = in.readString();
        longitude = in.readDouble();
        latitude = in.readDouble();
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
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeLong(stationId);
        dest.writeString(name);
        dest.writeString(polygonPathJsonStr);
        dest.writeDouble(price);
        dest.writeString(color);
        dest.writeInt(sequence);
        dest.writeString(address);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeInt(sort);
        dest.writeInt(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPositionModel that = (MapPositionModel) o;

        if (id != that.id) return false;
        if (stationId != that.stationId) return false;
        if (Double.compare(that.price, price) != 0) return false;
        if (sequence != that.sequence) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;
        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (sort != that.sort) return false;
        if (type != that.type) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (polygonPathJsonStr != null ? !polygonPathJsonStr.equals(that.polygonPathJsonStr) : that.polygonPathJsonStr != null)
            return false;
        if (color != null ? !color.equals(that.color) : that.color != null) return false;
        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = (int) (id ^ (id >>> 32));
        result = 31 * result + (int) (stationId ^ (stationId >>> 32));
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (polygonPathJsonStr != null ? polygonPathJsonStr.hashCode() : 0);
        temp = Double.doubleToLongBits(price);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + sequence;
        result = 31 * result + (address != null ? address.hashCode() : 0);
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(latitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + sort;
        result = 31 * result + type;
        return result;
    }
}
