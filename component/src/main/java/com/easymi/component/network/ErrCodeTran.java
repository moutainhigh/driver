package com.easymi.component.network;

/**
 * Created by liuzihao on 2018/2/8.
 */

public enum ErrCodeTran {


    SUCCESS(1, "成功"),
    /**
     * 請求參數無效
     */
    INVALID_PARAMETER_CODE(20000, "請求參數無效"),
    /**
     * appkey無效
     */
    INVALID_APPKEY_CODE(20001, "appkey無效"),
    /**
     * 公司id無效
     */
    INVALID_COMPANY_CODE(20003, "公司id無效"),
    /**
     * 電話無效
     */
    INVALID_PHONE_CODE(20004, "電話無效"),
    /**
     * 國家編碼無效
     */
    INVALID_COUNTRY_CODE(20005, "國家編碼無效"),
    /**
     * 無效id
     */
    INVALID_ID_CODE(20006, "無效id"),

    /**
     * 無效等級名稱
     */
    INVALID_GRADE_NAME_CODE(20007, "無效等級名稱"),
    /**
     * 無效並行訂單數量
     */
    INVALID_PARALLEL_NUM_CODE(20008, "無效並行訂單數量"),
    /**
     * 無效單次下單數量
     */
    INVALID_SINGLE_NUM_CODE(20009, "無效單次下單數量"),
    /**
     * 無效等級大小
     */
    INVALID_GRADE_SORT_CODE(20010, "無效等級大小"),
    /**
     * 無效pid
     */
    INVALID_PID_CODE(20011, "無效pid"),
    /**
     * 無效部門名稱
     */
    INVALID_DEPARTMENT_CODE(20012, "無效部門名稱"),
    /**
     * 無效客戶id
     */
    INVALID_PASSENGER_CODE(20013, "無效客戶id"),
    /**
     * 無效經度
     */
    INVALID_LNG_CODE(20014, "無效經度"),
    /**
     * 無效緯度
     */
    INVALID_LAT_CODE(20015, "無效緯度"),
    /**
     * 無效地址
     */
    INVALID_ADDRESS_CODE(20016, "無效地址"),
    /**
     * 無效地址類
     */
    INVALID_ADDRESS_TYPE_CODE(20017, "無效地址類"),
    /**
     * 無效應用id
     */
    INVALID_APP_ID_CODE(20018, "無效應用id"),
    /**
     * 無效應用名稱
     */
    INVALID_APP_NAME_CODE(20019, "無效應用名稱"),
    /**
     * 無效創建人
     */
    INVALID_CREATOR_CODE(20020, "無效創建人"),
    /**
     * 無效修改者
     */
    INVALID_MODIFIER_CODE(20023, "無效修改者"),
    /**
     * 無效集群名稱
     */
    INVALID_CLUSTER_CODE(20024, "無效集群名稱"),
    /**
     * 無效操作權限
     */
    INVALID_AUTHORITY_CODE(20025, "無效操作權限"),
    /**
     * 無效環境名稱
     */
    INVALID_ENV_CODE(20026, "無效環境名稱"),
    /**
     * 無效命名空間
     */
    INVALID_NAMESPACE_CODE(20027, "無效命名空間"),
    /**
     * 無效key
     */
    INVALID_KEY_CODE(20028, "無效key"),
    /**
     * 無效value
     */
    INVALID_VALUE_CODE(20029, "無效value"),

    /**
     * 用戶鎖定
     */
    ADMIN_LOCKED(20030, "用戶鎖定"),
    /**
     * 用戶凍結
     */
    ADMIN_FREEZED(20031, "用戶凍結"),
    /**
     * 數據不匹配
     */
    NOT_MATCH(20032, "數據已更新"),
    /**
     * 用戶id無效
     */
    INVALID_USERID_CODE(20033, "用戶id無效"),
    /**
     * 角色已被使用，不能刪除
     */
    USED_ROLE_CODE(20034, "角色已被使用，不能刪除"),
    /**
     * 參數對象不能為空
     */
    INVALID_OBJECT_CODE(20035, "參數對象不能為空"),
    /**
     * 公司名稱不能為空
     */
    INVALID_COMPANYNAME_CODE(20036, "公司名稱不能為空"),
    /**
     * 公司簡稱不能為空
     */
    INVALID_COMPANYSHORTNAME_CODE(20037, "公司簡稱不能為空"),
    /**
     * 公司所屬國家不能為空
     */
    INVALID_COMPANY_COUNTRY_CODE(20038, "公司所屬國家不能為空"),
    /**
     * 公司所屬省份不能為空
     */
    INVALID_COMPANY_PROVINCE_CODE(20039, "公司所屬省份不能為空"),
    /**
     * 公司所屬城市不能為空
     */
    INVALID_COMPANY_CITY_CODE(20040, "公司所屬城市不能為空"),
    /**
     * 角色名稱不能為空
     */
    INVALID_ROLENMAE_CODE(20041, "角色名稱不能為空"),
    /**
     * 是否是內置角色不能為空
     */
    INVALID_ISINTERNAL_CODE(20042, "是否是內置角色不能為空"),
    /**
     * 用戶id無效
     */
    INVALID_USERNAME_CODE(20043, "用戶id無效"),
    /**
     * 無效的citycode
     */
    INVALID_CITY_CODE(20044, "無效的citycode"),
    /**
     * 無效的adcode
     */
    INVALID_AD_CODE(20045, "無效的adcode"),
    /**
     * 密碼錯誤
     * /*
     */
    PASSWORD_ERROR_CODE(20046, "密碼錯誤"),

