package com.easymi.personal.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.easymi.component.Config;
import com.easymi.component.utils.Log;
import android.widget.TextView;

import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: WXPayEntryActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private int errcode;
    private TextView payResult;
    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Config.WX_APP_ID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onResp(BaseResp resp) {
        errcode = resp.errCode;
        Log.e("datadata", "resp.code--->" + resp.errCode);
        if (errcode == 0) {
            ToastUtil.showMessage(this,getString(R.string.wx_pay_success));
            finish();
        } else {
            ToastUtil.showMessage(this,getString(R.string.wx_pay_fail));
            finish();
        }
    }
}