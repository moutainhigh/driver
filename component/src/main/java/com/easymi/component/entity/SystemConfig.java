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
 * @date 2018/6/8
 * @since 5.0.0.000
 */
public class SystemConfig {
    @SerializedName("withdrawals_base")
    public int tixianBase;//提现基数

    @SerializedName("withdrawals_min")
    public int tixianMin;//提现最小值

    @SerializedName("withdrawals_max")
    public int tixianMax;//提现最大值

    @SerializedName("withdrawals_memo")
    public String tixianMemo;//提现备注

    @SerializedName("employ_money_1")
    public int payMoney1;//充值金额1

    @SerializedName("employ_money_2")
    public int payMoney2;//充值金额2

    @SerializedName("employ_money_3")
    public int payMoney3;//充值金额3

    @SerializedName("allow_employ_phone")
    public int canCallDriver;//能否拨打附近司机电话

    public String payType;//可以支付的类型

    public void save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_systemconfig", null, null);//先删除再创建
        ContentValues values = new ContentValues();

        values.put("tixianBase", tixianBase);
        values.put("tixianMin", tixianMin);
        values.put("tixianMax", tixianMax);
        values.put("tixianMemo", tixianMemo);
        values.put("payMoney1", payMoney1);
        values.put("payMoney2", payMoney2);
        values.put("payMoney3", payMoney3);
        values.put("canCallDriver", canCallDriver);
        values.put("payType", payType);
        db.insert("t_systemconfig", null, values);
    }

    public static SystemConfig findOne() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery("select * from t_systemconfig", null);
        SystemConfig systemConfig = null;
        try {
            if (cursor.moveToFirst()) {
                systemConfig = new SystemConfig();
                systemConfig.tixianBase = cursor.getInt(cursor.getColumnIndex("tixianBase"));
                systemConfig.tixianMin = cursor.getInt(cursor.getColumnIndex("tixianMin"));
                systemConfig.tixianMax = cursor.getInt(cursor.getColumnIndex("tixianMax"));
                systemConfig.tixianMemo = cursor.getString(cursor.getColumnIndex("tixianMemo"));
                systemConfig.payMoney1 = cursor.getInt(cursor.getColumnIndex("payMoney1"));
                systemConfig.payMoney2 = cursor.getInt(cursor.getColumnIndex("payMoney2"));
                systemConfig.payMoney3 = cursor.getInt(cursor.getColumnIndex("payMoney3"));
                systemConfig.canCallDriver = cursor.getInt(cursor.getColumnIndex("canCallDriver"));
                systemConfig.payType = cursor.getString(cursor.getColumnIndex("payType"));
            }
        } finally {
            cursor.close();
        }
        return systemConfig;
    }

    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_systemconfig", null, null);
    }
}