    //無效的短信驗證碼
    INVALID_PHONE_CODE_ERROR(20047, "無效的短信驗證碼"),


    /**
     * 密碼錯誤
     * /*
     */
    PASSWORD_CODE(20048, "密碼錯誤"),

    /**
     * 服務人員凍結
     */
    EMPLOY_FREEZED(20049, "服務人員凍結"),

    /**
     * 操作失敗
     */
    FAIL(20050, "操作失敗"),
    /**
     * 密碼錯誤
     */
    PASSWORD(20051, "密碼錯誤"),

    /**
     * 無效類型
     */
    INVALID_TYPE_CODE(20052, "無效類型"),
    /**
     * 無效客戶類型
     */
    INVALID_PASSENGER_TYPE_CODE(20053, "無效客戶類型"),
    /**
     * 無效的開始時間
     */
    INVALID_BEGIN_CODE(20054, "無效的開始時間"),
    /**
     * 無效的車牌號
     */
    INVALID_PLATE_NUMBER(20055, "無效的車牌號"),
    /**
     * 無效優惠券編碼
     */
    INVALID_COUPON_CODE(20056, "無效優惠券編碼"),

    /**
     * 無效優惠券類型
     */
    INVALID_COUPON_TYPE(20057, "無效優惠券類型"),
    /**
     * 無效優惠券折扣抵扣
     */
    INVALID_COUPON_DISCOUNT(20058, "無效優惠券折扣抵扣"),
    /**
     * 無效的標題
     */
    INVALID_TITLE_CODE(20059, "無效的標題"),
    /**
     * 無效的內容
     */
    INVALID_CONTENT_CODE(20060, "無效的內容"),
    /**
     * 無效的排序
     */
    INVALID_SORT_CODE(20061, "無效的排序"),
    /**
     * 無效開始時間
     */
    INVALID_BEGIN_TIME(20062, "無效開始時間"),
    /**
     * 無效活動中心圖片
     */
    INVALID_CENTER_IMAGE(20063, "無效活動中心圖片"),
    /**
     * 無效服務類型
     */
    INVALID_SERVICE_TYPE(20064, "無效服務類型"),
    /**
     * 無效活動獎勵類型
     */
    INVALID_REWARD(20065, "無效活動獎勵類型"),
    /**
     * 無效活動類型
     */
    INVALID_ACTIVE(20066, "無效活動類型"),
    /**
     * 無效訂單類型
     */
    INVALID_ORDER_TYPE(20067, "無效訂單類型"),
    /**
     * 無效的分享平台
     */
    INVALID_PLATFORM(20068, "無效的分享平台"),
    /**
     * 無效的發放時間
     */
    INVALID_RELEASE(20069, "無效的發放時間"),
    /**
     * 無效的獎勵等級
     */
    INVALID_REWARD_GRADE(20070, "無效的獎勵等級"),
    /**
     * 無效執行時間
     */
    INVALID_EXE_TIME(20071, "無效執行時間"),
    /**
     * 無效模板
     */
    INVALID_TEMPLATE(20072, "無效模板"),
    /**
     * 無效語種
     */
    INVALID_LANGUAGE(20073, "無效語種"),
    /**
     * 無效簽名
     */
    INVALID_SIGN(20074, "無效簽名"),

    //數據已使用
    DATA_IS_USED(20075, "數據已使用"),

    /**
     * 含有屏蔽詞
     */
    SHIELDING_WORDS(20076, "含有屏蔽詞"),

    /**
     * 創建支付失敗
     */
    CREATE_PAY_ERROR(20077, "創建支付失敗"),

    /**
     * 支付通知失敗
     */
    PAY_NOTIFY_ERROR(20078, "支付通知失敗"),


