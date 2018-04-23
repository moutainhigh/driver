package com.easymi.common.util;

import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.entity.PushDataLoc;
import com.easymi.common.entity.PushDataOrder;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.entity.BaseEmploy;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/12/22.
 */

public class BuildPushUtil {

    public static String buildPush(BuildPushData buildPushData) {
        PushData pushData = new PushData();

        EmLoc emLoc = buildPushData.emLoc;
        pushData.employ = new BaseEmploy().employ2This();
        pushData.calc = new PushDataLoc();
        pushData.calc.lat = emLoc.latitude;
        pushData.calc.lng = emLoc.longitude;
        pushData.calc.speed = emLoc.speed;
        pushData.calc.locationType = emLoc.locationType;
        pushData.calc.appKey = EmUtil.getAppKey();
//        pushData.calc.darkCost = buildPushData.darkCost;
//        pushData.calc.darkMileage = buildPushData.darkMileage;
        pushData.calc.positionTime = System.currentTimeMillis() / 1000;
        pushData.calc.accuracy = (float) emLoc.accuracy;

        List<PushDataOrder> orderList = new ArrayList<>();
        for (DymOrder dymOrder : DymOrder.findAll()) {
            PushDataOrder dataOrder = new PushDataOrder();
            dataOrder.orderId = dymOrder.orderId;
            dataOrder.orderType = dymOrder.orderType;
            dataOrder.status = 0;
            dataOrder.addedKm = dymOrder.addedKm;
            dataOrder.addedFee = dymOrder.addedFee;
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
        /**
         * 历史未上传的位置信息
         */
        String cacheStr = FileUtil.readPushCache();
        if (StringUtils.isBlank(cacheStr)) {
            dataList = new ArrayList<>();
        } else {
            dataList = new Gson().fromJson(cacheStr,
                    new TypeToken<List<PushData>>() {
                    }.getType());
        }

        //本次的位置信息
        dataList.add(pushData);

        PushBean pushBean = new PushBean("gps", dataList);

        String pushStr = new Gson().toJson(pushBean);
        Log.e("MQTTService", "push loc data--->" + pushStr);
        return pushStr;
    }

}
