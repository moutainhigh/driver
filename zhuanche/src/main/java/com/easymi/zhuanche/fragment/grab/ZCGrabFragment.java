package com.easymi.zhuanche.fragment.grab;

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
import com.easymi.zhuanche.R;

import java.text.DecimalFormat;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: ZCGrabFragment
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 专车抢单界面
 * History:
 */

public class ZCGrabFragment extends Fragment {

    private TextView start_place;
    private TextView end_place;
    private TextView order_time_text;//即时
    private TagContainerLayout tag_container;

    private TextView order_type;
    private TextView order_time_day;//预约时间 填
    private TextView order_time;//预约时间 分秒
    private TextView order_dis;
    private TextView tvMark;

    private TextView tv_isBookOrder;

    /**
     * 订单信息
     */
    private MultipleOrder zcOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.zc_grab_fragment, container, false);
        start_place = view.findViewById(R.id.start_place);
        end_place = view.findViewById(R.id.end_place);
        order_time_text = view.findViewById(R.id.order_time_text);
        tag_container = view.findViewById(R.id.tag_container);

        order_type = view.findViewById(R.id.order_type);
        order_time_day = view.findViewById(R.id.order_time_day);
        order_time = view.findViewById(R.id.order_time);
        order_dis = view.findViewById(R.id.order_dis);
        tvMark = view.findViewById(R.id.tvMark);

        tv_isBookOrder = view.findViewById(R.id.tv_isBookOrder);

        if (zcOrder != null) {
            Log.e(ZCGrabFragment.class.getName(), "showBase");
            showBase();
        }
        return view;
    }

    /**
     * 显示基本信息
     */
    private void showBase() {
        if (TextUtils.isEmpty(zcOrder.orderRemark)) {
            tvMark.setText("无备注");
        } else {
            tvMark.setText(zcOrder.orderRemark);
        }
        start_place.setText(zcOrder.getStartSite().address);
        end_place.setText(zcOrder.getEndSite().address);
        order_time_text.setText(zcOrder.isBookOrder == 1 ? getString(R.string.appoint) : getString(R.string.jishi));
        tag_container.removeAllTags();
        if(StringUtils.isNotBlank(zcOrder.passengerTags)){
            if(zcOrder.passengerTags.contains(",")){
                String[] tags = zcOrder.passengerTags.split(",");
                for (String tag : tags) {
                    tag_container.addTag(tag);
                }
            } else {
                tag_container.addTag(zcOrder.passengerTags);
            }
        }

        order_type.setText(zcOrder.getOrderType());

        if (zcOrder.isBookOrder == 1){
            tv_isBookOrder.setVisibility(View.VISIBLE);
        }else {
            tv_isBookOrder.setVisibility(View.GONE);
        }

        long today = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));
        long orderDay = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", zcOrder.bookTime));

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
            day = TimeUtil.getTime("yyyy-MM-dd", zcOrder.bookTime);
        }
        order_time_day.setText(day);

        String minSec = TimeUtil.getTime("HH:mm", zcOrder.bookTime);
        order_time.setText(minSec);

        LatLng start = null;
        for (Address address : zcOrder.orderAddressVos) {
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
        zcOrder = (MultipleOrder) args.getSerializable("order");
        Log.e(ZCGrabFragment.class.getName(), zcOrder.toString());
    }

    /**
     * 路径规划对象
     */
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
                RouteSearch.DRIVING_SINGLE_SHORTEST, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    /**
     * 抢单接单语音配置
     * @param lineDis
     */
    public void grabVoice(double lineDis) {
        String voiceStr = "";
        if (zcOrder.status == DJOrderStatus.NEW_ORDER) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.grab_order) + ",";//抢单
        } else {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.send_order) + ",";//派单
        }
        if (zcOrder.serviceType.equals(Config.DAIJIA)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.create_daijia)
                    + XApp.getInstance().getString(com.easymi.common.R.string.order) + ",";//代驾订单
        } else if (zcOrder.serviceType.equals(Config.ZHUANCHE)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.create_zhuanche)
                    + XApp.getInstance().getString(com.easymi.common.R.string.order) + ",";//专车订单
        } else if (zcOrder.serviceType.equals(Config.TAXI)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.create_taxi)
                    + XApp.getInstance().getString(com.easymi.common.R.string.order) + ",";//专车订单
        }
        String dis = 0 + XApp.getInstance().getString(com.easymi.common.R.string.meter);
        if (EmUtil.getLastLoc() != null && zcOrder.orderAddressVos != null && zcOrder.orderAddressVos.size() != 0) {
            LatLng my = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);

            for (Address address : zcOrder.orderAddressVos) {
                if (address.type == 1) {
                    LatLng start = new LatLng(address.latitude, address.longitude);
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
                        + zcOrder.getStartSite().address //xxx
                        + XApp.getInstance().getString(com.easymi.common.R.string.out)//出发
                        + ",";
        if (StringUtils.isNotBlank(zcOrder.destination)) {
            voiceStr += XApp.getInstance().getString(com.easymi.common.R.string.to) + zcOrder.destination;//到xxx
        }

        XApp.getInstance().shake();
        XApp.getInstance().syntheticVoice(voiceStr, XApp.GRAB);
    }
}
