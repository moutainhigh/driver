package com.easymin.carpooling;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class StaticVal {
    //起点
    public static final int MARKER_FLAG_START = 20;
    //终点
    public static final int MARKER_FLAG_END = 21;
    //途经点 -- 亮色
    public static final int MARKER_FLAG_PASS_ENABLE = 0x19;
    //途经点 -- 灰色
    public static final int MARKER_FLAG_PASS_DISABLE = 0x18;

    //规划送人
    public static final int PLAN_SEND = 0x17;
    //规划接人
    public static final int PLAN_ACCEPT = 0x16;

    public static final int TOOLBAR_NOT_START = 0x15;
    public static final int TOOLBAR_CHANGE_ACCEPT = 0x14;
    public static final int TOOLBAR_CHANGE_SEND = 0x13;
    public static final int TOOLBAR_ACCEPT_ING = 0x12;
    public static final int TOOLBAR_SEND_ING = 0x11;
    public static final int TOOLBAR_FLOW = 0x10;
    public static final int TOOLBAR_FINISH = 0x9;

}
