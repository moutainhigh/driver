package com.easymi.common.mvp.work;

import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.entity.BaseOrder;
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

        RxManager getRxManager();
    }

    interface Presenter {
        void queryStats();

        void indexOrders();
        //...
    }

    interface Model {
        Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey);
    }

}
