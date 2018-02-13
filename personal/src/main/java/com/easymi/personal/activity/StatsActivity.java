package com.easymi.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.RatingBar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.entity.ShareInfo;
import com.easymi.personal.result.ShareResult;
import com.easymi.personal.result.StatisResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/10 0010.
 */

public class StatsActivity extends RxBaseActivity {

    RatingBar ratingBar;

    private String startTime;
    private String endTime;

    private String timeType = "today";

    private RadioGroup radioGroup;

    private RadioButton radioToday;

    TextView refuse_percent;
    TextView accept_percent;
    TextView accept_account;
    TextView finish_account;
    TextView income_cost;
    TextView total_cost;
    TextView star_text;

    @Override
    public int getLayoutId() {
        return R.layout.activity_stats;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        ratingBar = findViewById(R.id.rating_bar);
        radioToday = findViewById(R.id.radio_today);

        refuse_percent = findViewById(R.id.refuse_percent);
        accept_percent = findViewById(R.id.accept_percent);
        accept_account = findViewById(R.id.accept_account);
        finish_account = findViewById(R.id.finish_account);
        income_cost = findViewById(R.id.income_cost);
        total_cost = findViewById(R.id.total_cost);
        star_text = findViewById(R.id.star_text);

        ratingBar.setStarMark(5f);

        radioGroup = findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((radioGroup, id) -> {
            if (id == R.id.radio_today) {
                timeType = "today";
            } else if (id == R.id.radio_yesterday) {
                timeType = "yesterday";
            } else if (id == R.id.radio_this_month) {
                timeType = "thisMonth";
            } else if (id == R.id.radio_this_year) {
                timeType = "thisYear";
            }
            getStatis();
        });
        radioToday.setChecked(true);
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftIcon(R.drawable.ic_arrow_back, view -> {
            onBackPressed();
        });
        cusToolbar.setTitle(R.string.set_statistics);
    }


    private void getStatis() {
        getTimeZone();
        Employ employ = EmUtil.getEmployInfo();

        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<StatisResult> observable = api
                .driverSend(employ.id, Config.APP_KEY, startTime, endTime)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                false, result -> {
            if (null != result.driverSendInfo) {
                accept_account.setText(String.valueOf(result.driverSendInfo.send_count));
                finish_account.setText(String.valueOf(result.driverSendInfo.finish_count));
                refuse_percent.setText(String.valueOf(result.driverSendInfo.refuse) + "%");
                accept_percent.setText(String.valueOf(result.driverSendInfo.accept) + "%");
                income_cost.setText("¥" + String.valueOf(result.driverSendInfo.income_cost));
                total_cost.setText("¥" + String.valueOf(result.driverSendInfo.order_cost));
                star_text.setText(String.valueOf(result.driverSendInfo.rate));
                ratingBar.setStarMark(result.driverSendInfo.rate);
            }
        })));
    }

    private void getTimeZone() {
        if (timeType.equals("all")) {
            startTime = null;
            endTime = null;
        } else if (timeType.equals("today")) {
            startTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getDayBegin().getTime());
            endTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getDayEnd().getTime());
        } else if (timeType.equals("yesterday")) {
            startTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getBeginDayOfYesterday().getTime());
            endTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getEndDayOfYesterDay().getTime());
        } else if (timeType.equals("thisWeek")) {
            startTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getBeginDayOfWeek().getTime());
            endTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getEndDayOfWeek().getTime());
        } else if (timeType.equals("thisMonth")) {
            startTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getBeginDayOfMonth().getTime());
            endTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getEndDayOfMonth().getTime());
        } else if (timeType.equals("thisYear")) {
            startTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getBeginDayOfYear().getTime());
            endTime = TimeUtil.getTime("yyyy-MM-dd", TimeUtil.getEndDayOfYear().getTime());
        }
    }
}
