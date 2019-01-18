package com.easymi.personal.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: NotScrollViewPager
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 解决ViewPager和百度地图滑动冲突
 * History:
 */
public class NotScrollViewPager extends ViewPager {

    public NotScrollViewPager(Context context) {
        super(context);
    }

    public NotScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

}
