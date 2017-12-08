package com.easymi.component;

/**
 * Created by developerLzh on 2017/11/3 0003.
 * <p>
 * 系统地址
 */

public class Config {

    public static final String HOST = "http://192.168.0.72:8082/";
    public static final String IMG_SERVER = "http://192.168.0.111:8088/v1/img?img=";
    public static final String APP_KEY = "1f462eb305a2417c9564f2dfaf89da9c";

    public static final String SP_DRIVERID = "driverId";
    public static final String SP_ISLOGIN = "isLogin";
    public static final String SP_LAST_LOC = "lastLoc";
    public static final String SP_NEED_TRACE = "needTrace";//是否需要纠偏

    public static final String SP_CONGESTION = "congestion";//躲避拥堵
    public static final String SP_AVOID_HIGH_SPEED = "avoidhightspeed";//不走高速
    public static final String SP_COST = "cost";//避免收费
    public static final String SP_HIGHT_SPEED = "hightspeed";//高速优先
}
