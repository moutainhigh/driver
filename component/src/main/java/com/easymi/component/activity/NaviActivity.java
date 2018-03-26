package com.easymi.component.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.AMapNaviRingType;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.R;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by developerLzh on 2017/12/7 0007.
 */

public class NaviActivity extends RxBaseActivity implements AMapNaviListener, AMapNaviViewListener {

    AMapNaviView mAMapNaviView;

    protected AMapNavi mAMapNavi;

    protected NaviLatLng mEndLatlng;
    protected NaviLatLng mStartLatlng;
    protected final List<NaviLatLng> sList = new ArrayList<>();
    protected final List<NaviLatLng> eList = new ArrayList<>();

    protected List<NaviLatLng> wayPoints;

    private long orderId;
    private String orderType;

    LinearLayout simpleFeeCon;
    TextView lcTxt;
    TextView feeTxt;

    @Override
    public int getLayoutId() {
        return R.layout.activity_navi;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        mStartLatlng = getIntent().getParcelableExtra("startLatlng");
        mEndLatlng = getIntent().getParcelableExtra("endLatlng");

        orderId = getIntent().getLongExtra("orderId", -1);
        orderType = getIntent().getStringExtra("orderType");

        wayPoints = getIntent().getParcelableArrayListExtra("wayPoints");

        if (null == mStartLatlng || mEndLatlng == null) {
            finish();
            return;
        }

        sList.add(mStartLatlng);
        eList.add(mEndLatlng);

        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        mAMapNavi = AMapNavi.getInstance(getApplicationContext());
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setUseInnerVoice(true);

    }

    private Timer timer;
    private TimerTask timerTask;

