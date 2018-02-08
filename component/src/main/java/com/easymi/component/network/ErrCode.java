package com.easymi.component.network;

/**
 * Created by liuzihao on 2018/2/8.
 */

public enum ErrCode {


    SUCCESS(1, "成功"),
    /**
     * 请求参数无效
     */
    INVALID_PARAMETER_CODE(20000, "请求参数无效"),
    /**
     * appkey无效
     */
    INVALID_APPKEY_CODE(20001, "appkey无效"),
    /**
     * 公司id无效
     */
    INVALID_COMPANY_CODE(20003, "公司id无效"),
    /**
     * 电话无效
     */
    INVALID_PHONE_CODE(20004, "电话无效"),
    /**
     * 国家编码无效
     */
    INVALID_COUNTRY_CODE(20005, "国家编码无效"),
    /**
     * 无效id
     */
    INVALID_ID_CODE(20006, "无效id"),

    /**
     * 无效等级名称
     */
    INVALID_GRADE_NAME_CODE(20007, "无效等级名称"),
    /**
     * 无效并行订单数量
     */
    INVALID_PARALLEL_NUM_CODE(20008, "无效并行订单数量"),
    /**
     * 无效单次下单数量
     */
    INVALID_SINGLE_NUM_CODE(20009, "无效单次下单数量"),
    /**
     * 无效等级大小
     */
    INVALID_GRADE_SORT_CODE(20010, "无效等级大小"),
    /**
     * 无效pid
     */
    INVALID_PID_CODE(20011, "无效pid"),
    /**
     * 无效部门名称
     */
    INVALID_DEPARTMENT_CODE(20012, "无效部门名称"),
    /**
     * 无效客户id
     */
    INVALID_PASSENGER_CODE(20013, "无效客户id"),
    /**
     * 无效经度
     */
    INVALID_LNG_CODE(20014, "无效经度"),
    /**
     * 无效纬度
     */
    INVALID_LAT_CODE(20015, "无效纬度"),
    /**
     * 无效地址
     */
    INVALID_ADDRESS_CODE(20016, "无效地址"),
    /**
     * 无效地址类
     */
    INVALID_ADDRESS_TYPE_CODE(20017, "无效地址类"),
    /**
     * 无效应用id
     */
    INVALID_APP_ID_CODE(20018, "无效应用id"),
    /**
     * 无效应用名称
     */
    INVALID_APP_NAME_CODE(20019, "无效应用名称"),
    /**
     * 无效创建人
     */
    INVALID_CREATOR_CODE(20020, "无效创建人"),
    /**
     * 无效修改者
     */
    INVALID_MODIFIER_CODE(20023, "无效修改者"),
    /**
     * 无效集群名称
     */
    INVALID_CLUSTER_CODE(20024, "无效集群名称"),
    /**
     * 无效操作权限
     */
    INVALID_AUTHORITY_CODE(20025, "无效操作权限"),
    /**
     * 无效环境名称
     */
    INVALID_ENV_CODE(20026, "无效环境名称"),
    /**
     * 无效命名空间
     */
    INVALID_NAMESPACE_CODE(20027, "无效命名空间"),
    /**
     * 无效key
     */
    INVALID_KEY_CODE(20028, "无效key"),
    /**
     * 无效value
     */
    INVALID_VALUE_CODE(20029, "无效value"),

