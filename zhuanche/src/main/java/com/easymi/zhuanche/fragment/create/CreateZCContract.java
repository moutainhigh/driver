package com.easymi.zhuanche.fragment.create;

import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.easymi.common.result.CreateOrderResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.zhuanche.result.BudgetResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.easymi.zhuanche.result.ZCTypeResult;
import com.easymi.zhuanche.result.PassengerResult;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: CreateZCContract
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 创建订单mvp契合接口
 * History:
 */

public interface CreateZCContract {
    interface View {
        /**
         * 查询单个订单
         */
        void findById();

        /**
         * 初始化电话输入框
         */
        void initPhoneEdit();

        /**
         * 初始化地址
         */
        void initPlace();

        /**
         * 补单界面初始化布局和监听
         */
        void init();

        /**
         * 获取 RxManager
         * @return
         */
        RxManager getManager();

        /**
         * 显示类型tab
         * @param result
         */
        void showTypeTab(ZCTypeResult result);

        /**
         * 显示乘客信息
         * @param result
         */
        void showPassenger(PassengerResult result);

        /**
         * 显示预算信息
         * @param result
         */
        void showBudget(BudgetResult result);

        /**
         * 显示查询错误布局
         * @param tag
         */
        void showQueryTypeErr(int tag);

        /**
         * 显示查询客户信息失败布局
         * @param tag
         */
        void showQueryPasErr(int tag);

        /**
         * 显示查询预算失败布局
         * @param tag
         */
        void showQueryBudgetErr(int tag);

        /**
         * 显示选择时间选择弹窗dialog
         * @param textView
         */
        void showTimePickDialog(TextView textView);

        /**
         * 显示距离和时间
         * @param mile
         * @param sec
         */
        void showDisAndTime(float mile, float sec);

        /**
         * 显示时间距离获取失败的布局
         */
        void showDisAndTimeErr();

        /**
         * 创建订单成功布局
         * @param createOrderResult
         */
        void createSuc(CreateOrderResult createOrderResult);
    }

    interface Presenter {
        /**
         * 查询专车类型
         * @param adcode
         * @param citycode
         * @param carModel  车辆id
         * @param lat
         * @param lng
         */
        void queryZCType(String adcode, String citycode, int carModel, double lat, double lng);

        /**
         * 查询乘客信息
         * @param phone  乘客电话
         */
        void queryPassenger(String phone);

        /**
         * 查询预算
         * @param businessId 业务id
         * @param companyId
         * @param distance
         * @param time
         * @param modelId 车辆型号
         */
        void queryBudget(Long businessId, Long companyId, Double distance, Integer time, Long modelId);

        /**
         * 规划路线 获取里程时间
         * @param start
         * @param end
         */
        void routePlan(LatLonPoint start, LatLonPoint end);

        /**
         * 创建订单
         * @param bookTime  预约时间
         * @param budgetFee  预估费用
         * @param businessId 业务id
         * @param channelAlias 渠道
         * @param companyId 公司ud
         * @param driverId
         * @param driverName
         * @param driverPhone
         * @param modelId  车辆型号
         * @param orderAddress 订单地址
         * @param passengerId
         * @param passengerName
         * @param passengerPhone
         * @param serviceType  服务类型
         */
        void createOrder(Long bookTime,
                         Double budgetFee,
                         Long businessId,
                         String channelAlias,
                         Long companyId,
                         Long driverId,
                         String driverName,
                         String driverPhone,
                         Long modelId,
                         String orderAddress,
                         Long passengerId,
                         String passengerName,
                         String passengerPhone,
                         String serviceType);
    }

    interface Model {
        /**
         * 查询专车类型
         * @param adcode
         * @param citycode
         * @param carModel  车辆类型
         * @param lat
         * @param lng
         * @return
         */
        Observable<ZCTypeResult> queryZCType(String adcode, String citycode, int carModel, double lat, double lng);

        /**
         * 查询乘客数据
         * @param companyId
         * @param companyName
         * @param phone
         * @return
         */
        Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone);

        /**
         * 获取预算价格
         * @param businessId  业务id
         * @param companyId 公司id
         * @param distance 距离
         * @param time 时间
         * @param modelId  车辆型号
         * @return
         */
        Observable<BudgetResult> getBudgetPrice(Long businessId, Long companyId, Double distance, Integer time, Long modelId);

        /**
         * 创建订单
         * @param bookTime  预约时间
         * @param budgetFee  预估费用
         * @param businessId 业务id
         * @param channelAlias 渠道
         * @param companyId 公司ud
         * @param driverId
         * @param driverName
         * @param driverPhone
         * @param modelId  车辆型号
         * @param orderAddress 订单地址
         * @param passengerId
         * @param passengerName
         * @param passengerPhone
         * @param serviceType  服务类型
         */
        Observable<CreateOrderResult> createOrder(Long bookTime,
                                              Double budgetFee,
                                              Long businessId,
                                              String channelAlias,
                                              Long companyId,
                                              Long driverId,
                                              String driverName,
                                              String driverPhone,
                                              Long modelId,
                                              String orderAddress,
                                              Long passengerId,
                                              String passengerName,
                                              String passengerPhone,
                                              String serviceType);

    }
}
