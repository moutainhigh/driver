package com.easymin.carpooling.widget;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */

public interface OnItemTouchListener {
    /**
     * 移动监听
     * @param fromPosition
     * @param toPosition
     * @return
     */
    boolean onMove(int fromPosition, int toPosition);

    /**
     * 滑动监听
     * @param position
     */
    void onSwiped(int position);
}
