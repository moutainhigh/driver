package com.easymi.component.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by developerLzh on 2017/11/3 0003.
 * 所有订单的基本信息
 */
public class BaseOrder implements Serializable {

    @SerializedName("id")
    public long orderId;

    @SerializedName("orderTypeName")
    public String orderDetailType;

    @SerializedName("business")
    public String orderType; // daijia

    @SerializedName("book_time")
    public long orderTime;//预约时间

    @SerializedName("status")
    public int orderStatus;

    @SerializedName("book_address")
    public String startPlace;

    @SerializedName("destination")
    public String endPlace;

    @SerializedName("order_no")
    public String orderNumber;

//    /**
//     * 保存数据
//     */
//    public boolean save() {
//        SqliteHelper helper = SqliteHelper.getInstance();
//        SQLiteDatabase db = helper.openSqliteDatabase();
//        ContentValues values = new ContentValues();
//        values.put("orderId", orderId);
//        values.put("orderDetailType", orderDetailType);
//        values.put("orderType", orderType);
//        values.put("orderTime", orderTime);
//        values.put("orderStatus", orderStatus);
//        values.put("startPlace", startPlace);
//        values.put("endPlace", endPlace);
//        values.put("orderNumber", orderNumber);
//        /*
//         * values.put("age", age); values.put("jialing", jialing);
//		 */
//        boolean flag = db.insert("t_driverinfo", null, values) != -1;
//        return flag;
//    }
//
//
//    private BaseOrder(Parcel in) {
//        orderId = in.readLong();
//        orderDetailType = in.readString();
//        orderType = in.readString();
//        orderTime = in.readLong();
//        orderStatus = in.readInt();
//        startPlace = in.readString();
//        endPlace = in.readString();
//        orderNumber = in.readString();
//    }
//
//    public BaseOrder() {
//    }
//
//    public static final Parcelable.Creator<BaseOrder> CREATOR = new Parcelable.Creator<BaseOrder>() {
//        @Override
//        public BaseOrder createFromParcel(Parcel in) {
//            return new BaseOrder(in);
//        }
//
//        @Override
//        public BaseOrder[] newArray(int size) {
//            return new BaseOrder[size];
//        }
//    };
//
//    @Override
//    public String toString() {
//        return "BaseOrder{" +
//                "orderId=" + orderId +
//                ", orderDetailType='" + orderDetailType + '\'' +
//                ", orderType='" + orderType + '\'' +
//                ", orderTime=" + orderTime +
//                ", orderStatus=" + orderStatus +
//                ", startPlace='" + startPlace + '\'' +
//                ", endPlace='" + endPlace + '\'' +
//                ", orderNumber='" + orderNumber + '\'' +
//                '}';
//    }
}
