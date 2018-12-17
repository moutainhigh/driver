package com.easymin.driver.securitycenter.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
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

import static android.content.Context.MODE_PRIVATE;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AudioUtil
 * Author: shine
 * Date: 2018/12/14 下午1:26
 * Description:
 * History:
 */
public class AudioUtil {

    public void onRecord(Context context, boolean start) {
        Intent intent = new Intent(context, RecordingService.class);
        if (start) {
            File folder = new File(Environment.getExternalStorageDirectory() + "/SoundRecorder");
            if (!folder.exists()) {
                folder.mkdir();
            }
            context.startService(intent);
        } else {
            context.stopService(intent);

            String filePaht = context.getSharedPreferences("sp_name_audio", MODE_PRIVATE).getString("audio_path", "");
            Log.e("hufeng",filePaht);

            if (!TextUtils.isEmpty(filePaht)){
                CenterUtil centerUtil = new CenterUtil(context);
                centerUtil.putAudio(new File(filePaht),CenterConfig.QINIU_TOKEN);
            }
        }
    }

}
