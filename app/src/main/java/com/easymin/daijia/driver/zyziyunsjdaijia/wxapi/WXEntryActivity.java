package com.easymin.daijia.driver.zyziyunsjdaijia.wxapi;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.easymi.component.Config;
import com.easymi.personal.R;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: WXEntryActivity
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    /**
     * IWXAPI 是第三方app和微信通信的openapi接口
     */
    private IWXAPI api;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Config.WX_APP_ID, false);
        api.registerApp(Config.WX_APP_ID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.errCode == BaseResp.ErrCode.ERR_OK) {
            //非正常返回,分享失败
//            Toast.makeText(this, getResources().getString(R.string.wx_share_suc), Toast.LENGTH_SHORT).show();
        } else {
            //分享成功
            Toast.makeText(this, getResources().getString(R.string.wx_share_failed), Toast.LENGTH_SHORT).show();
        }

        this.finish();
    }
}
