package com.easymin.passengerbus.flowmvp;

import android.content.Context;

import com.easymi.component.Config;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.LoadingButton;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.BusStationsBean;

import java.util.List;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowPresenter
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class FlowPresenter implements FlowContract.Presenter {

    private Context context;
    private FlowContract.View view;
    private FlowContract.Model model;


    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel(context);
    }

    @Override
    public void startStation(long scheduleId,LoadingButton button) {
        view.getManager().add(model.startStation(scheduleId).subscribe(new MySubscriber<>(context,
                button,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                        view.showNext();
                    }

                    @Override
                    public void onError(int code) {

                    }
                })));
    }

    @Override
    public void arriveStation(long scheduleId, long stationId) {
        view.getManager().add(model.arriveStation(scheduleId, stationId).subscribe(new MySubscriber<>(context,
                true,
                true,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
//                        view.showBusLineInfo(result.getData());
                    }

                    @Override
                    public void onError(int code) {
//                        ToastUtil.showMessage(context, "获取班车路线失败！");
                    }
                })));
    }

    @Override
    public void endStation(long scheduleId,LoadingButton button) {
        view.getManager().add(model.endStation(scheduleId).subscribe(new MySubscriber<>(context,
                button,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
                        view.finishFragment();
                    }

                    @Override
                    public void onError(int code) {
//                        ToastUtil.showMessage(context, "获取班车路线失败！");
                    }
                })));
    }

    @Override
    public void findBusOrderById(long id) {
        view.getManager().add(model.findBusOrderById(id).subscribe(new MySubscriber<>(context,
                true,
                true,
                new HaveErrSubscriberListener<EmResult2<BusStationResult>>() {

                    @Override
                    public void onNext(EmResult2<BusStationResult> result) {
                        if (result.getCode() == 1) {
                            for (BusStationsBean busStation : result.getData().stationVos) {
                                if (!BusStationsBean.existsById(busStation.id, result.getData().id)) {
                                    busStation.scheduleId = result.getData().id;
                                    busStation.orderType = Config.COUNTRY;
                                    busStation.save();
                                }
                            }
                            List<BusStationsBean> stations = BusStationsBean.findAll();
                            for (BusStationsBean station : stations) {
                                boolean isExist = false;
                                for (BusStationsBean order : result.getData().stationVos) {
                                    if (order.id == station.id && result.getData().id == station.scheduleId) {
                                        isExist = true;
                                        break;
                                    }
                                }
                                if (!isExist) {
                                    station.delete(station.id,station.scheduleId);
                                }
                            }
                        }
                        view.showBusLineInfo(result.getData());
                    }

                    @Override
                    public void onError(int code) {
                        view.showBusLineInfo(null);
                    }
                })));
    }

    @Override
    public void toNextStation(long scheduleId, long stationId) {
        view.getManager().add(model.toNextStation(scheduleId, stationId).subscribe(new MySubscriber<>(context,
                true,
                true,
                new HaveErrSubscriberListener<EmResult2<Object>>() {

                    @Override
                    public void onNext(EmResult2<Object> result) {
//                        view.showBusLineInfo(result.getData());
                    }

                    @Override
                    public void onError(int code) {
//                        ToastUtil.showMessage(context, "获取班车路线失败！");
                    }
                })));
    }

}