package com.easymi.common.entity;

import com.chad.library.adapter.base.entity.MultiItemEntity;
import com.easymi.common.R;
import com.easymi.common.mvp.grab.GrabActivity2;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.BaseOrder;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 *
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class MultipleOrder extends BaseOrder implements Serializable, MultiItemEntity {


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

    public int orderNum;
    public int ticketNum;
    public int noPay;
    public int haveNewPassenger;

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

    /**
     * 获取订单预约起点
     *
     * @return
     */
    public Address getStartSite() {
        Address start = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0) {
            for (Address address : orderAddressVos) {
                if (address.type == 1) {
                    return address;
                }
            }
            if (start == null) {
                start = new Address();
                start.address = XApp.getInstance().getResources().getString(R.string.unknown_site);
            }
        } else {
            start = new Address();
            start.address = XApp.getInstance().getResources().getString(R.string.unknown_site);
        }
        return start;
    }

    /**
     * 获取订单预约终点
     *
     * @return
     */
    public Address getEndSite() {
        Address end = null;
        if (orderAddressVos != null && orderAddressVos.size() != 0) {
            for (Address address : orderAddressVos) {
                if (address.type == 3) {
                    return address;
                }
            }
            if (end == null) {
                end = new Address();
                end.address = XApp.getInstance().getResources().getString(R.string.unknown_site);
            }
        } else {
            end = new Address();
            end.address = XApp.getInstance().getResources().getString(R.string.unknown_site);
        }
        return end;
    }

    /**
     * 获取业务类型名称
     *
     * @return
     */
    public String getOrderType() {
        String orderType = null;
        if (serviceType.equals(Config.ZHUANCHE)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_zhuanche);
        } else if (serviceType.equals(Config.TAXI)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_taxi);
        } else if (serviceType.equals(Config.CITY_LINE)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_zhuanxian);
        } else if (serviceType.equals(Config.CHARTERED)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_chartered);
        } else if (serviceType.equals(Config.RENTAL)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_rental);
        } else if (serviceType.equals(Config.COUNTRY)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_custombus);
        } else if (serviceType.equals(Config.GOV)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_gov);
        } else if (serviceType.equals(Config.CARPOOL)) {
            orderType = XApp.getInstance().getResources().getString(R.string.create_carpool);
        }
        return orderType;
    }


}