    /**
     * 用户锁定
     */
    ADMIN_LOCKED(20030, "用户锁定"),
    /**
     * 用户冻结
     */
    ADMIN_FREEZED(20031, "用户冻结"),
    /**
     * 数据不匹配
     */
    NOT_MATCH(20032, "数据不匹配"),
    /**
     * 用户id无效
     */
    INVALID_USERID_CODE(20033, "用户id无效"),
    /**
     * 角色已被使用，不能删除
     */
    USED_ROLE_CODE(20034, "角色已被使用，不能删除"),
    /**
     * 参数对象不能为空
     */
    INVALID_OBJECT_CODE(20035, "参数对象不能为空"),
    /**
     * 公司名称不能为空
     */
    INVALID_COMPANYNAME_CODE(20036, "公司名称不能为空"),
    /**
     * 公司简称不能为空
     */
    INVALID_COMPANYSHORTNAME_CODE(20037, "公司简称不能为空"),
    /**
     * 公司所属国家不能为空
     */
    INVALID_COMPANY_COUNTRY_CODE(20038, "公司所属国家不能为空"),
    /**
     * 公司所属省份不能为空
     */
    INVALID_COMPANY_PROVINCE_CODE(20039, "公司所属省份不能为空"),
    /**
     * 公司所属城市不能为空
     */
    INVALID_COMPANY_CITY_CODE(20040, "公司所属城市不能为空"),
    /**
     * 角色名称不能为空
     */
    INVALID_ROLENMAE_CODE(20041, "角色名称不能为空"),
    /**
     * 是否是内置角色不能为空
     */
    INVALID_ISINTERNAL_CODE(20042, "是否是内置角色不能为空"),
    /**
     * 用户id无效
     */
    INVALID_USERNAME_CODE(20043, "用户id无效"),
    /**
     * 无效的citycode
     */
    INVALID_CITY_CODE(20044, "无效的citycode"),
    /**
     * 无效的adcode
     */
    INVALID_AD_CODE(20045, "无效的adcode"),
    /**
     * 密码错误
     * /*
     */
    PASSWORD_ERROR_CODE(20046, "密码错误"),

    //无效的短信验证码
    INVALID_PHONE_CODE_ERROR(20047, "无效的短信验证码"),


    /**
     * 密码错误
     * /*
     */
    PASSWORD_CODE(20048, "密码错误"),

    /**
     * 服务人员冻结
     */
    EMPLOY_FREEZED(20049, "服务人员冻结"),

    /**
     * 操作失败
     */
    FAIL(20050, "操作失败"),
    /**
     * 密码错误
     */
    PASSWORD(20051, "密码错误"),

    /**
     * 无效类型
     */
    INVALID_TYPE_CODE(20052, "无效类型"),
    /**
     * 无效客户类型
     */
    INVALID_PASSENGER_TYPE_CODE(20053, "无效客户类型"),
    /**
     * 无效的开始时间
     */
    INVALID_BEGIN_CODE(20054, "无效的开始时间"),
    /**
     * 无效的车牌号
     */
    INVALID_PLATE_NUMBER(20055, "无效的车牌号"),
    /**
     * 无效优惠券编码
     */
    INVALID_COUPON_CODE(20056, "无效优惠券编码"),

    /**
     * 无效优惠券类型
     */
    INVALID_COUPON_TYPE(20057, "无效优惠券类型"),
    /**
     * 无效优惠券折扣抵扣
     */
    INVALID_COUPON_DISCOUNT(20058, "无效优惠券折扣抵扣"),
    /**
     * 无效的标题
     */
    INVALID_TITLE_CODE(20059, "无效的标题"),
    /**
     * 无效的内容
     */
    INVALID_CONTENT_CODE(20060, "无效的内容"),
    /**
     * 无效的排序
     */
    INVALID_SORT_CODE(20061, "无效的排序"),
    /**
     * 无效开始时间
     */
    INVALID_BEGIN_TIME(20062, "无效开始时间"),
    /**
     * 无效活动中心图片
     */
    INVALID_CENTER_IMAGE(20063, "无效活动中心图片"),
    /**
     * 无效服务类型
     */
    INVALID_SERVICE_TYPE(20064, "无效服务类型"),
    /**
     * 无效活动奖励类型
     */
    INVALID_REWARD(20065, "无效活动奖励类型"),
    /**
     * 无效活动类型
     */
    INVALID_ACTIVE(20066, "无效活动类型"),
    /**
     * 无效订单类型
     */
    INVALID_ORDER_TYPE(20067, "无效订单类型"),
    /**
     * 无效的分享平台
     */
    INVALID_PLATFORM(20068, "无效的分享平台"),
    /**
     * 无效的发放时间
     */
    INVALID_RELEASE(20069, "无效的发放时间"),
    /**
     * 无效的奖励等级
     */
    INVALID_REWARD_GRADE(20070, "无效的奖励等级"),
    /**
     * 无效执行时间
     */
    INVALID_EXE_TIME(20071, "无效执行时间"),
    /**
     * 无效模板
     */
    INVALID_TEMPLATE(20072, "无效模板"),
    /**
     * 无效语种
     */
    INVALID_LANGUAGE(20073, "无效语种"),
    /**
     * 无效签名
     */
    INVALID_SIGN(20074, "无效签名"),

