package cn.projcet.hf.securitycenter.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import cn.projcet.hf.securitycenter.CApp;
import cn.projcet.hf.securitycenter.CenterConfig;
import cn.projcet.hf.securitycenter.ComConfig;
import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.activity.CallPoliceActivity;
import cn.projcet.hf.securitycenter.activity.EmergeActivity;
import cn.projcet.hf.securitycenter.activity.LuyinActivity;
import cn.projcet.hf.securitycenter.activity.WebActivity;
import cn.projcet.hf.securitycenter.network.ApiManager;
import cn.projcet.hf.securitycenter.network.HttpResultFunc;
import cn.projcet.hf.securitycenter.network.MySubscriber;
import cn.projcet.hf.securitycenter.result.EmResult;
import cn.projcet.hf.securitycenter.rxmvp.RxManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MainDialog
 * Author: shine
 * Date: 2018/11/26 下午7:16
 * Description:
 * History:
 */
public class MainDialog {

    ImageView iv_colse;
    LinearLayout lin_line_share;
    LinearLayout lin_baojin;
    LinearLayout lin_luyin;
    TextView tv_luyin_status;
    LinearLayout lin_lianxiren;
    TextView tv_lianxiren_status;
    LinearLayout lin_decript;
    TextView tv_descript_status;

    private Context mContext;
    private CusBottomSheetDialog dialog;

    private int passengerId;
    private String appKey;

    public MainDialog(Context context, int passengerId, String appKey,String aeskey) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_main, null, false);

        iv_colse = view.findViewById(R.id.iv_colse);
        lin_line_share = view.findViewById(R.id.lin_line_share);
        lin_baojin = view.findViewById(R.id.lin_baojin);
        lin_luyin = view.findViewById(R.id.lin_luyin);
        tv_luyin_status = view.findViewById(R.id.tv_luyin_status);
        lin_lianxiren = view.findViewById(R.id.lin_lianxiren);
        tv_lianxiren_status = view.findViewById(R.id.tv_lianxiren_status);
        lin_decript = view.findViewById(R.id.lin_decript);
        tv_descript_status = view.findViewById(R.id.tv_descript_status);

        this.passengerId = passengerId;
        this.appKey = appKey;

        CApp.getPreferencesEditor().putString(CenterConfig.AES_PASSWORD,aeskey).apply();

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);
//        initData(passengerId,appKey);
        initView();
    }


    public void initView() {
        iv_colse.setOnClickListener(v -> {
            dialog.dismiss();
        });
        lin_line_share.setOnClickListener(v -> {
            LineShareDialog shareDialog = new LineShareDialog(mContext);
            shareDialog.show();
        });
        lin_baojin.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CallPoliceActivity.class);
            mContext.startActivity(intent);
        });
        lin_luyin.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LuyinActivity.class);
            mContext.startActivity(intent);
        });
        lin_lianxiren.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EmergeActivity.class);
            mContext.startActivity(intent);
        });
        lin_decript.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebActivity.class);
            mContext.startActivity(intent);
        });
    }

    public void show() {
        dialog.show();
    }

    public void initData(int passengerId,String appKey) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComConfig.class)
                .checkingAuth(passengerId,appKey)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, true,
                true, emResult -> {

            dialog.show();
        })));
    }
}
