package com.easymin.carpooling.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeAddress;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeAddress;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.MapPositionModel;
import com.easymin.carpooling.entity.Station;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: SelectPlaceOnMapActivity
 * @Author: hufeng
 * @Date: 2019/5/21 上午11:51
 * @Description:
 * @History:
 */
public class SelectPlaceOnMapActivity extends RxBaseActivity implements
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

    private HashMap<MapPositionModel, Polygon> polygonMap;

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
        mMapView = findViewById(R.id.map_view);
        mConfirmPosBtn = findViewById(R.id.confirm_pos_btn);
        tv_call_server = findViewById(R.id.tv_call_server);

        mConfirmPosBtn.setOnClickListener(this);
        mMapView.onCreate(savedInstanceState);

        polygonMap = new HashMap<>();

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
        List<LatLng> latLngs = new ArrayList<>();
        for (MapPositionModel mapPositionModel : station.coordinate) {
                latLngs.clear();
                String data = mapPositionModel.polygonPathJsonStr.substring(1, mapPositionModel.polygonPathJsonStr.length() - 1);
                Log.e("SelectPlaceOnMapActivity", "addFenceAndDraw " + data);
                List<Double[]> dataList = new Gson().fromJson(data, new TypeToken<List<Double[]>>() {
                }.getType());
                if (dataList.size() < 3) {
                    continue;
                }
                Log.e("SelectPlaceOnMapActivity", "addFenceAndDraw " + dataList.size());
                for (Double[] doubles : dataList) {
                    LatLng latLng = new LatLng(doubles[0], doubles[1]);
                    latLngs.add(latLng);
                }
                PolygonOptions polygonOption = new PolygonOptions();
                polygonOption.addAll(latLngs);
                polygonOption
                        .fillColor(Color.parseColor(mapPositionModel.color));
//                    .fillColor(Color.parseColor("#33089A55"))
//                    .strokeWidth(5);
                polygonMap.put(mapPositionModel, mAMap.addPolygon(polygonOption));
        }
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
        if (null != mlocationClient) {
            mlocationClient.onDestroy();
        }
    }

    private MapPositionModel mapPositionModel;
    boolean isIn = false;

    private void checkIsIn(LatLng latLng) {
        Log.e("SelectPlaceOnMapActivity", "checkisIn 1");
        if (polygonMap.size() == 0) {
            mConfirmPosBtn.setEnabled(false);
            return;
        }
        Log.e("SelectPlaceOnMapActivity", "checkisIn 2    +" + polygonMap.size());

        for (Map.Entry<MapPositionModel, Polygon> mapPositionModelPolygonEntry : polygonMap.entrySet()) {
            isIn = mapPositionModelPolygonEntry.getValue().contains(latLng);
            Log.e("SelectPlaceOnMapActivity", "checkisIn 3");
            if (isIn) {
                Log.e("SelectPlaceOnMapActivity", "checkisIn 3.5");
                mapPositionModel = mapPositionModelPolygonEntry.getKey();
                break;
            }
        }
        Log.e("SelectPlaceOnMapActivity", "checkisIn 4");
        checkButton();
    }

    public void checkButton() {
        if (isIn && isGeo) {
            mConfirmPosBtn.setEnabled(true);
            mConfirmPosBtn.setBackgroundResource(R.drawable.pc_shape_button_blue);
        } else {
            mConfirmPosBtn.setEnabled(false);
            mConfirmPosBtn.setBackgroundResource(R.drawable.pc_shape_button_gray);
        }
    }

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

    private LatLng centerLatLng = null;

    /**
     * 移动到中间位置
     */
    private void toCenter() {
        switch (getIntent().getIntExtra("select_place_type", -1)) {
            // TODO: 2019-11-21 默认位置
            case 1:
                centerLatLng = new LatLng(mMapPosList.get(0).latitude, mMapPosList.get(0).longitude);
                mAMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centerLatLng, 14));
                break;
            case 3:
                centerLatLng = new LatLng(mMapPosList.get(mMapPosList.size() - 1).latitude, mMapPosList.get(mMapPosList.size() - 1).longitude);
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
            for (Map.Entry<MapPositionModel, Polygon> mapPositionModelPolygonEntry : polygonMap.entrySet()) {
                Polygon polygon = mapPositionModelPolygonEntry.getValue();
                polygon.remove();
            }
            polygonMap.clear();
            for (Station station : mMapPosList) {
                if (mMapPosList.indexOf(station)==0) {
                    addFenceAndDraw(station);
                }
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

    boolean isGeo = false;

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

        isGeo = false;
        checkButton();
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
        Log.e("hufeng/latLng", latLng.latitude + "/" + latLng.longitude);
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

        mLongitude = (float) regeocodeResult.getRegeocodeQuery().getPoint().getLongitude();
        mLatitude = (float) regeocodeResult.getRegeocodeQuery().getPoint().getLatitude();
        mInputEt.setText(simpleAddress);

        isGeo = true;
        checkButton();
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
            mapPositionModel.address = mInputEt.getText().toString();
            mapPositionModel.longitude = mLongitude;
            mapPositionModel.latitude = mLatitude;
            switch (getIntent().getIntExtra("select_place_type", -1)) {
                case 1:
                    //起点
                    mapPositionModel.sort = 0;
                    mapPositionModel.type = 1;
                    break;
                case 3:
                    //终点
                    mapPositionModel.sort = 1;
                    mapPositionModel.type = 3;
                    break;
            }
            intent.putExtra("pos_model", mapPositionModel);
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
