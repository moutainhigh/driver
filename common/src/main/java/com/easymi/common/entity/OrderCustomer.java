package com.easymi.common.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import com.easymi.component.Config;
import com.easymi.component.db.SqliteHelper;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:OrderCustomer
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 专线订单-->订单客户 一对多的关系 (移动到common便于上传位置信息的时候给专线订单赋值)
 * History:
 */

public class OrderCustomer implements Serializable {
    /**
     * 专线订单未支付
     */
    public static final int CITY_LINE_STATUS_PAY = 1;
    /**
     * 专线订单已支付
     */
    public static final int CITY_LINE_STATUS_NEW = 5;
    /**
     * 专线订单等待接人
     */
    public static final int CITY_LINE_STATUS_TAKE = 10;
    /**
     * 专线订单接人后出发
     */
    public static final int CITY_LINE_STATUS_RUN = 15;
    /**
     * 专线订单跳过执行
     */
    public static final int CITY_LINE_STATUS_SKIP = 20;
    /**
     * 专线订单已到达终点站
     */
    public static final int CITY_LINE_STATUS_FINISH = 25;
    /**
     * 专线订单已评价
     */
    public static final int CITY_LINE_STATUS_REVIEW = 30;
    /**
     * 专线订单已退票
     */
    public static final int CITY_LINE_STATUS_CANCEL = 35;

    /**
     * 主键
     */
    public long id;

    /**
     * 用户Id
     */
    @SerializedName("passengerId")
    public long customerId;

    /**
     * 订单Id 外键
     */
    public long orderId;

    /**
     * 订单类型
     */
    public String orderType;

    /**
     * 客户姓名
     */
    @SerializedName("passengerName")
    public String name;

    /**
     * 客户电话
     */
    @SerializedName("passengerPhone")
    public String phone;

    /**
     * 客户头像
     */
    @SerializedName("avatar")
    public String photo;

    /**
     * 位置信息集合
     */
    public List<OrderAddressVo> orderAddressVos;

    /**
     * 起点
     */
    public String startAddr;

    /**
     * 终点
     */
    public String endAddr;

    public double startLat;
    public double startLng;
    public double endLat;
    public double endLng;

    /**
     * 预约时间
     */
    @SerializedName("bookTime")
    public long appointTime;

    /**
     * 接的顺序(这个是位置顺序，可以拖动排序的)
     */
    public int acceptSequence;
    /**
     * 送的顺序(这个是位置顺序，可以拖动排序的)
     */
    public int sendSequence;
    /**
     * 序号(这个是序号，一开始确定后就不会再变了)
     */
    public int num;

    //在第一次保存时，sequence是和num一致的

    /**
     * 0 未接 1 已接 2 跳过接 3 未送 4 已送 5 跳过送
     */
    public int status;
    /**
     * (接客户时的子状态) 0未到达预约地 1等待客户上车中
     */
    public int subStatus;

    /**
     * 获取客户状态
     * @return
     */
    public String getOrderStatus() {
        if (status == 0) {
            return "未接";
        }
        if (status == 1) {
            return "已接";
        }
        if (status == 2) {
            return "跳过";
        }
        if (status == 3) {
            return "未送";
        }
        if (status == 4) {
            return "已送";
        }
        if (status == 5) {
            return "跳过";
        }
        return "";
    }

    /**
     * 保存到数据库
     * @return
     */
    public boolean save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("customerId", customerId);
        values.put("orderId", orderId);
        values.put("orderType", orderType);
        values.put("name", name);
        values.put("phone", phone);
        values.put("startAddr", startAddr);
        values.put("endAddr", endAddr);
        values.put("startLat", startLat);
        values.put("startLng", startLng);
        values.put("endLat", endLat);
        values.put("endLng", endLng);
        values.put("appointTime", appointTime);
        values.put("acceptSequence", acceptSequence);
        values.put("sendSequence", sendSequence);
        values.put("num", num);
        values.put("status", status);
        values.put("photo", photo);
        values.put("subStatus", subStatus);

