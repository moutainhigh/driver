package com.easymin.carpooling.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.util.LongSparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.fence.GeoFence;
import com.amap.api.fence.GeoFenceClient;
import com.amap.api.fence.GeoFenceListener;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeQuery;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.MapPositionModel;
import com.easymin.carpooling.entity.Station;
import com.jaredrummler.android.processes.models.Stat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: SelectPlaceOnMapActivity
 * @Author: hufeng
 * @Date: 2019/5/21 上午11:51
 * @Description:
 * @History:
 */
public class SelectPlaceOnMapActivity extends RxBaseActivity implements GeoFenceListener,
        GeocodeSearch.OnGeocodeSearchListener,
        LocationSource,
        AMapLocationListener,
        View.OnClickListener {
    private TextView mInputEt;
    private Button mConfirmPosBtn;
    private MapView mMapView;
    private AMap mAMap;

    private TextView tv_call_server;

    /**
     * 起点或者终点电子围栏点集合
     */
    private List<Station> mMapPosList = new ArrayList<>();
    /**
     * 地图定位对象
     */
    private AMapLocationClient mlocationClient;
    /**
     * 定位监听
     */
    private LocationSource.OnLocationChangedListener mListener;
    /**
     * 多边形围栏的边界点
     */
    private List<LatLng> polygonPoints = new ArrayList<>();

    // 当前的坐标点集合，主要用于进行地图的可视区域的缩放
    private LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
    // 地理围栏客户端
    private GeoFenceClient fenceClient = null;
    /**
     * 触发地理围栏的行为，默认为进入提醒
     * 地理围栏的广播action
     */
    private static final String GEOFENCE_BROADCAST_ACTION = "com.easymi.cityline.GEOFENCE_BROADCAST_ACTION";
    /**
     * 记录已经添加成功的围栏
     */
    private HashMap<String, GeoFence> fenceMap = new HashMap<>();
    /**
     * 逆地理编码
     */
    private GeocodeSearch mGeocodeSearch;
    /**
     * 当前城市
     */
    private String mNowCity;
    /**
     * 当前位置经纬度
     */
    private float mLongitude, mLatitude;
    /**
     * 是否是第一次加载
     */
    private boolean mIsFirstIn = true;

    private LongSparseArray<Polygon> polygonMap;

    private int CHOSE_FLAG = 0;//0起点 1终点
    public static final int REQUEST_CODE = 0X12;

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(typeText);
    }

    /**
     * 标题内容
     */
    String typeText;

    @Override
    public void initViews(@Nullable Bundle savedInstanceState) {
        mMapPosList = getIntent().getParcelableArrayListExtra("pos_list");
        fenceClient = new GeoFenceClient(getApplicationContext());
        mMapView = findViewById(R.id.map_view);
        mConfirmPosBtn = findViewById(R.id.confirm_pos_btn);
        tv_call_server = findViewById(R.id.tv_call_server);

        mConfirmPosBtn.setOnClickListener(this);
        mMapView.onCreate(savedInstanceState);

        polygonMap = new LongSparseArray<Polygon>();

        init();

        mInputEt = findViewById(R.id.input_place_et);

        mInputEt.setOnClickListener(view -> {
            Intent intent = new Intent(SelectPlaceOnMapActivity.this, PlaceActivity.class);
            intent.putExtra("hint", CHOSE_FLAG == 0 ? "在哪儿接我" : "送我到哪儿");
            startActivityForResult(intent, REQUEST_CODE);
        });

        typeText = "";
        switch (getIntent().getIntExtra("select_place_type", -1)) {
            case 1:
                //上车点
                typeText = getString(R.string.pc_select_aboard_place);
                mInputEt.setHint(R.string.pc_input_detail_on_address);
                CHOSE_FLAG = 1;
                break;
            case 3:
                //下车点
                typeText = getString(R.string.pc_select_debus_place);
                mInputEt.setHint(R.string.pc_input_detail_off_address);
                CHOSE_FLAG = 3;
                break;
            default:
                break;
        }
        tv_call_server.setOnClickListener(view -> {
            PhoneUtil.call(this, EmUtil.getEmployInfo().serviceTel);
        });

        createMarker();


    }

    /**
     * 添加多边形围栏并画图
     */
    private void addFenceAndDraw(Station station) {
        if (null == station.coordinate || station.coordinate.size() < 3) {
            return;
        }

        List<LatLng> latLngs = new ArrayList<>();
        for (int i = 0; i < station.coordinate.size(); i++) {
            LatLng latLng = new LatLng(station.coordinate.get(i).getLatitude(), station.coordinate.get(i).getLongitude());
            latLngs.add(latLng);
        }

        PolygonOptions polygonOption = new PolygonOptions();
        polygonOption.addAll(latLngs);
        polygonOption.strokeColor(Color.parseColor("#089A55")).fillColor(Color.parseColor("#33089A55")).strokeWidth(5);

        polygonMap.put(station.id, mAMap.addPolygon(polygonOption));
    }

    /**
     * 地图初始化
     */
    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
            mAMap.getUiSettings().setLogoBottomMargin(-50);//隐藏logo
