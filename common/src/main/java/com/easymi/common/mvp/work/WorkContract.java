package com.easymi.common.mvp.work;

import android.content.Context;

import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.entity.BaseOrder;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface WorkContract {

    interface View {
        void findById();

        void initToolbar();

        void initRecycler();

        void showOrders(List<BaseOrder> baseOrders);

        void initMap();

        void initRefreshBtn();

        void onlineSuc();

        RxManager getRxManager();
    }

    interface Presenter {
        void queryStats();

        void indexOrders();

        void startLocService(Context context);

        void online();
        //...
    }

    interface Model {
        Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey);
        Observable<EmResult> online(Long driverId, String appKey);
    }

}
