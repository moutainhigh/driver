package com.easymin.custombus.mvp;

import com.amap.api.maps.model.LatLng;
import com.amap.api.navi.model.AMapNaviPath;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.widget.LoadingButton;

import rx.Observable;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FlowContract
 * @Author: hufeng
 * @Date: 2019/2/18 下午5:11
 * @Description:
 * @History:
 */
public interface FlowContract {

    interface View {

        /**
         * 显示剩余距离和时间
         * @param dis
         * @param time
         */
        void showLeft(int dis, int time);

        /**
         * 获取 RxManager
         * @return
         */
        RxManager getManager();
    }

    interface Presenter {
        /**
         * 通过导航规划路线
         * @param endLat
         * @param endLng
         */
        void routePlanByNavi(Double endLat, Double endLng);

        /**
         * 停止导航
         */
        void stopNavi();
    }

    interface Model {

    }
}