//            setUpMap();
            mGeocodeSearch = new GeocodeSearch(this);
            mGeocodeSearch.setOnGeocodeSearchListener(this);
        }
        IntentFilter filter = new IntentFilter();
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        registerReceiver(mGeoFenceReceiver, filter);
        /*
         * 创建pendingIntent
         */
        fenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
        fenceClient.setGeoFenceListener(this);
        /*
         * 设置地理围栏的触发行为,默认为进入
         */
        fenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位监听
        mAMap.setLocationSource(this);
        // 设置默认定位按钮是否显示
        mAMap.getUiSettings().setMyLocationButtonEnabled(true);
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        // 自定义定位蓝点图标
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.pc_navi_map_gps_locked));
        // 自定义精度范围的圆形边框颜色
        myLocationStyle.strokeColor(Color.argb(0, 0, 0, 0));
        // 自定义精度范围的圆形边框宽度
        myLocationStyle.strokeWidth(0);
        // 设置圆形的填充颜色
        myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0));
        // 将自定义的 myLocationStyle 对象添加到地图上
        mAMap.setMyLocationStyle(myLocationStyle);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        mAMap.setMyLocationEnabled(true);
        // 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        mAMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
        deactivate();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        try {
            unregisterReceiver(mGeoFenceReceiver);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        if (null != fenceClient) {
            fenceClient.removeGeoFence();
        }
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    /**
     * 根据类型显示围栏路径
     *
     * @param fence
     */
    private void drawFence(GeoFence fence) {
        switch (fence.getType()) {
            case GeoFence.TYPE_POLYGON:
            case GeoFence.TYPE_DISTRICT:
                drawPolygon(fence);
                break;
        }
        // 设置所有maker显示在当前可视区域地图中
        LatLngBounds bounds = boundsBuilder.build();
        mAMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 150));
        polygonPoints.clear();
    }

    /**
     * 添加围栏线条颜色等
     *
     * @param fence
     */
    private void drawPolygon(GeoFence fence) {
        final List<List<DPoint>> pointList = fence.getPointList();
        if (null == pointList || pointList.isEmpty()) {
            return;
        }
        for (List<DPoint> subList : pointList) {
            List<LatLng> lst = new ArrayList<>();
            PolygonOptions polygonOption = new PolygonOptions();
            for (DPoint point : subList) {
                lst.add(new LatLng(point.getLatitude(), point.getLongitude()));
                boundsBuilder.include(new LatLng(point.getLatitude(), point.getLongitude()));
            }
            polygonOption.addAll(lst);
            polygonOption.strokeColor(Color.BLUE).fillColor(R.color.pc_color_8cb4ef).strokeWidth(3);
            mAMap.addPolygon(polygonOption);
        }
    }

    private long id;

    private void checkIsIn(LatLng latLng) {
        if (polygonMap.size() == 0) {
            mConfirmPosBtn.setEnabled(false);
            return;
        }
        boolean isIn = false;
        for (int i = 0; i < polygonMap.size(); i++) {
            long currentId = polygonMap.keyAt(i);
            isIn = polygonMap.get(currentId).contains(latLng);
            if (isIn) {
                id = currentId;
                break;
            }
        }

        if (isIn) {
            mConfirmPosBtn.setEnabled(true);
            mConfirmPosBtn.setBackgroundResource(R.drawable.pc_shape_button_blue);
        } else {
            mConfirmPosBtn.setEnabled(false);
            mConfirmPosBtn.setBackgroundResource(R.drawable.pc_shape_button_gray);
        }
    }

    final Object lock = new Object();

    /**
     * 将围栏添加到地图上
     */
    void drawFence2Map() {
        new Thread() {
            @Override
            public void run() {
                try {
                    synchronized (lock) {
                        if (null == fenceList || fenceList.isEmpty()) {
                            return;
                        }
                        for (GeoFence fence : fenceList) {
                            if (fenceMap.containsKey(fence.getFenceId())) {
                                continue;
                            }
                            drawFence(fence);
                            fenceMap.put(fence.getFenceId(), fence);
                        }
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    Log.e("tagGeo", "添加围栏成功 ");
                    drawFence2Map();
                    break;
                case 1:
                    int errorCode = msg.arg1;
                    Log.e("tagGeo", "添加围栏失败 " + errorCode);
                    break;
                case 2:
                    String statusStr = (String) msg.obj;
                    Log.e("tagGeo", statusStr);
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 围栏点集合
     */
    List<GeoFence> fenceList = new ArrayList<>();

    @Override
    public void onGeoFenceCreateFinished(final List<GeoFence> geoFenceList, int errorCode, String customId) {
        Message msg = Message.obtain();
        if (errorCode == GeoFence.ADDGEOFENCE_SUCCESS) {
            fenceList.addAll(geoFenceList);
            msg.obj = customId;
            msg.what = 0;
        } else {
            msg.arg1 = errorCode;
            msg.what = 1;
        }
        handler.sendMessage(msg);
    }

    /**
     * 接收触发围栏后的广播,当添加围栏成功之后，会立即对所有围栏状态进行一次侦测，如果当前状态与用户设置的触发行为相符将会立即触发一次围栏广播；
     * 只有当触发围栏之后才会收到广播,对于同一触发行为只会发送一次广播不会重复发送，除非位置和围栏的关系再次发生了改变。
     */
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == null) return;
            // 接收广播
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) return;
                String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID);
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID);
                //status标识的是当前的围栏状态，不是围栏行为
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                StringBuilder sb = new StringBuilder();
                switch (status) {
                    case GeoFence.STATUS_LOCFAIL:
                        sb.append("定位失败");
                        break;
                    case GeoFence.STATUS_IN:
                        sb.append("进入围栏 ");
                        break;
                    case GeoFence.STATUS_OUT:
                        sb.append("离开围栏 ");
                        break;
                    case GeoFence.STATUS_STAYED:
                        sb.append("停留在围栏内 ");
                        break;
                    default:
                        break;
                }
                if (status != GeoFence.STATUS_LOCFAIL) {
                    if (!TextUtils.isEmpty(customId)) {
                        sb.append(" customId: ").append(customId);
                    }
                    sb.append(" fenceId: ").append(fenceId);
                }
                String str = sb.toString();
                Message msg = Message.obtain();
                msg.obj = str;
                msg.what = 2;
                handler.sendMessage(msg);
            }
        }
    };

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                mNowCity = amapLocation.getCity();
                // 显示系统小蓝点
                mListener.onLocationChanged(amapLocation);
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                Log.e("tagPos", errText);
            }
        }
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
            // 设置定位监听
            mlocationClient.setLocationListener(this);
            // 设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            // 只是为了获取当前位置，所以设置为单次定位
            mLocationOption.setOnceLocation(true);
            // 设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

