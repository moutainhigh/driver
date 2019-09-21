package com.easymin.driver.securitycenter;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: Config
 *@Author: shine
 * Date: 2018/11/28 下午10:43
 * Description:
 * History:
 */
public class CenterConfig {

    /**
     * 主机地址
     */
//    public static String HOST = "http://api.xiaokakj.com/";//线上测试
    public static String HOST = "http://v6api.rvaka.cn/";//线上测试
    public static String H5_HOST = "http://h5.xiaokakj.com/";//线上测试
    public static String IMG_SERVER = "http://assets.xiaokakj.com/";
    /**
     * aes加密key
     */
    public static final String AES_PASSWORD = "aes_password";

    public static final String SHARED_PREFERENCES_NAME  = "em"; //SharedPreferences 文件名

    /**
     * aes加密key
     */
    public static  String AES_KEY = "aes_key";
    /**
     * token
     */
    public static  String TOKEN = "token";
    /**
     * Appkey
     */
    public static  String APPKEY = "appkey";
    /**
     * PASSENGERID
     */
    public static  long PASSENGERID = 0;
    /**
     * PASSENGERPHONE
     */
    public static  String PASSENGERPHONE = "";
    /**
     * ORDERID
     */
    public static  long ORDERID = 0;
    /**
     * PASSENGERID
     */
    public static  String DRIVER = "";
//
//    /**
//     * 录音授权 0未授权
//     */
//    public static int soundRecordCheck;
//    /**
//     * 紧急联系人设置 0未设置
//     */
//    public static int emergeContackCheck;

    /**
     * 七牛云上传地址
     */
    public static final String QINIU_HOST = "http://up-z2.qiniu.com";

    /**
     * 七牛云上传地址
     */
    public static  String QINIU_TOKEN = "";
}
