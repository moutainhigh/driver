package com.easymi.common.mvp.work;

import com.easymi.common.CommApiService;
import com.easymi.common.result.AnnouncementResult;
import com.easymi.common.result.CityLineResult;
import com.easymi.common.result.LoginResult;
import com.easymi.common.result.NearDriverResult;
import com.easymi.common.result.NotitfyResult;
import com.easymi.common.result.QueryOrdersResult;
import com.easymi.common.result.SettingResult;
import com.easymi.common.result.SystemResult;
import com.easymi.common.result.WorkStatisticsResult;
import com.easymi.component.Config;
import com.easymi.component.entity.SystemConfig;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/17 0017.
 */

public class WorkModel implements WorkContract.Model {

    @Override
    public Observable<QueryOrdersResult> indexOrders(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
//                .queryRunningOrders(driverId, appKey, 1, 100)
                .getZCOrders(driverId,appKey)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> online(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .online(driverId,EmUtil.getEmployInfo().companyId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> offline(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .offline(driverId,EmUtil.getEmployInfo().companyId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<NotitfyResult> loadNotice(long driverId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .loadNotice(driverId, EmUtil.getAppKey(), 1, 1000)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<AnnouncementResult> loadAnn(long companyId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .loadAnn(companyId, EmUtil.getAppKey(), 1, 1000)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<WorkStatisticsResult> getDriverStatistics(Long id, String nowDate, int isOnline, int minute, String driverNo, long companyId) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
//                .workStatistics(id, nowDate, EmUtil.getAppKey(), isOnline, minute, driverNo, companyId)
                .workStatistics()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<NearDriverResult> queryNearDriver(Long driverId, Double lat, Double lng, Double distance, String business) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getNearDrivers(driverId, lat, lng, distance, business, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<LoginResult> getEmploy(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getDriverInfo(driverId, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<SettingResult> getAppSetting(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getAppSetting(EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<SystemResult> getSysConfig() {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getSysCofig(EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<EmResult> readOne(long id) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .readNotice(id, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<QueryOrdersResult> getTaxiOrders(String driverPhone, int page, int size, String status) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getTaxiOrders(driverPhone,page,size,status)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<CityLineResult> getCityLineOrders(Long driverId, String appKey) {
        return ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getCityLineOrders(driverId,appKey)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


}
