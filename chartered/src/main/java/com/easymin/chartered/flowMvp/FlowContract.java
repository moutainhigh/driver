package com.easymin.chartered.flowMvp;
import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.services.route.DriveRouteResult;
import com.easymi.component.rxmvp.RxManager;

import java.util.List;

import rx.Observable;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FlowContract
 * Author: shine
 * Date: 2018/12/18 下午1:55
 * Description:
 * History:
 */
public interface FlowContract {

    interface View {
        void initFragment();

        void initMap();

        void initBridget();

        void addMarker(LatLng latLng, int flag);

        void addMarker(LatLng latLng, int flag, int num);

        void boundsZoom(List<LatLng> latLngs);

        void locZoom(int level);

        void showPath(int[] ints, AMapNaviPath path);

        void showPath(DriveRouteResult result);

        void showLeft(int dis, int time);

        void showFragmentByStatus();

        void changeToolbar(int flag);


        RxManager getManager();
    }

    interface Presenter {
        void routeLineByNavi(LatLng start, List<LatLng> latLngs, LatLng end);

        void routePlanByRouteSearch(LatLng start, List<LatLng> latLngs, LatLng end);

        void stopNavi();
    }

    interface Model {

    }
}
