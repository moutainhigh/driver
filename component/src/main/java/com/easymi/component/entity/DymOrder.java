package com.easymi.component.entity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.easymi.component.db.SqliteHelper;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by liuzihao on 2017/12/20.
 * <p>
 * 本地保存的费用信息字段 保存到数据库的
 */

public class DymOrder implements Serializable {

    public long id;

    public long orderId;

    public String orderType;

    public long passengerId;

    //起步费
    @SerializedName("start_price")
    public double startFee;

    //等候时间 分
    @SerializedName("wait_time")
    public int waitTime;

    //等候费
    @SerializedName("wait_fee")
    public double waitTimeFee;

    //行驶时间 分
    @SerializedName("time")
    public int travelTime;

    //行驶时间费
    @SerializedName("time_fee")
    public double travelFee;

    //行驶里程
    public double distance;

    //行驶时间费
    @SerializedName("distance_fee")
    public double disFee;

    //总价(计价算出来的钱)
    public double totalFee;

    //订单状态
    public int orderStatus;

    //垫付的钱
    @SerializedName("cross_fee")
    public double paymentFee;

    //附加的服务费用
    @SerializedName("other_fee")
    public double extraFee;

    //备注
    public String remark;

    //优惠券抵扣的金额
    @SerializedName("coupon_fee")
    public double couponFee;

    //订单总价钱 计价算出来的钱 + 垫付 + 附加费用 (不算优惠券的钱)
    @SerializedName("total_fee")
    public double orderTotalFee;

    //还需支付的钱 订单总价钱 - 优惠金额 - 预付费
    @SerializedName("real_pay")
    public double orderShouldPay;

    //预付费用
    @SerializedName("pre_pay")
    public double prepay;

    public DymOrder(long orderId, String orderType, long passengerId, int orderStatus) {
        this.orderId = orderId;
        this.orderType = orderType;
        this.passengerId = passengerId;
        this.orderStatus = orderStatus;
    }

    public DymOrder() {
    }

    /**
     * 保存数据
     */
    public boolean save() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("orderId", orderId);
        values.put("orderType", orderType);
        values.put("passengerId", passengerId);
        values.put("startFee", startFee);
        values.put("waitTime", waitTime);
        values.put("waitTimeFee", waitTimeFee);
        values.put("travelTime", travelTime);
        values.put("travelFee", travelFee);
        values.put("totalFee", totalFee);
        values.put("distance", distance);
        values.put("disFee", disFee);
        values.put("orderStatus", orderStatus);

