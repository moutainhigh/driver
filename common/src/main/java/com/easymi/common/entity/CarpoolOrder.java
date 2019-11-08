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
    public String startAddress;

    /**
     * 终点
     */
    public String endAddress;

    public double startLatitude;
    public double startLongitude;
    public double endLatitude;
    public double endLongitude;
    /**
     * 是否已经联系过客户（点击过拨打用户按钮）
     * 0未联系 1已经联系过
     */
    public int isContract;

    public int advanceAssign;

    public double money;



    /**
     * 订单顺序
     */
    public int sequence;

    /**
     * 上车站点id
     */
    public long startStationId;

    /**
     * 下车站点id
     */
    public long endStationId;

    /**
     * 站点订单列表初始查询的下标
     */
    public int beginIndex;


    /**
     * 当前订单在当前站点的订单集合的顺序
     */
    public int index;


///选座模式新增字段  start

    /**
     * 座位信息（位置）
     */
    public String sorts;

    /**
     * 类型 1-五座 2-七座
     */
    public int type;

    /**
     * 座位类型信息（1儿童还是成人2）
     */
    public String sortsType;

///选座模式新增字段  end

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

    public class OrderAddressVo {
        public String address;
        public double latitude;
        public double longitude;
        //1是起点  3是终点
        public int type;
    }


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
        values.put("startAddress", startAddress);
        values.put("endAddress", endAddress);
        values.put("startLatitude", startLatitude);
        values.put("startLongitude", startLongitude);
        values.put("endLatitude", endLatitude);
        values.put("endLongitude", endLongitude);
        values.put("advanceAssign", advanceAssign);
        values.put("money", money);
        values.put("waitMinute", waitMinute);
        values.put("orderRemark", orderRemark);

        boolean flag = db.insert("t_cp_order_customer", null, values) != -1;
        return flag;
    }

    public void updateAdvanceAssign() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("advanceAssign", advanceAssign);
        db.update("t_cp_order_customer", values, " id = ? ",
                new String[]{String.valueOf(id)});
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
    public static boolean existsByOrderId(long orderId, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_cp_order_customer where id = ? and orderType = ?",
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

//    /**
//     * 根据ID和type查询数据
//     */
//    public static List<CarpoolOrder> findByIDTypeOrderBySendSeq(long orderId, String serviceType) {
//        List<CarpoolOrder> carpoolOrders = new ArrayList<>();
//
//        SqliteHelper helper = SqliteHelper.getInstance();
//        SQLiteDatabase db = helper.openSqliteDatabase();
//
//        Cursor cursor = db.rawQuery("select * from t_cp_order_customer where orderId = ? and serviceType = ? order by sendSequence"
//                , new String[]{String.valueOf(orderId), serviceType});
//
//        try {
//            while (cursor.moveToNext()) {
//                carpoolOrders.add(cursorToOrder(cursor));
//            }
//        } finally {
//            cursor.close();
//        }
//
//
//        //把状态大的放在前面去，因为状态为最大的是已跳过的
//        Collections.sort(carpoolOrders, (o1, o2) -> {
//            if (o1.customeStatus < o2.customeStatus) {
//                return 1;
//            } else if (o1.customeStatus > o2.customeStatus) {
//                return -1;
//            } else {
//                return 0;
//            }
//        });
//
//        return carpoolOrders;
//    }


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
        carpoolOrder.startAddress = cursor.getString(cursor.getColumnIndex("startAddress"));
        carpoolOrder.endAddress = cursor.getString(cursor.getColumnIndex("endAddress"));
        carpoolOrder.startLatitude = cursor.getDouble(cursor.getColumnIndex("startLatitude"));
        carpoolOrder.startLongitude = cursor.getDouble(cursor.getColumnIndex("startLongitude"));
        carpoolOrder.endLatitude = cursor.getDouble(cursor.getColumnIndex("endLatitude"));
        carpoolOrder.endLongitude = cursor.getDouble(cursor.getColumnIndex("endLongitude"));
        carpoolOrder.waitMinute = cursor.getInt(cursor.getColumnIndex("waitMinute"));
        carpoolOrder.isContract = cursor.getInt(cursor.getColumnIndex("isContract"));
        carpoolOrder.advanceAssign = cursor.getInt(cursor.getColumnIndex("advanceAssign"));
        carpoolOrder.money = cursor.getDouble(cursor.getColumnIndex("money"));
        carpoolOrder.orderRemark = cursor.getString(cursor.getColumnIndex("orderRemark"));
        return carpoolOrder;
    }

    /**
     * 通过订单id和订单类型删除（整个班次）
     *
     * @param id
     * @param orderType
     */
    public static void delete(long id, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_order_customer", "id = ? and orderType = ? ",
                new String[]{String.valueOf(id), orderType});
    }

    /**
     * 通过订单id删除(单个订单)
     *
     * @param orderId
     */
    public static void delete(long orderId) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_cp_order_customer", "orderId = ?",
                new String[]{String.valueOf(orderId)});
    }


    public boolean saveOrUpdate() {
        if (existsByOrderId(orderId, Config.CARPOOL)) {
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

        values.put("startAddress", startAddress);
        values.put("endAddress", endAddress);
        values.put("startLatitude", startLatitude);
        values.put("startLongitude", startLongitude);
        values.put("endLatitude", endLatitude);
        values.put("endLongitude", endLongitude);

        values.put("orderId", orderId);
        values.put("waitMinute", waitMinute);
        values.put("advanceAssign", advanceAssign);
        values.put("money", money);

        values.put("id", id);

        boolean flag = db.update("t_cp_order_customer", values, " orderId = ? ",
                new String[]{String.valueOf(orderId)}) == 1;
        return flag;
    }

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
        if (status <= CARPOOL_STATUS_ARRIVED) {
            return "未接";
        }
        if (status == CARPOOL_STATUS_RUNNING) {
            return "已接";
        }
        if (status == CARPOOL_STATUS_SKIP) {
            return "跳过";
        }
        if (status == CARPOOL_STATUS_RUNNING) {
            return "未送";
        }
        if (status == CARPOOL_STATUS_FINISH || status == CARPOOL_STATUS_REVIEW) {
            return "已送";
        }
        return "";
    }

    @Override
    public boolean equals(Object obj) {
        CarpoolOrder other = (CarpoolOrder) obj;
        if (orderId == other.orderId){
            return true;
        }
        return false;
    }
}
