package com.easymi.taxi.fragment.grab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.common.entity.Address;
import com.easymi.common.entity.MultipleOrder;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.taxi.R;

import java.text.DecimalFormat;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: FinishActivity
 *@Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class TaxiGrabFragment extends Fragment {

    private TextView start_place;
    private TextView end_place;
    private TextView order_time_text;//即时
    private TagContainerLayout tag_container;

    private TextView order_type;
    private TextView order_time_day;//预约时间 填
    private TextView order_time;//预约时间 分秒
    private TextView order_dis;
    private TextView tvMark;

    private MultipleOrder taxiOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.taxi_grab_fragment, container, false);
        start_place = view.findViewById(R.id.start_place);
        end_place = view.findViewById(R.id.end_place);
        order_time_text = view.findViewById(R.id.order_time_text);
        tag_container = view.findViewById(R.id.tag_container);

        order_type = view.findViewById(R.id.order_type);
        order_time_day = view.findViewById(R.id.order_time_day);
        order_time = view.findViewById(R.id.order_time);
        order_dis = view.findViewById(R.id.order_dis);
        tvMark = view.findViewById(R.id.tvMark);

        if (taxiOrder != null) {
            Log.e(TaxiGrabFragment.class.getName(), "showBase");
            showBase();
        }
        return view;
    }

    private void showBase() {
        if (TextUtils.isEmpty(taxiOrder.orderRemark)) {
            tvMark.setText("无备注");
        } else {
            tvMark.setText(taxiOrder.orderRemark);
        }
        start_place.setText(taxiOrder.getStartSite().address);
        end_place.setText(taxiOrder.getEndSite().address);
        order_time_text.setText(taxiOrder.isBookOrder == 1 ? getString(R.string.appoint) : getString(R.string.jishi));
        tag_container.removeAllTags();
        if(StringUtils.isNotBlank(taxiOrder.passengerTags)){
            if(taxiOrder.passengerTags.contains(",")){
                String[] tags = taxiOrder.passengerTags.split(",");
                for (String tag : tags) {
                    tag_container.addTag(tag);
                }
            } else {
                tag_container.addTag(taxiOrder.passengerTags);
            }
        }

        long today = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));
        long orderDay = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", taxiOrder.bookTime));

        String day;
        if (today == orderDay) {
            day = getString(R.string.today);
        } else if (orderDay - today == 24 * 60 * 60 * 1000) {
            day = getString(R.string.tomorrow);
        } else if (orderDay - today == 2 * 24 * 60 * 60 * 1000) {
            day = getString(R.string.houtian);
        } else if (orderDay - today == 3 * 24 * 60 * 60 * 1000) {
            day = getString(R.string.waitian);
        } else {
            day = TimeUtil.getTime("yyyy-MM-dd", taxiOrder.bookTime);
        }
        order_time_day.setText(day);

        String minSec = TimeUtil.getTime("HH:mm", taxiOrder.bookTime);
        order_time.setText(minSec);

        List<Address> addresses = taxiOrder.orderAddressVos;
        LatLng start = null;
        for (Address address : addresses) {
            if (address.type == 1) {
                start = new LatLng(address.latitude, address.longitude);
                break;
            }
        }
        if (null != start) {
            routePlanByRouteSearch(start.latitude, start.longitude);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        taxiOrder = (MultipleOrder) args.getSerializable("order");
        Log.e(TaxiGrabFragment.class.getName(), taxiOrder.toString());
    }

    RouteSearch routeSearch;

    /**
     * 路径规划
     *
     * @param endLat
     * @param endLng
     */
    public void routePlanByRouteSearch(Double endLat, Double endLng) {
        if (null == routeSearch) {
            routeSearch = new RouteSearch(getActivity());
            routeSearch.setRouteSearchListener(new RouteSearch.OnRouteSearchListener() {
                @Override
                public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

                }

                @Override
                public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int code) {
                    if (code == 1000) {
                        List<DrivePath> paths = driveRouteResult.getPaths();
                        if (paths != null && paths.size() != 0) {
                            DrivePath path = paths.get(0);
                            int dis = (int) path.getDistance();
                            //添加语音播报 hf add
                            grabVoice(dis);

                            if (dis > 1000) {
                                double km = (double) dis / 1000;
                                DecimalFormat format = new DecimalFormat("#0.0");
                                order_dis.setText(format.format(km) + getString(R.string.km));
                            } else {
                                order_dis.setText(dis + getString(R.string.meter));
                            }
                        }
                    }
                }

                @Override
                public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

                }

                @Override
                public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

                }
            });
        }
        LatLonPoint start = new LatLonPoint(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
        LatLonPoint end = new LatLonPoint(endLat, endLng);

        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, end);
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }


    public void grabVoice(double lineDis) {
        String voiceStr = "";
        if (taxiOrder.status == DJOrderStatus.NEW_ORDER) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.grab_order) + ",";//抢单
        } else {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.send_order) + ",";//派单
        }
        if (taxiOrder.serviceType.equals(Config.DAIJIA)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.create_daijia)
                    + XApp.getInstance().getString(com.easymi.common.R.string.order) + ",";//代驾订单
        } else if (taxiOrder.serviceType.equals(Config.ZHUANCHE)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.create_zhuanche)
                    + XApp.getInstance().getString(com.easymi.common.R.string.order) + ",";//专车订单
        } else if (taxiOrder.serviceType.equals(Config.TAXI)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.create_taxi)
                    + XApp.getInstance().getString(com.easymi.common.R.string.order) + ",";//专车订单
        }
        String dis = 0 + XApp.getInstance().getString(com.easymi.common.R.string.meter);
        if (EmUtil.getLastLoc() != null && taxiOrder.orderAddressVos != null && taxiOrder.orderAddressVos.size() != 0) {
            LatLng my = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);

            for (Address address : taxiOrder.orderAddressVos) {
                if (address.type == 1) {
                    LatLng start = new LatLng(address.latitude, address.longitude);
//                    double meter = AMapUtils.calculateLineDistance(my, start);
                    DecimalFormat format;
                    if (lineDis > 1000) {
                        format = new DecimalFormat("#0.0");
                        dis = format.format(lineDis / (double) 1000) + XApp.getInstance().getString(com.easymi.common.R.string.k_meter);
                    } else {
                        format = new DecimalFormat("#0");
                        dis = format.format(lineDis) + XApp.getInstance().getString(com.easymi.common.R.string.meter);
                    }
                }
            }
        }
        voiceStr +=
                XApp.getInstance().getString(com.easymi.common.R.string.to_you)//距您
                        + dis //0.5公里
                        + ","
                        + XApp.getInstance().getString(com.easymi.common.R.string.from)//从
                        + taxiOrder.getStartSite().address //xxx
                        + XApp.getInstance().getString(com.easymi.common.R.string.out)//出发
                        + ",";
        if (StringUtils.isNotBlank(taxiOrder.destination)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.to) + taxiOrder.destination;//到xxx
        }

        XApp.getInstance().shake();
        XApp.getInstance().syntheticVoice(voiceStr, XApp.GRAB);
    }
}
