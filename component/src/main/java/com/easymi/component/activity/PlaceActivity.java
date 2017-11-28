package com.easymi.component.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.adapter.PlaceAdapter;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.SwipeRecyclerView;
import com.google.gson.Gson;

/**
 * Created by liuzihao on 2017/11/27.
 */

public class PlaceActivity extends RxBaseActivity {

    EditText editText;
    SwipeRecyclerView recyclerView;

    private PlaceAdapter adapter;

    private PoiSearch search;

    private int page = 1;

    private EmLoc emLoc;

    @Override
    public int getLayoutId() {
        return R.layout.activity_place;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

        editText = findViewById(R.id.edit_search);
        recyclerView = findViewById(R.id.recyclerView);

        String hint = getIntent().getStringExtra("hint");
        if (StringUtils.isNotBlank(hint)) {
            editText.setHint(hint);
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new PlaceAdapter(this);
        adapter.setItemClickListener(item -> {
            Intent intent = new Intent();
            intent.putExtra("poiItem", item);
            setResult(RESULT_OK, intent);
            finish();
        });
        recyclerView.setAdapter(adapter);

        recyclerView.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                page = 0;
                searchNearBy("", emLoc.cityCode);
            }

            @Override
            public void onLoadMore() {
                page++;
                searchNearBy("", emLoc.cityCode);
            }
        });

        emLoc = new Gson().fromJson(XApp.getMyPreferences().getString(Config.SP_LAST_LOC, ""), EmLoc.class);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                if (StringUtils.isNotBlank(s)) {
                    searchNearBy(s, emLoc.cityCode);
                }
            }
        });

        searchNearBy("", emLoc.cityCode);
    }

    private void searchNearBy(String keyWord, String cityCode) {

        PoiSearch.Query query = new PoiSearch.Query(keyWord, "", cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码，码表可以参考下方（而非文字）
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageNum(page);
        query.setPageSize(10);
        search = new PoiSearch(this, query);
        search.setBound(new PoiSearch.SearchBound(new LatLonPoint(emLoc.latitude,
                emLoc.longitude), 2000));
        search.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
            @Override
            public void onPoiSearched(PoiResult poiResult, int i) {
                if (null != poiResult && poiResult.getPois() != null) {
                    adapter.setPoiList(poiResult.getPois());
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
}
