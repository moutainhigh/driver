package com.easymi.common.mvp.work;

import com.easymi.common.entity.AnnAndNotice;
import com.easymi.common.entity.CityLine;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.common.entity.NearDriver;
import com.easymi.common.entity.WorkStatistics;
import com.easymi.common.push.CountEvent;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.CityLineResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.SystemResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.entity.SystemConfig;
import com.easymi.component.result.EmResult;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;

import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public interface WorkContract {

    interface View {
        /**
         * 加载控件
         */
        void findById();

        /**
         * 初始化列表
         */
        void initRecycler();

        /**
         * 工作台订单数据
         * @param MultipleOrders
         */
        void showOrders(List<MultipleOrder> MultipleOrders);

        /**
         * 初始化地图
         */
        void initMap();

        /**
         * 加载底部地图刷新按钮
         */
        void initRefreshBtn();

        /**
         * 上线成功
         */
        void onlineSuc();

        /**
         * 下线成功
         */
        void offlineSuc();

        /**
         * 公告通知
         * @param notifity
         */
        void showNotify(AnnAndNotice notifity);

        /**
         * 展示附近司机数据
         * @param drivers
         */
        void showDrivers(List<NearDriver> drivers);

        /**
         * 展示公告
         * @param announcement
         */
        void showAnn(AnnAndNotice announcement);

        /**
         * 工作台统计
         * @param countEvent
         */
        void showStatis(CountEvent countEvent);

        /**
         * 显示上线布局
         */
        void showOnline();

        /**
         * 显示下线布局
         */
        void showOffline();

        /**
         * 停止列表刷新
         */
        void stopRefresh();

        /**
         * 根据司机状态展示上下线布局
         */
        void showDriverStatus();

        /**
         * 显示工作台公告和通知
         * @param annAndNoticeList
         */
        void showHomeAnnAndNotice(List<AnnAndNotice> annAndNoticeList);

        /**
         * 隐藏空布局
         */
        void hideEmpty();

        /**
         * 显示空布局
         * @param type
         */
        void showEmpty(int type);

        /**
         * 获取RxManager
         * @return
         */
        RxManager getRxManager();

        /**
         * 注册弹窗 已废弃
         * @param companyPhone
         * @param type
         * @param reason
         */
        void showRegisterDialog(String companyPhone, int type, String reason);
        void hideRegisterDialog();
    }

    interface Presenter {
        /**
         * 开启保活服务
         */
        void initDaemon();

        /**
         * 请求工作台订单列表
         */
        void indexOrders();

        /**
         * 开启定位服务
         */
        void startLocService();

        /**
         * 上线
         * @param btn
         */
        void online(LoadingButton btn);

        /**
         * 下线
         */
        void offline();

        /**
         * 查询底部地图的附近司机
         * @param lat
         * @param lng
         */
        void queryNearDriver(Double lat, Double lng);

        /**
         * OnResume生命周期中调用的方法
         */
        void loadDataOnResume();

        /**
         * 加载司机信息
         * @param id
         */
        void loadEmploy(long id);

        /**
         * 加载app配置和通知公告列表 已废弃
         * @param id
         */
        void getAppSetting(long id);
        void loadNoticeAndAnn();
    }

    interface Model {
        /**
         * 加载订单
         * @param driverId
         * @param appKey
         * @return
         */
        Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey);

        /**
         * 上线
         * @param driverId
         * @param appKey
         * @return
         */
        Observable<EmResult> online(Long driverId, String appKey);

        /**
         * 下线
         * @param driverId
         * @param appKey
         * @return
         */
        Observable<EmResult> offline(Long driverId, String appKey);

        /**
         * 加载通知
         * @param driverId
         * @return
         */
        Observable<NotitfyResult> loadNotice(long driverId);

        /**
         * 加载公告
         * @param companyId
         * @return
         */
        Observable<AnnouncementResult> loadAnn(long companyId);

        /**
         * 加载司机统计信息 已废弃
         * @param id
         * @param nowDate
         * @param isOnline
         * @param minute
         * @param driverNo
         * @param companyId
         * @return
         */
        Observable<WorkStatisticsResult> getDriverStatistics(Long id, String nowDate, int isOnline, int minute, String driverNo, long companyId);

        /**
         * 查询附近司机
         * @param lat
         * @param lng
         * @param distance
         * @param business
         * @return
         */
        Observable<NearDriverResult> queryNearDriver( Double lat, Double lng, Double distance, String business);

        /**
         * 获取司机信息
         * @param driverId
         * @param appKey
         * @return
         */
        Observable<LoginResult> getEmploy(Long driverId, String appKey);

        /**
         * 获取app配置
         * @param driverId
         * @return
         */
        Observable<SettingResult> getAppSetting(long driverId);

        /**
         * 获取系统配置
         * @return
         */
        Observable<SystemResult> getSysConfig();

        /**
         * 消息已读 获取专车 出租车订单列表 已废弃
         * @param id
         * @return
         */
        Observable<EmResult> readOne(long id);
        Observable<QueryOrdersResult> getTaxiOrders(String driverPhone,int page,int size,String status);
        Observable<CityLineResult> getCityLineOrders(Long driverId, String appKey);

    }

}
