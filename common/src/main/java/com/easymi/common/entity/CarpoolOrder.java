package com.easymi.common.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.Config;
import com.easymi.component.db.SqliteHelper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CarpoolOrder
 * @Author: hufeng
 * @Date: 2019/2/21 下午5:17
 * @Description: 订单乘客信息
 * @History:
 */
public class CarpoolOrder implements Serializable {


    /**
     * 主键
     */
    public long id;

    /**
     * 订单Id
     */
    public long orderId;

    /**
     * 发车时间/yyyy-MM-dd HH:mm:ss
     */
    public long bookTime;

    /**
     * 乘客主键
     */
    public long passengerId;

    /**
     * 乘客姓名
     */
    public String passengerName;

    /**
     * 乘客电话
     */
    public String passengerPhone;

    /**
     * 客户头像
     */
    public String avatar;
    /**
     * 创建时间/yyyy-MM-dd HH:mm:ss
     */
    public long created;
    /**
     * 线路名称
     */
    public String lineName;
    /**
     * 线路类型(1城际拼车基本路线2城际拼车机场专线)
     */
    public int lineType;
    /**
     * 票数
     */
    public int ticketNumber;

    /**
     * 时间段
     */
    public String timeSlot;

    /**
     * 订单状态
     */
    public int status;

    /**
     * 班次ID
     */
    public long scheduleId;

    /**
     * 订单类型
     */
    public String orderType;

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
     * 是否已经联系过客户（点击过拨打用户按钮）
     * 0未联系 1已经联系过
     */
    public int isContract;


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
    public int customeStatus;
    /**
     * (接客户时的子状态) 0未到达预约地 1等待客户上车中
     * (接客户时的子状态) 0为前往  1前往未到达 2等待客户上车中
     */
    public int subStatus;

    /**
     * 等候过期分钟数
     */
    public int waitMinute;


    //没存数据库的字段
    /**
     * 订单类型
     */
    public String serviceType;

    /**
     * 订单备注
     */
    public String orderRemark;

    /**
     * 起点
     */
    public String startAddress;

    /**
     * 终点
     */
    public String endAddress;
    /**
     * 服务时间天
     */
    public String day;

    /**
     * 起点站
     */
    public String startStationName;

    /**
     * 终点站
     */
    public String endStationName;

    /**
     * 公司电话
     */
    public String companyPhone;

    /**
     * 是否转单  1-转单
     */
    public int orderChange;


    public boolean save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("id", id);
        values.put("orderId", orderId);
        values.put("bookTime", bookTime);
        values.put("passengerId", passengerId);
        values.put("passengerPhone", passengerPhone);
        values.put("passengerName", passengerName);
        values.put("avatar", avatar);
        values.put("created", created);
        values.put("lineName", lineName);
        values.put("lineType", lineType);
        values.put("ticketNumber", ticketNumber);
        values.put("timeSlot", timeSlot);
        values.put("status", status);
        values.put("scheduleId", scheduleId);
        values.put("orderType", orderType);
        values.put("startAddr", startAddr);
        values.put("endAddr", endAddr);
        values.put("startLat", startLat);
        values.put("startLng", startLng);
        values.put("endLat", endLat);
        values.put("endLng", endLng);
        values.put("acceptSequence", acceptSequence);
        values.put("sendSequence", sendSequence);
        values.put("num", num);
        values.put("customeStatus", customeStatus);
        values.put("subStatus", subStatus);
        values.put("waitMinute", waitMinute);

