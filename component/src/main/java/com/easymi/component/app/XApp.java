package com.easymi.component.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;

import com.easymi.component.utils.Log;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.navi.AMapNavi;
import com.easymi.component.BuildConfig;
import com.easymi.component.Config;
import com.easymi.component.db.SqliteHelper;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.loc.LocService;
import com.easymi.component.utils.StringUtils;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.jaredrummler.android.processes.AndroidProcesses;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Created by xyin on 2016/9/30.
 * application 注:每启动一个新的进程就会调用application的onCreate方法(需要注意某些方法是否允许多次初始化).
 */

public class XApp extends MultiDexApplication {

    private static final String SHARED_PREFERENCES_NAME = "em"; //SharedPreferences 文件名

    private static XApp instance;    //实例化对象

    public static SpeechSynthesizer iflytekSpe;

    AudioManager audioManager;

    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        if (!isAppProcess()) {
            return;
        }

        //初始化路由
        if (BuildConfig.DEBUG) {
            ARouter.openDebug();   //非打包情况下,必须调用调用
            ARouter.openLog();
        }
        ARouter.init(this);
        SqliteHelper.init(this);

        SpeechUtility.createUtility(XApp.this, "appid=" + "57c91477");
        initIflytekTTS();

        initDataBase();
    }

    private void initDataBase() {
        SqliteHelper.init(this);
    }

    /**
     * 获取ApplicationContext.
     *
     * @return context
     */
    public static XApp getInstance() {
        return instance;
    }

    /**
     * 获取app的SharedPreferences.
     *
     * @return SharedPreferences对象
     */
    public static SharedPreferences getMyPreferences() {
        return instance.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    /**
     * 获取SharedPreferences.Editor.
     *
     * @return editor对象
     */
    public static SharedPreferences.Editor getPreferencesEditor() {
        return instance.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE).edit();
    }

    /**
     * 通过字符串资源id,获取字符串.
     *
     * @param resId 需要获取字符串的资源id
     * @return 返回资源id对应的字符串, 如果获取则返回null
     */
    public static String getMyString(@StringRes int resId) {
        String str = null;
        if (instance != null) {
            str = instance.getString(resId);
        }
        return str;
    }

    /**
     * 初始化讯飞语音
     */
    private void initIflytekTTS() {
        iflytekSpe = SpeechSynthesizer.createSynthesizer(this, new InitListener() {
            @Override
            public void onInit(int code) {
                Log.d("TAG", "InitListener init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    Log.e("initIflytekTTS", "初始化失败,错误码：" + code);
                } else {
                    setTtsParam();
                }
            }
        });

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        mFocusChangeListener = focusChange -> {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // Stop playback
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                // Lower the volume
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback or Raise it back to normal
            }
        };
    }

    public boolean requestFocus() {
        if (mFocusChangeListener != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    audioManager.requestAudioFocus(mFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            //这种方式获取焦点 会将其他音量调低而不中断其他播放
        }
        return false;
    }

    public boolean abandonFocus() {
        if (mFocusChangeListener != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    audioManager.abandonAudioFocus(mFocusChangeListener);
        }
        return false;
    }

    private void setTtsParam() {
        iflytekSpe.setParameter(SpeechConstant.PARAMS, null);
        // 清空参数
        iflytekSpe.setParameter(SpeechConstant.PARAMS, null);
        // 根据合成引擎设置相应参数
        iflytekSpe.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
        // 设置在线合成发音人
        iflytekSpe.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        //设置合成语速
        iflytekSpe.setParameter(SpeechConstant.SPEED, "60");
        //设置合成音调
        iflytekSpe.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        iflytekSpe.setParameter(SpeechConstant.VOLUME, "100");
        //设置播放器音频流类型
        iflytekSpe.setParameter(SpeechConstant.STREAM_TYPE, String.valueOf(AudioManager.STREAM_MUSIC));
        // 设置播放合成音频打断音乐播放，默认为true
        iflytekSpe.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "false");

    }

    private LinkedList<String> voiceList;

    /**
     * @param text    要播报的内容
     * @param isQueue 是否需要排队
     */
    public void syntheticVoice(String text, boolean isQueue) {
        if (voiceList == null) {
            voiceList = new LinkedList<>();
        }
        if (isQueue) {
            voiceList.add(text);
            if (!iflytekSpe.isSpeaking()) {
                syntheticVoice(voiceList.removeFirst());
            }
        } else {
            stopVoice();
            syntheticVoice(text);
        }
    }

    public void syntheticVoice(String msg) {
        if (requestFocus() && null != iflytekSpe) {
            int code = iflytekSpe.startSpeaking(msg, new SynthesizerListener() {
                @Override
                public void onSpeakBegin() {
                    AMapNavi.setTtsPlaying(true);
                }

                @Override
                public void onBufferProgress(int i, int i1, int i2, String s) {

                }

                @Override
                public void onSpeakPaused() {

                }

                @Override
                public void onSpeakResumed() {

                }

                @Override
                public void onSpeakProgress(int i, int i1, int i2) {

                }

                @Override
                public void onCompleted(SpeechError speechError) {
                    AMapNavi.setTtsPlaying(false);
                    if (voiceList != null && voiceList.size() != 0) {
                        syntheticVoice(voiceList.removeFirst());
                    }
                    abandonFocus();
                }

                @Override
                public void onEvent(int i, int i1, int i2, Bundle bundle) {

                }
            });
            Log.e("speechCode", code + "");
        } else {
            initIflytekTTS();
        }
    }

    public void stopVoice() {
        if (null != iflytekSpe) {
            if (iflytekSpe.isSpeaking()) {
                iflytekSpe.stopSpeaking();
            }
        }
    }

    public void clearVoiceList() {
        if (null != voiceList) {
            voiceList.clear();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != iflytekSpe) {
            iflytekSpe.destroy();
        }
    }

    /**
     * 开启定位服务
     */
    public void startLocService() {
        Intent intent = new Intent(this, LocService.class);
        intent.setAction(LocService.START_LOC);
        intent.setPackage(this.getPackageName());
        startService(intent);
    }

    /**
     * 关闭定位服务
     */
    public void stopLocService() {
        Intent intent = new Intent(this, LocService.class);
        intent.setAction(LocService.STOP_LOC);
        intent.setPackage(this.getPackageName());
        startService(intent);
    }

    /**
     * 因为应用内存在多进程 会重复调用Application的onCreate() 这里做个判断
     *
     * @return
     */
    protected boolean isAppProcess() {
        int pid = android.os.Process.myPid();
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = AndroidProcesses.getRunningAppProcessInfo(this);
        if (processInfos.size() != 0) {
            for (ActivityManager.RunningAppProcessInfo processInfo : processInfos) {
                if (pid == processInfo.pid) {
                    String currentProcessName = processInfo.processName;
                    if (StringUtils.isNotBlank(currentProcessName)
                            && currentProcessName.equals(Config.APP_PACKGE_NAME)) {
                        return true;
                    }
                }
            }
            return false;
        } else { //在7.0以上或者部分手机还是不能获取到
            return true;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        loadLanguage();
    }

    /**
     * 加载本地设置的语言
     */
    private void loadLanguage() {
        SharedPreferences preferences = getMyPreferences();
        Configuration config = getResources().getConfiguration();   //获取默认配置
        int language = preferences.getInt(Config.SP_USER_LANGUAGE, Config.SP_LANGUAGE_AUTO);
        switch (language) {
            case Config.SP_SIMPLIFIED_CHINESE:
                config.locale = Locale.SIMPLIFIED_CHINESE;  //加载简体中文
                break;

            case Config.SP_TRADITIONAL_CHINESE:
                config.locale = Locale.TAIWAN;  //加载台湾繁体
                break;

            case Config.SP_LANGUAGE_AUTO:
                config.locale = Locale.getDefault();    //获取默认区域
                break;

            case Config.SP_ENGLISH:
                config.locale = Locale.ENGLISH;    //获取默认区域
                break;
        }
        getBaseContext().getResources().updateConfiguration(config, null);   //更新配置文件
    }
}
