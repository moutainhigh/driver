package com.easymi.component.network;

/**
 * Created by liuzihao on 2018/2/8.
 */

public enum ErrCodeTran {


    SUCCESS(1, "成功"),
    /**
     * 驗證格式出錯
     */
    FORMAT_ERR(10000, "驗證格式出錯"),

    /**
     * 驗證碼不能為空
     */
    CODE_IS_NULL_ERR(11000, "驗證碼不能為空"),
    /**
     * 通用-用戶名格式錯誤
     */
    USERNAME_FORMAT_ERR(10001, "用戶名格式錯誤"),
    /**
     * 通用-密碼格式錯誤
     */
    PASSWORD_FORMAT_ERR(10002, "密碼格式錯誤"),
    /**
     * 通用-驗證碼格式錯誤
     */
    CODE_FORMAT_ERR(10003, "驗證碼格式錯誤"),
    /**
     * 通用-參數不能為空
     */
    PARAMETER_NULL_FORMAT_ERR(10004, "參數不能為空"),
    /**
     * 通用-參數格式錯誤
     */
    PARAMETER_FORMAT_ERR(10005, "參數格式錯誤"),
    /**
     * 通用-參數id為空
     */
    ID_NULL_FORMAT_ERR(10006, "參數id為空"),
    /**
     * 通用-appKey為空
     */
    APPKEY_NULL_FORMAT_ERR(10007, "appKey為空"),
    /**
     * 通用-驗證碼Id格式錯誤
     */
    CODE_ID_FORMAT_ERR(10008, "驗證碼Id格式錯誤"),

    /**
     * 通用-包含不安全的字符
     */
    CONTAINS_NOT_SAFE_CHARACTER(10009, "包含不安全的字符"),
    /**
     * 通用-公司id為空
     */
    COMPANY_ID_ISNULL_CHARACTER(10010, "公司id為空"),
    /**
     * 通用-page為空
     */
    PAGE_NULL_FORMAT_ERR(10011, "page為空"),
    /**
     * 通用-size為空
     */
    SIZE_NULL_FORMAT_ERR(100012, "size為空"),
    /**
     * 數據重復
     */
    DATA_REPEATED(100013, "數據重復"),
    /**
     * 數據不存在
     */
    DATA_NOT_EXIST(100014, "數據不存在"),

    /**
     * 添加錯誤
     */
    INSERT_VALIDATE_ERR(40001, "添加錯誤"),
    /**
     * 更新錯誤
     */
    UPDATE_VALIDATE_ERR(40002, "更新錯誤"),
    /**
     * 刪除錯誤
     */
    DELETE_VALIDATE_ERR(40003, "刪除錯誤"),
    /**
     * 查詢錯誤
     */
    QUERY_VALIDATE_ERR(40004, "未查到相關信息哦"),

    /**
     * 數據不存在
     */
    QUERY_VALIDATE_NULL(40005, "數據不存在"),

    /**
     * 用戶名或者密碼錯誤
     */
    USERNAME_OR_PASSWORD_ERR(40006, "用戶名或者密碼錯誤"),
    /**
     * 用戶名錯誤
     */
    USERNAME_ERR(41000, "用戶名錯誤"),

    /**
     * 該用戶被加入黑名單
     */
    USERNAME_BLACK_ERR(41001, "該用戶被加入黑名單"),

    /**
     * 圖形驗證碼錯誤
     */
    CODE_VALIDATE_ERR(40001, "圖形驗證碼錯誤"),

    /**
     * 密碼不相同
     */
    RESET_PASSWOED_ERROR_REGISTERED(40028, "密碼不相同"),

    /**
     * token已過期
     */
    TOKEN_EXPIRE_REGISTERED(40026, "token已過期"),

    /**
     * 發送短信驗證碼錯誤
     */
    SMS_SEND_CODE_ERR(40038, "發送短信驗證碼錯誤"),

    /**
     * 發送短信驗證碼重復發送錯誤
     */
    SMS_SEND_REPEAT_CODE_ERR(40039, "發送短信驗證碼重復發送錯誤"),
    /**
     * 發送短信驗證碼type類型錯誤
     */
    SMS_TYPE_VALIDATE_ERR(40039, "發送短信驗證碼type類型錯誤"),

    /**
     * 導出失敗
     */
    EXPORT_REPEAT_ERR(40042, "導出失敗"),

