package com.easymi.personal.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.McService;
import com.easymi.personal.R;
import com.easymi.personal.result.ArticleResult;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class FeedbackActivity extends RxBaseActivity {

    EditText editText;

    @Override
    public int getLayoutId() {
        return R.layout.activity_feedback;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        editText = findViewById(R.id.edit_text);
    }

    public void apply(View view) {
        if (StringUtils.isBlank(editText.getText().toString())) {
            ToastUtil.showMessage(this, getString(R.string.feedback_please_msg));
            return;
        }
        McService api = ApiManager.getInstance().createApi(Config.HOST, McService.class);

        Observable<EmResult> observable = api
                .feedback(EmUtil.getEmployId(),EmUtil.getEmployInfo().user_name, EmUtil.getEmployInfo().company_id, Config.APP_KEY, editText.getText().toString())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true,
                true, emResult -> {
            ToastUtil.showMessage(FeedbackActivity.this, getString(R.string.feedback_suc));
            finish();
        })));
    }
}