        boolean flag = db.insert("t_zx_order_customer", null, values) != -1;
        return flag;
    }

    /**
     * 更新接送客户的状态
     *
     * @return
     */
    public boolean updateStatus() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("status", status);

        boolean flag = db.update("t_zx_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 更新前往接的子状态
     *
     * @return
     */
    public boolean updateSubStatus() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("subStatus", subStatus);
        values.put("appointTime", appointTime);

        boolean flag = db.update("t_zx_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 更新送的排序
     *
     * @return
     */
    public boolean updateSendSequence() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("sendSequence", sendSequence);

        boolean flag = db.update("t_zx_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 更新接的排序
     *
     * @return
     */
    public boolean updateAcceptSequence() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("acceptSequence", acceptSequence);

        boolean flag = db.update("t_zx_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }


    /**
     * 判断数据库中是否已保存订单相关的OrderCustomer
     */
    public static boolean exists(long orderId, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_zx_order_customer where orderId = ? and orderType = ?",
                new String[]{String.valueOf(orderId), orderType});
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
     * 判断数据库中是否已保存订单相关的OrderCustomer
     */
    public static boolean existsById(long id, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_zx_order_customer where id = ? and orderType = ?",
                new String[]{String.valueOf(id), orderType});
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
     * 根据ID和type查询数据
     */
    public static List<OrderCustomer> findByIDTypeOrderByAcceptSeq(long orderId, String orderType) {
        List<OrderCustomer> orderCustomers = new ArrayList<>();

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_zx_order_customer where orderId = ? and orderType = ? order by acceptSequence"
                , new String[]{String.valueOf(orderId), orderType});

        try {
            while (cursor.moveToNext()) {
                orderCustomers.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }
        return orderCustomers;
    }

    /**
     * 根据ID和type查询数据
     */
    public static List<OrderCustomer> findByIDTypeOrderBySendSeq(long orderId, String orderType) {
        List<OrderCustomer> orderCustomers = new ArrayList<>();

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_zx_order_customer where orderId = ? and orderType = ? order by sendSequence"
                , new String[]{String.valueOf(orderId), orderType});

        try {
            while (cursor.moveToNext()) {
                orderCustomers.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }


        //把状态大的放在前面去，因为状态为最大的是已跳过的
        Collections.sort(orderCustomers, (o1, o2) -> {
            if (o1.status < o2.status) {
                return 1;
            } else if (o1.status > o2.status) {
                return -1;
            } else {
                return 0;
            }
        });

        return orderCustomers;
    }

    private static OrderCustomer cursorToOrder(Cursor cursor) {
        OrderCustomer orderCustomer = new OrderCustomer();
        orderCustomer.id = cursor.getLong(cursor.getColumnIndex("id"));
        orderCustomer.customerId = cursor.getLong(cursor.getColumnIndex("customerId"));
        orderCustomer.orderId = cursor.getLong(cursor.getColumnIndex("orderId"));
        orderCustomer.orderType = cursor.getString(cursor.getColumnIndex("orderType"));
        orderCustomer.name = cursor.getString(cursor.getColumnIndex("name"));
        orderCustomer.phone = cursor.getString(cursor.getColumnIndex("phone"));
        orderCustomer.startAddr = cursor.getString(cursor.getColumnIndex("startAddr"));
        orderCustomer.endAddr = cursor.getString(cursor.getColumnIndex("endAddr"));
        orderCustomer.startLat = cursor.getDouble(cursor.getColumnIndex("startLat"));
        orderCustomer.startLng = cursor.getDouble(cursor.getColumnIndex("startLng"));
        orderCustomer.endLat = cursor.getDouble(cursor.getColumnIndex("endLat"));
        orderCustomer.endLng = cursor.getDouble(cursor.getColumnIndex("endLng"));
        orderCustomer.appointTime = cursor.getLong(cursor.getColumnIndex("appointTime"));
        orderCustomer.acceptSequence = cursor.getInt(cursor.getColumnIndex("acceptSequence"));
        orderCustomer.sendSequence = cursor.getInt(cursor.getColumnIndex("sendSequence"));
        orderCustomer.num = cursor.getInt(cursor.getColumnIndex("num"));
        orderCustomer.status = cursor.getInt(cursor.getColumnIndex("status"));
        orderCustomer.photo = cursor.getString(cursor.getColumnIndex("photo"));
        orderCustomer.subStatus = cursor.getInt(cursor.getColumnIndex("subStatus"));

        return orderCustomer;
    }

    /**
     * 通过订单id和订单类型删除
     *
     * @param orderId
     * @param orderType
     */
    public static void delete(long orderId, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_zx_order_customer", "orderId = ? and orderType = ? ",
                new String[]{String.valueOf(orderId), orderType});
    }

    /**
     * 通过订单id和订单类型删除
     *
     * @param id
     */
    public static void delete(long id) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_zx_order_customer", "id = ?",
                new String[]{String.valueOf(id)});
    }

    public class OrderAddressVo {
        public String address;
        public double latitude;
        public double longitude;
        public int type;//1是起点  3是终点
    }

    public boolean saveOrUpdate() {
        //hf add
//        if (exists(orderId, Config.CITY_LINE)) {
        if (existsById(id, Config.CITY_LINE)) {
            return this.updateBase();
        } else {
            return this.save();
        }
    }

    /**
     * 更新基础信息
     *
     * @return
     */
    public boolean updateBase() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("customerId", customerId);
        values.put("name", name);
        values.put("phone", phone);
        values.put("photo", photo);
        values.put("startAddr", startAddr);
        values.put("endAddr", endAddr);
        values.put("startLat", startLat);
        values.put("startLng", startLng);
        values.put("endLat", endLat);
        values.put("endLng", endLng);
//        values.put("appointTime", appointTime);

        //hf add
        values.put("status", status);
        values.put("photo", photo);
        values.put("subStatus", subStatus);
        values.put("orderId",orderId);
        values.put("orderType", orderType);

        boolean flag = db.update("t_zx_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }


}
