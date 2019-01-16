package com.easymi.cityline;

/**
 * Created by liuzihao on 2018/11/15.
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
