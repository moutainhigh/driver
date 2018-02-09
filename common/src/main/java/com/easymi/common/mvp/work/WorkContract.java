package com.easymi.common.mvp.work;

import com.easymi.common.entity.Announcement;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.entity.Notifity;
import com.easymi.common.entity.WorkStatistics;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;

import java.util.List;

import rx.Observable;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public interface WorkContract {

    interface View {
        void findById();

        void initRecycler();

        void showOrders(List<MultipleOrder> MultipleOrders);

        void initMap();

        void initRefreshBtn();

        void onlineSuc();

        void offlineSuc();

        void showNotify(Notifity notifity);

        void showDrivers(List<NearDriver> drivers);

        void showAnn(Announcement announcement);

        void showStatis(WorkStatistics statistics);

        void showOnline();

        void showOffline();

        void stopRefresh();

        void showDriverStatus();

        RxManager getRxManager();
    }

    interface Presenter {

        void initDaemon();

        void indexOrders();

        void startLocService();

        void online(LoadingButton btn);

        void offline();

        void loadNotice(long id);

        void loadAnn(long id);

        void queryNearDriver(Double lat, Double lng);

        void queryStatis();

        void loadDataOnResume();

        void startLineTimer(WorkStatistics workStatistics);

        void onPause();

        void loadEmploy(long id);

//        void startOnlineTimer();
        //...
    }

    interface Model {
        Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey);

        Observable<EmResult> online(Long driverId, String appKey);

        Observable<EmResult> offline(Long driverId, String appKey);

        Observable<NotitfyResult> loadNotice(Long id);

        Observable<AnnouncementResult> loadAnn(Long id);

        Observable<WorkStatisticsResult> getDriverStatistics(Long id, String nowDate);

        Observable<NearDriverResult> queryNearDriver(Long driverId, Double lat, Double lng, Double distance);

        Observable<LoginResult> getEmploy(Long driverId,String appKey);
    }

}
