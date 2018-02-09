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

import co.lujun.androidtagview.ColorFactory;
import co.lujun.androidtagview.TagContainerLayout;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/15 0015.
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

        getEva();
//        ratingBar.setStarMark(4.4f);
//
//        tagContainerLayout.removeAllTags();
//        tagContainerLayout.addTag("长得帅");
//        tagContainerLayout.addTag("说话平和");
//        tagContainerLayout.addTag("很长的tag很长的tag很长的tag很长的tag");
//        tagContainerLayout.addTag("财大气粗");
    }

    @Override
    public void initToolBar() {
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setTitle(R.string.person_pingjia);
        cusToolbar.setLeftBack(view -> onBackPressed());
    }

    @SuppressLint("SetTextI18n")
    private void getEva() {
        Employ employ = EmUtil.getEmployInfo();

        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<EvaResult> observable = api
                .driverRate(employ.id, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                false, result -> {
            if (null != result.driverRate) {
                rate_text.setText(String.valueOf(result.driverRate.rate));
                ratingBar.setStarMark(result.driverRate.rate);
                total_number.setText(String.valueOf(result.driverRate.total_finish_count));

                star_one_number.setText(String.valueOf(result.driverRate.star_one) + getString(R.string.number));
                star_two_number.setText(String.valueOf(result.driverRate.star_two) + getString(R.string.number));
                star_three_number.setText(String.valueOf(result.driverRate.star_three) + getString(R.string.number));
                star_four_number.setText(String.valueOf(result.driverRate.star_four) + getString(R.string.number));
                star_five_number.setText(String.valueOf(result.driverRate.star_five) + getString(R.string.number));

                double onePercent = (double) result.driverRate.star_one / (double) result.driverRate.total_finish_count;
                one_star_progress.setProgress((int) (onePercent * 100));

                double twoPercent = (double) result.driverRate.star_two / (double) result.driverRate.total_finish_count;
                two_star_progress.setProgress((int) (twoPercent * 100));

                double threePercent = (double) result.driverRate.star_three / (double) result.driverRate.total_finish_count;
                three_star_progress.setProgress((int) (threePercent * 100));

                double fourPercent = (double) result.driverRate.star_four / (double) result.driverRate.total_finish_count;
                four_star_progress.setProgress((int) (fourPercent * 100));

                double fivePercent = (double) result.driverRate.star_five / (double) result.driverRate.total_finish_count;
                five_star_progress.setProgress((int) (fivePercent * 100));
            }
            if (null != result.rateInfos && result.rateInfos.size() != 0) {
                for (RateInfo rateInfo : result.rateInfos) {
                    tagContainerLayout.addTag(rateInfo.rate + "  " + rateInfo.count);
                }
            }
        })));
    }
}
