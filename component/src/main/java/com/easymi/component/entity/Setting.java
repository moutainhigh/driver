package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.db.SqliteHelper;
import com.google.gson.annotations.SerializedName;

/**
 * Created by liuzihao on 2018/3/6.
 * <p>
 * 1为true 2为false
 */

public class Setting {

    @SerializedName("is_paid")
    public int isPaid;//代付（1开启，2关闭)

    @SerializedName("is_expenses")
    public int isExpenses;//报销（1开启，2关闭）

    @SerializedName("is_add_price")
    public int isAddPrice;//是否允许调价（1开启，2关闭)

    @SerializedName("is_work_car")
    public int isWorkCar;//是否是工作车（1开启，2关闭)

    @SerializedName("work_car_change_order")
    public int workCarChangeOrder;//（1开启，2关闭)

    @SerializedName("employ_change_price")
    public int employChangePrice;//确认费用时是否能加垫付费之类的

    @SerializedName("employ_factor")
    public int doubleCheck;//双因子验证

    //附近司机推荐距离
    @SerializedName("emploies_km")
    public double emploiesKm;

    public double payMoney1;
    public double payMoney2;
    public double payMoney3;

    public int canCallDriver;//能否拨打附近司机电话

    public void save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_settinginfo", null, null);//先删除再创建
        ContentValues values = new ContentValues();

        values.put("isPaid", isPaid);
        values.put("isExpenses", isExpenses);
        values.put("isAddPrice", isAddPrice);
        values.put("isWorkCar", isWorkCar);
        values.put("workCarChangeOrder", workCarChangeOrder);
        values.put("employChangePrice", employChangePrice);
        values.put("doubleCheck", doubleCheck);
        values.put("canCallDriver", canCallDriver);
        values.put("payMoney1", payMoney1);
        values.put("payMoney2", payMoney2);
        values.put("payMoney3", payMoney3);
        db.insert("t_settinginfo", null, values);
    }

    public static Setting findOne() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery("select * from t_settinginfo", null);
        Setting settingInfo = new Setting();
        try {
            if (cursor.moveToFirst()) {
                settingInfo.isPaid = cursor.getInt(cursor.getColumnIndex("isPaid"));
                settingInfo.isExpenses = cursor.getInt(cursor.getColumnIndex("isExpenses"));
                settingInfo.isAddPrice = cursor.getInt(cursor.getColumnIndex("isAddPrice"));
                settingInfo.isWorkCar = cursor.getInt(cursor.getColumnIndex("isWorkCar"));
                settingInfo.workCarChangeOrder = cursor.getInt(cursor.getColumnIndex("workCarChangeOrder"));
                settingInfo.employChangePrice = cursor.getInt(cursor.getColumnIndex("employChangePrice"));
                settingInfo.doubleCheck = cursor.getInt(cursor.getColumnIndex("doubleCheck"));
                settingInfo.canCallDriver = cursor.getInt(cursor.getColumnIndex("canCallDriver"));

                settingInfo.payMoney1 = cursor.getDouble(cursor.getColumnIndex("payMoney1"));
                settingInfo.payMoney2 = cursor.getDouble(cursor.getColumnIndex("payMoney2"));
                settingInfo.payMoney3 = cursor.getDouble(cursor.getColumnIndex("payMoney3"));
            }
        } finally {
            cursor.close();
        }
        return settingInfo;
    }

    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_settinginfo", null, null);
    }


}
