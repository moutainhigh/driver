package com.easymi.common.register;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;

import com.easymi.common.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:
 * @Author: hufeng
 * Date: 2018/12/24 下午1:10
 * Description:
 * History:
 */
public class RatioImageView extends AppCompatImageView {

    //ratio = 宽/高
    private float ratio = 0f;

    public RatioImageView(Context context) {
        this(context,null);
    }

    public RatioImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RatioImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RatioImageView);
        ratio = a.getFloat(R.styleable.RatioImageView_ratio, 0f);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (ratio > 0) {
            setMeasuredDimension(View.getDefaultSize(0, widthMeasureSpec), View.getDefaultSize(0, heightMeasureSpec));
            int widthSpec = MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY);
            int heightSpec = MeasureSpec.makeMeasureSpec((int) (getMeasuredWidth() / ratio), MeasureSpec.EXACTLY);
            super.onMeasure(widthSpec, heightSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }
}
