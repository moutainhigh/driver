package com.easymi.component.share;

import com.easymi.component.utils.LogUtil;
import com.tencent.tauth.IUiListener;

/**
 * Created by xyin on 2017/3/6.
 * QQ分享接口回调.
 */

public abstract class QQShareListener implements IUiListener {
    private static final String TAG = "QQShareListener";

    @Override
    public void onCancel() {
        LogUtil.d(TAG, "QQ share on cancel");
    }
}
