package com.easymi.taxi.fragment.create;

import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.taxi.result.BudgetResult;
import com.easymi.taxi.result.TaxiOrderResult;
import com.easymi.taxi.result.ZCTypeResult;
import com.easymi.taxi.result.PassengerResult;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 * Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface CreateTaxiContract {
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

        void createSuc(TaxiOrderResult taxiOrderResult);
    }

    interface Presenter {
        void queryZCType();

        void queryPassenger(String phone);

        void queryBudget(Long passengerId, Double distance, Integer time, Long orderTime, Long typeId,Long modelId);

        void routePlan(LatLonPoint start, LatLonPoint end);

        void createOrder(Long passengerId, String passengerName,
                         String passengerPhone, long orderTime,
                         String bookAddress, Double bookAddressLat,
                         Double bookAddressLng, String destination,
                         Double destinationLat, Double destinationLng,
                         Double budgetFee, Long cid);
    }

    interface Model {
        Observable<ZCTypeResult> queryZCType(Long companyId,Integer serviceType);

        Observable<PassengerResult> queryPassenger(Long companyId, String companyName, String phone);

        Observable<BudgetResult> getBudgetPrice(Long passengerId, Long companyId, Double distance, Integer time, Long orderTime, Long typeId,Long modelId);

        Observable<TaxiOrderResult> createOrder(Long passengerId, String passengerName,
                                                String passengerPhone, long orderTime,
                                                String bookAddress, Double bookAddressLat,
                                                Double bookAddressLng, String destination,
                                                Double destinationLat, Double destinationLng,
                                                Long companyId, String companyName,
                                                Double budgetFee, Long cid,
                                                String orderPerson, Long orderPersonId);

    }
}