    /**
     * 司機沒有開通專車業務
     */
    DRIVER_NOT_SPECIAL(40043, "司機沒有開通專車業務"),

    /**
     * 司機與車輛不要重復綁定
     */
    DRIVER_BINDING_REPEATED(40044, "司機與車輛不要重復綁定"),


    /**
     * 捕獲到系統異常，未知錯誤
     */
    UNKNOWN_ERR(80001, "系統異常"),
    /**
     * 線路不存在
     */
    LINE_NOT_EXIST(40045, "線路不存在"),
    /**
     * 線路ID不能為空
     */
    LINE_ID_NOT_NULL(40046, "線路ID不能為空"),
    /**
     * 訂單狀態錯誤
     */
    ORDER_STATUS_ERR(40047, "訂單狀態錯誤"),
    /**
     * 訂單不存在
     */
    ORDER_IS_NULL_ERR(40048, "訂單不存在"),

    /**
     * 司機不存在
     */
    DRIVER_NOT_EXIST(40049, "司機不存在"),
    /**
     * 司機余額操作失敗
     */
    DRIVER_BALANCE_ERROR(40050, "司機余額操作失敗"),

    /**
     * 支付類型錯誤
     */
    PAY_TYPE_ERROR(40051, "支付類型錯誤"),

    /**
     * 終端ip錯誤
     */
    TERMINAL_IP_ERR(40052, "終端ip錯誤"),

    /**
     * 加密失敗
     */
    ENCODE_ERR(40053, "加密失敗"),

    /**
     * 請求失敗
     */
    REQUEST_ERR(40054, "請求失敗"),

    /**
     * 請求超時
     */
    READ_TIMEOUT_ERR(40055, "請求超時"),

    /**
     * 驗簽失敗
     */
    ENCRYPTING_ERR(40056, "驗簽失敗"),

    /**
     * 轉換失敗
     */
    CHANGE_ERR(40057, "轉換失敗"),

    /**
     * 支付操作人錯誤
     */
    PAY_OPERATOR_ERROR(40058, "支付操作人錯誤"),

    /**
     * 訂單創建失敗
     */
    ORDER_CREATE_ERROR(40059, "訂單創建失敗"),
    /**
     * 訂單狀態錯誤
     */
    ORDER_STATUS_ERROR(40060, "訂單狀態錯誤"),
    /**
     * 訂單派單失敗
     */
    ORDER_ASSIGN_ERROR(40061, "訂單派單失敗"),
    /**
     * 訂單搶單失敗
     */
    ORDER_GRAB_ERROR(40062, "訂單搶單失敗"),
    /**
     * 訂單接單失敗
     */
    ORDER_TAKE_ERROR(40063, "訂單接單失敗"),
    /**
     * 訂單到達預約地失敗
     */
    ORDER_GO_TO_BOOK_PLACE_ERROR(40064, "訂單到達預約地失敗"),
    /**
     * 訂單拒單失敗
     */
    ORDER_REFUSE_ERROR(40065, "訂單拒單失敗"),
    /**
     * 訂單銷單失敗
     */
    ORDER_CANCEL_ERROR(40066, "訂單銷單失敗"),

    /**
     * 支付狀態錯誤
     */
    PAY_STATUS_ERR(40067, "支付狀態錯誤"),
    /**
     * 站點被線路使用
     */
    STATION_IS_USED(40068, "站點被線路使用"),

    /**
     * 車牌號已存在
     */
    VEHICLE_NUMBER_EXISTS(40069, "車牌號已存在"),
    /**
     * 站點選擇錯誤
     */
    STATION_CHOOSE_ERROR(40070, "站點選擇錯誤"),
    /**
     * 客戶余額不足,不允許下單
     */
    PASSENGER_BALANCE_DEFICIENCY(40071, "余額不足,不允許下單"),
    /**
     * 客戶不存在
     */
    PASSENGER_NOT_EXIST(40072, "客戶不存在"),
    /**
     * 班次不存在
     */
    SCHEDULE_NOT_EXIST(40073, "班次不存在"),
    /**
     * 班次票數不足
     */
    SCHEDULE_TICKET_DEFICIENCY(40074, "班次票數不足"),

