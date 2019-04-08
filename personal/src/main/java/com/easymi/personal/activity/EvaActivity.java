package com.easymi.personal.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.RatingBar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.RateInfo;
import com.easymi.personal.result.EvaResult;
import com.easymi.personal.result.RateResult;

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: EvaActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 乘客评价
 * History:
 */

public class EvaActivity extends RxBaseActivity {

    RatingBar ratingBar;
    TagContainerLayout tagContainerLayout;

    TextView rate_text;
    TextView total_number;

    ProgressBar five_star_progress;
    ProgressBar four_star_progress;
    ProgressBar three_star_progress;
    ProgressBar two_star_progress;
    ProgressBar one_star_progress;


    TextView star_one_number;
    TextView star_two_number;
    TextView star_three_number;
    TextView star_four_number;
    TextView star_five_number;

    @Override
    public int getLayoutId() {
        return R.layout.activity_eva;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ratingBar = findViewById(R.id.rating_bar);
        tagContainerLayout = findViewById(R.id.eva_tag_container);

        rate_text = findViewById(R.id.rate_text);
        total_number = findViewById(R.id.total_number);
        five_star_progress = findViewById(R.id.five_star_progress);
        four_star_progress = findViewById(R.id.four_star_progress);
        three_star_progress = findViewById(R.id.three_star_progress);
        two_star_progress = findViewById(R.id.two_star_progress);
        one_star_progress = findViewById(R.id.one_star_progress);

        star_one_number = findViewById(R.id.star_one_number);
        star_two_number = findViewById(R.id.star_two_number);
        star_three_number = findViewById(R.id.star_three_number);
        star_four_number = findViewById(R.id.star_four_number);
        star_five_number = findViewById(R.id.star_five_number);

        getStar();
        getTag();
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setTitle(R.string.person_pingjia);
        cusToolbar.setLeftBack(view -> onBackPressed());
    }


    @Override
    public boolean isEnableSwipe() {
        return true;
    }

    /**
     * 获取星级
     */
    public void getStar() {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<RateResult> observable = api
                .driverstar()
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                false, result -> {
            if (null != result.data) {

                rate_text.setText(String.valueOf(EmUtil.getEmployInfo().star == 0 ? 5 : EmUtil.getEmployInfo().star));
                ratingBar.setStarMark(EmUtil.getEmployInfo().star == 0 ? 5 : (float) EmUtil.getEmployInfo().star);

                total_number.setText(String.valueOf(result.data.evaluateCount));

                star_one_number.setText(String.valueOf(result.data.one) + getString(R.string.number));
                star_two_number.setText(String.valueOf(result.data.two) + getString(R.string.number));
                star_three_number.setText(String.valueOf(result.data.three) + getString(R.string.number));
                star_four_number.setText(String.valueOf(result.data.four) + getString(R.string.number));
                star_five_number.setText(String.valueOf(result.data.five) + getString(R.string.number));

                double onePercent = (double) result.data.one / (double) result.data.evaluateCount;
                one_star_progress.setProgress((int) (onePercent * 100));

                double twoPercent = (double) result.data.two / (double) result.data.evaluateCount;
                two_star_progress.setProgress((int) (twoPercent * 100));

                double threePercent = (double) result.data.three / (double) result.data.evaluateCount;
                three_star_progress.setProgress((int) (threePercent * 100));

                double fourPercent = (double) result.data.four / (double) result.data.evaluateCount;
                four_star_progress.setProgress((int) (fourPercent * 100));

                double fivePercent = (double) result.data.five / (double) result.data.evaluateCount;
                five_star_progress.setProgress((int) (fivePercent * 100));
            }
        })));
    }

    /**
     * 获取标签
     */
    public void getTag() {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<EvaResult> observable = api
                .drivertag(1, 5)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                false, result -> {
            if (null != result.data && result.data.size() != 0) {
                for (RateInfo rateInfo : result.data) {
                    tagContainerLayout.addTag(rateInfo.content + "  " + rateInfo.count);
                }
            }
        })));
    }
}