        values.put("paymentFee", paymentFee);
        values.put("extraFee", extraFee);
        values.put("remark", remark);
        values.put("couponFee", couponFee);
        values.put("orderTotalFee", orderTotalFee);
        values.put("orderShouldPay", orderShouldPay);
        values.put("prepay", prepay);
        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.insert("t_dyminfo", null, values) != -1;
        return flag;
    }

    /**
     * 订单是否存在
     *
     * @param id
     * @return
     */
    public static boolean exists(long id) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_dyminfo where id = ? ",
                new String[]{String.valueOf(id)});
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
     * 判断订单是否存在
     */
    public static boolean exists(long orderId, String orderType) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        Cursor cursor = db.rawQuery(
                "select count(*) from t_dyminfo where orderId = ? and orderType = ?",
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
    public static DymOrder findByIDType(long orderId, String orderType) {

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_dyminfo where orderId = ? and orderType = ? "
                , new String[]{String.valueOf(orderId), orderType});

        try {
            if (cursor.moveToNext()) {
                return cursorToOrder(cursor);
            }
        } finally {
            cursor.close();
        }
        return null;
    }

    public static List<DymOrder> findAll() {

        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();

        Cursor cursor = db.rawQuery("select * from t_dyminfo", new String[]{});

        List<DymOrder> list = new LinkedList<>();

        try {
            while (cursor.moveToNext()) {
                list.add(cursorToOrder(cursor));
            }
        } finally {
            cursor.close();
        }
        return list;
    }

    private static DymOrder cursorToOrder(Cursor cursor) {
        DymOrder orderInfo = new DymOrder();
        orderInfo.id = cursor.getLong(cursor.getColumnIndex("id"));
        orderInfo.orderId = cursor.getLong(cursor.getColumnIndex("orderId"));
        orderInfo.orderType = cursor.getString(cursor.getColumnIndex("orderType"));
        orderInfo.startFee = cursor.getDouble(cursor.getColumnIndex("startFee"));
        orderInfo.waitTime = cursor.getInt(cursor.getColumnIndex("waitTime"));
        orderInfo.waitTimeFee = cursor.getDouble(cursor.getColumnIndex("waitTimeFee"));
        orderInfo.travelTime = cursor.getInt(cursor.getColumnIndex("travelTime"));
        orderInfo.travelFee = cursor.getDouble(cursor.getColumnIndex("travelFee"));
        orderInfo.totalFee = cursor.getDouble(cursor.getColumnIndex("totalFee"));
        orderInfo.distance = cursor.getDouble(cursor.getColumnIndex("distance"));
        orderInfo.disFee = cursor.getDouble(cursor.getColumnIndex("disFee"));

        orderInfo.orderStatus = cursor.getInt(cursor.getColumnIndex("orderStatus"));

        orderInfo.paymentFee = cursor.getDouble(cursor.getColumnIndex("paymentFee"));
        orderInfo.extraFee = cursor.getDouble(cursor.getColumnIndex("extraFee"));
        orderInfo.remark = cursor.getString(cursor.getColumnIndex("remark"));
        orderInfo.couponFee = cursor.getDouble(cursor.getColumnIndex("couponFee"));
        orderInfo.orderTotalFee = cursor.getDouble(cursor.getColumnIndex("orderTotalFee"));
        orderInfo.orderShouldPay = cursor.getDouble(cursor.getColumnIndex("orderShouldPay"));
        orderInfo.prepay = cursor.getDouble(cursor.getColumnIndex("prepay"));

        return orderInfo;
    }

    public boolean updateStatus() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("orderStatus", orderStatus);

        boolean flag = db.update("t_dyminfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean updateAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("orderId", orderId);
        values.put("orderType", orderType);
        values.put("startFee", startFee);
        values.put("waitTime", waitTime);
        values.put("waitTimeFee", waitTimeFee);
        values.put("travelTime", travelTime);
        values.put("travelFee", travelFee);
        values.put("totalFee", totalFee);
        values.put("distance", distance);
        values.put("disFee", disFee);

        values.put("orderStatus", orderStatus);

        values.put("paymentFee", paymentFee);
        values.put("extraFee", extraFee);
        values.put("remark", remark);
        values.put("couponFee", couponFee);
        values.put("orderTotalFee", orderTotalFee);
        values.put("orderShouldPay", orderShouldPay);
        values.put("prepay", prepay);
        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_dyminfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean updateFee() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("startFee", startFee);
        values.put("waitTime", waitTime);
        values.put("waitTimeFee", waitTimeFee);
        values.put("travelTime", travelTime);
        values.put("travelFee", travelFee);
        values.put("totalFee", totalFee);
        values.put("distance", distance);
        values.put("disFee", disFee);
        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_dyminfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }

    public boolean updateConfirm() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        ContentValues values = new ContentValues();
        values.put("paymentFee", paymentFee);
        values.put("extraFee", extraFee);
        values.put("remark", remark);
        values.put("couponFee", couponFee);
        values.put("orderTotalFee", orderTotalFee);
        values.put("orderShouldPay", orderShouldPay);
        values.put("prepay", prepay);
        /*
         * values.put("age", age); values.put("jialing", jialing);
		 */
        boolean flag = db.update("t_dyminfo", values, " id = ? ",
                new String[]{String.valueOf(id)}) == 1;
        return flag;
    }


    public boolean saveOrUpdate() {
        if (exists(id)) {
            return this.updateAll();
        } else {
            return this.save();
        }
    }

    public static void deleteAll() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_dyminfo", null, null);
    }

    public static void deleteById(Long id) {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_dyminfo", "id = ?", new String[]{String.valueOf(id)});
    }

    public void delete() {
        SqliteHelper helper = SqliteHelper.getInstance();
        SQLiteDatabase db = helper.openSqliteDatabase();
        db.delete("t_dyminfo", "id = ?", new String[]{String.valueOf(id)});
    }
}