    //数据已使用
    DATA_IS_USED(20075, "数据已使用"),

    /**
     * 含有屏蔽词
     */
    SHIELDING_WORDS(20076, "含有屏蔽词"),

    /**
     * 创建支付失败
     */
    CREATE_PAY_ERROR(20077, "创建支付失败"),

    /**
     * 支付通知失败
     */
    PAY_NOTIFY_ERROR(20078, "支付通知失败"),


    /**
     * 无效的推荐人
     */
    INVALID_REFEREE_CODE(20079, "无效的推荐人"),
    /**
     * 无效推荐人类型
     */
    INVALID_REFEREE_TYPE(20080, "无效推荐人类型"),
    /**
     * 无效的方式
     */
    INVALID_WAY_CODE(20081, "无效的方式"),
    /**
     * 优惠券已失效
     */
    INVALID_COUPON(20082, "优惠券已失效"),
    /**
     * 无效注册渠道
     */
    INVALID_CHANNEL(20083, "无效注册渠道"),
    /**
     * 无效订单数
     */
    INVALID_ORDER(20084, "无效订单数"),
    /**
     * 无效订单数
     */
    INVALID_FREQUENCY(20085, "无效订单数"),
    /**
     * 无效客户等级
     */
    INVALID_GRADE(20086, "无效客户等级"),

    /**
     * 当前客户不能签单
     */
    PASSENGER_NOT_SIGN(20087, "当前客户不能签单"),

    /**
     * 当前客户余额不足
     */
    PASSENGER_BALANCE(20088, "当前客户余额不足"),

    /**
     * 当前司机余额不足
     */
    EMPLOY_BALANCE(20089, "当前司机余额不足"),

    /**
     * 代付未开启
     */
    EMPLOY_HELPLY_NOT_OPEN(20090, "代付未开启"),

    /**
     * 执行中订单已达上线
     */
    TOO_MANY_RUNING_ORDERS_ERROR(20091, "执行中订单已达上线"),
    /**
     * 未找到收费标准
     */
    CHARGE_STANDARD_IS_NOT_EXIST(20092, "未找到收费标准"),

    /**
     * 未知错误
     */
    UNKNOW_ERROR(-1, "未知错误"),

    /**
     * 缓存添加失败
     */
    ORDER_CACHE_SAVE_ERROR(20093, "缓存添加失败"),
    /**
     * 无效赠送金额
     */
    CHILD_TABLE_DATA_EXISTS(20094, "无效赠送金额"),
    /**
     * 时间重叠
     */
    TIME_OVERLAP(20095, "时间重叠"),
    /**
     * 公里数重叠
     */
    MILEAGE_OVERLAP(20096, "公里数重叠"),

    //提成标准已被使用
    ROYALTY_IS_USED_CODE(20097, "提成标准已被使用"),

    //可提现金额不足
    DRIVER_BALANCE_NOT_ENOUGH(20098, "可提现金额不足"),

    /**
     * 无效优先级
     */
    INVALID_PRIORITY(20099, "无效优先级"),
    /**
     * 无效司机类型
     */
    INVALID_EMPLOY_TYPE(30001, "无效司机类型"),
    /**
     * 无效距离
     */
    INVALID_DISTANCE(30002, "无效距离"),
    /**
     * 无效金额
     */
    INVALID_MONEY(30003, "无效金额"),
    /**
     * 无效电话模式
     */
    INVALID_PHONE_MODE(30004, "无效电话模式"),
    /**
     * 无效设备类型
     */
    INVALID_DEVICE_MODE(30005, "无效设备类型"),
    /**
     * 无效更新模式
     */
    INVALID_UPDATE_MODE(30006, "无效更新模式"),

    /**
     * 无效活动状态
     */
    INVALID_ACTIVE_STATE(30007, "无效活动状态"),
    /**
     * 无效条件
     */
    INVALID_CONDITION(30008, "无效条件"),

