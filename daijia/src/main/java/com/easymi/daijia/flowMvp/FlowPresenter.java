package com.easymi.daijia.flowMvp;

import android.content.Context;

import com.easymi.daijia.entity.DJOrder;

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
    public void acceptOrder() {

    }

    @Override
    public void refuseOrder() {

    }

    @Override
    public void navi(DJOrder djOrder) {

    }
}
