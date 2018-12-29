package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.easymi.common.mvp.grab.GrabActivity2;
import com.easymi.component.Config;
import com.easymi.component.entity.BaseOrder;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by liuzihao on 2017/11/15.
 * <p>
 * 复合型订单，包含了所有业务订单的字段
 */

public class MultipleOrder extends BaseOrder implements Serializable, MultiItemEntity {
//    /**
//     * 是否是预约单
//     */
//    public int isBookOrder;//1是预约单 2是即时单
//
//    @SerializedName("company_id")
//    public long companyId;
//
//    @SerializedName("company_name")
//    public String companyName;

    public int countTime = GrabActivity2.GRAB_TOTAL_TIME;

    public List<Address> orderAddressVos;

//专车特殊字段
    /**
     * 专车业务id
     */
    private Long businessId;

    /**
     * 专车车型id
     */
    private Long modelId;


    public static final int ITEM_HEADER = 1;//头
    public static final int ITEM_POSTER = 2;//内容
    public static final int ITEM_DESC = 3;//底

    public int viewType;

    public MultipleOrder() {
    }

    public MultipleOrder(int type) {
        this.viewType = type;
    }

    @Override
    public int getItemType() {
        return viewType;
    }

    @Override
    public String toString() {
        return "MultipleOrder{" +
                "orderId=" + id +
//                ", orderDetailType='" + orderDetailType + '\'' +
//                ", orderType='" + orderType + '\'' +
//                ", orderTime=" + orderTime +
//                ", isBookOrder=" + isBookOrder +
//                ", orderStatus=" + orderStatus +
//                ", companyId=" + companyId +
//                ", startPlace='" + startPlace + '\'' +
//                ", companyName='" + companyName + '\'' +
//                ", countTime=" + countTime +
//                ", endPlace='" + endPlace + '\'' +
//                ", orderNumber='" + orderNumber + '\'' +
//                ", addresses=" + addresses +
//                ", passengerId=" + passengerId +
//                ", passengerName='" + passengerName + '\'' +
//                ", viewType=" + viewType +
//                ", passengerPhone='" + passengerPhone + '\'' +
//                ", orderMoney=" + orderMoney +
//                ", baoxiaoStatus=" + baoxiaoStatus +
                '}';
    }

    public Address getStartSite(){
        Address start = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0){
            for (Address address : orderAddressVos) {
                if (address.type == 1) {
                    return address;
                }
            }
            if (start == null){
                start = new Address();
                start.address = "未知位置";
            }
        }else {
            start = new Address();
            start.address = "未知位置";
        }
        return start;
    }


    public Address getEndSite(){
        Address end = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0){
            for (Address address : orderAddressVos) {
                if (address.type == 3) {
                    return address;
                }
            }
            if (end == null){
                end = new Address();
                end.address = "未知位置";
            }
        }else {
            end = new Address();
            end.address = "未知位置";
        }
        return end;
    }


    public String getOrderType(){
        String orderType = null;
        if (serviceType.equals(Config.ZHUANCHE)){
            orderType = "城内打车";
        }else if (serviceType.equals(Config.TAXI)){
            orderType = "出租车";
        }else if (serviceType.equals(Config.CITY_LINE)){
            orderType = "城际专线";
        }else if (serviceType.equals(Config.CHARTERED)){
            orderType = "定制包车";
        }else if (serviceType.equals(Config.RENTAL)){
            orderType = "包车租车";
        } else if (serviceType.equals(Config.COUNTRY)) {
            orderType = "客运班车";
        }
        return orderType;
    }


}
