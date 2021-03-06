package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.db.SqliteHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Copyright (C) , 2012-2018 , 四川小咖科技有限公司
 *
 * @author Lzh
 * @version 5.0.0.000
 * @date 2018/5/22
 * @since 5.0.0.000
 * <p>
 * 车辆信息  主键为司机id
 */
public class Vehicle {

    public long employId;

    @SerializedName("id")
    public long vehicleId;//车型id

    @SerializedName("companyId")
    public long companyId;

    public String brand;// 车辆厂牌

    @SerializedName("carModel")
    public long vehicleModel; // 车辆型号

    @SerializedName("plateColor")
    public String plateColor; // 车牌颜色

    @SerializedName("vehicleNo")
    public String vehicleNo;// 车牌

    @SerializedName("vehicleType")
    public String vehicleType;// 车辆使用类型

    @SerializedName("commercialType")
    public int commercialType; // 开展业务

    @SerializedName("serviceType")
    public String serviceType;// 车辆类型

    /**
     * 保存数据
     */
    public boolean save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("employId", employId);
        values.put("vehicleId", vehicleId);
        values.put("companyId", companyId);
        values.put("vehicleBrand", brand);
        values.put("vehicleModel", vehicleModel);
        values.put("plateColor", plateColor);
        values.put("vehicleNo", vehicleNo);
        values.put("vehicleType", vehicleType);
        values.put("commercialType", commercialType);
        values.put("serviceType", serviceType);

        /*
         * values.put("age", age); values.put("jialing", jialing);
         */
        boolean flag = db.insert("t_vehicle", null, values) != -1;
        return flag;
    }

    /**
     * 更新数据
     */
    public boolean update() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("employId", employId);
        values.put("vehicleId", vehicleId);
        values.put("companyId", companyId);
        values.put("vehicleBrand", brand);
        values.put("vehicleModel", vehicleModel);
        values.put("plateColor", plateColor);
        values.put("vehicleNo", vehicleNo);
        values.put("vehicleType", vehicleType);
        values.put("commercialType", commercialType);
        values.put("serviceType", serviceType);

        /*
         * values.put("age", age); values.put("jialing", jialing);
         */
        boolean flag = db.update("t_Vehicle", values, " employId = ? ",
                new String[]{String.valueOf(employId)}) == 1;
        return flag;
    }

    public static Vehicle findByEmployId(long employId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery("select * from t_Vehicle where employId = ?", new String[]{String.valueOf(employId)});
        Vehicle vehicle = null;
        try {
            if (cursor.moveToFirst()) {
                vehicle = new Vehicle();
                vehicle.employId = cursor.getInt(cursor.getColumnIndex("employId"));
                vehicle.vehicleId = cursor.getInt(cursor.getColumnIndex("vehicleId"));
                vehicle.companyId = cursor.getInt(cursor.getColumnIndex("companyId"));

                vehicle.brand = cursor.getString(cursor.getColumnIndex("vehicleBrand"));
                vehicle.vehicleModel = cursor.getLong(cursor.getColumnIndex("vehicleModel"));
                vehicle.plateColor = cursor.getString(cursor.getColumnIndex("plateColor"));
                vehicle.vehicleNo = cursor.getString(cursor.getColumnIndex("vehicleNo"));
                vehicle.vehicleType = cursor.getString(cursor.getColumnIndex("vehicleType"));

                vehicle.commercialType = cursor.getInt(cursor.getColumnIndex("commercialType"));
                vehicle.serviceType = cursor.getString(cursor.getColumnIndex("serviceType"));
            }
        } finally {
            cursor.close();
        }
        return vehicle;
    }

    public boolean saveOrUpdate(long employId) {
        this.employId = employId;
        if (exists(employId)) {
            return this.update();
        } else {
            return this.save();
        }
    }

    /**
     * 判断司机对应的车型是否存在
     */
    public static boolean exists(Long employId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        boolean flag = false;
        try (Cursor cursor = db.rawQuery("select count(*) from t_Vehicle where employId = ? ", new String[]{String.valueOf(employId)})) {
            if (cursor.moveToNext()) {
                flag = (cursor.getInt(0) == 1);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return flag;
    }
}
