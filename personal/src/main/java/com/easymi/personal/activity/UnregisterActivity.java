package com.easymi.personal.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.McService;
import com.easymi.personal.R;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UnregisterActivity extends RxBaseActivity {
    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_p_unregister;
    }

    AlertDialog alertDialog;

    @Override
    public void initViews(Bundle savedInstanceState) {
        TextView account_info = findViewById(R.id.account_info);
        String phone = EmUtil.getEmployInfo().phone;
        if (StringUtils.isNotBlank(phone) && phone.length() == 11) {
            phone = phone.substring(0, 3) + "****" + phone.substring(7, 11);
        }
        account_info.setText("将" + phone + "所绑定的账号注销");

        findViewById(R.id.unregister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View view = LayoutInflater.from(UnregisterActivity.this).inflate(R.layout.confirm_unregister_dialog, null, false);

                Button cancel = view.findViewById(R.id.cancel);
                Button confirm_unregister = view.findViewById(R.id.confirm_unregister);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                confirm_unregister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                        if (DymOrder.findAll().size() != 0) {
                            ToastUtil.showMessage(UnregisterActivity.this, "存在执行中的订单，无法注销账号");
                            return;
                        }
                        unregisterAccount(EmUtil.getEmployId());
                    }
                });
                alertDialog = new AlertDialog.Builder(UnregisterActivity.this)
                        .setView(view)
                        .create();

                alertDialog.show();
            }
        });
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(v -> finish());
        cusToolbar.setTitle("注销账号");
    }


    private void unregisterAccount(long id) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, McService.class)
                .unregisterAccount(id)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, false, false, emResult -> {
            XApp.getEditor().clear().apply();
            AlertDialog dialog = new AlertDialog.Builder(UnregisterActivity.this)
                    .setMessage("账号已注销成功").create();
            dialog.setOnDismissListener(dialog1 -> EmUtil.employLogout(UnregisterActivity.this));
            dialog.show();
        })));
    }
}
