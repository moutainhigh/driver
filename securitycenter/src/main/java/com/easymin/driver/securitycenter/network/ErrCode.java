package com.easymin.driver.securitycenter.network;

/**
 * Created by liuzihao on 2018/2/8.
 */

public enum ErrCode {


    SUCCESS(1, "成功"),

    /**
     * 验证格式出错
     */
    FORMAT_ERR(10000, "验证格式出错"),

    /**
     * 验证码不能为空
     */
    CODE_IS_NULL_ERR(11000, "验证码不能为空"),
    /**
     * 通用-用户名格式错误
     */
    USERNAME_FORMAT_ERR(10001, "用户名格式错误"),
    /**
     * 通用-密码格式错误
     */
    PASSWORD_FORMAT_ERR(10002, "密码格式错误"),
    /**
     * 通用-验证码格式错误
     */
    CODE_FORMAT_ERR(10003, "验证码格式错误"),
    /**
     * 通用-参数不能为空
     */
    PARAMETER_NULL_FORMAT_ERR(10004, "参数不能为空"),
    /**
     * 通用-参数格式错误
     */
    PARAMETER_FORMAT_ERR(10005, "参数格式错误"),
    /**
     * 通用-参数id为空
     */
    ID_NULL_FORMAT_ERR(10006, "参数id为空"),
    /**
     * 通用-appKey为空
     */
    APPKEY_NULL_FORMAT_ERR(10007, "appKey为空"),
    /**
     * 通用-验证码Id格式错误
     */
    CODE_ID_FORMAT_ERR(10008, "验证码Id格式错误"),

    /**
     * 通用-包含不安全的字符
     */
    CONTAINS_NOT_SAFE_CHARACTER(10009, "包含不安全的字符"),
    /**
     * 通用-公司id为空
     */
    COMPANY_ID_ISNULL_CHARACTER(10010, "公司id为空"),
    /**
     * 通用-page为空
     */
    PAGE_NULL_FORMAT_ERR(10011, "page为空"),
    /**
     * 通用-size为空
     */
    SIZE_NULL_FORMAT_ERR(100012, "size为空"),
    /**
     * 数据重复
     */
    DATA_REPEATED(100013, "数据重复"),
    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(100014, "数据不存在"),

    /**
     * 添加错误
     */
    INSERT_VALIDATE_ERR(40001, "添加错误"),
    /**
     * 更新错误
     */
    UPDATE_VALIDATE_ERR(40002, "更新错误"),
    /**
     * 删除错误
     */
    DELETE_VALIDATE_ERR(40003, "删除错误"),
    /**
     * 查询错误
     */
    QUERY_VALIDATE_ERR(40004, "查询错误"),

    /**
     * 数据不存在
     */
    QUERY_VALIDATE_NULL(40005, "数据不存在"),

    /**
     * 用户名或者密码错误
     */
    USERNAME_OR_PASSWORD_ERR(40006, "用户名或者密码错误"),
    /**
     * 用户名错误
     */
    USERNAME_ERR(41000, "用户名错误"),

    /**
     * 该用户被加入黑名单
     */
    USERNAME_BLACK_ERR(41001, "该用户被加入黑名单"),

    /**
     * 图形验证码错误
     */
    CODE_VALIDATE_ERR(40001, "图形验证码错误"),

    /**
     * 密码不相同
     */
    RESET_PASSWOED_ERROR_REGISTERED(40028, "密码不相同"),

    /**
     * token已过期
     */
    TOKEN_EXPIRE_REGISTERED(40026, "token已过期"),

    /**
     * 发送短信验证码错误
     */
    SMS_SEND_CODE_ERR(40038, "发送短信验证码错误"),

    /**
     * 发送短信验证码重复发送错误
     */
    SMS_SEND_REPEAT_CODE_ERR(40039, "发送短信验证码重复发送错误"),
    /**
     * 发送短信验证码type类型错误
     */
    SMS_TYPE_VALIDATE_ERR(40039, "发送短信验证码type类型错误"),