    /**
     * 無效的推薦人
     */
    INVALID_REFEREE_CODE(20079, "無效的推薦人"),
    /**
     * 無效推薦人類型
     */
    INVALID_REFEREE_TYPE(20080, "無效推薦人類型"),
    /**
     * 無效的方式
     */
    INVALID_WAY_CODE(20081, "無效的方式"),
    /**
     * 優惠券已失效
     */
    INVALID_COUPON(20082, "優惠券已失效"),
    /**
     * 無效註冊渠道
     */
    INVALID_CHANNEL(20083, "無效註冊渠道"),
    /**
     * 無效訂單數
     */
    INVALID_ORDER(20084, "無效訂單數"),
    /**
     * 無效訂單數
     */
    INVALID_FREQUENCY(20085, "無效訂單數"),
    /**
     * 無效客戶等級
     */
    INVALID_GRADE(20086, "無效客戶等級"),

    /**
     * 當前客戶不能簽單
     */
    PASSENGER_NOT_SIGN(20087, "當前客戶不能簽單"),

    /**
     * 當前客戶餘額不足
     */
    PASSENGER_BALANCE(20088, "當前客戶餘額不足"),

    /**
     * 當前司機餘額不足
     */
    EMPLOY_BALANCE(20089, "當前司機餘額不足"),

    /**
     * 代付未開啟
     */
    EMPLOY_HELPLY_NOT_OPEN(20090, "代付未開啟"),

    /**
     * 執行中訂單已達上線
     */
    TOO_MANY_RUNING_ORDERS_ERROR(20091, "執行中訂單已達上線"),
    /**
     * 未找到收費標準
     */
    CHARGE_STANDARD_IS_NOT_EXIST(20092, "未找到收費標準"),

    /**
     * 未知錯誤
     */
    UNKNOW_ERROR(-1, "您的網絡出小差了"),

    /**
     * 緩存添加失敗
     */
    ORDER_CACHE_SAVE_ERROR(20093, "緩存添加失敗"),
    /**
     * 無效贈送金額
     */
    CHILD_TABLE_DATA_EXISTS(20094, "無效贈送金額"),
    /**
     * 時間重疊
     */
    TIME_OVERLAP(20095, "時間重疊"),
    /**
     * 公里數重疊
     */
    MILEAGE_OVERLAP(20096, "公里數重疊"),

    //提成標準已被使用
    ROYALTY_IS_USED_CODE(20097, "提成標準已被使用"),

    //可提現金額不足
    DRIVER_BALANCE_NOT_ENOUGH(20098, "可提現金額不足"),

    /**
     * 無效優先級
     */
    INVALID_PRIORITY(20099, "無效優先級"),
    /**
     * 無效司機類型
     */
    INVALID_EMPLOY_TYPE(30001, "無效司機類型"),
    /**
     * 無效距離
     */
    INVALID_DISTANCE(30002, "無效距離"),
    /**
     * 無效金額
     */
    INVALID_MONEY(30003, "無效金額"),
    /**
     * 無效電話模式
     */
    INVALID_PHONE_MODE(30004, "無效電話模式"),
    /**
     * 無效設備類型
     */
    INVALID_DEVICE_MODE(30005, "無效設備類型"),
    /**
     * 無效更新模式
     */
    INVALID_UPDATE_MODE(30006, "無效更新模式"),

    /**
     * 無效活動狀態
     */
    INVALID_ACTIVE_STATE(30007, "無效活動狀態"),
    /**
     * 無效條件
     */
    INVALID_CONDITION(30008, "無效條件"),

