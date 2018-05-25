package com.easymi.component.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;

import com.alibaba.android.arouter.launcher.ARouter;
import com.amap.api.navi.AMapNavi;
import com.easymi.component.BuildConfig;
import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.db.SqliteHelper;
import com.easymi.component.loc.LocService;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.SysUtil;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by xyin on 2016/9/30.
 * application 注:每启动一个新的进程就会调用application的onCreate方法(需要注意某些方法是否允许多次初始化).
 */

public class XApp extends MultiDexApplication {

    /**
     * 提示音标志位--start
     */
    public static int ON_LINE = 1;
    public static int OFF_LINE = 2;
    public static int GRAB = 3;
    public static int CANCEL = 4;
    public static int NEW_MSG = 5;
    public static int NEW_ANN = 6;
    /**
     * 提示音标志位--end
     */

    private static final String SHARED_PREFERENCES_NAME = "em"; //SharedPreferences 文件名

    private static XApp instance;    //实例化对象

    public static SpeechSynthesizer iflytekSpe;

    AudioManager audioManager;

    public MediaPlayer player;

    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener;

    private boolean haveFoucs = false;//是否拥有焦点 通过此变量来判断player是否在正常播放

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

//        BGASwipeBackHelper.init(instance, null);

        SpeechUtility.createUtility(XApp.this, "appid=" + "57c91477");
        initIflytekTTS(false,"");

        initDataBase();

        CrashReport.initCrashReport(getApplicationContext(), "3816555448", false);
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
    private void initIflytekTTS(boolean speakNow, String msg) {
        iflytekSpe = SpeechSynthesizer.createSynthesizer(this, code -> {
            Log.d("TAG", "InitListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                if (speakNow) {
                    syntheticVoice(msg);
                }
                Log.e("initIflytekTTS", "初始化失败,错误码：" + code);
            } else {
                setTtsParam();
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

    /**
     * 请求语音播放焦点
     *
     * @return
     */
    public boolean requestFocus() {
        haveFoucs = true;
        if (mFocusChangeListener != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    audioManager.requestAudioFocus(mFocusChangeListener,
                            AudioManager.STREAM_MUSIC,
                            AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);
            //这种方式获取焦点 会将其他音量调低而不中断其他播放
        }
        return false;
    }

    /**
     * 放弃语音播放焦点
     *
     * @return
     */
    public boolean abandonFocus() {
        haveFoucs = false;
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
     * @param text
     * @param flag 语音播放前的提示音
     */
    public void syntheticVoice(String text, int flag) {
        int resId = 0;
        if (flag == ON_LINE) {
            resId = R.raw.online_sound;
        } else if (flag == OFF_LINE) {
            resId = R.raw.offline_sound;
        } else if (flag == GRAB) {
            resId = R.raw.grab;
        } else if (flag == NEW_MSG) {
            resId = R.raw.new_msg;
        } else if (flag == CANCEL) {
            resId = R.raw.cancel_order;
        } else if (flag == NEW_ANN) {
            resId = R.raw.new_ann;
        }
        if (resId == 0) {
            return;
        }
        if (player != null && player.isPlaying()) {
            player.stop();
        }
        player = MediaPlayer.create(this, resId);
        if (null != player) {
            player.setOnCompletionListener(mediaPlayer -> {
                if (StringUtils.isNotBlank(text)) {
                    syntheticVoice(text);
                } else {
                    abandonFocus();
                }
            });
            requestFocus();
            player.start();
        }
    }

    /**
     * 循环播放静音音频大道保活
     */
    public void playSlientMusic() {
//        if (!getMyPreferences().getBoolean(Config.SP_PLAY_CLIENT_MUSIC, true)) {
//            return;
//        }

        if (haveFoucs) {//有焦点时说明在正常播放音频
            return;
        }

        if (!getMyPreferences().getBoolean(Config.SP_ISLOGIN, false)) {
            return;
        }

        if (player != null && player.isPlaying()) {
            player.stop();
        }
        player = MediaPlayer.create(this, R.raw.silent);
        if (null != player) {
            player.setOnCompletionListener(mediaPlayer -> {
                Log.e("AudioFocus", "播放静音音频完成，循环播放中..");
                playSlientMusic();
            });
            player.start();
            Log.e("AudioFocus", "开始播放静音音频");
        }
    }

    /**
     * 停止播放静音音频
     */
    public void stopPlaySlientMusic() {
        if (haveFoucs) {
            return;
        }

        if (player != null && player.isPlaying()) {
            player.stop();
            Log.e("AudioFocus", "停止播放静音音频");
        }
    }

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
        boolean voiceAble = getMyPreferences().getBoolean(Config.SP_VOICE_ABLE, true);
        if (!voiceAble) {
            return;
        }
        if (iflytekSpe == null) {
            initIflytekTTS(true,msg);
            return;
        }
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
        }
    }

    public void stopVoice() {
        if (null != iflytekSpe) {
            if (iflytekSpe.isSpeaking()) {
                iflytekSpe.stopSpeaking();
            }
        }
        abandonFocus();
    }

    public void clearVoiceList() {
        if (null != voiceList) {
            voiceList.clear();
        }
    }

    public void shake() {
        boolean shakeAble = XApp.getMyPreferences().getBoolean(Config.SP_SHAKE_ABLE, true);
        if (shakeAble) {//震动
            PhoneUtil.vibrate(XApp.getInstance(), false);
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
        if (SysUtil.isServiceWork(this, "com.easymi.component.loc.LocService")) {
            return;
        }
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
                            && currentProcessName.equals(getPackageName())) {
                        return true;
                    }
                }
            }
            return false;
        } else { //在7.0以上或者部分手机还是不能获取到
            return true;
        }
    }
}
