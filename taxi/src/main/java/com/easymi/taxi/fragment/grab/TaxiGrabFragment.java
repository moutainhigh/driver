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
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.TimeUtil;
import com.easymi.taxi.R;

import java.text.DecimalFormat;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

/**
 * Created by developerLzh on 2018/1/5 0005.
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
        start_place.setText(taxiOrder.bookAddress);
        end_place.setText(taxiOrder.destination);
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

        order_type.setText(taxiOrder.orderDetailType);

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
}
