package com.easymi.zhuanche.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.LatLng;
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
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.utils.StringUtils;
import com.easymi.zhuanche.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestActivity extends RxBaseActivity implements AMapNaviListener, AMapNaviViewListener {

    AMapNaviView mAMapNaviView;

    protected AMapNavi mAMapNavi;

    protected NaviLatLng mEndLatlng;
    protected NaviLatLng mStartLatlng;
    protected final List<NaviLatLng> sList = new ArrayList<>();
    protected final List<NaviLatLng> eList = new ArrayList<>();

    protected List<NaviLatLng> wayPoints;

    private long orderId;
    private String orderType;
    private int naviMode;

//    LinearLayout simpleFeeCon;
//    TextView lcTxt;
//    TextView feeTxt;

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        mStartLatlng = getIntent().getParcelableExtra("startLatlng");
        mEndLatlng = getIntent().getParcelableExtra("endLatlng");

        orderId = getIntent().getLongExtra("orderId", -1);
        orderType = getIntent().getStringExtra("orderType");
        naviMode = getIntent().getIntExtra(Config.NAVI_MODE, Config.DRIVE_TYPE);

        wayPoints = getIntent().getParcelableArrayListExtra("wayPoints");

        if (null == mStartLatlng || mEndLatlng == null) {
            finish();
            return;
        }

        sList.add(mStartLatlng);
        eList.add(mEndLatlng);

        mAMapNaviView = findViewById(com.easymi.component.R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);
        mAMapNaviView.setAMapNaviViewListener(this);

        mAMapNavi = AMapNavi.getInstance(this);
        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setUseInnerVoice(true);

    }

    private Timer timer;
    private TimerTask timerTask;

//    private void showFee() {
//        simpleFeeCon = findViewById(com.easymi.component.R.id.simple_fee_con);
//        lcTxt = findViewById(com.easymi.component.R.id.lc);
//        feeTxt = findViewById(com.easymi.component.R.id.fee);
//        DymOrder dymOrder = DymOrder.findByIDType(orderId, orderType);
//        if (dymOrder != null && dymOrder.orderType.equals(Config.DAIJIA)) {
//            if (dymOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
//                simpleFeeCon.setVisibility(View.VISIBLE);
//                lcTxt.setText(getString(com.easymi.component.R.string.order_dis) + dymOrder.distance + getString(com.easymi.component.R.string.dis_unit));
//                feeTxt.setText(getString(com.easymi.component.R.string.order_fee) + dymOrder.totalFee + getString(com.easymi.component.R.string.money_unit));
//
//                timer = new Timer();
//                timerTask = new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(() -> {
//                            DymOrder dymOrder1 = DymOrder.findByIDType(orderId, orderType);
//                            lcTxt.setText(getString(com.easymi.component.R.string.order_dis) + dymOrder1.distance + getString(com.easymi.component.R.string.dis_unit));
//                            feeTxt.setText(getString(com.easymi.component.R.string.order_fee) + dymOrder1.totalFee + getString(com.easymi.component.R.string.money_unit));
//                        });
//                    }
//                };
//                timer.schedule(timerTask, 2000, 2000);
//            } else {
//                simpleFeeCon.setVisibility(View.GONE);
//            }
//        }
//    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

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
        //mAMapNavi是全局的，执行订单页面还需要用，所以这里不能销毁资源
//        mAMapNavi.stopNavi();
//        mAMapNavi.destroy();
//        XApp.getInstance().stopVoice();
    }

    @Override
    public void onInitNaviFailure() {
        com.easymi.component.utils.Log.e("NaviActivity","初始化导航失败");
    }

    @Override
    public void onInitNaviSuccess() {

        if (naviMode == Config.WALK_TYPE) {
            mAMapNavi.calculateWalkRoute(mStartLatlng, mEndLatlng);
        } else {
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
                        XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, false),
                        XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false),
                        XApp.getMyPreferences().getBoolean(Config.SP_COST, false),
                        XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false),
                        false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mAMapNavi.calculateDriveRoute(sList, eList, wayPoints, strategy);
        }

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
        Toast.makeText(this, "路线规划失败", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onReCalculateRouteForYaw() {
        XApp.getInstance().syntheticVoice("您已偏航，正在重新规划路径");
    }

    @Override
    public void onReCalculateRouteForTrafficJam() {
        XApp.getInstance().syntheticVoice("为躲避拥堵，正在重新规划路径");
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
        mAMapNavi.destroy();
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