    /**
     * 导出失败
     */
    EXPORT_REPEAT_ERR(40042, "导出失败"),

    /**
     * 司机没有开通专车业务
     */
    DRIVER_NOT_SPECIAL(40043, "司机没有开通专车业务"),

    /**
     * 司机与车辆不要重复绑定
     */
    DRIVER_BINDING_REPEATED(40044, "司机与车辆不要重复绑定"),


    /**
     * 捕获到系统异常，未知错误
     */
    UNKNOWN_ERR(80001, "系统异常"),
    /**
     * 线路不存在
     */
    LINE_NOT_EXIST(40045, "线路不存在"),
    /**
     * 线路ID不能为空
     */
    LINE_ID_NOT_NULL(40046, "线路ID不能为空"),
    /**
     * 订单状态错误
     */
    ORDER_STATUS_ERR(40047, "订单状态错误"),
    /**
     * 订单不存在
     */
    ORDER_IS_NULL_ERR(40048, "订单不存在"),

    /**
     * 司机不存在
     */
    DRIVER_NOT_EXIST(40049, "司机不存在"),
    /**
     * 司机余额操作失败
     */
    DRIVER_BALANCE_ERROR(40050, "司机余额操作失败"),

    /**
     * 支付类型错误
     */
    PAY_TYPE_ERROR(40051, "支付类型错误"),

    /**
     * 终端ip错误
     */
    TERMINAL_IP_ERR(40052, "终端ip错误"),

    /**
     * 加密失败
     */
    ENCODE_ERR(40053, "加密失败"),

    /**
     * 请求失败
     */
    REQUEST_ERR(40054, "请求失败"),

    /**
     * 请求超时
     */
    READ_TIMEOUT_ERR(40055, "请求超时"),

    /**
     * 验签失败
     */
    ENCRYPTING_ERR(40056, "验签失败"),

    /**
     * 转换失败
     */
    CHANGE_ERR(40057, "转换失败"),

    /**
     * 支付操作人错误
     */
    PAY_OPERATOR_ERROR(40058, "支付操作人错误"),

    /**
     * 订单创建失败
     */
    ORDER_CREATE_ERROR(40059, "订单创建失败"),
    /**
     * 订单状态错误
     */
    ORDER_STATUS_ERROR(40060, "订单状态错误"),
    /**
     * 订单派单失败
     */
    ORDER_ASSIGN_ERROR(40061, "订单派单失败"),
    /**
     * 订单抢单失败
     */
    ORDER_GRAB_ERROR(40062, "订单抢单失败"),
    /**
     * 订单接单失败
     */
    ORDER_TAKE_ERROR(40063, "订单接单失败"),
    /**
     * 订单到达预约地失败
     */
    ORDER_GO_TO_BOOK_PLACE_ERROR(40064, "订单到达预约地失败"),
    /**
     * 订单拒单失败
     */
    ORDER_REFUSE_ERROR(40065, "订单拒单失败"),
    /**
     * 订单销单失败
     */
    ORDER_CANCEL_ERROR(40066, "订单销单失败"),

    /**
     * 支付状态错误
     */
    PAY_STATUS_ERR(40067, "支付状态错误"),
    /**
     * 站点被线路使用
     */
    STATION_IS_USED(40068, "站点被线路使用"),

