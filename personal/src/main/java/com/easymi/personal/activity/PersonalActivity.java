package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.launcher.ARouter;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/3 0003.
 */
@Route(path = "/personal/PersonalActivity")
public class PersonalActivity extends RxBaseActivity {
    @Override
    public int getLayoutId() {
        return R.layout.activity_personal;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    public void toLiushui(View view) {
        ARouter.getInstance()
                .build("/common/LiushuiActivity")
                .navigation();
    }

    public void toPocket(View view) {
        Intent intent = new Intent(this, PocketActivity.class);
        startActivity(intent);
    }

    public void toRefer(View view) {
        Intent intent = new Intent(this, RecommendMoneyActivity.class);
        startActivity(intent);
    }

    public void toEva(View view) {
//        Intent intent = new Intent(this,RecommendMoneyActivity.class);
//        startActivity(intent);
    }

    public void toMessage(View view) {
        Intent intent = new Intent(this, NotifityActivity.class);
        startActivity(intent);
    }

    public void toSet(View view) {
        Intent intent = new Intent(this, SetActivity.class);
        startActivity(intent);
    }
}
