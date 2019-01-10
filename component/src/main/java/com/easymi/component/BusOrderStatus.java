package com.easymi.component;

import com.easymi.component.app.XApp;

public class BusOrderStatus {

    /**
     * 新班次
     */
    public static final int SCHEDULE_STATUS_NEW = 1;
    /**
     * 班次已出发
     */
    public static final int SCHEDULE_STATUS_RUNNING = 5;
    /**
     * 班次已结束
     */
    public static final int SCHEDULE_STATUS_FINISH = 10;

    public static String status2Str(int status) {
        switch (status) {
            case SCHEDULE_STATUS_NEW:
                return XApp.getInstance().getString(R.string.bus_status_new);
            case SCHEDULE_STATUS_RUNNING:
                return XApp.getInstance().getString(R.string.bus_status_begain);
            case SCHEDULE_STATUS_FINISH:
                return XApp.getInstance().getString(R.string.bus_status_end);
            default:
                return "";
        }
    }
}
