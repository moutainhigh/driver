package com.easymi.common.util;

import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.entity.PushDataLoc;
import com.easymi.common.entity.PushDataOrder;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.entity.BaseEmploy;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/12/22.
 */

public class BuildPushUtil {

    public static String buildPush(EmLoc emLoc) {

        PushData pushData = new PushData();
        pushData.employ = new BaseEmploy().employ2This();
        pushData.calc = new PushDataLoc();
        pushData.calc.lat = emLoc.latitude;
        pushData.calc.lng = emLoc.longitude;
        pushData.calc.appKey = Config.APP_KEY;
        pushData.calc.darkCost = 0;
        pushData.calc.darkMileage = 0;
        pushData.calc.positionTime = System.currentTimeMillis() / 1000;
        pushData.calc.accuracy = emLoc.bearing;

        List<PushDataOrder> orderList = new ArrayList<>();
        for (DymOrder dymOrder : DymOrder.findAll()) {
            PushDataOrder dataOrder = new PushDataOrder();
            dataOrder.orderId = dymOrder.orderId;
            dataOrder.orderType = dymOrder.orderType;
            dataOrder.status = 0;
            if (dymOrder.orderType.equals("daijia")) {
                if (dymOrder.orderStatus < DJOrderStatus.GOTO_DESTINATION_ORDER) {//出发前
                    dataOrder.status = 1;
                } else if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {//行驶中
                    dataOrder.status = 2;
                } else if (dymOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {//中途等待
                    dataOrder.status = 3;
                }
            }
            if (dataOrder.status != 0) {
                orderList.add(dataOrder);
            }
        }
        pushData.calc.orderInfo = orderList;

        List<PushData> dataList;
        //历史未上传的点
        String pushCacheStr = FileUtil.readPushCache();
        if (StringUtils.isBlank(pushCacheStr)) {
            dataList = new ArrayList<>();
        } else {
            dataList = new Gson().fromJson(pushCacheStr,
                    new TypeToken<List<PushData>>() {
                    }.getType());
        }

        dataList.add(pushData);

        PushBean<List<PushData>> pushBean = new PushBean<>("gps", dataList);

        String pushStr = new Gson().toJson(pushBean);
        Log.e("MQTTService", "push loc data--->" + pushStr);
        return pushStr;
    }

    public static String buildPush(LatLng emLoc) {

        PushData pushData = new PushData();
        pushData.employ = new BaseEmploy().employ2This();
        pushData.calc = new PushDataLoc();
        pushData.calc.lat = emLoc.latitude;
        pushData.calc.lng = emLoc.longitude;
        pushData.calc.appKey = Config.APP_KEY;
        pushData.calc.darkCost = 0;
        pushData.calc.darkMileage = 0;
        pushData.calc.positionTime = System.currentTimeMillis() / 1000;
        pushData.calc.accuracy = 0;

        List<PushDataOrder> orderList = new ArrayList<>();
        for (DymOrder dymOrder : DymOrder.findAll()) {
            PushDataOrder dataOrder = new PushDataOrder();
            dataOrder.orderId = dymOrder.orderId;
            dataOrder.orderType = dymOrder.orderType;
            dataOrder.status = 0;
            if (dymOrder.orderType.equals("daijia")) {
                if (dymOrder.orderStatus < DJOrderStatus.GOTO_DESTINATION_ORDER) {//出发前
                    dataOrder.status = 1;
                } else if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {//行驶中
                    dataOrder.status = 2;
                } else if (dymOrder.orderStatus == DJOrderStatus.START_WAIT_ORDER) {//中途等待
                    dataOrder.status = 3;
                }
            }
            if (dataOrder.status != 0) {
                orderList.add(dataOrder);
            }
        }
        pushData.calc.orderInfo = orderList;

        List<PushData> dataList;
        //历史未上传的点
        String pushCacheStr = FileUtil.readPushCache();
        if (StringUtils.isBlank(pushCacheStr)) {
            dataList = new ArrayList<>();
        } else {
            dataList = new Gson().fromJson(pushCacheStr,
                    new TypeToken<List<PushData>>() {
                    }.getType());
        }

        dataList.add(pushData);

        PushBean<List<PushData>> pushBean = new PushBean<>("gps", dataList);

        String pushStr = new Gson().toJson(pushBean);
        Log.e("MQTTService", "push trace data--->" + pushStr);
        return pushStr;
    }

}
