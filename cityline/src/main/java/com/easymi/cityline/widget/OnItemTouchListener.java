package com.easymi.cityline.widget;

/**
 * Created by liuzihao on 2018/11/14.
 */

public interface OnItemTouchListener {
    boolean onMove(int fromPosition,int toPosition);
    void onSwiped(int position);
}
