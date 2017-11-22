package com.easymi.daijia.activity.grab;

import android.content.Context;
import android.content.Intent;

import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.ToastUtil;
import com.easymi.daijia.flowMvp.FlowActivity;
import com.easymi.daijia.result.DJOrderResult;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public class GrabPresenter implements GrabContract.Presenter {

    private GrabContract.Model model;
    private GrabContract.View view;

    private Context context;

    public GrabPresenter(Context context, GrabContract.View view) {
        this.context = context;
        this.view = view;
        this.model = new GrabModel();
    }

    @Override
    public void queryOrder(Long orderId) {
        Observable<DJOrderResult> observable = model.queryOrder(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                djOrderResult.order.addresses = djOrderResult.address;
                view.showBase(djOrderResult.order);
            }

            @Override
            public void onError(int code) {
                view.showBase(null);
            }
        })));
    }

    @Override
    public void grabOrder(Long orderId) {
        Observable<DJOrderResult> observable = model.grabOrder(orderId);

        view.getManager().add(observable.subscribe(new MySubscriber<>(context, true, false, new HaveErrSubscriberListener<DJOrderResult>() {
            @Override
            public void onNext(DJOrderResult djOrderResult) {
                ToastUtil.showMessage(context, "抢单成功");
                Intent intent = new Intent(context, FlowActivity.class);
                intent.putExtra("orderId", djOrderResult.order.orderId);
                context.startActivity(intent);
                view.finishActivity();
            }

            @Override
            public void onError(int code) {
                view.showBase(null);
            }
        })));
    }
}
