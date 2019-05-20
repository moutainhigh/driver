package com.easymin.driver.securitycenter.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.text.TextUtils;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.servicer.RecordingService;

import java.io.File;

import static android.content.Context.MODE_PRIVATE;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: AudioUtil
 *@Author: shine
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


            if (!TextUtils.isEmpty(filePaht)){
                CenterUtil centerUtil = new CenterUtil(context);
                centerUtil.putAudio(new File(filePaht),CenterConfig.QINIU_TOKEN);
            }
        }
    }

}
