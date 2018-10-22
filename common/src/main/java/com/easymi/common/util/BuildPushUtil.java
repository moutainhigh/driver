package com.easymi.common.util;

import com.easymi.common.entity.BuildPushData;
import com.easymi.common.entity.PushBean;
import com.easymi.common.entity.PushData;
import com.easymi.common.entity.PushDataLoc;
import com.easymi.common.entity.PushDataOrder;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.entity.BaseEmploy;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.entity.Employ;
import com.easymi.component.entity.PushEmploy;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.FileUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.LogUtil;
import com.easymi.component.utils.StringUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzihao on 2017/12/22.
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
        BaseEmploy employ1 = new BaseEmploy().employ2This();
        PushEmploy pe;
        if (employ1 != null && employ1 instanceof Employ) {
            Employ employ = (Employ) employ1;
            pe = new PushEmploy();
            pe.child_type = employ.child_type;
            pe.id = employ.id;
            pe.status = employ.status;
            pe.real_name = employ.real_name;
            pe.company_id = employ.company_id;
            pe.phone = employ.phone;
            pe.child_type = employ.child_type;
            pe.business = employ.service_type;
            if (employ.vehicle != null) {
                pe.model_id = employ.vehicle.serviceType;
            }
        } else {
            //司机信息异常不处理
            return null;
        }

        pushData.employ = pe;
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
            } else if (dymOrder.orderType.equals("zhuanche")) {
                if (dymOrder.orderStatus < ZCOrderStatus.GOTO_DESTINATION_ORDER) {//出发前
                    dataOrder.status = 1;
                } else if (dymOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER) {//行驶中
                    dataOrder.status = 2;
                } else if (dymOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER) {//中途等待
                    dataOrder.status = 3;
                }
                dataOrder.peakMile = dymOrder.peakMile;
                dataOrder.nightTime = dymOrder.nightTime;
                dataOrder.nightMile = dymOrder.nightMile;
                dataOrder.nightTimePrice = dymOrder.nightTimePrice;
            }
            if (dataOrder.status != 0) {
                orderList.add(dataOrder);
            }
        }
        pushData.calc.orderInfo = orderList;


        /**
         * 历史未上传的位置信息
         */
        String cacheStr = FileUtil.readPushCache();
        List<PushData> dataList = new ArrayList<>();
        if (!StringUtils.isBlank(cacheStr)) {
            List<PushData> list = GsonUtil.parseToList(cacheStr, PushData[].class);
            if (list != null && !list.isEmpty()) {
                Log.e("MQTTService", "缓存点");
                dataList.addAll(list);
            }
        }

        //本次的位置信息
        dataList.add(pushData);

        List<PushData> newestDataList = new ArrayList<>();

        //能上传网络定位或者不限制任何
        boolean canPushNetLoc = GPSSetting.getInstance().getNetEnable() || noLimit;
        if (!canPushNetLoc) {
            for (PushData pd : dataList) {
                if (pd != null && pd.calc != null && pd.calc.locationType == 1) {
                    //只上传GPS类型的定位
                    newestDataList.add(pd);
                }
            }
        } else {
            newestDataList.addAll(dataList);
        }

        PushBean pushBean = new PushBean("gps", newestDataList);

        String pushStr = new Gson().toJson(pushBean);
        Log.e("MQTTService", "push loc data--->" + pushStr);
        return pushStr;
    }

}
