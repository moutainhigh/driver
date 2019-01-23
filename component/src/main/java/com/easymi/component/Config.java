package com.easymi.component;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午5:00
 * Description:
 * History:
 */

public class Config {

    //TODO 通用版需要将app名字换位Rvakva服务端,非通用版名字为心归服务
    //TODO 换HOST(线上有Https) 换MQTT 换AppKey 换COMM_USE
    //TODO 通用版需要将名字换位Rvakva服务端
    //TODO 非通用版名字为心归服务
    /**
     * 主机地址
     */
//    public static final String HOST = "http://10.10.2.12:10001/";  //张鹏
//    public static final String HOST = "http://10.10.2.43:10001/";  //杰克
//    public static final String HOST = "http://10.10.2.35:10001/";//兵哥的
//    public static final String HOST = "http://47.94.204.67:10022/";//外网
//    public static final String HOST = "http://10.10.2.23:10001/";//俊俏
//    public static final String HOST = "http://10.10.2.130:10001/";//130测试地址 （内网）
//    public static final String HOST = "http://47.94.204.67:20001/";//130测试地址（外网）
//    public static final String HOST = "http://api.xiaokakj.com/";//线上地址
    public static final String HOST = "http://api.xiaokakj.cn/";//预发
    /**
     * MQTT配置
     */
    public static final String MQTT_HOST = "tcp://118.190.131.49:1883";//预发地址
//    public static final String MQTT_HOST = "tcp://ws.xiaokayun.cn";//线上地址
//    public static final String MQTT_HOST = "tcp://10.10.4.12:1883";//本地地址
    /**
     * APP_KEY
     */
    public static final String APP_KEY = "1HAcient1kLqfeX7DVTV0dklUkpGEnUC";//资运
//    public static final String APP_KEY = "xoRfvofrZw25b95ZBZY2venOYlSwWFOV";//v6
//    public static final String APP_KEY = "SsCMJL77sZMI0iudBi5XZeNjzmILOjih";  // 林凯
//    public static final String APP_KEY = "eubACO3wp3rjz1OBcDSa4LeS4qM4586o";  // 麒策
//    public static final String APP_KEY = "VC9NFbO9LnaxiFRi5HDQspvp45p8uP6w";  // 哈喽
//    public static final String APP_KEY = "4ji3EvuwNziPKF8QXqXMTukGqPmlwOFJ";  //v6rvakva
//    public static final String APP_KEY = "E0RzxsO1n9hZyKxOr5VvozmDsgU3EuSH";  //小鹏

    /**
     * 上传图片地址.
     */
    public static final String HOST_UP_PIC = "http://up-z2.qiniu.com";
    //是否是通用司机端
    public static final boolean COMM_USE = false;

    /**
     * 司机注册地址
     */
    public static final String REGISTER_URL = "http://register.xiaokayun.cn/employ/registerOne/";
    /**
     * QQ分享id
     */
    public static final String QQ_APP_ID = "1107818477";
    /**
     * 微信分享id
     */
    public static final String WX_APP_ID = "wxe2bbe0ee7fa51624"; //微信app id

    /**
     * 图片服务器地址
     */
//    public static final String IMG_SERVER = "https://assets.xiaokayun.cn/";
//    public static final String IMG_SERVER = "http://pimkcibsb.bkt.clouddn.com/";  //本地
    public static final String IMG_SERVER = "http://assets.xiaokakj.com/";  //线上
//    public static final String IMG_PATH = "?imageView2/1/w/10/h/10/format/webp/q/75|imageslim";
    public static final String IMG_PATH = "";

    /**
     * MQTT
     */
    //线上和预发
    public static final String MQTT_USER_NAME = "xiaoka";
    public static final String MQTT_PSW = "&mv7dHapB5J!95BJ";
//    //本地
//    public static final String MQTT_USER_NAME = "admin";
//    public static final String MQTT_PSW = "public";

    public static final String MQTT_PUSH_TOPIC = "/driver/gps";

    /**
     * SharedPrefence 常量配置
     */
    public static final String SP_DRIVERID = "driverId";
    public static final String SP_ISLOGIN = "isLogin";
    public static final String SP_LAST_LOC = "lastLoc";

    public static final String SP_QIYE_CODE = "qiye_code"; //企业编码
    public static final String SP_APP_KEY = "app_key"; //app_key
    public static final String SP_LAT_QIYE_CODE = "last_qiye_code"; //上次的企业编码


    public static final String SP_CONGESTION = "congestion";//躲避拥堵
    public static final String SP_AVOID_HIGH_SPEED = "avoidhightspeed";//不走高速
    public static final String SP_COST = "cost";//避免收费
    public static final String SP_HIGHT_SPEED = "hightspeed";//高速优先

