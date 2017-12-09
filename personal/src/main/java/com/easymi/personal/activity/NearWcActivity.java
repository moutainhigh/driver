package com.easymi.personal.activity;

import android.os.Bundle;

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
import com.easymi.component.utils.MapUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;
import com.easymi.personal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by developerLzh on 2017/12/7 0007.
 */

public class NearWcActivity extends RxBaseActivity {
    CusToolbar cusToolbar;
    MapView mapView;
    SwipeRecyclerView recyclerView;

    AMap aMap;

    PoiSearch search;

    BitmapDescriptor bitmapDescriptor;

    private int page = 0;

    private List<PoiItem> items;
    private List<Marker> markers;

    @Override
    public int getLayoutId() {
        return R.layout.activity_near_wc;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mapView = findViewById(R.id.map_view);
        recyclerView = findViewById(R.id.wc_recycler);
        items = new ArrayList<>();
        markers = new ArrayList<>();

        bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.mipmap.ic_wc);

        mapView.onCreate(savedInstanceState);
        aMap = mapView.getMap();
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
        query.setPageSize(10);
        search = new PoiSearch(this, query);

        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(loc.latitude,
                loc.longitude), 0));

        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                recyclerView.complete();
                if (null != poiResult && poiResult.getPois() != null) {
                    if (page == 0) {
                        items.clear();
                        items.addAll(poiResult.getPois());
                        removeAllMarker();
                    } else {
                        items.addAll(poiResult.getPois());
                    }
                    for (PoiItem poiItem : poiResult.getPois()) {
                        Marker marker;
                        MarkerOptions options = new MarkerOptions().
                                position(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()))
                                .icon(bitmapDescriptor)
                                .draggable(false);
                        marker = aMap.addMarker(options);
                        markers.add(marker);
                    }

                    showCamera();

//                    adapter.setPoiList(items);
                    if (poiResult.getPageCount() > page) {
                        recyclerView.setLoadMoreEnable(true);
                    } else {
                        recyclerView.setLoadMoreEnable(false);
                    }
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
        if (items.size() == 0) {
            CameraUpdate update = CameraUpdateFactory
                    .newLatLngZoom(new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude), 15);
            aMap.animateCamera(update);
        } else {
            List<LatLng> latLngs = new ArrayList<>();
            for (PoiItem item : items) {
                LatLng latLng = new LatLng(item.getLatLonPoint().getLatitude(), item.getLatLonPoint().getLongitude());
                latLngs.add(latLng);
            }
            LatLngBounds bounds = MapUtil.getBounds(latLngs, new LatLng(EmUtil.getLastLoc().latitude, EmUtil.getLastLoc().longitude));
            aMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 200));
        }
    }
}
