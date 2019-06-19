package com.easymi.component.app;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.support.annotation.StringRes;
import android.support.multidex.MultiDexApplication;
import android.text.TextUtils;

import com.alibaba.android.arouter.launcher.ARouter;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.easymi.component.BuildConfig;
import com.easymi.component.Config;
import com.easymi.component.R;
import com.easymi.component.db.SqliteHelper;
import com.easymi.component.entity.TtsPojo;
import com.easymi.component.loc.LocService;
import com.easymi.component.tts.InitConfig;
import com.easymi.component.tts.NonBlockSyntherizer;
import com.easymi.component.utils.CsEditor;
import com.easymi.component.utils.CsSharedPreferences;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.SysUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jaredrummler.android.processes.AndroidProcesses;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author xyin
 * @date 2016/9/30
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


    /**
     * 实例化对象
     */
    private static XApp instance;

    public NonBlockSyntherizer mSpeechSynthesizer;

    AudioManager audioManager;

    public MediaPlayer player;

    private AudioManager.OnAudioFocusChangeListener mFocusChangeListener;

    /**
     * 是否拥有焦点 通过此变量来判断player是否在正常播放
     */
    private boolean haveFoucs = false;
    private boolean isSpeeching;

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

        initBaiduTTs();

        initDataBase();

        CrashReport.initCrashReport(getApplicationContext(), "28ff5239b4", true);

        int lastVersion = getMyPreferences().getInt(Config.SP_VERSION, 0);
        int current = SysUtil.getVersionCode(this);
        if (current > lastVersion) {
            getEditor().clear()
                    .putLong(Config.SP_DRIVERID, -1)
                    .putBoolean(Config.SP_ISLOGIN, false)
                    .putInt(Config.SP_VERSION, current)
                    .apply();
        } else {
            getEditor().remove("TTS_SPEAK").apply();
        }
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
    public static CsSharedPreferences getMyPreferences() {
        return CsSharedPreferences.getInstance();
    }

    /**
     * 获取SharedPreferences.Editor.
     *
     * @return editor对象
     */
    public static CsEditor getEditor() {
        return CsEditor.getInstance();
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

    private Map<String, String> getParams() {
        Map<String, String> params = new HashMap<String, String>();
        // 以下参数均为选填
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声 2 特别男声 3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        params.put(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置合成的音量，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_VOLUME, "9");
        // 设置合成的语速，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        params.put(SpeechSynthesizer.PARAM_PITCH, "5");
        return params;
    }

    /**
     * 初始化讯飞语音
     */
    private void initBaiduTTs() {
        // 设置初始化参数
        // 此处可以改为 含有您业务逻辑的SpeechSynthesizerListener的实现类
        SpeechSynthesizerListener listener = new SpeechSynthesizerListener() {
            @Override
            public void onSynthesizeStart(String s) {

            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

            }

            @Override
            public void onSynthesizeFinish(String s) {

            }

            @Override
            public void onSpeechStart(String s) {
                isSpeeching = true;
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }

            @Override
            public void onSpeechFinish(String s) {
                isSpeeching = false;
                if (voiceList != null && !voiceList.isEmpty()) {
                    syntheticVoice(voiceList.removeFirst());
                }
                abandonFocus();
            }

            @Override
            public void onError(String s, SpeechError speechError) {

            }
        };

        Map<String, String> params = getParams();

        InitConfig initConfig = new InitConfig(Config.TTS_APP_ID, Config.TTS_APP_KEY, Config.TTS_APP_SECRET, TtsMode.ONLINE, params, listener);

        // 此处可以改为MySyntherizer 了解调用过程
        mSpeechSynthesizer = new NonBlockSyntherizer(this, initConfig, null);


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
            player.release();
            player = null;//release时必须将其置位null，不然isPlaying（）会抛异常
        }
        player = MediaPlayer.create(this, resId);
        if (null != player) {
            Log.e("MediaPlayer", "MediaPlayer 创建成功");
            player.setOnCompletionListener(mediaPlayer -> {
                if (null != player) {
                    player.release();
                    player = null;//release时必须将其置位null，不然isPlaying（）会抛异常
                }
                if (StringUtils.isNotBlank(text)) {
                    syntheticVoice(text);
                } else {
                    abandonFocus();
                }
            });
            requestFocus();
            player.start();
        } else {
            Log.e("MediaPlayer", "MediaPlayer 创建失败");
        }
    }

    /**
     * 循环播放静音音频大道保活
     */
    public void playSlientMusic() {
//        if (!getMyPreferences().getBoolean(Config.SP_PLAY_CLIENT_MUSIC, true)) {
//            return;
//        }
        //有焦点时说明在正常播放音频
        if (haveFoucs) {
            return;
        }

        if (!getMyPreferences().getBoolean(Config.SP_ISLOGIN, false)) {
            return;
        }

        if (player != null && player.isPlaying()) {
            player.stop();
            player.release();
            //release时必须将其置位null，不然isPlaying（）会抛异常
            player = null;
        }
        player = MediaPlayer.create(this, R.raw.silent);
        if (null != player) {
            player.setOnCompletionListener(mediaPlayer -> {
                if (null != player) {
                    player.release();
                    //release时必须将其置位null，不然isPlaying（）会抛异常
                    player = null;
                }
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
            player.release();
            //release时必须将其置位null，不然isPlaying（）会抛异常
            player = null;
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
            if (!isSpeeching) {
                syntheticVoice(voiceList.removeFirst());
            }
        } else {
            stopVoice();
            syntheticVoice(text);
        }
    }


    public void syntheticVoice(String msg, long orderId, int type) {
        if (orderId != -1 && type != -1) {
            String content = getMyPreferences().getString("TTS_SPEAK", "");
            List<TtsPojo> data = new ArrayList<>();
            if (!TextUtils.isEmpty(content)) {
                data.addAll(new Gson().fromJson(content, new TypeToken<List<TtsPojo>>() {
                }.getType()));
            }

            TtsPojo lastPojo = null;
            for (TtsPojo datum : data) {
                if (datum.getId() == orderId && datum.getType() == type) {
                    lastPojo = datum;
                    break;
                }
            }

            if (lastPojo == null || System.currentTimeMillis() - lastPojo.getTime() > 3000) {
                stopVoice();
                speak(msg);
                if (lastPojo != null) {
                    data.remove(lastPojo);
                }
                TtsPojo currentPojo = new TtsPojo(orderId, type, System.currentTimeMillis());
                data.add(currentPojo);
                getEditor().putString("TTS_SPEAK", new Gson().toJson(data)).apply();
            }
        } else {
            speak(msg);
        }
    }

    private void speak(String msg) {
        boolean voiceAble = getMyPreferences().getBoolean(Config.SP_VOICE_ABLE, true);
        if (!voiceAble) {
            return;
        }
        if (mSpeechSynthesizer == null) {
            initBaiduTTs();
            return;
        }
        if (requestFocus() && null != mSpeechSynthesizer) {
            int code = mSpeechSynthesizer.speak(msg);
        }
    }

    /**
     * 播放语音
     *
     * @param msg
     */
    public void syntheticVoice(String msg) {
        syntheticVoice(msg, -1, -1);
    }

    /**
     * 停止播放
     */
    public void stopVoice() {
        if (null != mSpeechSynthesizer) {
            if (isSpeeching) {
                mSpeechSynthesizer.stop();
            }
        }
        abandonFocus();
    }

    /**
     * 清理播放列表
     */
    public void clearVoiceList() {
        if (null != voiceList) {
            voiceList.clear();
        }
    }

    /**
     * 震动
     */
    public void shake() {
        boolean shakeAble = XApp.getMyPreferences().getBoolean(Config.SP_SHAKE_ABLE, true);
        if (shakeAble) {
            PhoneUtil.vibrate(XApp.getInstance(), false);
        }
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.release();
        }
    }

    /**
     * 开启定位服务
     */
    public void startLocService() {
//        if (SysUtil.isServiceWork(this, "com.easymi.component.loc.LocService")) {
//            return;
//        }
        Intent intent = new Intent(this, LocService.class);
        intent.setAction(LocService.START_LOC);
        intent.setPackage(this.getPackageName());
        try {
            startService(intent);
        } catch (Exception ex) {
            CrashReport.postCatchedException(ex);
        }
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
