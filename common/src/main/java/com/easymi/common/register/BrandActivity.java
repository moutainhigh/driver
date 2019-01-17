package com.easymi.common.register;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.Brands;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.widget.city.LetterIndexView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @author hufeng
 * 已废弃
 */
public class BrandActivity extends RxBaseActivity {

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.com_activity_brand;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        bindViews();
        getBrands();
    }

    private BrandAdapter adapter;
    private TextView txtCenter;

    private void bindViews() {
        findViewById(R.id.left_icon).setOnClickListener(v -> finish());
        RecyclerView rv = findViewById(R.id.rv);
        LetterIndexView letterIndexView = findViewById(R.id.letterIndexView);
        txtCenter = findViewById(R.id.txt_center);


        adapter = new BrandAdapter();
        adapter.setOnBrandClickListener(brand -> {
            Intent intent = new Intent();
            intent.putExtra("brandName", brand.chinese);
            intent.putExtra("brandId", brand.id);
            setResult(Activity.RESULT_OK, intent);
            finish();
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv.setLayoutManager(layoutManager);
        int stickyHeight = dp2px(this, 33);
        rv.addItemDecoration(new BrandDecoration(this, stickyHeight, 1));
        rv.setAdapter(adapter);

        // 右边字母竖排的初始化以及监听
        letterIndexView.init(new LetterIndexView.OnTouchLetterIndex() {
            //实现移动接口
            @Override
            public void touchLetterWitch(String letter) {
                // 中间显示的首字母
                txtCenter.setVisibility(View.VISIBLE);
                txtCenter.setText(letter);

                if (TextUtils.isEmpty(letter)) {
                    return;
                }
                int index = -1;
                for(int i =0;i<cacheList.size() ;i++) {
                    String first = cacheList.get(i).initialsCn.toUpperCase();
                    if (TextUtils.equals(first, letter.toUpperCase())) {
                        index = i;
                        break;
                    }
                }
                if (index > 0) {
                    layoutManager.scrollToPositionWithOffset(index, 0);
                }
            }

            //实现抬起接口
            @Override
            public void touchFinish() {
                txtCenter.setVisibility(View.GONE);
            }
        });


    }

    private List<Brands.Brand> cacheList = new ArrayList<>();
    private void getBrands() {
        Observable<List<Brands.Brand>> observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getBrands()
                .filter(new HttpResultFunc<>())
                .map(brands -> {
                    MemberSortUtil sortUtil = new MemberSortUtil();
                    Collections.sort(brands.brandList, sortUtil);
                    return brands.brandList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        Subscription d = observable.subscribe(new MySubscriber<>(this, false, false, brandList -> {
            if (brandList != null && !brandList.isEmpty()) {
                cacheList.addAll(brandList);
            }
            adapter.setDatas(brandList);
        }));
        mRxManager.add(d);
    }

    private static int dp2px(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    private class MemberSortUtil implements Comparator<Brands.Brand> {
        @Override
        public int compare(Brands.Brand o1, Brands.Brand o2) {
            return o1.initialsCn.charAt(0) - o2.initialsCn.charAt(0);
        }
    }


}
