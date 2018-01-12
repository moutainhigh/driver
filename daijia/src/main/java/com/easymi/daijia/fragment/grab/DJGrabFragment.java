package com.easymi.daijia.fragment.grab;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.easymi.component.utils.TimeUtil;
import com.easymi.daijia.R;

import java.text.DecimalFormat;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout;

/**
 * Created by developerLzh on 2018/1/5 0005.
 */

public class DJGrabFragment extends Fragment {

    TextView start_place;
    TextView end_place;
    TextView order_time_text;//即时
    TagContainerLayout tag_container;

    TextView order_type;
    TextView order_time_day;//预约时间 填
    TextView order_time;//预约时间 分秒
    TextView order_dis;

    MultipleOrder djOrder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dj_grab_fragment, container, false);
        start_place = view.findViewById(R.id.start_place);
        end_place = view.findViewById(R.id.end_place);
        order_time_text = view.findViewById(R.id.order_time_text);
        tag_container = view.findViewById(R.id.tag_container);

        order_type = view.findViewById(R.id.order_type);
        order_time_day = view.findViewById(R.id.order_time_day);
        order_time = view.findViewById(R.id.order_time);
        order_dis = view.findViewById(R.id.order_dis);

        if (djOrder != null) {
            showBase();
        }
        return view;
    }

    private void showBase() {
        start_place.setText(djOrder.startPlace);
        end_place.setText(djOrder.endPlace);
        order_time_text.setText(djOrder.isBookOrder == 1 ? getString(R.string.appoint) : getString(R.string.jishi));
        tag_container.addTag("不要抽烟");
        tag_container.addTag("带手套开车");

        order_type.setText(djOrder.orderDetailType);

        long today = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", System.currentTimeMillis()));
        long orderDay = TimeUtil.parseTime("yyyy-MM-dd", TimeUtil.getTime("yyyy-MM-dd", djOrder.orderTime * 1000));

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
            day = TimeUtil.getTime("yyyy-MM-dd", djOrder.orderTime);
        }
        order_time_day.setText(day);

        String minSec = TimeUtil.getTime("MM:ss", djOrder.orderTime);
        order_time.setText(minSec);

        List<Address> addresses = djOrder.addresses;
        LatLng start = null;
        for (Address address : addresses) {
            if (address.addrType == 1) {
                start = new LatLng(address.lat, address.lng);
                break;
            }
        }
        if (null != start) {
            routePlanByRouteSearch(start.latitude, start.longitude);
        }
    }

    @Override
    public void setArguments(Bundle args) {
        djOrder = (MultipleOrder) args.getSerializable("order");
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
