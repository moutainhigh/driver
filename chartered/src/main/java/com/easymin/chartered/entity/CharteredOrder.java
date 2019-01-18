package com.easymin.chartered.entity;

import com.easymi.common.entity.Address;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.BaseOrder;

import java.io.Serializable;
import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CharteredOrder
 *@Author: shine
 * Date: 2018/12/24 下午1:53
 * Description:
 * History:
 * @author hufeng
 */
public class CharteredOrder extends BaseOrder implements Serializable {

    public List<Address> orderAddressVos;
    /**
     * 获取订单预约起点
     * @return
     */
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
                start.address = XApp.getInstance().getResources().getString(com.easymi.common.R.string.unknown_site);
            }
        }else {
            start = new Address();
            start.address = XApp.getInstance().getResources().getString(com.easymi.common.R.string.unknown_site);
        }
        return start;
    }

    /**
     * 获取订单预约终点
     * @return
     */
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
                end.address = XApp.getInstance().getResources().getString(com.easymi.common.R.string.unknown_site);
            }
        }else {
            end = new Address();
            end.address = XApp.getInstance().getResources().getString(com.easymi.common.R.string.unknown_site);
        }
        return end;
    }
}