//    /**
//     * 添加多边形围栏
//     */
//    private void addPolygonFence() {
//        if (null == polygonPoints || polygonPoints.size() < 3) {
//            ToastUtil.showMessage(getApplicationContext(), "参数不全", Toast.LENGTH_SHORT);
//            return;
//        }
//        List<DPoint> pointList = new ArrayList<>();
//        for (LatLng latLng : polygonPoints) {
//            pointList.add(new DPoint(latLng.latitude, latLng.longitude));
//        }
//        fenceClient.addGeoFence(pointList, "333333");
//    }

    private LatLng centerLatLng = null;

    /**
     * 移动到中间位置
     */
    private void toCenter() {
        switch (getIntent().getIntExtra("select_place_type", -1)) {
            case 1:
                centerLatLng = new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
                break;

            case 3:
                centerLatLng = new LatLng(mMapPosList.get(mMapPosList.size()-1).latitude, mMapPosList.get(mMapPosList.size()-1).longitude);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
                break;
            default:
                break;
        }
        addMarkersToMap();
    }

    /**
     * 创建地图marker
     */
    public void createMarker() {
        //map  地图添加   监听mark     必须的
        mAMap.setOnMapLoadedListener(() -> {

            toCenter();

            for (int i = 0; i < polygonMap.size(); i++) {
                Polygon polygon = polygonMap.valueAt(i);
                polygon.remove();
            }
            polygonMap.clear();
            for (Station station : mMapPosList) {
                addFenceAndDraw(station);
            }
            checkIsIn(centerLatLng);

        });
        //拖动地图  获取地图位置设置地图状态的监听接口。
        mAMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                centerLatLng = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                getAddressByLatlng(centerLatLng);
                checkIsIn(centerLatLng);

            }
        });// 对amap添加移动地图事件监听器
    }

    /**
     * @param text 地址
     */
    private void moveToPosByText(String text) {
        GeocodeQuery query = new GeocodeQuery(text, mNowCity);
        mGeocodeSearch.getFromLocationNameAsyn(query);
    }

    /**
     * 判断坐标点是否在多边形范围内
     *
     * @param aMap       map
     * @param latLngList 顶点坐标经纬度
     * @param latLng     需要判断的点
     * @return 判断结果
     */
    private boolean isInGeoArea(AMap aMap, List<LatLng> latLngList, LatLng latLng) {
        PolygonOptions options = new PolygonOptions();
        for (LatLng i : latLngList) {
            options.add(i);
        }
        options.visible(false); //设置区域是否显示
        Polygon polygon = aMap.addPolygon(options);
        boolean contains = polygon.contains(latLng);
        polygon.remove();
        return contains;
    }

    /**
     * 逆地理编码
     *
     * @param latLng
     */
    private void getAddressByLatlng(LatLng latLng) {
        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        mGeocodeSearch.getFromLocationAsyn(query);
    }

    /**
     * 在屏幕中心添加一个Marker
     * 可视区域的位置（CameraPosition）
     * 可视区域的位置由以下属性组成：
     * 目的地（target）
     * 缩放级别（zoom）
     * 方向（bearing）
     * 倾斜角度（tilt）
     * 屏幕当前可视区域的位置可以通过 AMap.getCameraPosition() 方法获取。
     * 目的地（target）
     * 屏幕中心marker 跳动
     * 该mark  始终在屏幕的中心点 不跟随地图的移动而移动  因此在监听地图拖动监听事件时获取地理位置  也要进行中心点的判断
     * getCameraPosition（）  和   aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {} CameraPosition  一致
     */
    private void addMarkersToMap() {
        //  目的地（target）         缩放级别（zoom）       方向（bearing）        倾斜角度（tilt）
        LatLng latLng = mAMap.getCameraPosition().target;
        Log.e("hufeng/latLng",latLng.latitude+"/"+latLng.longitude);
        Point screenPosition = mAMap.getProjection().toScreenLocation(latLng);
        Marker screenMarker = mAMap.addMarker(new MarkerOptions().title("")
                .anchor(0.5f, 1f)
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.pc_ic_mark_position)));
        //设置Marker在屏幕上,不跟随地图移动
        screenMarker.setPositionByPixels(screenPosition.x, screenPosition.y);
    }

    @Override
    public int getLayoutId() {
        return R.layout.pc_activity_select_place_on_map;
    }


    @Override
    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
        RegeocodeAddress regeocodeAddress = regeocodeResult.getRegeocodeAddress();
        String formatAddress = regeocodeAddress.getFormatAddress();
        String simpleAddress = formatAddress.substring(9);
        Log.e("tagTest", "查询经纬度对应详细地址：\n" + simpleAddress);
        if (mIsFirstIn) {
            mLongitude = (float) regeocodeResult.getRegeocodeQuery().getPoint().getLongitude();
            mLatitude = (float) regeocodeResult.getRegeocodeQuery().getPoint().getLatitude();
            mIsFirstIn = false;
        }
        mInputEt.setText(simpleAddress);
    }

    @Override
    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {
        if (i == AMapException.CODE_AMAP_SUCCESS) {
            if (geocodeResult != null && geocodeResult.getGeocodeAddressList() != null && geocodeResult.getGeocodeAddressList().size() > 0) {
                GeocodeAddress address = geocodeResult.getGeocodeAddressList().get(0);
                //获取到的经纬度
                LatLonPoint latLongPoint = address.getLatLonPoint();
                mLatitude = (float) latLongPoint.getLatitude();
                mLongitude = (float) latLongPoint.getLongitude();
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLatitude, mLongitude), 13.0f));
//                mInputEt.clearFocus();
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.confirm_pos_btn) {
            Intent intent = new Intent();
            MapPositionModel result = new MapPositionModel();
            result.setId(id);
            result.setAddress(mInputEt.getText().toString());
            result.setLongitude(mLongitude);
            result.setLatitude(mLatitude);
            result.setSequence(mMapPosList.get((int)id).sequence);
            switch (getIntent().getIntExtra("select_place_type", -1)) {
                case 1:
                    //起点
                    result.setSort(0);
                    result.setType(1);
                    break;
                case 3:
                    //终点
                    result.setSort(1);
                    result.setType(3);
                    break;
            }
            intent.putExtra("pos_model", result);
            intent.putExtra("selected_pos", mInputEt.getText().toString());
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data.getParcelableExtra("poiItem") != null) {
                PoiItem poiItem = data.getParcelableExtra("poiItem");
                mInputEt.setText(poiItem.getTitle());
                LatLonPoint latLongPoint = poiItem.getLatLonPoint();
                mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng((float) latLongPoint.getLatitude(), (float) latLongPoint.getLongitude()), mAMap.getCameraPosition().zoom));
            } else {
                ToastUtil.showMessage(this, "获取数据失败，请重试");
            }
        }
    }
}
