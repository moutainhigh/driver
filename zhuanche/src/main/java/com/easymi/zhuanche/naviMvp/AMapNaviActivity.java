package com.easymi.zhuanche.naviMvp;

import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
import com.amap.api.navi.enums.AMapNaviRingType;
import com.amap.api.navi.enums.IconType;
import com.amap.api.navi.enums.NaviType;
import com.amap.api.navi.model.AMapLaneInfo;
import com.amap.api.navi.model.AMapModelCross;
import com.amap.api.navi.model.AMapNaviCameraInfo;
import com.amap.api.navi.model.AMapNaviCross;
import com.amap.api.navi.model.AMapNaviInfo;
import com.amap.api.navi.model.AMapNaviLocation;
import com.amap.api.navi.model.AMapNaviPath;
import com.amap.api.navi.model.AMapNaviTrafficFacilityInfo;
import com.amap.api.navi.model.AMapServiceAreaInfo;
import com.amap.api.navi.model.AimLessModeCongestionInfo;
import com.amap.api.navi.model.AimLessModeStat;
import com.amap.api.navi.model.NaviInfo;
import com.amap.api.navi.model.NaviLatLng;
import com.amap.api.services.route.DriveRouteResult;
import com.autonavi.tbt.TrafficFacilityInfo;
import com.easymi.component.Config;
import com.easymi.component.DJOrderStatus;
import com.easymi.component.ZCOrderStatus;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.widget.CustomSlideToUnlockView;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.ConsumerInfo;
import com.easymi.zhuanche.entity.ZCOrder;
import com.easymi.zhuanche.flowMvp.FlowContract;
import com.easymi.zhuanche.flowMvp.FlowPresenter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: AMapNaviActivity
 * @Author: hufeng
 * @Date: 2019/2/11 下午1:57
 * @Description: 订单导航跑单界面  开发中 未使用
 * @History:
 */
@Route(path = "/zhuanche/AMapNaviActivity")
public class AMapNaviActivity extends RxBaseActivity implements AMapNaviListener, AMapNaviViewListener, FlowContract.View {

    /**
     * 导航基础界面
     */
    private AMapNaviView mAMapNaviView;
    /**
     * 导航对象
     */
    protected AMapNavi mAMapNavi;
    /**
     * 订单id
     */
    long orderId;
    /**
     * 滑动按钮
     */
    private CustomSlideToUnlockView slider;

    TextView tv_destance;
    ImageView iv_close;
    TextView tv_next_site;
    TextView tv_total_destance;
    TextView tv_total_time;
    TextView tv_arrive;
    TextView tv_turn;

    private FlowPresenter presenter;

    ZCOrder zcOrder;

