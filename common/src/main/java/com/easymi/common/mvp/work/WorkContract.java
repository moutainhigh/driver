package com.easymi.common.mvp.work;

import android.content.Context;

import com.easymi.common.entity.Announcement;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.entity.Notifity;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
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

        void offlineSuc();

        void showNotify(Notifity notifity);

        void showDrivers(List<NearDriver> drivers);

        void showAnn(Announcement announcement);

        RxManager getRxManager();
    }

    interface Presenter {
        void queryStats();

        void indexOrders();

        void startLocService(Context context);

        void online();
        void offline();

        void loadNotice(long id);
        void loadAnn(long id);

        void queryNearDriver(Double lat, Double lng);
        //...
    }

    interface Model {
        Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey);

        Observable<EmResult> online(Long driverId, String appKey);
        Observable<EmResult> offline(Long driverId, String appKey);

        Observable<NotitfyResult> loadNotice(Long id);
        Observable<AnnouncementResult> loadAnn(Long id);

        Observable<NearDriverResult> queryNearDriver(Long driverId, Double lat, Double lng, Double distance);
    }

}