    /**
     * 當前司機未凍結
     */
    DRIVER_UN_FROZEN(40075, "當前司機未凍結"),
    /**
     * 當前司機已被凍結
     */
    DRIVER_FROZEN(40076, "當前司機已被凍結"),
    /**
     * 車輛不存在
     */
    CAR_NOT_EXIST(40077, "車輛不存在"),
    /**
     * 公司不存在
     */
    COMPANY_NOT_EXIST(40078, "公司不存在"),
    /**
     * 支付金額錯誤
     */
    FEE_ERROR(40079, "支付金額錯誤"),
    /**
     * 該車輛已經是該線路的常用車輛不能重復添加
     */
    LINE_VEHICLE_EXIST(40080, "該車輛已經是該線路的常用車輛不能重復添加"),

    /**
     * 申請提現失敗
     */
    DRIVER_PUT_FORWARD_ERR(40082, "申請提現失敗"),

    /**
     * 提現申請不存在
     */
    PUT_FORWARD_NOT_EXIST(40083, "提現申請不存在"),

    /**
     * 提現申請已經處理
     */
    PUT_FORWARD_HAS_DEAL(40084, "提現申請已經處理"),

    /**
     * 訂單報銷記錄已經存在
     */
    ORDER_REIMBURSE_HAS_EXIST(40085, "訂單報銷記錄已經存在"),

    /**
     * 訂單報銷不存在
     */
    ORDER_REIMBURSE_NOT_EXIST(40086, "訂單報銷不存在"),

    /**
     * 訂單報銷已經處理
     */
    ORDER_REIMBURSE_HAS_DEAL(40087, "訂單報銷已經處理"),
    /**
     * 司機賬戶余額不足
     */
    DRIVER_BALANCE_NOT_ENOUGH(40088, "司機賬戶余額不足"),
    /**
     * 專線訂單跳過執行失敗
     */
    ORDER_SKIP_ERROR(40089, "專線訂單跳過執行失敗"),
    /**
     * 專線訂單出發失敗
     */
    ORDER_RUN_ERROR(40090, "專線訂單出發失敗"),
    /**
     * 專線訂單支付失敗
     */
    ORDER_PAY_ERROR(40091, "專線訂單支付失敗"),
    /**
     * 終止任務失敗
     */
    STOP_TASK_ERROR(40092, "終止任務失敗"),
    /**
     * 訂單已經申請過發票
     */
    ORDER_HAS_INVOICE(40093, "訂單已經申請過發票"),
    /**
     * 申請發票失敗
     */
    INVOICE_ERR(40094, "申請發票失敗"),
    /**
     * 訂單完成失敗
     */
    ORDER_FINISH_ERROR(40095, "訂單完成失敗"),
    /**
     * 發票申請已經處理
     */
    INVOICE_HAS_DEAL(40096, "發票申請已經處理"),
    /**
     * 發票申請不存在
     */
    INVOICE_NOT_EXIST(40097, "發票申請不存在"),
    /**
     * 臟數據
     */
    VERSION_ERROR(40098, "問題數據"),


    /**
     * 分布式鎖，加鎖失敗
     */
    LOCK_ERROR(40099, "分布式鎖，加鎖失敗"),

    /**
     * 訂單到達預約地失敗
     */
    ORDER_ARRIVE_BOOK_PLACE_ERROR(40100, "訂單到達預約地失敗"),

    /**
     * 訂單前往目的地失敗
     */
    ORDER_GOTO_DESTINATION_ERROR(40101, "訂單前往目的地失敗"),

    /**
     * 訂單到達目的地失敗
     */
    ORDER_ARRIVE_DESTINATION_ERROR(40102, "訂單到達目的地失敗"),

    /**
     * 主賬號不允許修改
     */
    ADMIN_IS_MAIN_UPDATE_ERR(40103, "主賬號不允許修改"),
    /**
     * 司機已經存在,不能重復添加
     */
    DRIVER_HAD_EXIST(40104, "司機已經存在,不能重復添加"),

    /**
     * 賬號重復
     */
    ACCOUNT_REPEAT_ERR(40105, "賬號重復"),

    /**
     * 手機號重復
     */
    PHONE_REPEAT_ERR(40106, "手機號重復"),

    /**
     * 同壹區域不能存在多個服務機構
     */
    SERVER_REPEAT_ERR(40107, "同壹區域不能存在多個服務機構"),

    /**
     * 司機密碼錯誤
     */
    DRIVER_PASSWORD_ERROR(40108, "司機密碼錯誤"),
    /**
     * 開通服務機構已達到上限
     */
    COMPANY_NUM_MAX_ERROR(40109, "開通服務機構已達到上限"),

