package com.easymi.component.update;

import android.content.Context;

import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.SysUtil;
import com.easymi.component.utils.ToastUtil;
import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/6/3.
 */

public class UpdateHelper {

    private Context context;

    private OnNextListener onNextListener;

    public UpdateHelper(Context context, OnNextListener onNextListener) {
        this.context = context;
        this.onNextListener = onNextListener;
        if (null != context && null != onNextListener) {
            check(520);
        }
    }

    private UpdateInfo updateInfo;

    private void check(final int notifyId) {
        UpdateManager.create(context).setUrl(checkUrl()).setManual(true).setNotifyId(notifyId).setParser(new IUpdateParser() {
            @Override
            public UpdateInfo parse(String source) throws Exception {
                Log.e("update", "source" + source);
                Gson gson = new Gson();
                CheckUpdateResult result = gson.fromJson(source, CheckUpdateResult.class);

                updateInfo = new UpdateInfo();
                updateInfo.hasUpdate = result.hasNew;
                updateInfo.updateContent = result.updateInfo;
                updateInfo.versionCode = result.code;
                updateInfo.versionName = result.version;
                updateInfo.url = result.downloadUrl;
                updateInfo.md5 = context.getPackageName() + "_" + result.version;//md5值本该是文件的MD5值  但是后台没有返回文件MD5，这里就采用包名+版本名校验是否存在
                updateInfo.size = result.size * 1024;
                updateInfo.isForce = result.force;
                updateInfo.isIgnorable = false;
                updateInfo.isSilent = false;
                return updateInfo;
            }
        }).setOnNext(() -> onNextListener.onNext()).setOnFailureListener(error -> {
            if (null != updateInfo && updateInfo.hasUpdate && updateInfo.isForce) {
                ToastUtil.showMessage(context, context.getString(R.string.update_failed));
                EmUtil.employLogout(context);
            } else {
                onNextListener.onNext();
            }
        }).setWifiOnly(false).check();
    }

    public String checkUrl() {
        return "http://vs.xiaoka.me:8080/api/v1/checkForUpdates?channel=OFFICIAL&platform=ANDROID&type=1&shortVersion=" + SysUtil.getVersionCode(context)
                + "&appkey=" + Config.APP_KEY;
    }

    public interface OnNextListener {
        void onNext();

        void onNoVersion();
    }
}
