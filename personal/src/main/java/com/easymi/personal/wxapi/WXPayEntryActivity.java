package com.easymi.personal.wxapi;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.utils.ToastUtil;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private int errcode;
    private TextView payResult;
    private IWXAPI api;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        api = WXAPIFactory.createWXAPI(this, Config.WX_APPID);
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
            ToastUtil.showMessage(this,getString(R.string.wx_pay_suc));
            finish();
        } else {
            ToastUtil.showMessage(this,getString(R.string.wx_pay_failed));
            finish();
        }
    }
}