    /**
     * 修改失败
     */
    MODIFY_ERROR(30009, "修改失败"),
    /**
     * 删除失败
     */
    DELETE_ERROR(30010, "删除失败"),
    /**
     * 查询失败
     */
    QUERY_ERROR(30011, "查询失败"),
    /**
     * 脏数据
     */
    VERSION_ERROR(30012, "脏数据"),
    /**
     * 电话号码已存在
     */
    PHONE_EXISTS(30013, "电话号码已存在"),
    /**
     * 已是黑名单客户
     */
    BLACKLIST_ALREADY(30014, "已是黑名单客户"),
    /**
     * 不是黑名单客户
     */
    BLACKLIST_NOT_IN(30015, "不是黑名单客户"),
    /**
     * 乘客不存在
     */
    PASSENGER_NOT_EXIST(30016, "乘客不存在"),
    /**
     * 收藏地址不能超过8个
     */
    ADDRESS_LIMIT(30017, "收藏地址不能超过8个"),
    /**
     * 收藏地址不存在
     */
    ADDRESS_NOT_EXIST(30018, "收藏地址不存在"),
    /**
     * 部门已存在
     */
    DEPARTMENT_EXIST(30019, "部门已存在"),
    /**
     * 部门不存在
     */
    DEPARTMENT_NOT_EXIST(30020, "部门不存在"),
    /**
     * 下属存在客户
     */
    PASSENGER_UNDER(30021, "下属存在客户"),
    /**
     * 下属存在部门
     */
    DEPARTMENT_UNDER(30022, "下属存在部门"),
    /**
     * 等级已存在
     */
    GRADE_EXIST(30023, "等级已存在"),
    /**
     * 等级不存在
     */
    GRADE_NOT_EXIST(30024, "等级不存在"),
    /**
     * 初始化等级失败
     */
    INIT_GRADE_ERROR(30025, "初始化等级失败"),
    /**
     * 应用存在
     */
    APP_EXIST(30026, "应用存在"),
    /**
     * 应用不存在
     */
    APP_NOT_EXIST(30027, "应用不存在"),
    /**
     * 存在下属集群
     */
    CLUSTER_UNTER(30028, "存在下属集群"),
    /**
     * 集群已经存在
     */
    CLUSTER_EXIST(30029, "集群已经存在"),
    /**
     * 集群不存在
     */
    CLUSTER_NOT_EXIST(30030, "集群不存在"),
    /**
     * 存在下属命名空间
     */
    NAMESPACE_UNDER(30031, "存在下属命名空间"),
    /**
     * 没有无权限操作
     */
    NO_POWER_OPERATE(30032, "没有无权限操作"),
    /**
     * 命名空间不存在
     */
    NAMESPACE_NOT_EXIST(30033, "命名空间不存在"),
    /**
     * 下属存在实例
     */
    ITEM_UNDER(30034, "下属存在实例"),
    /**
     * 发布失败
     */
    RELEASE_ERROR(30035, "发布失败"),
    /**
     * 命名空间没有实例
     */
    WITHOUT_ITEM(30036, "命名空间没有实例"),
    /**
     * 回滚失败
     */
    ROLLBACK_ERROR(30037, "回滚失败"),
    /**
     * 实例不存在
     */
    ITEM_NOT_EXIST(30038, "实例不存在"),
    /**
     * 实例已存在
     */
    ITEM_EXIST(30039, "实例已存在"),
    /**
     * 命名空间存在
     */
    NAMESPACE_EXIST(30040, "命名空间存在"),

    /**
     * RSA加密失败
     */
    RSA_ENCRYPT_ERROR(30041, "RSA加密失败"),
    /**
     * RSA解密失败
     */
    RSA_DECRYPT_ERROR(30042, "RSA解密失败"),
    /**
     * 数据已存在
     */
    DATA_EXISTS(30043, "数据已存在"),
    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(30044, "数据不存在"),
    /**
     * 数据添加出错
     */
    INSERT_ERROR(30045, "数据添加出错"),

    //新密码在最近5次修改密码中已使用
    PASSWORD_IS_USED(30046, "新密码在最近5次修改密码中已使用"),

    /**
     * 无效充值金额
     */
    INVALID_RECHARGE_CODE(30047, "无效充值金额"),
    /**
     * 无效赠送金额
     */
    INVALID_LARGESS_CODE(30048, "无效赠送金额");

    private int code;
    private String showMsg;

    ErrCode(int code, String showMsg) {
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
