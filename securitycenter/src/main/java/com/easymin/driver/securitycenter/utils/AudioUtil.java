package com.easymin.driver.securitycenter.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.view.View;
import android.widget.Toast;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.ComService;
import com.easymin.driver.securitycenter.R;
import com.easymin.driver.securitycenter.network.ApiManager;
import com.easymin.driver.securitycenter.network.HttpResultFunc;
import com.easymin.driver.securitycenter.network.MySubscriber;
import com.easymin.driver.securitycenter.result.EmResult;
import com.easymin.driver.securitycenter.rxmvp.RxManager;
import com.easymin.driver.securitycenter.servicer.RecordingService;

import java.io.File;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AudioUtil
 * Author: shine
 * Date: 2018/12/14 下午1:26
 * Description:
 * History:
 */
public class AudioUtil {

    private void onRecord(Context context, boolean start) {
        Intent intent = new Intent(context, RecordingService.class);
        if (start) {
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }
            context.startService(intent);
        } else {
            context.stopService(intent);
        }
    }

}
