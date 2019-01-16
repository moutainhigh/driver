package com.easymi.cityline.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author hufeng
 */
public class MapPositionModel implements Parcelable {
    private String address;
    private double latitude;//纬度
    private double longitude;//经度
    private int sort;//顺序
    private int type;//类型1代表出发地，3代表目的地


    public MapPositionModel() {
        super();
    }


    public MapPositionModel(String address, double latitude, double longitude, int sort, int type) {
        super();
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.sort = sort;
        this.type=type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeInt(sort);
        dest.writeInt(type);
    }

    public static final Parcelable.Creator<MapPositionModel> CREATOR = new Creator<MapPositionModel>() {

        @Override
        public MapPositionModel[] newArray(int size) {
            return new MapPositionModel[size];
        }

        @Override
        public MapPositionModel createFromParcel(Parcel source) {
            return new MapPositionModel(source.readString(), source.readDouble(), source.readDouble(), source.readInt(),source.readInt());
        }
    };

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
}
