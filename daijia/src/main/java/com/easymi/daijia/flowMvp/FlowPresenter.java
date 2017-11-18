package com.easymi.daijia.flowMvp;

import android.content.Context;

import com.easymi.component.app.XApp;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.daijia.entity.DJOrder;
import com.easymi.daijia.result.DJOrderResult;

import rx.Observable;

/**
 * Created by liuzihao on 2017/11/15.
 */

public class FlowPresenter implements FlowContract.Presenter {

    private Context context;

    private FlowContract.View view;
    private FlowContract.Model model;

    public FlowPresenter(Context context,FlowContract.View view) {
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
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }

    @Override
    public void refuseOrder(Long orderId) {

    }

    @Override
    public void navi(DJOrder djOrder) {

    }

    @Override
    public void findOne(Long orderId) {
        Observable<DJOrderResult> observable = model.findOne(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                view.showOrder(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showOrder(null);
            }
        })));
    }
}
