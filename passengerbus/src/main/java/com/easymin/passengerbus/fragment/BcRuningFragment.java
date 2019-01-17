package com.easymin.passengerbus.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.help.Tip;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.DriveStep;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.base.RxBaseFragment;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymin.passengerbus.R;
import com.easymin.passengerbus.entity.BusStationResult;
import com.easymin.passengerbus.entity.BusStationsBean;
import com.easymin.passengerbus.flowmvp.ActFraCommBridge;
import com.easymin.passengerbus.flowmvp.BcFlowActivity;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 行程中
 */
public class BcRuningFragment extends RxBaseFragment implements RouteSearch.OnRouteSearchListener{

    private TextView tvLineAddress;
    private TextView tvTip;
    private LinearLayout lin_wait;

    private LinearLayout lineLayout;
    private TextView tvWaiteTime;
    private CustomSlideToUnlockView slider;
    private LinearLayout controlCon;
    private ActFraCommBridge bridge;

    /**
     * 当前状态
     */
    int index;

    private long scheduleId;

    private List<BusStationsBean> listLine = new ArrayList<>();

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        scheduleId = args.getLong("scheduleId");
    }

    /**
     * 设置bridge
     * @param bridge
     */
    public void setBridge(ActFraCommBridge bridge) {
        this.bridge = bridge;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.flow_status_two_step_layout;
    }

    @Override
    public void finishCreateView(Bundle state) {

        initView();

    }

    private void initView() {
        tvLineAddress = $(R.id.tv_line_address);
        tvTip = $(R.id.tv_tip);
        lin_wait = $(R.id.lin_wait);
        lineLayout = $(R.id.line_layout);
        tvWaiteTime = $(R.id.tv_waite_time);
        slider = $(R.id.slider);
        controlCon = $(R.id.control_con);

        listLine = BusStationsBean.findByScheduleId(scheduleId);

        if (listLine == null || listLine.size() == 0) {
            return;
        }

        mRouteSearch = new RouteSearch(getContext());
        mRouteSearch.setRouteSearchListener(this);

        for (int i = 0; i < listLine.size(); i++) {
            if (listLine.get(i).status == BusStationsBean.TO_STATION || listLine.get(i).status == BusStationsBean.ARRIVE_WAIT) {
                index = i;
            }
        }

        if (listLine.get(index).status == BusStationsBean.TO_STATION) {
            slider.setHint("滑动到达站点");
            bridge.changeToolbar(BcFlowActivity.RUNNING);
            tvLineAddress.setText("下一站：" + listLine.get(index).address);
            tvTip.setVisibility(View.VISIBLE);
            lin_wait.setVisibility(View.GONE);

            searchRouteResult(listLine.get(index));
        } else if (listLine.get(index).status == BusStationsBean.ARRIVE_WAIT) {
            slider.setHint("滑动前往下一站");
            bridge.changeToolbar(BcFlowActivity.ENDRUNING);
            tvLineAddress.setText("该站：" + listLine.get(index).address);
            tvTip.setVisibility(View.GONE);
            lin_wait.setVisibility(View.VISIBLE);

            initTimer(listLine.get(index));
            ((BcFlowActivity)getActivity()).initPop(index);
        }

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                cancelTimer();
                if (index < listLine.size() - 1) {
                    if (listLine.get(index).status == BusStationsBean.ARRIVE_WAIT) {
                        index = index + 1;
                        bridge.slideToNext(index);

                        listLine.get(index-1).status = BusStationsBean.LEAVE_STATION;
                        listLine.get(index).status = BusStationsBean.TO_STATION;
                        listLine.get(index).updateStatus();

                        searchRouteResult(listLine.get(index));
                    } else if (listLine.get(index).status == BusStationsBean.TO_STATION) {

                        bridge.sideToArrived(index);

                        listLine.get(index).status = BusStationsBean.ARRIVE_WAIT;
                        listLine.get(index).updateStatus();

                        listLine.get(index).waitTime = System.currentTimeMillis() + 10 * 60 * 1000 ;
                        listLine.get(index).updateWaitTime();

                        initTimer(listLine.get(index));
                    }
                } else if (index == listLine.size() - 1) {
                    if (listLine.get(index).status == BusStationsBean.TO_STATION) {
                        bridge.sideToArrived(index);
                    }
                    bridge.showEndFragment();
                }
                resetView();
//                if (listLine.size() > 0 && index < listLine.size() - 1) {
//                    //下标从0开始
//                    index = index + 1;
//                    if (listLine.get(index).status == BusStationsBean.ARRIVE_WAIT) {
//
//                        bridge.slideToNext(listLine.get(index).id);
//                        listLine.get(index).status = BusStationsBean.LEAVE_STATION;
//                        listLine.get(index+1).status = BusStationsBean.TO_STATION;
//                        listLine.get(index).updateStatus();
//                    } else if (listLine.get(index).status == BusStationsBean.TO_STATION) {
//                        bridge.sideToArrived(listLine.get(index).id);
//
//                    }
//                } else if (index == listLine.size() - 1) {
//                    if (listLine.get(index).status == BusStationsBean.ARRIVE_WAIT) {
//                        bridge.sideToArrived(listLine.get(index).id);
//                    }
//                    bridge.showEndFragment();
//                }
//                resetView();
            }
        });
    }

    Handler handler = new Handler();

    private void resetView() {
        if (listLine.get(index).status == BusStationsBean.TO_STATION) {
            slider.setHint("滑动到达站点");
            tvTip.setVisibility(View.VISIBLE);
            lin_wait.setVisibility(View.GONE);

            tvLineAddress.setText("下一站：" + listLine.get(index).address);

        } else if (listLine.get(index).status == BusStationsBean.ARRIVE_WAIT) {
            slider.setHint("滑动前往下一站");
            tvTip.setVisibility(View.GONE);
            lin_wait.setVisibility(View.VISIBLE);

            tvLineAddress.setText("该站：" + listLine.get(index).address);
        }

        handler.postDelayed(() -> getActivity().runOnUiThread(() -> {
            slider.resetView();
            slider.setVisibility(View.VISIBLE);
        }), 1000);
        //防止卡顿
    }

    private long timeSeq = 0;

    private Timer timer;
    private TimerTask timerTask;

    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
    }

    private void initTimer(BusStationsBean busStationsBean) {
        if (null != timer) {
            timer.cancel();
            timer = null;
        }
        if (null != timerTask) {
            timerTask.cancel();
            timerTask = null;
        }
        long appoint = busStationsBean.waitTime;
        timeSeq = (appoint - System.currentTimeMillis()) / 1000;
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                timeSeq--;
                setTimeText();
            }
        };
        timer.schedule(timerTask, 0, 1000);
        setTimeText();
    }

    private void setTimeText() {
        if (getActivity() != null){
            getActivity().runOnUiThread(() -> {

                StringBuilder sb = new StringBuilder();
                int minute = (int) (Math.abs(timeSeq) / 60);
                int sec = (int) (Math.abs(timeSeq) % 60);
                if (minute < 10) {
                    sb.append("0");
                }
                sb.append(minute).append(":");
                if (sec < 10) {
                    sb.append("0");
                }
                sb.append(sec).append("");
                if (timeSeq < 0) { //超时
                    tvWaiteTime.setText("00:00");
                } else { //正常计时
                    tvWaiteTime.setText(sb.toString());
                }
            });
        }
    }

    /**
     * 路径规划相关
     */
    private RouteSearch mRouteSearch;

    /**
     * 开始搜索路径规划方案
     */
    private void searchRouteResult(BusStationsBean busStationsBean) {
        if (null != mRouteSearch) {
            LatLonPoint start = new LatLonPoint(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);

            LatLonPoint endPoint = new LatLonPoint(busStationsBean.latitude, busStationsBean.longitude);

            RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(start, endPoint);
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                    RouteSearch.DRIVING_MULTI_STRATEGY_FASTEST_SHORTEST_AVOID_CONGESTION, null, null, "");
            mRouteSearch.calculateDriveRouteAsyn(query);
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        if (i == 1000) {
            List<DrivePath> paths = driveRouteResult.getPaths();
            if (paths != null && paths.size() != 0) {
                DrivePath path = paths.get(0);//选取第一条路线
                List<DriveStep> steps = path.getSteps();
                float dis = 0;//米
                float dur = 0;//秒
                for (DriveStep step : steps) {
                    dis += step.getDistance();
                    dur += step.getDuration();
                }
                showLeft((int) dis,(int)dur);
            }
        }
    }

    public void showLeft(int dis, int time) {
//        距离2.5公里，预计5分钟到达

            String disStr = "距离";
            int km = dis / 1000;
            if (km >= 1) {
                String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
                disStr += disStr +
                        disKm + "公里";
            } else {
                disStr +=disStr +
                        dis + "米";
            }

            String timeStr = "预计";
            int hour = time / 60 / 60;
            int minute = time / 60;
            if (hour > 0) {
                timeStr = timeStr +
                        hour +
                        "小时" +
                        time / 60 % 60 +
                        "分钟到达";
            } else {
                timeStr = timeStr +
                        minute +
                        "分钟到达";
            }
        tvTip.setText(disStr+","+timeStr);
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}
