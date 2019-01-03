package com.easymin.passengerbus.flowMvp;

import android.content.Context;

import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.ToastUtil;
import com.easymin.passengerbus.entity.BusStationResult;


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
    public void startStation(long scheduleId) {
        view.getManager().add(model.startStation(scheduleId).subscribe(new MySubscriber<>(context,
                true,
                true,
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
    public void endStation(long scheduleId) {
        view.getManager().add(model.endStation(scheduleId).subscribe(new MySubscriber<>(context,
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
    public void findBusOrderById(long id) {
        view.getManager().add(model.findBusOrderById(id).subscribe(new MySubscriber<>(context,
                true,
                true,
                new HaveErrSubscriberListener<EmResult2<BusStationResult>>() {

                    @Override
                    public void onNext(EmResult2<BusStationResult> result) {
                        view.showBusLineInfo(result.getData());
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtil.showMessage(context, "获取班车路线失败！");
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