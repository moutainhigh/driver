package com.easymi.daijia.flowMvp;

import android.content.Context;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.daijia.entity.Address;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import rx.Observable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowPresenter implements FlowContract.Presenter, INaviInfoCallback {

    private Context context;

    private FlowContract.View view;
    private FlowContract.Model model;

    public FlowPresenter(Context context, FlowContract.View view) {
        this.context = context;
        this.view = view;
        model = new FlowModel();
    }

    @Override
    public void acceptOrder(Long orderId) {
        Observable<DJOrderResult> observable = model.doAccept(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void refuseOrder(Long orderId, String remark) {
        Observable<DJOrderResult> observable = model.refuseOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                view.refuseSuc();
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void toStart(Long orderId) {
        Observable<DJOrderResult> observable = model.toStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void arriveStart(Long orderId) {
        Observable<DJOrderResult> observable = model.arriveStart(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void startWait(Long orderId) {
        Observable<DJOrderResult> observable = model.startWait(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void startDrive(Long orderId) {
        Observable<DJOrderResult> observable = model.startDrive(orderId);
        XApp.getPreferencesEditor().putBoolean(Config.SP_NEED_TRACE, true).apply();//将其置为需要纠偏
        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void arriveDes(DJOrder djOrder) {
        Observable<DJOrderResult> observable = model.arriveDes(djOrder);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void navi(LatLng latLng, String poi) {
        AmapNaviPage.getInstance()
                .showRouteActivity(context,
                        new AmapNaviParams(null, null, new Poi(poi, latLng, ""), AmapNaviType.DRIVER),
                        this);
    }

    @Override
    public void findOne(Long orderId) {
        Observable<DJOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void changeEnd(Long orderId, Double lat, Double lng, String address) {
        Observable<DJOrderResult> observable = model.changeEnd(orderId, lat, lng, address);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void cancelOrder(Long orderId, String remark) {
        Observable<DJOrderResult> observable = model.cancelOrder(orderId, remark);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, true, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                view.cancelSuc();
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onGetNavigationText(String s) {
        XApp.getInstance().syntheticVoice(s, true);
    }

    @Override
    public void onStopSpeaking() {
        XApp.getInstance().stopVoice();
        XApp.getInstance().clearVoiceList();
    }
}