    protected final List<NaviLatLng> sList = new ArrayList<>();
    protected final List<NaviLatLng> eList = new ArrayList<>();

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_amap_navi;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            finish();
            return;
        }
        findview();
        mAMapNaviView = findViewById(R.id.navi_view);
        mAMapNaviView.onCreate(savedInstanceState);

        slider = findViewById(R.id.slider);

        presenter = new FlowPresenter(this, this);
        presenter.findOne(orderId);
    }

    public void findview() {
        tv_destance = findViewById(R.id.tv_destance);
        iv_close = findViewById(R.id.iv_close);
        tv_next_site = findViewById(R.id.tv_next_site);
        tv_total_destance = findViewById(R.id.tv_total_destance);
        tv_total_time = findViewById(R.id.tv_total_time);
        tv_arrive = findViewById(R.id.tv_arrive);
        tv_turn = findViewById(R.id.tv_turn);
    }

    public void initListener() {

    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        Log.e("NaviActivity", "初始化导航失败");
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
                    XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_COST, false),
                    XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false),
                    false);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAMapNavi.calculateDriveRoute(sList, eList, null, strategy);
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
        showLeft(naviInfo.getPathRetainDistance(), naviInfo.getPathRetainTime());
        naviInfo.getIconType();
        naviInfo.getCurStepRetainDistance();

        tv_destance.setText(naviInfo.getCurStepRetainDistance() + "米后");
        tv_next_site.setText(naviInfo.getNextRoadName());

        if (naviInfo.getIconType() == IconType.LEFT){
            tv_turn.setText("左转");
        }else if (naviInfo.getIconType() == IconType.RIGHT){
            tv_turn.setText("右转");
        }else if (naviInfo.getIconType() == IconType.LEFT_FRONT){
            tv_turn.setText("左前方行驶");
        }else if (naviInfo.getIconType() == IconType.RIGHT_FRONT){
            tv_turn.setText("右前方行驶");
        }else if (naviInfo.getIconType() == IconType.LEFT_BACK){
            tv_turn.setText("左后方行驶");
        }else if (naviInfo.getIconType() == IconType.RIGHT_BACK){
            tv_turn.setText("右后方行驶");
        }else if (naviInfo.getIconType() == IconType.LEFT_TURN_AROUND){
            tv_turn.setText("左转掉头");
        }else if (naviInfo.getIconType() == IconType.STRAIGHT){
            tv_turn.setText("直行");
        }
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

        //驾车导航
        mAMapNavi.startNavi(NaviType.GPS);
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
        if (i == AMapNaviRingType.RING_CAMERA) {
            //（导航状态）高速上通过测速电子眼的提示音

        } else if (i == AMapNaviRingType.RING_EDOG) {
            // 巡航状态下通过电子狗的提示音

        } else if (i == AMapNaviRingType.RING_REROUTE) {
            //偏航重算的提示音L

        } else if (i == AMapNaviRingType.RING_TURN) {
            // 马上到达转向路口的提示音

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


    /**
     * 订单操作相关
     */
    @Override
    public void initToolbar() {

    }

    @Override
    public void showTopView() {

    }

    @Override
    public void showToPlace(String toPlace) {

    }

    @Override
    public void showLeftTime(String leftTime) {

    }

    @Override
    public void initBridge() {

    }

    @Override
    public void showBottomFragment(ZCOrder zcOrder) {

    }

    @Override
    public void showOrder(ZCOrder zcOrder) {
        if (zcOrder == null) {
            finish();
        }
        this.zcOrder = zcOrder;
        NaviLatLng start = null;
        NaviLatLng end = null;
        if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.PAIDAN_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.TAKE_ORDER) {
            if (null != zcOrder.getStartSite()) {
                start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
                end = new NaviLatLng(zcOrder.getStartSite().lat, zcOrder.getStartSite().lng);
            }
        } else if (zcOrder.orderStatus == ZCOrderStatus.GOTO_BOOKPALCE_ORDER) {
            if (null != zcOrder.getStartSite()) {
                start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
                end = new NaviLatLng(zcOrder.getStartSite().lat, zcOrder.getStartSite().lng);
            }
        } else if (zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_BOOKPLACE_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.GOTO_DESTINATION_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.START_WAIT_ORDER
                || zcOrder.orderStatus == ZCOrderStatus.ARRIVAL_DESTINATION_ORDER
                ) {
            if (null != zcOrder.getEndSite()) {
                start = new NaviLatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
                end = new NaviLatLng(zcOrder.getEndSite().lat, zcOrder.getEndSite().lng);
            }
        } else {
            finish();
        }

        sList.add(start);
        eList.add(end);

        mAMapNaviView.setAMapNaviViewListener(this);

        if (null == mAMapNavi) {
            mAMapNavi = AMapNavi.getInstance(this);
            mAMapNavi.addAMapNaviListener(this);
            mAMapNavi.setUseInnerVoice(true);
        }

        int strateFlag = mAMapNavi.strategyConvert(
                XApp.getMyPreferences().getBoolean(Config.SP_CONGESTION, false),
                XApp.getMyPreferences().getBoolean(Config.SP_AVOID_HIGH_SPEED, false),
                XApp.getMyPreferences().getBoolean(Config.SP_COST, false),
                XApp.getMyPreferences().getBoolean(Config.SP_HIGHT_SPEED, false),
                false);

        mAMapNavi.calculateDriveRoute(sList, eList, null, strateFlag);

        setSliderText(zcOrder);
    }

    @Override
    public void initMap() {

    }

    @Override
    public void showMapBounds() {

    }

    @Override
    public void cancelSuc() {

    }

    @Override
    public void refuseSuc() {

    }

    @Override
    public void showPath(int[] ints, AMapNaviPath path) {

    }

    @Override
    public void showPath(DriveRouteResult result) {

    }

    @Override
    public void showPayType(double money, ConsumerInfo consumerInfo) {

    }

    @Override
    public void paySuc() {

    }

    @Override
    public void showLeft(int dis, int time) {
        if (zcOrder.orderStatus == ZCOrderStatus.NEW_ORDER) {
//            String disStr = getString(R.string.to_start_about);
//            int km = dis / 1000;
//            if (km >= 1) {
//                String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
//                disStr += "<font color='blue'><b><tt>" +
//                        disKm + "</tt></b></font>" + getString(R.string.km);
//            } else {
//                disStr += getString(R.string.left) +
//                        "<font color='blue'><b><tt>" +
//                        dis + "</tt></b></font>"
//                        + getString(R.string.meter);
//            }
//
//            String timeStr;
//            int hour = time / 60 / 60;
//            int minute = time / 60;
//            if (hour > 0) {
//                timeStr = "<font color='blue'><b><tt>" +
//                        hour +
//                        "</tt></b></font>"
//                        + getString(R.string.hour_) +
//                        "<font color='black'><b><tt>" +
//                        time / 60 % 60 +
//                        "</tt></b></font>" +
//                        getString(R.string.minute_);
//            } else {
//                timeStr = "<font color='blue'><b><tt>" +
//                        minute +
//                        "</tt></b></font>" +
//                        getString(R.string.minute_);
//            }
//            left_time.setText(Html.fromHtml(disStr + timeStr));
        } else {
            String disStr;
            int km = dis / 1000;
            if (km >= 1) {
                String disKm = new DecimalFormat("#0.0").format((double) dis / 1000);
                disStr = getString(R.string.left) +
                        "<font color='white'><b><tt>" +
                        disKm + "</tt></b></font>" + getString(R.string.km);
            } else {
                disStr = getString(R.string.left) +
                        "<font color='white'><b><tt>" +
                        dis + "</tt></b></font>"
                        + getString(R.string.meter);
            }
            tv_total_destance.setText(Html.fromHtml(disStr));

            String timeStr;
            int hour = time / 60 / 60;
            int minute = time / 60;
            if (hour > 0) {
                timeStr = getString(R.string.about_) +
                        "<font color='white'><b><tt>" +
                        hour +
                        "</tt></b></font>"
                        + getString(R.string.hour_) +
                        "<font color='white'><b><tt>" +
                        time / 60 % 60 +
                        "</tt></b></font>" +
                        getString(R.string.minute_);
            } else {
                timeStr = getString(R.string.about_) +
                        "<font color='white'><b><tt>" +
                        minute +
                        "</tt></b></font>" +
                        getString(R.string.minute_);
            }
            tv_total_time.setText(Html.fromHtml(timeStr));
        }
    }

    @Override
    public void showReCal() {

    }

    @Override
    public void showToEndFragment() {

    }

    @Override
    public void showConsumer(ConsumerInfo consumerInfo) {

    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void reRout() {

    }


    /**
     * 设置滑动按钮文字及其监听
     */
    public void setSliderText(ZCOrder zcOrder) {
        if (zcOrder.orderStatus == DJOrderStatus.NEW_ORDER ||
                zcOrder.orderStatus == DJOrderStatus.PAIDAN_ORDER) {
            slider.setHint("滑动接单");
        } else if (zcOrder.orderStatus == DJOrderStatus.TAKE_ORDER) {
            slider.setHint("滑动前往预约地");
        } else if (zcOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
            slider.setHint("滑动到达预约地");
        } else if (zcOrder.orderStatus == DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
            slider.setHint("滑动出发");
        } else if (zcOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
            slider.setHint("滑动到达目的地");
        }

        slider.setmCallBack(new CustomSlideToUnlockView.CallBack() {
            @Override
            public void onSlide(int distance) {

            }

            @Override
            public void onUnlocked() {
                if (zcOrder.orderStatus == DJOrderStatus.NEW_ORDER ||
                        zcOrder.orderStatus == DJOrderStatus.PAIDAN_ORDER) {
                    presenter.acceptOrder(zcOrder.orderId, zcOrder.version, null);
                } else if (zcOrder.orderStatus == DJOrderStatus.TAKE_ORDER) {
                    presenter.toStart(zcOrder.orderId, zcOrder.version, null);
                } else if (zcOrder.orderStatus == DJOrderStatus.GOTO_BOOKPALCE_ORDER) {
                    presenter.arriveStart(zcOrder.orderId, zcOrder.version);
                } else if (zcOrder.orderStatus == DJOrderStatus.ARRIVAL_BOOKPLACE_ORDER) {
                    presenter.startDrive(zcOrder.orderId, zcOrder.version);
                } else if (zcOrder.orderStatus == DJOrderStatus.GOTO_DESTINATION_ORDER) {
                    presenter.arriveDes(zcOrder, zcOrder.version, null, DymOrder.findByIDType(zcOrder.orderId, zcOrder.orderType));
                }
                resetView();
            }
        });
    }

    /**
     * 滑动重置handler
     */
    Handler handler = new Handler();

    /**
     * 重置slider
     */
    private void resetView() {
        handler.postDelayed(() -> runOnUiThread(() -> {
            slider.resetView();
            slider.setVisibility(View.VISIBLE);
        }), 1000);
        //防止卡顿
    }

}
