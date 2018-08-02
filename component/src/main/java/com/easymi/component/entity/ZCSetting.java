package com.easymi.component.entity;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.db.SqliteHelper;
import com.google.gson.annotations.SerializedName;

public class ZCSetting {

    //附近司机推荐距离
    @SerializedName("driver_distance")
    public double emploiesKm;

    //代付（1开启，2关闭)
    @SerializedName("driver_rep_pay")
    public int isPaid;

    //是否允许调价（1开启，2关闭)
    public int isAddPrice = 2;

    //确认费用时是否能加垫付费之类的
    @SerializedName("driver_add_charge")
    public int employChangePrice;

    @SerializedName("driver_reimburs_charge")
    public int isExpenses;//报销（1开启，2关闭）

    //是否能消单
    @SerializedName("driver_cancel_order")
    public int canCancelOrder;


    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_zc_settinginfo", null, null);
    }

    public void save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_zc_settinginfo", null, null);//先删除再创建
        ContentValues values = new ContentValues();
        values.put("isPaid", isPaid);
        values.put("isExpenses", isExpenses);
        values.put("canCancelOrder", canCancelOrder);
        values.put("isAddPrice", isAddPrice);
        values.put("employChangePrice", employChangePrice);
        db.insert("t_zc_settinginfo", null, values);
    }

    public static ZCSetting findOne() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery("select * from t_zc_settinginfo", null);
        ZCSetting settingInfo = new ZCSetting();
        try {
            if (cursor.moveToFirst()) {
                settingInfo.isPaid = cursor.getInt(cursor.getColumnIndex("isPaid"));
                settingInfo.isExpenses = cursor.getInt(cursor.getColumnIndex("isExpenses"));
                settingInfo.canCancelOrder = cursor.getInt(cursor.getColumnIndex("canCancelOrder"));
                settingInfo.isAddPrice = cursor.getInt(cursor.getColumnIndex("isAddPrice"));
                settingInfo.employChangePrice = cursor.getInt(cursor.getColumnIndex("employChangePrice"));
            }
        } finally {
            cursor.close();
        }
        return settingInfo;
    }


}
