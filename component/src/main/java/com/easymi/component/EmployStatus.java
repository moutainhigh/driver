package com.easymi.component;

/**
 * Created by liuzihao on 2017/12/22.
 */

public class EmployStatus {
    /**
     * 离线
     */
    public static final String OFFLINE = String.valueOf(1);
    /**
     * 在线
     */
    public static final String ONLINE = String.valueOf(2);

    /**
     * 空闲
     */
    public static final String FREE = String.valueOf(2);
    /**
     * 派单
     */
    public static final String SEND_ORDER = String.valueOf(5);
    /**
     * 接单
     */
    public static final String ACCEPT_ORDER = String.valueOf(10);
    /**
     * 前往预约地
     */
    public static final String TO_START = String.valueOf(15);
    /**
     * 到达预约地
     */
    public static final String ARRIVE_START = String.valueOf(20);
    /**
     * 前往目的地
     */
    public static final String TO_END = String.valueOf(25);
    /**
     * 中途等待
     */
    public static final String MIDDLE_WAIT = String.valueOf(28);
    /**
     * 冻结
     */
    public static final String FROZEN = String.valueOf(45);
}
