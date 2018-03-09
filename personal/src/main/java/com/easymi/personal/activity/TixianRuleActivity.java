package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.Html;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.ArticleResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixianRuleActivity extends RxBaseActivity {

    CusToolbar cusToolbar;

    TextView tixianRule;

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.tixian_rule);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian_rules;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        tixianRule = findViewById(R.id.titian_rule);

        getArticle("DriverRuleOfCash");
    }

    private void getArticle(String alias) {
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<ArticleResult> observable = api
                .getArticle(Config.APP_KEY, alias,
                        EmUtil.getEmployInfo() == null ? null : EmUtil.getEmployInfo().company_id)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, articleResult -> tixianRule.setText(Html.fromHtml(articleResult.article.contents)))));
    }
}
