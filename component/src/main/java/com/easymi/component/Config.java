package com.easymi.component;

/**
 * Created by developerLzh on 2017/11/3 0003.
 * <p>
 * 系统地址
 */

public class Config {

    /**
     * 主机地址
     */
    public static final String HOST = "http://192.168.0.72:8082/";
    /**
     * 图片服务器地址
     */
    public static final String IMG_SERVER = "http://192.168.0.111:8088/v1/img?img=";
    /**
     * APP_KEY
     */
    public static final String APP_KEY = "1f462eb305a2417c9564f2dfaf89da9c";
    public static final String WX_APPID = "";

    /**
     * MQTT配置
     */
    public static final String MQTT_HOST = "tcp://192.168.0.84:1883";
    public static final String MQTT_USER_NAME = "admin";
    public static final String MQTT_PSW = "dsajkdghj**@@##$$sagdgha";
    public static final String MQTT_PUSH_TOPIC = "/driver";

    /**
     * SharedPrefence 常量配置
     */
    public static final String SP_DRIVERID = "driverId";
    public static final String SP_ISLOGIN = "isLogin";
    public static final String SP_LAST_LOC = "lastLoc";

    public static final String SP_CONGESTION = "congestion";//躲避拥堵
    public static final String SP_AVOID_HIGH_SPEED = "avoidhightspeed";//不走高速
    public static final String SP_COST = "cost";//避免收费
    public static final String SP_HIGHT_SPEED = "hightspeed";//高速优先


    public static final String SP_VOICE_ABLE = "voice_able";//能否语音播报
    public static final String SP_SHAKE_ABLE = "shake_able";//能否震动
    public static final String SP_ALWAYS_OREN = "always_oren";//是否始终横屏计价

    public static final String APP_PACKGE_NAME = "com.easymi.v5driver";

    public static final String DAIJIA = "daijia";

    public static final String BROAD_CANCEL_ORDER = "com.easymi.v5driver.BROAD_CANCEL_ORDER";
}
