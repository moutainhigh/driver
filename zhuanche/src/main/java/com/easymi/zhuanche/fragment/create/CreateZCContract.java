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
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface CreateZCContract {
    interface View {
        void findById();

        void initPhoneEdit();

        void initPlace();

        void init();

        RxManager getManager();

        void showTypeTab(ZCTypeResult result);

        void showPassenger(PassengerResult result);

        void showBudget(BudgetResult result);

        void showQueryTypeErr(int tag);

        void showQueryPasErr(int tag);

        void showQueryBudgetErr(int tag);

        void showTimePickDialog(TextView textView);

        void showDisAndTime(float mile, float sec);

        void showDisAndTimeErr();

        void createSuc(CreateOrderResult createOrderResult);
    }

    interface Presenter {
        void queryZCType(String adcode, String citycode, int carModel, double lat, double lng);

        void queryPassenger(String phone);

        void queryBudget(Long businessId, Long companyId, Double distance, Integer time, Long modelId);

        void routePlan(LatLonPoint start, LatLonPoint end);

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
        Observable<ZCTypeResult> queryZCType(String adcode, String citycode, int carModel, double lat, double lng);

        Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone);

        Observable<BudgetResult> getBudgetPrice(Long businessId, Long companyId, Double distance, Integer time, Long modelId);

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
