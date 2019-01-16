package com.easymi.cityline.widget;

/**
 *
 * @author liuzihao
 * @date 2018/11/14
 */

public interface OnItemTouchListener {
    /**
     * 移动监听
     * @param fromPosition
     * @param toPosition
     * @return
     */
    boolean onMove(int fromPosition,int toPosition);

    /**
     * 滑动监听
     * @param position
     */
    void onSwiped(int position);
}