    /**
     * 车牌号已存在
     */
    VEHICLE_NUMBER_EXISTS(40069, "车牌号已存在"),
    /**
     * 站点选择错误
     */
    STATION_CHOOSE_ERROR(40070, "站点选择错误"),
    /**
     * 客户余额不足,不允许下单
     */
    PASSENGER_BALANCE_DEFICIENCY(40071, "余额不足,不允许下单"),
    /**
     * 客户不存在
     */
    PASSENGER_NOT_EXIST(40072, "客户不存在"),
    /**
     * 班次不存在
     */
    SCHEDULE_NOT_EXIST(40073, "班次不存在"),
    /**
     * 班次票数不足
     */
    SCHEDULE_TICKET_DEFICIENCY(40074, "班次票数不足"),

//    /**
//     * 请求参数无效
//     */
//    INVALID_PARAMETER_CODE(20000, "请求参数无效"),
//    /**
//     * appkey无效
//     */
//    INVALID_APPKEY_CODE(20001, "appkey无效"),
//    /**
//     * 公司id无效
//     */
//    INVALID_COMPANY_CODE(20003, "公司id无效"),
//    /**
//     * 电话无效
//     */
//    INVALID_PHONE_CODE(20004, "电话无效"),
//    /**
//     * 国家编码无效
//     */
//    INVALID_COUNTRY_CODE(20005, "国家编码无效"),
//    /**
//     * 无效id
//     */
//    INVALID_ID_CODE(20006, "无效id"),
//
//    /**
//     * 无效等级名称
//     */
//    INVALID_GRADE_NAME_CODE(20007, "无效等级名称"),
//    /**
//     * 无效并行订单数量
//     */
//    INVALID_PARALLEL_NUM_CODE(20008, "无效并行订单数量"),
//    /**
//     * 无效单次下单数量
//     */
//    INVALID_SINGLE_NUM_CODE(20009, "无效单次下单数量"),
//    /**
//     * 无效等级大小
//     */
//    INVALID_GRADE_SORT_CODE(20010, "无效等级大小"),
//    /**
//     * 无效pid
//     */
//    INVALID_PID_CODE(20011, "无效pid"),
//    /**
//     * 无效部门名称
//     */
//    INVALID_DEPARTMENT_CODE(20012, "无效部门名称"),
//    /**
//     * 无效客户id
//     */
//    INVALID_PASSENGER_CODE(20013, "无效客户id"),
//    /**
//     * 无效经度
//     */
//    INVALID_LNG_CODE(20014, "无效经度"),
//    /**
//     * 无效纬度
//     */
//    INVALID_LAT_CODE(20015, "无效纬度"),
//    /**
//     * 无效地址
//     */
//    INVALID_ADDRESS_CODE(20016, "无效地址"),
//    /**
//     * 无效地址类
//     */
//    INVALID_ADDRESS_TYPE_CODE(20017, "无效地址类"),
//    /**
//     * 无效应用id
//     */
//    INVALID_APP_ID_CODE(20018, "无效应用id"),
//    /**
//     * 无效应用名称
//     */
//    INVALID_APP_NAME_CODE(20019, "无效应用名称"),
//    /**
//     * 无效创建人
//     */
//    INVALID_CREATOR_CODE(20020, "无效创建人"),
//    /**
//     * 无效修改者
//     */
//    INVALID_MODIFIER_CODE(20023, "无效修改者"),
//    /**
//     * 无效集群名称
//     */
//    INVALID_CLUSTER_CODE(20024, "无效集群名称"),
//    /**
//     * 无效操作权限
//     */
//    INVALID_AUTHORITY_CODE(20025, "无效操作权限"),
//    /**
//     * 无效环境名称
//     */
//    INVALID_ENV_CODE(20026, "无效环境名称"),
//    /**
//     * 无效命名空间
//     */
//    INVALID_NAMESPACE_CODE(20027, "无效命名空间"),
//    /**
//     * 无效key
//     */
//    INVALID_KEY_CODE(20028, "无效key"),
//    /**
//     * 无效value
//     */
//    INVALID_VALUE_CODE(20029, "无效value"),
//
//    /**
//     * 用户锁定
//     */
//    ADMIN_LOCKED(20030, "用户锁定"),
//    /**
//     * 用户冻结
//     */
//    ADMIN_FREEZED(20031, "用户冻结"),
//    /**
//     * 数据不匹配
//     */
    NOT_MATCH(20032, "数据已更新"),
//    /**
//     * 用户id无效
//     */
//    INVALID_USERID_CODE(20033, "用户id无效"),
//    /**
//     * 角色已被使用，不能删除
//     */
//    USED_ROLE_CODE(20034, "角色已被使用，不能删除"),
//    /**
//     * 参数对象不能为空
//     */
//    INVALID_OBJECT_CODE(20035, "参数对象不能为空"),
//    /**
//     * 公司名称不能为空
//     */
//    INVALID_COMPANYNAME_CODE(20036, "公司名称不能为空"),
//    /**
//     * 公司简称不能为空
//     */
//    INVALID_COMPANYSHORTNAME_CODE(20037, "公司简称不能为空"),
//    /**
//     * 公司所属国家不能为空
//     */
//    INVALID_COMPANY_COUNTRY_CODE(20038, "公司所属国家不能为空"),
//    /**
//     * 公司所属省份不能为空
//     */
//    INVALID_COMPANY_PROVINCE_CODE(20039, "公司所属省份不能为空"),
//    /**
//     * 公司所属城市不能为空
//     */
//    INVALID_COMPANY_CITY_CODE(20040, "公司所属城市不能为空"),
//    /**
//     * 角色名称不能为空
//     */
//    INVALID_ROLENMAE_CODE(20041, "角色名称不能为空"),
//    /**
//     * 是否是内置角色不能为空
//     */
//    INVALID_ISINTERNAL_CODE(20042, "是否是内置角色不能为空"),
//    /**
//     * 用户id无效
//     */
//    INVALID_USERNAME_CODE(20043, "用户id无效"),
//    /**
//     * 无效的citycode
//     */
//    INVALID_CITY_CODE(20044, "无效的citycode"),
//    /**
//     * 无效的adcode
//     */
//    INVALID_AD_CODE(20045, "无效的adcode"),
//    /**
//     * 密码错误
//     * /*
//     */
//    PASSWORD_ERROR_CODE(20046, "密码错误"),
//
//    //无效的短信验证码
//    INVALID_PHONE_CODE_ERROR(20047, "无效的短信验证码"),
//
//
//    /**
//     * 密码错误
//     * /*
//     */
//    PASSWORD_CODE(20048, "密码错误"),
//
//    /**
//     * 服务人员冻结
//     */
//    EMPLOY_FREEZED(20049, "服务人员冻结"),
//
//    /**
//     * 操作失败
//     */
//    FAIL(20050, "操作失败"),
//    /**
//     * 密码错误
//     */
//    PASSWORD(20051, "密码错误"),
//
//    /**
//     * 无效类型
//     */
//    INVALID_TYPE_CODE(20052, "无效类型"),
//    /**
//     * 无效客户类型
//     */
//    INVALID_PASSENGER_TYPE_CODE(20053, "无效客户类型"),
//    /**
//     * 无效的开始时间
//     */
//    INVALID_BEGIN_CODE(20054, "无效的开始时间"),
//    /**
//     * 无效的车牌号
//     */
//    INVALID_PLATE_NUMBER(20055, "无效的车牌号"),
//    /**
//     * 无效优惠券编码
//     */
//    INVALID_COUPON_CODE(20056, "无效优惠券编码"),
//
//    /**
//     * 无效优惠券类型
//     */
//    INVALID_COUPON_TYPE(20057, "无效优惠券类型"),
//    /**
//     * 无效优惠券折扣抵扣
//     */
//    INVALID_COUPON_DISCOUNT(20058, "无效优惠券折扣抵扣"),
//    /**
//     * 无效的标题
//     */
//    INVALID_TITLE_CODE(20059, "无效的标题"),
//    /**
//     * 无效的内容
//     */
//    INVALID_CONTENT_CODE(20060, "无效的内容"),
//    /**
//     * 无效的排序
//     */
//    INVALID_SORT_CODE(20061, "无效的排序"),
//    /**
//     * 无效开始时间
//     */
//    INVALID_BEGIN_TIME(20062, "无效开始时间"),
//    /**
//     * 无效活动中心图片
//     */
//    INVALID_CENTER_IMAGE(20063, "无效活动中心图片"),
//    /**
//     * 无效服务类型
//     */
//    INVALID_SERVICE_TYPE(20064, "无效服务类型"),
//    /**
//     * 无效活动奖励类型
//     */
//    INVALID_REWARD(20065, "无效活动奖励类型"),
//    /**
//     * 无效活动类型
//     */
//    INVALID_ACTIVE(20066, "无效活动类型"),
//    /**
//     * 无效订单类型
//     */
//    INVALID_ORDER_TYPE(20067, "无效订单类型"),
//    /**
//     * 无效的分享平台
//     */
//    INVALID_PLATFORM(20068, "无效的分享平台"),
//    /**
//     * 无效的发放时间
//     */
//    INVALID_RELEASE(20069, "无效的发放时间"),
//    /**
//     * 无效的奖励等级
//     */
//    INVALID_REWARD_GRADE(20070, "无效的奖励等级"),
//    /**
//     * 无效执行时间
//     */
//    INVALID_EXE_TIME(20071, "无效执行时间"),
//    /**
//     * 无效模板
//     */
//    INVALID_TEMPLATE(20072, "无效模板"),
//    /**
//     * 无效语种
//     */
//    INVALID_LANGUAGE(20073, "无效语种"),
//    /**
//     * 无效签名
//     */
//    INVALID_SIGN(20074, "无效签名"),
//
//    //数据已使用
//    DATA_IS_USED(20075, "数据已使用"),
//
//    /**
//     * 含有屏蔽词
//     */
//    SHIELDING_WORDS(20076, "含有屏蔽词"),
//
//    /**
//     * 创建支付失败
//     */
//    CREATE_PAY_ERROR(20077, "创建支付失败"),
//
//    /**
//     * 支付通知失败
//     */
//    PAY_NOTIFY_ERROR(20078, "支付通知失败"),
//
//
//    /**
//     * 无效的推荐人
//     */
//    INVALID_REFEREE_CODE(20079, "无效的推荐人"),
//    /**
//     * 无效推荐人类型
//     */
//    INVALID_REFEREE_TYPE(20080, "无效推荐人类型"),
//    /**
//     * 无效的方式
//     */
//    INVALID_WAY_CODE(20081, "无效的方式"),
//    /**
//     * 优惠券已失效
//     */
//    INVALID_COUPON(20082, "优惠券已失效"),
//    /**
//     * 无效注册渠道
//     */
//    INVALID_CHANNEL(20083, "无效注册渠道"),
//    /**
//     * 无效订单数
//     */
//    INVALID_ORDER(20084, "无效订单数"),
//    /**
//     * 无效订单数
//     */
//    INVALID_FREQUENCY(20085, "无效订单数"),
//    /**
//     * 无效客户等级
//     */
//    INVALID_GRADE(20086, "无效客户等级"),
//
//    /**
//     * 当前客户不能签单
//     */
//    PASSENGER_NOT_SIGN(20087, "当前客户不能签单"),
//
//    /**
//     * 当前客户余额不足
//     */
//    PASSENGER_BALANCE(20088, "当前客户余额不足"),
//
//    /**
//     * 当前司机余额不足
//     */
//    EMPLOY_BALANCE(20089, "当前司机余额不足"),
//
//    /**
//     * 代付未开启
//     */
//    EMPLOY_HELPLY_NOT_OPEN(20090, "代付未开启"),
//
//    /**
//     * 执行中订单已达上线
//     */
//    TOO_MANY_RUNING_ORDERS_ERROR(20091, "执行中订单已达上线"),
//    /**
//     * 未找到收费标准
//     */
//    CHARGE_STANDARD_IS_NOT_EXIST(20092, "未找到收费标准"),
//
//    /**
//     * 未知错误
//     */
//    UNKNOW_ERROR(-1, "您的网络出小差了"),
//
//    /**
//     * 缓存添加失败
//     */
//    ORDER_CACHE_SAVE_ERROR(20093, "缓存添加失败"),
//    /**
//     * 无效赠送金额
//     */
//    CHILD_TABLE_DATA_EXISTS(20094, "无效赠送金额"),
//    /**
//     * 时间重叠
//     */
//    TIME_OVERLAP(20095, "时间重叠"),
//    /**
//     * 公里数重叠
//     */
//    MILEAGE_OVERLAP(20096, "公里数重叠"),
//
//    //提成标准已被使用
//    ROYALTY_IS_USED_CODE(20097, "提成标准已被使用"),
//
//    //可提现金额不足
//    DRIVER_BALANCE_NOT_ENOUGH(20098, "可提现金额不足"),
//
//    /**
//     * 无效优先级
//     */
//    INVALID_PRIORITY(20099, "无效优先级"),
//    /**
//     * 无效司机类型
//     */
//    INVALID_EMPLOY_TYPE(30001, "无效司机类型"),
//    /**
//     * 无效距离
//     */
//    INVALID_DISTANCE(30002, "无效距离"),
//    /**
//     * 无效金额
//     */
//    INVALID_MONEY(30003, "无效金额"),
//    /**
//     * 无效电话模式
//     */
//    INVALID_PHONE_MODE(30004, "无效电话模式"),
//    /**
//     * 无效设备类型
//     */
//    INVALID_DEVICE_MODE(30005, "无效设备类型"),
//    /**
//     * 无效更新模式
//     */
//    INVALID_UPDATE_MODE(30006, "无效更新模式"),
//
//    /**
//     * 无效活动状态
//     */
//    INVALID_ACTIVE_STATE(30007, "无效活动状态"),
//    /**
//     * 无效条件
//     */
//    INVALID_CONDITION(30008, "无效条件"),
//
//    /**
//     * 修改失败
//     */
//    MODIFY_ERROR(30009, "修改失败"),
//    /**
//     * 删除失败
//     */
//    DELETE_ERROR(30010, "删除失败"),
//    /**
//     * 查询失败
//     */
    QUERY_ERROR(30011, "查询失败"),
//    /**
//     * 脏数据
//     */
//    VERSION_ERROR(30012, "脏数据"),
//    /**
//     * 电话号码已存在
//     */
//    PHONE_EXISTS(30013, "电话号码已存在"),
//    /**
//     * 已是黑名单客户
//     */
//    BLACKLIST_ALREADY(30014, "已是黑名单客户"),
//    /**
//     * 不是黑名单客户
//     */
//    BLACKLIST_NOT_IN(30015, "不是黑名单客户"),
//    /**
//     * 乘客不存在
//     */
//    PASSENGER_NOT_EXIST(30016, "乘客不存在"),
//    /**
//     * 收藏地址不能超过8个
//     */
//    ADDRESS_LIMIT(30017, "收藏地址不能超过8个"),
//    /**
//     * 收藏地址不存在
//     */
//    ADDRESS_NOT_EXIST(30018, "收藏地址不存在"),
//    /**
//     * 部门已存在
//     */
//    DEPARTMENT_EXIST(30019, "部门已存在"),
//    /**
//     * 部门不存在
//     */
//    DEPARTMENT_NOT_EXIST(30020, "部门不存在"),
//    /**
//     * 下属存在客户
//     */
//    PASSENGER_UNDER(30021, "下属存在客户"),
//    /**
//     * 下属存在部门
//     */
//    DEPARTMENT_UNDER(30022, "下属存在部门"),
//    /**
//     * 等级已存在
//     */
//    GRADE_EXIST(30023, "等级已存在"),
//    /**
//     * 等级不存在
//     */
//    GRADE_NOT_EXIST(30024, "等级不存在"),
//    /**
//     * 初始化等级失败
//     */
//    INIT_GRADE_ERROR(30025, "初始化等级失败"),
//    /**
//     * 应用存在
//     */
//    APP_EXIST(30026, "应用存在"),
//    /**
//     * 应用不存在
//     */
//    APP_NOT_EXIST(30027, "应用不存在"),
//    /**
//     * 存在下属集群
//     */
//    CLUSTER_UNTER(30028, "存在下属集群"),
//    /**
//     * 集群已经存在
//     */
//    CLUSTER_EXIST(30029, "集群已经存在"),
//    /**
//     * 集群不存在
//     */
//    CLUSTER_NOT_EXIST(30030, "集群不存在"),
//    /**
//     * 存在下属命名空间
//     */
//    NAMESPACE_UNDER(30031, "存在下属命名空间"),
//    /**
//     * 没有无权限操作
//     */
//    NO_POWER_OPERATE(30032, "没有无权限操作"),
//    /**
//     * 命名空间不存在
//     */
//    NAMESPACE_NOT_EXIST(30033, "命名空间不存在"),
//    /**
//     * 下属存在实例
//     */
//    ITEM_UNDER(30034, "下属存在实例"),
//    /**
//     * 发布失败
//     */
//    RELEASE_ERROR(30035, "发布失败"),
//    /**
//     * 命名空间没有实例
//     */
//    WITHOUT_ITEM(30036, "命名空间没有实例"),
//    /**
//     * 回滚失败
//     */
//    ROLLBACK_ERROR(30037, "回滚失败"),
//    /**
//     * 实例不存在
//     */
//    ITEM_NOT_EXIST(30038, "实例不存在"),
//    /**
//     * 实例已存在
//     */
//    ITEM_EXIST(30039, "实例已存在"),
//    /**
//     * 命名空间存在
//     */
//    NAMESPACE_EXIST(30040, "命名空间存在"),
//
//    /**
//     * RSA加密失败
//     */
//    RSA_ENCRYPT_ERROR(30041, "RSA加密失败"),
//    /**
//     * RSA解密失败
//     */
//    RSA_DECRYPT_ERROR(30042, "RSA解密失败"),
//    /**
//     * 数据已存在
//     */
//    DATA_EXISTS(30043, "数据已存在"),
//    /**
//     * 数据不存在
//     */
//    DATA_NOT_EXIST(30044, "数据不存在"),
//    /**
//     * 数据添加出错
//     */
//    INSERT_ERROR(30045, "数据添加出错"),
//
//    //新密码在最近5次修改密码中已使用
//    PASSWORD_IS_USED(30046, "新密码在最近5次修改密码中已使用"),
//
//    /**
//     * 无效充值金额
//     */
//    INVALID_RECHARGE_CODE(30047, "无效充值金额"),
//    /**
//     * 无效赠送金额
//     */
//    INVALID_LARGESS_CODE(30048, "无效赠送金额"),
//    /**
//     * 该服务人员已绑定其他设备
//     */
//    EMPLOY_DEVICE_ERROR(30049, "您已绑定其他设备"),
//    /**
//     * 该服务人员已存在执行中的订单，不能再次接单
//     */
    DRIVER_GOTO_PRE_ORDER_CODE(30050, "您已存在执行中的订单，不能再次接单"),
//    /**
//     * 差一点就抢到了
//     */
    GRAB_ORDER_ERROR(30051, "差一点就抢到了"),
//    /**
//     * 该服务人员已存在执行中的订单，不能再次补单
//     */
//    DRIVER_HAVE_ORDER_CODE(30052, "您已存在执行中的订单，不能再次补单"),
    /**
     * 服务人员不存在
     */
    EMPLOY_NOT_EXIST(31001, "服务人员不存在");
//    /**
//     * 服务人员还有订单未完成
//     */
//    EMPLOY_HAS_ORDERS_TODO(31004, "您还有订单未完成"),
//
//    /**
//     * 车辆正在使用.
//     */
//    VEHICLE_IS_BUSY(31054, "车辆正在使用"),
//
//    NO_VEHICLE_CANNOT_ONLINE(31041, "您还未绑定车辆，请联系管理员"),
//
//    CANNOT_DOING_MORE_ORDERS(31042, "不能同时执行多个订单"),
//
//    PHONE_TIME_ERROR(50018, "手机时间与服务器时间相差过大,请调整手机时间");

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
