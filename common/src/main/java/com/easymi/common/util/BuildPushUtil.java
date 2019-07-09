package com.easymi.common.util;

import android.util.Log;

import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.CarpoolOrder;
import com.easymi.common.entity.OrderCustomer;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.entity.PushDataLoc;
import com.easymi.common.entity.PushDataOrder;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.PushEmploy;
import com.easymi.component.entity.Vehicle;
import com.easymi.component.utils.EmUtil;
import com.google.gson.Gson;

import java.util.ArrayList;
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

public class BuildPushUtil {

    /**
     * @param buildPushData 需要推送的数据
     * @param noLimit       不限制任何
     */
    public static String buildPush(BuildPushData buildPushData, boolean noLimit) {

        EmLoc emLoc = buildPushData.emLoc;

        PushData pushData = new PushData();

        //转换一下
        Employ employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, 0));
        PushEmploy pe;
        if (employ != null) {
            pe = new PushEmploy();
            pe.id = employ.id;
            pe.name = employ.realName;
            pe.status = employ.status;
            pe.companyId = employ.companyId;
            pe.phone = employ.phone;
            pe.business = employ.serviceType;
            pe.sex = employ.sex;
            if (Vehicle.exists(employ.id)) {
                Vehicle vehicle = Vehicle.findByEmployId(employ.id);
                pe.vehicleNo = vehicle.vehicleNo;
                pe.modelId = vehicle.vehicleModel;
                Log.e("BuildPushUtil", "buildPush " + pe.modelId);
            } else {
                pe.vehicleNo = "";
            }
            pushData.serviceType = employ.serviceType;
        } else {
            //司机信息异常不处理
            return null;
        }

        pushData.driver = pe;
        pushData.appKey = EmUtil.getAppKey();

        pushData.location = new PushDataLoc();
        pushData.location.latitude = emLoc.latitude;
        pushData.location.longitude = emLoc.longitude;
        pushData.location.speed = emLoc.speed;
        pushData.location.locationType = emLoc.locationType;
        pushData.location.appKey = EmUtil.getAppKey();
//        pushData.calc.darkCost = buildPushData.darkCost;
//        pushData.calc.darkMileage = buildPushData.darkMileage;
        pushData.location.positionTime = System.currentTimeMillis() / 1000;
        pushData.location.accuracy = (float) emLoc.accuracy;

        pushData.location.adCode = emLoc.adCode;
        pushData.location.cityCode = emLoc.cityCode;
        pushData.location.bearing = emLoc.bearing;
        pushData.location.provider = emLoc.provider;
        pushData.location.altitude = emLoc.altitude;
        pushData.location.time = System.currentTimeMillis() / 1000;

        List<PushDataOrder> orderList = new ArrayList<>();

        for (DymOrder dymOrder : DymOrder.findAll()) {
            PushDataOrder dataOrder = new PushDataOrder();
            if (dymOrder.orderType.equals(Config.CITY_LINE)) {
                if (dymOrder.orderStatus == 30 || dymOrder.orderStatus == 35) {
                    for (OrderCustomer orderCustomer : OrderCustomer.findByIDTypeOrderByAcceptSeq(dymOrder.orderId, dymOrder.orderType)) {
                        dataOrder.orderId = orderCustomer.orderId;
                        dataOrder.orderType = orderCustomer.orderType;

                        dataOrder.business = orderCustomer.orderType;
                        dataOrder.passengerId = orderCustomer.customerId;
                    }
                    orderList.add(dataOrder);
                }
            } else if (dymOrder.orderType.equals(Config.CARPOOL)) {
                if (dymOrder.orderStatus >= 10 && dymOrder.orderStatus <= 30) {
                    for (CarpoolOrder carpoolOrder : CarpoolOrder.findByIDTypeOrderByAcceptSeq(dymOrder.orderId, dymOrder.orderType)) {
                        dataOrder.orderId = carpoolOrder.id;
                        dataOrder.orderType = carpoolOrder.orderType;

                        dataOrder.business = carpoolOrder.orderType;
                        dataOrder.passengerId = carpoolOrder.passengerId;

                        dataOrder.status = carpoolOrder.status;
                    }
                    orderList.add(dataOrder);
                }
            } else {
                dataOrder.orderId = dymOrder.orderId;
                dataOrder.orderType = dymOrder.orderType;
                dataOrder.status = dymOrder.orderStatus;
                dataOrder.addedKm = dymOrder.addedKm;
                dataOrder.addedFee = dymOrder.addedFee;

                dataOrder.business = dymOrder.orderType;
                dataOrder.passengerId = dymOrder.passengerId;

                if (dymOrder.orderType.equals(Config.DAIJIA)) {

                } else if (dymOrder.orderType.equals(Config.ZHUANCHE)
                        || dymOrder.orderType.equals(Config.TAXI)
                        || dymOrder.orderType.equals(Config.GOV)) {

                    dataOrder.status = dymOrder.orderStatus;

                    dataOrder.peakMile = dymOrder.peakMile;
                    dataOrder.nightTime = dymOrder.nightTime;
                    dataOrder.nightMile = dymOrder.nightMile;
                    dataOrder.nightTimePrice = dymOrder.nightTimePrice;

                    dataOrder.orderNo = dymOrder.orderNo;
                }
                if (dataOrder.status != 0) {
                    orderList.add(dataOrder);
                }
            }
        }

        pushData.location.orderInfo = orderList;

//        /**
//         * 历史未上传的位置信息
//         */
//        String cacheStr = FileUtil.readPushCache();
//        List<PushData> dataList = new ArrayList<>();
//        if (!StringUtils.isBlank(cacheStr)) {
//            List<PushData> list = GsonUtil.parseToList(cacheStr, PushData[].class);
//            if (list != null && !list.isEmpty()) {
//                Log.e("MqttManager", "缓存点");
//                dataList.addAll(list);
//            }
//        }
//
//        //本次的位置信息
//        dataList.add(pushData);
//
//        List<PushData> newestDataList = new ArrayList<>();
//
//        //能上传网络定位或者不限制任何
//        boolean canPushNetLoc = GPSSetting.getInstance().getNetEnable() || noLimit;
//        if (!canPushNetLoc) {
//            for (PushData pd : dataList) {
//                if (pd != null && pd.location != null && pd.location.locationType == 1) {
//                    //只上传GPS类型的定位
//                    newestDataList.add(pd);
//                }
//            }
//        } else {
//            newestDataList.addAll(dataList);
//        }
//        PushBean pushBean = new PushBean("gps", newestDataList);

        PushBean pushBean = new PushBean("gps", pushData);
        String pushStr = new Gson().toJson(pushBean);
        return pushStr;
    }

}
