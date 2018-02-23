package com.easymi.personal.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.MapUtil;
import com.easymi.component.widget.CusErrLayout;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.R;
import com.easymi.personal.adapter.NearWcAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/12/7 0007.
 */

public class NearWcActivity extends RxBaseActivity implements AMap.OnMarkerClickListener {
    CusToolbar cusToolbar;
    MapView mapView;
    SwipeRecyclerView recyclerView;
    CusErrLayout cusErrLayout;

    AMap aMap;

    PoiSearch search;

    BitmapDescriptor bitmapDescriptor;

    private int page = 0;

    private List<PoiItem> items;
    private List<Marker> markers;

    private NearWcAdapter adapter;

    LinearLayoutManager layoutManager;

    @Override
    public int getLayoutId() {
        return R.layout.activity_near_wc;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        recyclerView = findViewById(R.id.wc_recycler);
        cusErrLayout = findViewById(R.id.cus_err_layout);
        items = new ArrayList<>();
        markers = new ArrayList<>();

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_wc);

        mapView.onCreate(savedInstanceState);

        adapter = new NearWcAdapter(this);

        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 0;
                searchNearBy();
            }

            @Override
            public void onLoadMore() {
                page++;
                searchNearBy();
            }
        });
        adapter.setOnItemClickLis(poiItem -> {
            CameraUpdate update = CameraUpdateFactory.
                    newLatLngZoom(new LatLng(poiItem.getLatLonPoint().getLatitude(),
                            poiItem.getLatLonPoint().getLongitude()), 20);
            aMap.animateCamera(update);
        });

        aMap = mapView.getMap();
        aMap.getUiSettings().setZoomControlsEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setRotateGesturesEnabled(false);
        aMap.getUiSettings().setTiltGesturesEnabled(false);//倾斜手势
        aMap.setOnMarkerClickListener(this);

        showCamera();
        searchNearBy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.near_wc);
    }

    private void searchNearBy() {

        String keyWord = "厕所|公厕|WC";
        EmLoc loc = EmUtil.getLastLoc();

        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", loc.cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageNum(page);
        query.setPageSize(30);
        search = new PoiSearch(this, query);

        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(loc.latitude,
                loc.longitude), 2500));

        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                recyclerView.complete();
                if (null != poiResult && poiResult.getPois() != null) {
                    if (page == 0) {
                        items.clear();
                        removeAllMarker();
                    }
                    items.addAll(poiResult.getPois() == null ? new ArrayList<>() : poiResult.getPois());
                    for (PoiItem poiItem : poiResult.getPois()) {
                        Marker marker;
                        MarkerOptions options = new MarkerOptions().
                                position(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()))
                                .icon(bitmapDescriptor)
                                .draggable(false)
                                .zIndex(markers.size());
                        marker = aMap.addMarker(options);
                        marker.setClickable(true);
                        markers.add(marker);
                    }

                    showCamera();

                    adapter.setItems(items);
                    if (items.size() == 0) {
                        showErr(0);
                    } else {
                        hideErr();
                    }
                    if (poiResult.getPageCount() > page) {
                        recyclerView.setLoadMoreEnable(true);
                    } else {
                        recyclerView.setLoadMoreEnable(false);
                    }
                } else {
                    showErr(-100);
                }
            }

            @Override
            public void onPoiItemSearched(PoiItem poiItem, int i) {

            }
        });
        search.searchPOIAsyn();
    }

    private void removeAllMarker() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

    private Marker myMarker;

    /**
     * 缩放
     */
    private void showCamera() {
        if (myMarker != null) {
            myMarker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .draggable(false)
                .position(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude))
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_my_loc));
        myMarker = aMap.addMarker(options);
        myMarker.setClickable(false);

        if (items.size() == 0) {
            CameraUpdate update = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude), 20);
            aMap.animateCamera(update);
        } else {
            List<LatLng> latLngs = new ArrayList<>();
            for (PoiItem item : items) {
                LatLng latLng = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
                latLngs.add(latLng);
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }

    /**
     * @param tag 0代表空数据  其他代表网络问题
     */
    private void showErr(int tag) {
        if (tag != 0) {
            cusErrLayout.setErrText(tag);
            cusErrLayout.setErrImg();
        }
        cusErrLayout.setVisibility(View.VISIBLE);
        cusErrLayout.setText(R.string.near_no_wc);
    }

    private void hideErr() {
        cusErrLayout.setVisibility(View.GONE);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Log.e("tag", "" + (int) marker.getZIndex());
        CameraUpdate update = CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 20);
        aMap.animateCamera(update);
        SwipeRecyclerView.moveToPosition(layoutManager, recyclerView.getRecyclerView(), (int) marker.getZIndex());
        return true;
    }
}