    public static final String SP_USER_LANGUAGE = "user_choice_language";
    public static final int SP_LANGUAGE_AUTO = 0x01;   //语言跟随系统
    public static final int SP_SIMPLIFIED_CHINESE = 0x02; //简体中文
    public static final int SP_TRADITIONAL_CHINESE = 0x03; //繁体中文
    public static final int SP_ENGLISH = 0x04; //英文

    public static final String SP_SYS_LANGUAGE = "sys_language"; //英文


    public static final String SP_VOICE_ABLE = "voice_able";//能否语音播报
    public static final String SP_SHAKE_ABLE = "shake_able";//能否震动
    public static final String SP_ALWAYS_OREN = "always_oren";//是否始终横屏计价
    public static final String SP_DEFAULT_NAVI = "default_navi";//默认启动导航


    public static final String SP_LOGIN_ACCOUNT = "login_account";//账号
    public static final String SP_LOGIN_PSW = "login_psw";//密码
    public static final String SP_REMEMBER_PSW = "remember_psw";//密码

    public static final String SP_UDID = "udid";//密码

    public static final String SP_LAST_SPLASH_TIME = "last_splash_animate";//上次显示动画的时间

    public static final String SP_DAIJIA_LISTEN_ORDER = "daijia_listen_order";//代驾是否听单
    public static final String SP_ZHUANCHE_LISTEN_ORDER = "zhuanche_listen_order";//专车是否听单

    public static final String SP_PLAY_CLIENT_MUSIC = "play_slient_music";//是否播放静音音乐音乐

    public static final String SP_SHOW_GUIDE = "show_guide";//是否播放静音音乐音乐

    public static final String SP_LAST_VERSION = "sp_last_version";//是否播放静音音乐音乐
    public static final String SP_LAST_GPS_PUSH_TIME = "sp_last_gps_push_time";//mqtt上次响应的时间点

    //是否只上传GPS类型点
    public static final String SP_GPS_FILTER = "sp_gps_filter";

    public static final String DAIJIA = "daijia";
    public static final String ZHUANCHE = "special";
    public static final String TAXI = "taxi";
    public static final String CITY_LINE = "cityline";
    //定制包车
    public static final String CHARTERED = "chartered";
    //租车
    public static final String RENTAL = "rental";
    //班车
    public static final String COUNTRY = "country";

    //需要导航的模式
    public static final String NAVI_MODE = "navi_mode";
    public static final int DRIVE_TYPE = 1;
    public static final int WALK_TYPE = 2;

    public static final String BROAD_CANCEL_ORDER = "com.easymi.v5driver.BROAD_CANCEL_ORDER";
    public static final String BROAD_BACK_ORDER = "com.easymi.v5driver.BROAD_BACK_ORDER";
    public static final String BROAD_EMPLOY_STATUS_CHANGE = "com.easymi.v5driver.EMPLOY_STATUS_CHANGE";
    public static final String BROAD_NOTICE = "com.easymi.v5driver.BROAD_NOTICE";
    public static final String BROAD_ANN = "com.easymi.v5driver.BROAD_ANN";
    public static final String BROAD_FINISH_ORDER = "com.easymi.v5driver.BROAD_FINISH_ORDER";
    public static final String TIRED_NOTICE = "com.easymi.v5driver.TIRED_NOTICE";
    public static final String HTTP_CUSTOM = "com.easymi.v5driver.HTTP_CUSTOM";
    public static final String ORDER_REFRESH = "com.easymi.v5driver.ORDER_REFRESH";

//    public static final int FREE_LOC_TIME = 5000;//闲时定位时间 毫秒
//    public static final int BUSY_LOC_TIME = 5000;//忙时定位时间 毫秒
    public static final int NORMAL_LOC_TIME = 5000;//固定定位时间 毫秒
    //是否纠偏
    public static final boolean NEED_TRACE = false;
    //是否保存位置信息
    public static final boolean SAVE_LOGO = false;

    public static final String MI_APPID = "2882303761517793325";
    public static final String MI_APPKEY = "5891779394325";

    public static final String SP_TOKEN = "sp_token";

    public static final String AES_PASSWORD = "aes_password";

    public static final String ONLINE_TIME = "online_time";

    public static final String DOWN_TIME = "down_time";


    /**
     * 注册开通业务配置
     */
    //开通专车业务
    public static final boolean KT_ZHUANCHE = true;
    //开通专线业务
    public static final boolean KT_ZHUANXIAN = false;
    //开通出租车业务
    public static final boolean KT_CHUZUCHE = false;
    //开通包车业务
    public static final boolean KT_BAOCHE = false;
    //开通租车业务
    public static final boolean KT_ZUCHE = false;
    //开通班车业务
    public static final boolean KT_BANCHE = false;
    //开通公务车业务
    public static final boolean KT_GONGWU = false;
}
