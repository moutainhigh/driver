package com.easymin.custombus;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

import com.easymi.component.utils.Log;

public class MyScrollVIew extends NestedScrollView {

    private OnScrollListener onScrollListener;

    public MyScrollVIew(Context context) {
        super(context);
    }

    public MyScrollVIew(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyScrollVIew(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    //设置接口
    public interface OnScrollListener {
        void onScroll(int scrollY);
    }

    public void setScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        Log.e("MyScrollVIew", "onScrollChanged" + t + "  " + oldt);
        if (onScrollListener != null) {
            onScrollListener.onScroll(t);
        }
    }
}
