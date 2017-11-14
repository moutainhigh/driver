package com.easymi.component.update;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/6/3.
 */

public class UpdateHelper {

    private Context context;
    private OnNextListener listener;
    private String mCheckUrl;

    public UpdateHelper(Context context, @NonNull String checkUrl, OnNextListener listener) {
        this.context = context;
        this.listener = listener;
        this.mCheckUrl = checkUrl;
        if (null != context && null != listener) {
            check(530);
        }
    }

    private void check(final int notifyId) {

        UpdateManager.create(context).setUrl(mCheckUrl).setManual(true).setNotifyId(notifyId).setParser(new IUpdateParser() {
            @Override
            public UpdateInfo parse(String source) throws Exception {
                Log.e("update", "source" + source);
                Gson gson = new Gson();
                CheckUpdateResult result = gson.fromJson(source, CheckUpdateResult.class);

                UpdateInfo info = new UpdateInfo();
                info.hasUpdate = result.hasNew;
                info.updateContent = result.updateInfo;
                info.versionCode = result.code;
                info.versionName = result.version;
                info.url = result.downloadUrl;
                info.md5 = context.getPackageName() + "_" + result.version;//md5值本该是文件的MD5值  但是后台没有返回文件MD5，这里就采用包名+版本名校验是否存在
                info.size = result.size * 1024;
                info.isForce = result.force;
                info.isIgnorable = false;
                info.isSilent = false;
                return info;
            }
        }).setOnNext(new IUpdateNext() {
            @Override
            public void next() {
                listener.onNext();
            }
        }).setOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(UpdateError error) {
                listener.onNoVersion();
            }
        }).setWifiOnly(false).check();
    }

    public interface OnNextListener {
        void onNext();

        void onNoVersion();
    }
}
