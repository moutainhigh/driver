package com.easymi.component.tts;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.HashMap;

import static android.content.ContentValues.TAG;


/**
 * Created by fujiayi on 2017/5/19.
 */

public class OfflineResource {

    private AssetManager assets;
    private String destPath;

    private String textFilename;
    private String modelFilename;

    private static HashMap<String, Boolean> mapInitied = new HashMap<String, Boolean>();

    public OfflineResource(Context context) throws IOException {
        context = context.getApplicationContext();
        this.assets = context.getApplicationContext().getAssets();
        this.destPath = FileUtil.createTmpDir(context);
        setOfflineVoiceType();
    }

    public String getModelFilename() {
        return modelFilename;
    }

    public String getTextFilename() {
        return textFilename;
    }

    public void setOfflineVoiceType() throws IOException {
        String text = "bd_etts_text.dat";
        String model = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        textFilename = copyAssetsFile(text);
        modelFilename = copyAssetsFile(model);
    }


    private String copyAssetsFile(String sourceFilename) throws IOException {
        String destFilename = destPath + "/" + sourceFilename;
        boolean recover = false;
        Boolean existed = mapInitied.get(sourceFilename); // 启动时完全覆盖一次
        if (existed == null || !existed) {
            recover = true;
        }
        FileUtil.copyFromAssets(assets, sourceFilename, destFilename, recover);
        Log.i(TAG, "文件复制成功：" + destFilename);
        return destFilename;
    }


}