    /**
     * 優惠券已使用或已失效
     */
    COUPON_STATUS_ERROR(40110, "優惠券已使用或已失效"),

    /**
     * 優惠券業務類型錯誤
     */
    COUPON_SERVICE_TYPE_ERROR(40111, "優惠券業務類型錯誤"),

    /**
     * 優惠券類型錯誤
     */
    COUPON_TYPE_ERROR(40112, "優惠券類型錯誤"),
    /**
     * 班次已經存在
     */
    SCHEDULE_IS_EXIST(40113, "班次已經存在"),

    /**
     * 不是超級管理員 沒有權限
     */
    ADMIN_IS_NOT_MAIN_ERR(40114, "不是超級管理員 沒有權限"),

    /**
     * 自動派單自動搶單不能同時打開
     */
    GRAB_DISTRIBUTE_ORDER(40115, "自動派單自動搶單不能同時打開"),

    /**
     * 車型名稱已經存在
     */
    VEHICLE_MODELS_HAS_EXIST(40116, "車型名稱已經存在"),

    /**
     * 開通業務名稱已經存在
     */
    BUSINESS_TYPE_HAS_EXIST(40117, "開通業務名稱已經存在"),

    /**
     * 提成名稱已經存在
     */
    ROYALTY_RATIO_HAS_EXIST(40118, "提成名稱已經存在"),
    /**
     * 收費標準名稱已經存在
     */
    CHARGING_HAS_EXIST(40119, "收費標準名稱已經存在"),
    /**
     * 客戶電話已經存在
     */
    PASSENGER_PHONE_HAS_EXIST(40120, "客戶電話已經存在"),

    /**
     * 客戶身份證已經存在
     */
    PASSENGER_ID_CARD_HAS_EXIST(40121, "客戶身份證已經存在"),
    /**
     * 當前車型正在使用中
     */
    VEHICLE_MODELS_IN_USE(40122, "當前車型正在使用中"),
    /**
     * 當前類型正在使用中
     */
    BUSINESS_TYPE_IN_USE(40123, "當前類型正在使用中"),
    /**
     * 超級權限不能變更
     */
    ADMIN_UPDATE_MAIN_ERR(40124, "超級權限不能變更"),
    /**
     * 司機重復綁定車輛
     */
    DRIVER_BIND_CAR_REPEATED(40125, "司機重復綁定車輛"),
    /**
     * 司機沒有綁定車輛
     */
    DRIVER_NOT_BIND_CAR(40127, "司機沒有綁定車輛"),

    /**
     * 司機有正在執行的訂單
     */
    DRIVER_HAS_RUNNING_ORDER(40128, "司機有正在執行的訂單"),

    /**
     * 司機已在線上
     */
    DRIVER_LOGIN_REPEATED(40129, "司機已在線上"),

    /**
     * 提示該角色正在使用，不能刪除
     */
    ROLE_OCCUPATION_ERR(40130, "提示該角色正在使用，不能刪除"),

    /**
     * 提示該角色正在使用，不能刪除
     */
    IS_COMMON_VEHICLE_ERR(40133, "同車司機正在線上不能登錄"),

    /**
     * 還有未完成的訂單
     */
    ORDER_NOT_FINISH(40138, "還有未完成的訂單"),

    /**
     * 訂單已被收回
     */
    ORDER_CANCEL(40139, "訂單已被收回"),

    /**
     * 乘客賬號異常，請聯系管理員處理
     */
    PASSENGER_IN_BLACK_LIST(40181, "乘客賬號異常，請聯系管理員處理"),

//old
    /**
     * 數據不匹配
     */
    NOT_MATCH(20032, "數據已更新"),
    /**
     * 查詢失敗
     */
    QUERY_ERROR(30011, "查詢失敗"),
    /**
     * 該服務人員已存在執行中的訂單，不能再次接單
     */
    DRIVER_GOTO_PRE_ORDER_CODE(30050, "您已存在執行中的訂單，不能再次接單"),
    /**
     * 差壹點就搶到了
     */
    GRAB_ORDER_ERROR(30051, "差壹點就搶到了"),
    /**
     * 服務人員不存在
     */
    EMPLOY_NOT_EXIST(31001, "服務人員不存在");

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
