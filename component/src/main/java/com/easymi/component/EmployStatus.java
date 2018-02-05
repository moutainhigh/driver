package com.easymi.component;

/**
 * Created by liuzihao on 2017/12/22.
 */

public class EmployStatus {
    /**
     * 离线
     */
    public static final int OFFLINE = 0;
    /**
     * 在线
     */
    public static final int ONLINE = 5;

    /**
     * 空闲
     */
    public static final int FREE = 10;
    /**
     * 派单
     */
    public static final int SEND_ORDER = 15;
    /**
     * 接单
     */
    public static final int ACCEPT_ORDER = 20;
    /**
     * 前往预约地
     */
    public static final int TO_START = 25;
    /**
     * 到达预约地
     */
    public static final int ARRIVE_START = 30;
    /**
     * 前往目的地
     */
    public static final int TO_END = 35;
    /**
     * 中途等待
     */
    public static final int MIDDLE_WAIT = 40;
    /**
     * 冻结
     */
    public static final int FROZEN = 45;
}
