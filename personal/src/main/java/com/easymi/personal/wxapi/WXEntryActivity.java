package com.easymi.personal.wxapi;


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

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    // IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂，获取IWXAPI的实例
        api = WXAPIFactory.createWXAPI(this, Config.WX_APPID, false);
        api.registerApp(Config.WX_APPID);

        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.errCode != BaseResp.ErrCode.ERR_OK) {
            //非正常返回,分享失败
            Toast.makeText(this, getResources().getString(R.string.wx_share_suc), Toast.LENGTH_SHORT).show();
        } else {
            //分享成功
            Toast.makeText(this, getResources().getString(R.string.wx_share_failed), Toast.LENGTH_SHORT).show();
        }

        this.finish();
    }
}