        boolean flag = db.insert("t_cp_order_customer", null, values) != -1;
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
        values.put("customeStatus", customeStatus);

        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    /**
     * 更新是否拨打客户电话状态
     *
     * @return
     */
    public boolean updateIsContract() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("isContract", isContract);

        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
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
        values.put("bookTime", bookTime);

        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
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

        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
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

        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }


    /**
     * 判断数据库中是否已保存订单相关的CarpoolOrder
     */
    public static boolean exists(long orderId, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_cp_order_customer where orderId = ? and orderType = ?",
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
     * 判断数据库中是否已保存订单相关的CarpoolOrder
     */
    public static boolean existsById(long id, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_cp_order_customer where id = ? and orderType = ?",
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
    public static List<CarpoolOrder> findByIDTypeOrderByAcceptSeq(long orderId, String orderType) {
        List<CarpoolOrder> carpoolOrders = new ArrayList<>();

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_cp_order_customer where orderId = ? and orderType = ? order by acceptSequence"
                , new String[]{String.valueOf(orderId), orderType});

        try {
            while (cursor.moveToNext()) {
                carpoolOrders.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }
        return carpoolOrders;
    }

    /**
     * 根据ID和type查询数据
     */
    public static List<CarpoolOrder> findByIDTypeOrderBySendSeq(long orderId, String orderType) {
        List<CarpoolOrder> carpoolOrders = new ArrayList<>();

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_cp_order_customer where orderId = ? and orderType = ? order by sendSequence"
                , new String[]{String.valueOf(orderId), orderType});

        try {
            while (cursor.moveToNext()) {
                carpoolOrders.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }


        //把状态大的放在前面去，因为状态为最大的是已跳过的
        Collections.sort(carpoolOrders, (o1, o2) -> {
            if (o1.customeStatus < o2.customeStatus) {
                return 1;
            } else if (o1.customeStatus > o2.customeStatus) {
                return -1;
            } else {
                return 0;
            }
        });

        return carpoolOrders;
    }


    private static CarpoolOrder cursorToOrder(Cursor cursor) {
        CarpoolOrder carpoolOrder = new CarpoolOrder();
        carpoolOrder.id = cursor.getLong(cursor.getColumnIndex("id"));
        carpoolOrder.orderId = cursor.getLong(cursor.getColumnIndex("orderId"));
        carpoolOrder.bookTime = cursor.getLong(cursor.getColumnIndex("bookTime"));
        carpoolOrder.passengerId = cursor.getLong(cursor.getColumnIndex("passengerId"));
        carpoolOrder.passengerPhone = cursor.getString(cursor.getColumnIndex("passengerPhone"));
        carpoolOrder.passengerName = cursor.getString(cursor.getColumnIndex("passengerName"));
        carpoolOrder.avatar = cursor.getString(cursor.getColumnIndex("avatar"));
        carpoolOrder.created = cursor.getLong(cursor.getColumnIndex("created"));
        carpoolOrder.lineName = cursor.getString(cursor.getColumnIndex("lineName"));
        carpoolOrder.lineType = cursor.getInt(cursor.getColumnIndex("lineType"));
        carpoolOrder.ticketNumber = cursor.getInt(cursor.getColumnIndex("ticketNumber"));
        carpoolOrder.timeSlot = cursor.getString(cursor.getColumnIndex("timeSlot"));
        carpoolOrder.status = cursor.getInt(cursor.getColumnIndex("status"));
        carpoolOrder.scheduleId = cursor.getLong(cursor.getColumnIndex("scheduleId"));
        carpoolOrder.orderType = cursor.getString(cursor.getColumnIndex("orderType"));
        carpoolOrder.startAddr = cursor.getString(cursor.getColumnIndex("startAddr"));
        carpoolOrder.endAddr = cursor.getString(cursor.getColumnIndex("endAddr"));
        carpoolOrder.startLat = cursor.getDouble(cursor.getColumnIndex("startLat"));
        carpoolOrder.startLng = cursor.getDouble(cursor.getColumnIndex("startLng"));
        carpoolOrder.endLat = cursor.getDouble(cursor.getColumnIndex("endLat"));
        carpoolOrder.endLng = cursor.getDouble(cursor.getColumnIndex("endLng"));
        carpoolOrder.acceptSequence = cursor.getInt(cursor.getColumnIndex("acceptSequence"));
        carpoolOrder.sendSequence = cursor.getInt(cursor.getColumnIndex("sendSequence"));
        carpoolOrder.num = cursor.getInt(cursor.getColumnIndex("num"));
        carpoolOrder.customeStatus = cursor.getInt(cursor.getColumnIndex("customeStatus"));
        carpoolOrder.subStatus = cursor.getInt(cursor.getColumnIndex("subStatus"));
        carpoolOrder.waitMinute = cursor.getInt(cursor.getColumnIndex("waitMinute"));
        carpoolOrder.isContract = cursor.getInt(cursor.getColumnIndex("isContract"));

        return carpoolOrder;
    }

    /**
     * 通过订单id和订单类型删除（整个班次）
     *
     * @param orderId
     * @param orderType
     */
    public static void delete(long orderId, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_order_customer", "orderId = ? and orderType = ? ",
                new String[]{String.valueOf(orderId), orderType});
    }

    /**
     * 通过订单id删除(单个订单)
     *
     * @param orderId
     */
    public static void delete(long orderId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_order_customer", "id = ?",
                new String[]{String.valueOf(orderId)});
    }

    public class OrderAddressVo {
        public String address;
        public double latitude;
        public double longitude;
        public int type;//1是起点  3是终点
    }

    public boolean saveOrUpdate() {
        if (existsById(id, Config.CARPOOL)) {
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
        values.put("passengerId", passengerId);
        values.put("passengerName", passengerName);
        values.put("passengerPhone", passengerPhone);
        values.put("avatar", avatar);
        values.put("lineName", lineName);
        values.put("lineType", lineType);
        values.put("ticketNumber", ticketNumber);
        values.put("timeSlot", timeSlot);
        values.put("status", status);
        values.put("scheduleId", scheduleId);
        values.put("orderType", orderType);

        values.put("startAddr", startAddr);
        values.put("endAddr", endAddr);
        values.put("startLat", startLat);
        values.put("startLng", startLng);
        values.put("endLat", endLat);
        values.put("endLng", endLng);

        values.put("customeStatus", customeStatus);
        values.put("subStatus", subStatus);
        values.put("orderId", orderId);
        values.put("waitMinute", waitMinute);

        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

//    /**
//     * 更新接人时间
//     *
//     * @return
//     */
//    public boolean updateBookTime() {
//        SqliteHelper helper = SqliteHelper.getInstance();
//        SQLiteDatabase db = helper.openSqliteDatabase();
//        ContentValues values = new ContentValues();
//
//        values.put("appointTime", appointTime);
//
//        boolean flag = db.update("t_cp_order_customer", values, " id = ? ",
//                new String[]{String.valueOf(id)}) == 1;
//        return flag;
//    }


    /**
     * 城际拼车订单未支付
     */
    public static final int CARPOOL_STATUS_PAY = 1;
    /**
     * 城际拼车订单已支付未指派
     */
    public static final int CARPOOL_STATUS_NEW = 5;
    /**
     * 城际拼车订单已指派
     */
    public static final int CARPOOL_STATUS_ASSIGN = 10;
    /**
     * 城际拼车订单前往预约地
     */
    public static final int CARPOOL_STATUS_START = 15;
    /**
     * 城际拼车订单已到达预约地(默认开始等待)
     */
    public static final int CARPOOL_STATUS_ARRIVED = 20;
    /**
     * 城际拼车订单行程中(已经上车)
     */
    public static final int CARPOOL_STATUS_RUNNING = 25;
    /**
     * 城际拼车订单已到达目的地
     */
    public static final int CARPOOL_STATUS_FINISH = 30;
    /**
     * 城际拼车订单已跳过
     */
    public static final int CARPOOL_STATUS_SKIP = 35;
    /**
     * 城际拼车订单已评价
     */
    public static final int CARPOOL_STATUS_REVIEW = 40;
    /**
     * 城际拼车订单已取消
     */
    public static final int CARPOOL_STATUS_CANCEL = 45;

    public String getOrderStatus() {
        if (customeStatus == 0) {
            return "未接";
        }
        if (customeStatus == 1) {
            return "已接";
        }
        if (customeStatus == 2) {
            return "跳过";
        }
        if (customeStatus == 3) {
            return "未送";
        }
        if (customeStatus == 4) {
            return "已送";
        }
        if (customeStatus == 5) {
            return "跳过";
        }
        return "";
    }

}
