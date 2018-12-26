package com.easymi.zhuanche.activity;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.navi.AMapNavi;
import com.amap.api.navi.AMapNaviListener;
import com.amap.api.navi.AMapNaviView;
import com.amap.api.navi.AMapNaviViewListener;
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
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.loc.LocObserver;
import com.easymi.component.loc.LocReceiver;
import com.easymi.component.utils.ToastUtil;
import com.easymi.zhuanche.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

@Route(path = "/zhuanche/TestActivity")
public class TestActivity extends RxBaseActivity implements LocObserver ,AMap.OnMyLocationChangeListener {

//    AMapNaviView mAMapNaviView;

    MapView map_view;

    private AMap aMap;
    protected AMapNavi mAMapNavi;

    protected NaviLatLng mEndLatlng;
    protected NaviLatLng mStartLatlng;

    protected final List<NaviLatLng> sList = new ArrayList<>();
    protected final List<NaviLatLng> eList = new ArrayList<>();

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;

    private LatLng lastLatlng;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
//        mAMapNaviView = findViewById(R.id.navi_view);
//        mAMapNaviView = findViewById(com.easymi.component.R.id.navi_view);
//        mAMapNaviView.onCreate(savedInstanceState);
//        mAMapNaviView.setAMapNaviViewListener(this);

        map_view = findViewById(R.id.map_view);
        map_view.onCreate(savedInstanceState);

        mAMapNavi = AMapNavi.getInstance(this);
//        mAMapNavi.addAMapNaviListener(this);
        mAMapNavi.setUseInnerVoice(true);

        mStartLatlng = new NaviLatLng(30.875611,103.675388);
        mEndLatlng = new NaviLatLng(30.875634,103.675312);

        sList.add(mStartLatlng);
        eList.add(mEndLatlng);

        initMap();
    }


    public void initMap() {
        aMap = map_view.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo

//        aMap.setOnMyLocationChangeListener(this);

        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false
        aMap.setMyLocationEnabled(true);
        aMap.setOnMyLocationChangeListener(this);

        String locStr = XApp.getMyPreferences().getString(Config.SP_LAST_LOC, "");
        EmLoc emLoc = new Gson().fromJson(locStr, EmLoc.class);
//        if (null != emLoc) {
            lastLatlng = new LatLng(emLoc.latitude, emLoc.longitude);
            receiveLoc(emLoc);//手动调用上次位置 减少从北京跳过来的时间
//            aMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLatlng, 17));//移动镜头，首次镜头快速跳到指定位置
//
//            MarkerOptions markerOption = new MarkerOptions();
//            markerOption.position(new LatLng(emLoc.latitude, emLoc.longitude));
//            markerOption.draggable(false);//设置Marker可拖动
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
//            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//            markerOption.setFlat(true);//设置marker平贴地图效果
//            myFirstMarker = aMap.addMarker(markerOption);

        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);
        myLocationStyle.strokeWidth(0);
        myLocationStyle.strokeColor(R.color.white);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
                .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
        aMap.setMyLocationStyle(myLocationStyle);


//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocReceiver.getInstance().addObserver(this);//添加位置订阅
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocReceiver.getInstance().deleteObserver(this);//取消位置订阅
    }

    @Override
    protected void onResume() {
        super.onResume();
        map_view.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        map_view.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map_view.onDestroy();
    }


    @Override
    public void receiveLoc(EmLoc loc) {
        // 如果不设置标志位，此时再拖动地图时，它会不断将地图移动到当前的位置
        if (isFirstLoc) {
            //设置缩放级别
            aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            //将地图移动到定位点
            aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(loc.latitude, loc.longitude)));

            //添加图钉
//            MarkerOptions markerOption = new MarkerOptions();
//            markerOption.position(new LatLng(loc.latitude, loc.longitude));
//            markerOption.draggable(false);//设置Marker可拖动
//            markerOption.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory
//                    .decodeResource(getResources(), R.mipmap.ic_flow_my_pos)));
//            // 将Marker设置为贴地显示，可以双指下拉地图查看效果
//            markerOption.setFlat(true);//设置marker平贴地图效果
//
//            aMap.addMarker(markerOption);
            isFirstLoc = false;
        }
    }


    @Override
    public void onMyLocationChange(Location location) {

    }
}