    private void showFee() {
        simpleFeeCon = findViewById(R.id.simple_fee_con);
        lcTxt = findViewById(R.id.lc);
        feeTxt = findViewById(R.id.fee);
        DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
        if (dymOrder != null && dymOrder.orderType.equals(Config.DAIJIA)) {
            if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                simpleFeeCon.setVisibility(View.VISIBLE);
                lcTxt.setText("订单里程：" + dymOrder.distance + "Km");
                feeTxt.setText("订单费用：" + dymOrder.totalFee + "元");

                timer = new Timer();
                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            DymOrder dymOrder1 = DymOrder.findByIDType(orderId, orderType);
                            lcTxt.setText("订单里程：" + dymOrder1.distance + "Km");
                            feeTxt.setText("订单费用：" + dymOrder1.totalFee + "元");
                        });
                    }
                };
                timer.schedule(timerTask, 2000, 2000);
            } else {
                simpleFeeCon.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (orderId != -1 && StringUtils.isNotBlank(orderType)) {
            showFee();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != timer) {
            timer.cancel();
        }
        if (null != timerTask) {
            timerTask.cancel();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mAMapNaviView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mAMapNaviView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAMapNaviView.onDestroy();
        //since 1.6.0 不再在naviview destroy的时候自动执行AMapNavi.stopNavi();请自行执行
        mAMapNavi.stopNavi();
        mAMapNavi.destroy();
//        XApp.getInstance().stopVoice();
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onInitNaviSuccess() {
        /**
         * 方法: int strategy=mAMapNavi.strategyConvert(congestion, avoidhightspeed, cost, hightspeed, multipleroute); 参数:
         *
         * @congestion 躲避拥堵
         * @avoidhightspeed 不走高速
         * @cost 避免收费
         * @hightspeed 高速优先
         * @multipleroute 多路径
         *
         *  说明: 以上参数都是boolean类型，其中multipleroute参数表示是否多条路线，如果为true则此策略会算出多条路线。
         *  注意: 不走高速与高速优先不能同时为true 高速优先与避免收费不能同时为true
         */
        int strategy = 0;
        try {
            //再次强调，最后一个参数为true时代表多路径，否则代表单路径
            strategy = mAMapNavi.strategyConvert(
                    XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, true),
                    XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_COST, true),
                    XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false),
                    false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, wayPoints, strategy);
    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onTrafficStatusUpdate() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onGetNavigationText(int i, String s) {

    }

    @Override
    public void onGetNavigationText(String s) {
//        XApp.getInstance().syntheticVoice(s, true); //设置使用高德内置语音时将不再回调此方法
    }

    @Override
    public void onEndEmulatorNavi() {

    }

    @Override
    public void onArriveDestination() {

    }

    @Override
    public void onCalculateRouteFailure(int i) {
//        Toast.makeText(this, "路线规划失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {
//        XApp.getInstance().syntheticVoice("您已偏航，正在重新规划路径");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
//        XApp.getInstance().syntheticVoice("为躲避拥堵，正在重新规划路径");
    }

    @Override
    public void onArrivedWayPoint(int i) {

    }

    @Override
    public void onGpsOpenStatus(boolean b) {

    }

    @Override
    public void onNaviInfoUpdate(NaviInfo naviInfo) {

    }

    @Override
    public void onNaviInfoUpdated(AMapNaviInfo aMapNaviInfo) {

    }

    @Override
    public void updateCameraInfo(AMapNaviCameraInfo[] aMapNaviCameraInfos) {

    }

    @Override
    public void onServiceAreaUpdate(AMapServiceAreaInfo[] aMapServiceAreaInfos) {

    }

    @Override
    public void showCross(AMapNaviCross aMapNaviCross) {

    }

    @Override
    public void hideCross() {

    }

    @Override
    public void showModeCross(AMapModelCross aMapModelCross) {

    }

    @Override
    public void hideModeCross() {

    }

    @Override
    public void showLaneInfo(AMapLaneInfo[] aMapLaneInfos, byte[] bytes, byte[] bytes1) {

    }

    @Override
    public void hideLaneInfo() {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {
//        XApp.getInstance().syntheticVoice("路径规划成功");
        mAMapNavi.startNavi(NaviType.GPS);//驾车导航
    }

    @Override
    public void notifyParallelRoad(int i) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo aMapNaviTrafficFacilityInfo) {

    }

    @Override
    public void OnUpdateTrafficFacility(AMapNaviTrafficFacilityInfo[] aMapNaviTrafficFacilityInfos) {

    }

    @Override
    public void OnUpdateTrafficFacility(TrafficFacilityInfo trafficFacilityInfo) {

    }

    @Override
    public void updateAimlessModeStatistics(AimLessModeStat aimLessModeStat) {

    }

    @Override
    public void updateAimlessModeCongestionInfo(AimLessModeCongestionInfo aimLessModeCongestionInfo) {

    }

    @Override
    public void onPlayRing(int i) {
//        XApp.getInstance().syntheticVoice("叮咚", false);
        if (i == AMapNaviRingType.RING_CAMERA) {//（导航状态）高速上通过测速电子眼的提示音

        } else if (i == AMapNaviRingType.RING_EDOG) { // 巡航状态下通过电子狗的提示音

        } else if (i == AMapNaviRingType.RING_REROUTE) {//偏航重算的提示音L

        } else if (i == AMapNaviRingType.RING_TURN) { // 马上到达转向路口的提示音

        }
    }

    @Override
    public void onNaviSetting() {

    }

    @Override
    public void onNaviCancel() {
        finish();
    }

    @Override
    public boolean onNaviBackClick() {
        return false;
    }

    @Override
    public void onNaviMapMode(int i) {

    }

    @Override
    public void onNaviTurnClick() {

    }

    @Override
    public void onNextRoadClick() {

    }

    @Override
    public void onScanViewButtonClick() {

    }

    @Override
    public void onLockMap(boolean b) {

    }

    @Override
    public void onNaviViewLoaded() {

    }

    public void showLaneInfo(AMapLaneInfo info) {

    }

    public void updateIntervalCameraInfo(AMapNaviCameraInfo info1, AMapNaviCameraInfo info2, int i) {

    }
}
