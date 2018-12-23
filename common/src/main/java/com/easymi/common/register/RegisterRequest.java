package com.easymi.common.register;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class RegisterRequest implements Parcelable {

    public boolean needCarInfo;

    //代驾和无车注册  (新版整理)
    public long driverId;
    @SerializedName("realName")
    public String driverName;
    @SerializedName("phone")
    public String driverPhone;
    public String idCard;
    public String emergency;
    public String emergencyPhone;
    public String serviceType;
    public long companyId;
    @SerializedName("driveLicenceStart")
    public long startTime;
    @SerializedName("driveLicenceEnd")
    public long endTime;
    public String introducer;

    public String remark;
    public String companyName;
    public int version;


    //头像图片
    public String portraitPath;
    //身份证图片
    @SerializedName("idCardHeadPath")
    public String idCardPath;
    //身份证背面图片
    public String idCardBackPath;
    //驾驶证图片
    public String driveLicensePath;
    //全身照
    public String fullBodyPath;

    //有车注册需要传
    //车辆照片
    public String carPhoto;
    public String brand;
    public String model;
    public String plateColor;
    public String vehicleNo;
    public String vehicleType;
    public Integer seats;
    public Float mileage;
    public String useProperty;
    public String vin;
    public String fuelType;
    public Long buyDate;
    public Long certifyDate;
    //行驶证照片
    public String drivingLicensePhoto;
    public Long nextFixDate;
    //运输证照片
    public String transPhoto;
    public String vehicleColor;

    public RegisterRequest() {
    }


    protected RegisterRequest(Parcel in) {
        needCarInfo = in.readByte() != 0;
        driverId = in.readLong();
        driverName = in.readString();
        driverPhone = in.readString();
        idCard = in.readString();
        emergency = in.readString();
        emergencyPhone = in.readString();
        serviceType = in.readString();
        companyId = in.readLong();
        startTime = in.readLong();
        endTime = in.readLong();
        introducer = in.readString();
        portraitPath = in.readString();
        idCardPath = in.readString();
        idCardBackPath = in.readString();
        driveLicensePath = in.readString();
        fullBodyPath = in.readString();
        carPhoto = in.readString();
        brand = in.readString();
        model = in.readString();
        plateColor = in.readString();
        vehicleNo = in.readString();
        vehicleType = in.readString();
        if (in.readByte() == 0) {
            seats = null;
        } else {
            seats = in.readInt();
        }
        if (in.readByte() == 0) {
            mileage = null;
        } else {
            mileage = in.readFloat();
        }
        useProperty = in.readString();
        vin = in.readString();
        fuelType = in.readString();
        if (in.readByte() == 0) {
            buyDate = null;
        } else {
            buyDate = in.readLong();
        }
        if (in.readByte() == 0) {
            certifyDate = null;
        } else {
            certifyDate = in.readLong();
        }
        drivingLicensePhoto = in.readString();
        if (in.readByte() == 0) {
            nextFixDate = null;
        } else {
            nextFixDate = in.readLong();
        }
        transPhoto = in.readString();
        vehicleColor = in.readString();
    }

    public static final Creator<RegisterRequest> CREATOR = new Creator<RegisterRequest>() {
        @Override
        public RegisterRequest createFromParcel(Parcel in) {
            return new RegisterRequest(in);
        }

        @Override
        public RegisterRequest[] newArray(int size) {
            return new RegisterRequest[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (needCarInfo ? 1 : 0));
        dest.writeLong(driverId);
        dest.writeString(driverName);
        dest.writeString(driverPhone);
        dest.writeString(idCard);
        dest.writeString(emergency);
        dest.writeString(emergencyPhone);
        dest.writeString(serviceType);
        dest.writeLong(companyId);
        dest.writeLong(startTime);
        dest.writeLong(endTime);
        dest.writeString(introducer);
        dest.writeString(portraitPath);
        dest.writeString(idCardPath);
        dest.writeString(idCardBackPath);
        dest.writeString(driveLicensePath);
        dest.writeString(fullBodyPath);
        dest.writeString(carPhoto);
        dest.writeString(brand);
        dest.writeString(model);
        dest.writeString(plateColor);
        dest.writeString(vehicleNo);
        dest.writeString(vehicleType);
        if (seats == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(seats);
        }
        if (mileage == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(mileage);
        }
        dest.writeString(useProperty);
        dest.writeString(vin);
        dest.writeString(fuelType);
        if (buyDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(buyDate);
        }
        if (certifyDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(certifyDate);
        }
        dest.writeString(drivingLicensePhoto);
        if (nextFixDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(nextFixDate);
        }
        dest.writeString(transPhoto);
        dest.writeString(vehicleColor);
    }
}