    /**
     * 修改失敗
     */
    MODIFY_ERROR(30009, "修改失敗"),
    /**
     * 刪除失敗
     */
    DELETE_ERROR(30010, "刪除失敗"),
    /**
     * 查詢失敗
     */
    QUERY_ERROR(30011, "查詢失敗"),
    /**
     * 臟數據
     */
    VERSION_ERROR(30012, "臟數據"),
    /**
     * 電話號碼已存在
     */
    PHONE_EXISTS(30013, "電話號碼已存在"),
    /**
     * 已是黑名單客戶
     */
    BLACKLIST_ALREADY(30014, "已是黑名單客戶"),
    /**
     * 不是黑名單客戶
     */
    BLACKLIST_NOT_IN(30015, "不是黑名單客戶"),
    /**
     * 乘客不存在
     */
    PASSENGER_NOT_EXIST(30016, "乘客不存在"),
    /**
     * 收藏地址不能超過8個
     */
    ADDRESS_LIMIT(30017, "收藏地址不能超過8個"),
    /**
     * 收藏地址不存在
     */
    ADDRESS_NOT_EXIST(30018, "收藏地址不存在"),
    /**
     * 部門已存在
     */
    DEPARTMENT_EXIST(30019, "部門已存在"),
    /**
     * 部門不存在
     */
    DEPARTMENT_NOT_EXIST(30020, "部門不存在"),
    /**
     * 下屬存在客戶
     */
    PASSENGER_UNDER(30021, "下屬存在客戶"),
    /**
     * 下屬存在部門
     */
    DEPARTMENT_UNDER(30022, "下屬存在部門"),
    /**
     * 等級已存在
     */
    GRADE_EXIST(30023, "等級已存在"),
    /**
     * 等級不存在
     */
    GRADE_NOT_EXIST(30024, "等級不存在"),
    /**
     * 初始化等級失敗
     */
    INIT_GRADE_ERROR(30025, "初始化等級失敗"),
    /**
     * 應用存在
     */
    APP_EXIST(30026, "應用存在"),
    /**
     * 應用不存在
     */
    APP_NOT_EXIST(30027, "應用不存在"),
    /**
     * 存在下屬集群
     */
    CLUSTER_UNTER(30028, "存在下屬集群"),
    /**
     * 集群已經存在
     */
    CLUSTER_EXIST(30029, "集群已經存在"),
    /**
     * 集群不存在
     */
    CLUSTER_NOT_EXIST(30030, "集群不存在"),
    /**
     * 存在下屬命名空間
     */
    NAMESPACE_UNDER(30031, "存在下屬命名空間"),
    /**
     * 沒有無權限操作
     */
    NO_POWER_OPERATE(30032, "沒有無權限操作"),
    /**
     * 命名空間不存在
     */
    NAMESPACE_NOT_EXIST(30033, "命名空間不存在"),
    /**
     * 下屬存在實例
     */
    ITEM_UNDER(30034, "下屬存在實例"),
    /**
     * 發布失敗
     */
    RELEASE_ERROR(30035, "發布失敗"),
    /**
     * 命名空間沒有實例
     */
    WITHOUT_ITEM(30036, "命名空間沒有實例"),
    /**
     * 回滾失敗
     */
    ROLLBACK_ERROR(30037, "回滾失敗"),
    /**
     * 實例不存在
     */
    ITEM_NOT_EXIST(30038, "實例不存在"),
    /**
     * 實例已存在
     */
    ITEM_EXIST(30039, "實例已存在"),
    /**
     * 命名空間存在
     */
    NAMESPACE_EXIST(30040, "命名空間存在"),

    /**
     * RSA加密失敗
     */
    RSA_ENCRYPT_ERROR(30041, "RSA加密失敗"),
    /**
     * RSA解密失敗
     */
    RSA_DECRYPT_ERROR(30042, "RSA解密失敗"),
    /**
     * 數據已存在
     */
    DATA_EXISTS(30043, "數據已存在"),
    /**
     * 數據不存在
     */
    DATA_NOT_EXIST(30044, "數據不存在"),
    /**
     * 數據添加出錯
     */
    INSERT_ERROR(30045, "數據添加出錯"),

    //新密碼在最近5次修改密碼中已使用
    PASSWORD_IS_USED(30046, "新密碼在最近5次修改密碼中已使用"),

    /**
     * 無效充值金額
     */
    INVALID_RECHARGE_CODE(30047, "無效充值金額"),
    /**
     * 無效贈送金額
     */
    INVALID_LARGESS_CODE(30048, "無效贈送金額"),
    /**
     * 該服務人員已綁定其他設備
     */
    EMPLOY_DEVICE_ERROR(30049, "該服務人員已綁定其他設備"),
    /**
     * 該服務人員已存在執行中的訂單，不能再次接單
     */
    DRIVER_GOTO_PRE_ORDER_CODE(30050, "該服務人員已存在執行中的訂單，不能再次接單"),
    /**
     * 差一點就搶到了
     */
    GRAB_ORDER_ERROR(30051, "差一點就搶到了"),
    /**
     * 該服務人員已存在執行中的訂單，不能再次補單
     */
    DRIVER_HAVE_ORDER_CODE(30052, "該服務人員已存在執行中的訂單，不能再次補單"),
    /**
     * 服務人員不存在
     */
    EMPLOY_NOT_EXIST(31001, "服務人員不存在"),
    /**
     * 服務人員還有訂單未完成
     */
    EMPLOY_HAS_ORDERS_TODO(31004, "服務人員還有訂單未完成"),

    /**
     * 车辆正在使用.
     */
    VEHICLE_IS_BUSY(31054, "车辆正在使用"),

    NO_VEHICLE_CANNOT_ONLINE(31041, "您还未绑定车辆，请联系管理员"),

    CANNOT_DOING_MORE_ORDERS(31042, "不能同时执行多个订单"),

    PHONE_TIME_ERROR(50018, "手机时间与服务器时间相差过大,请调整手机时间");

    private int code;
    private String showMsg;

    ErrCodeTran(int code, String showMsg) {
        this.code = code;
        this.showMsg = showMsg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getShowMsg() {
        return showMsg;
    }

    public void setShowMsg(String showMsg) {
        this.showMsg = showMsg;
    }
}
