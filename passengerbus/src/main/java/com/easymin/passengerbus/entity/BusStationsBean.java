package com.easymin.passengerbus.entity;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.Config;
import com.easymi.component.db.SqliteHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: BusStationsBean
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class BusStationsBean implements Serializable {
    /**
     * "id": 9,
     * "companyId": 1,
     * "name": "站点1",
     * "namePinyin": "Z",
     * "address": "万盛(地铁站)",
     * "longitude": 103.818887,
     * "latitude": 30.675357,
     * "region": "四川省成都市温江区",
     * "autoReceive": 1,
     * "created": 1545461802,
     * "updated": 1545461802,
     * "appKey": "1HAcient1kLqfeX7DVTV0dklUkpGEnUC"
     */

    //自增长id
    public int growId;
    //站点id
    public long id;
    public String name;
    public String address;
    public double longitude;
    public double latitude;

    //班次id
    public long scheduleId;
    //业务类型
    public String orderType;
    //自定义字段
    //0未到达本站 1前往本站中 2，到达等待 3，已过本站
    public int status;
    //当前站点开始等待时间戳
    public long waitTime;

    /**
     * 保存数据
     * @return
     */
    public boolean save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("name", name);
        values.put("address", address);
        values.put("longitude", longitude);
        values.put("latitude", latitude);
        values.put("scheduleId", scheduleId);
        values.put("orderType", orderType);
        values.put("status", status);
        values.put("waitTime", waitTime);

        boolean flag = db.insert("t_bus_order_station", null, values) != -1;
        return flag;
    }


    private static BusStationsBean cursorToOrder(Cursor cursor) {
        BusStationsBean busStation = new BusStationsBean();
        busStation.id = cursor.getLong(cursor.getColumnIndex("id"));
        busStation.name = cursor.getString(cursor.getColumnIndex("name"));
        busStation.address = cursor.getString(cursor.getColumnIndex("address"));
        busStation.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
        busStation.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
        busStation.scheduleId = cursor.getLong(cursor.getColumnIndex("scheduleId"));
        busStation.orderType = cursor.getString(cursor.getColumnIndex("orderType"));
        busStation.status = cursor.getInt(cursor.getColumnIndex("status"));
        busStation.waitTime = cursor.getLong(cursor.getColumnIndex("waitTime"));

        return busStation;
    }

    /**
     * 通过班次id和站点id类型删除
     *
     * @param id
     */
    public static void delete(long id, long scheduleId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_bus_order_station", "id = ? and scheduleId = ?",
                new String[]{String.valueOf(id), String.valueOf(scheduleId)});
    }

    /**
     * 通过班次和订单类型删除
     *
     * @param scheduleId
     */
    public static void deleteByScheduleId(long scheduleId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_bus_order_station", "scheduleId = ? ",
                new String[]{String.valueOf(scheduleId)});
    }

    /**
     * 删除全部站点
     */
    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_bus_order_station", null, null);
    }

    /**
     * 更新状态信息
     *
     * @return
     */
    public boolean updateStatus() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();

        values.put("status", status);

        boolean flag = db.update("t_bus_order_station", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 更新状态信息
     *
     * @return
     */
    public boolean updateWaitTime() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();

        values.put("waitTime", waitTime);

        boolean flag = db.update("t_bus_order_station", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 根据ID和scheduleId查询数据
     */
    public static BusStationsBean findByIDAndScheduleId(long id, long scheduleId) {
        BusStationsBean busStation = new BusStationsBean();

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_bus_order_station where id = ? and scheduleId = ?"
                , new String[]{String.valueOf(id), String.valueOf(scheduleId)});

        try {
            if (cursor.moveToFirst()) {
                busStation = cursorToOrder(cursor);
            }
        } finally {
            cursor.close();
        }
        return busStation;
    }

    /**
     * 根据scheduleId查询数据列表
     */
    public static List<BusStationsBean> findByScheduleId(long scheduleId) {
        List<BusStationsBean> busStations = new ArrayList<>();

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_bus_order_station where scheduleId = ?"
                , new String[]{String.valueOf(scheduleId)});

        try {
            while (cursor.moveToNext()) {
                busStations.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }
        return busStations;
    }

    /**
     * 查询所有
     * @return
     */
    public static List<BusStationsBean> findAll() {

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_bus_order_station", new String[]{});

        List<BusStationsBean> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                list.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    /**
     * 判断数据库中是否已保存该站点信息
     * 多线路多站点可能会出现站点重复，站点id重复 需要联合查询
     */
    public static boolean existsById(long id, long scheduleId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_bus_order_station where id = ? and scheduleId = ?",
                new String[]{String.valueOf(id), String.valueOf(scheduleId)});
        boolean flag = false;
        try {
            if (cursor.moveToNext()) {
                flag = (cursor.getInt(0) == 1);
            }
        } finally {
            cursor.close();
        }
        return flag;
    }

    /**
     * 未到
     */
    public static final int NO_ARRIVE = 0;
    /**
     * 前往本站
     */
    public static final int TO_STATION = 1;
    /**
     * 到达等待
     */
    public static final int ARRIVE_WAIT = 2;
    /**
     * 离开本站
     */
    public static final int LEAVE_STATION = 3;

}
