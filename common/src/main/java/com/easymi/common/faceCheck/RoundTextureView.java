package com.easymi.common.faceCheck;

import android.content.Context;
import android.graphics.Outline;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.TextureView;
import android.view.View;
import android.view.ViewOutlineProvider;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: RoundTextureView
 * @Author: hufeng
 * @Date: 2019/11/15 上午11:56
 * @Description:
 * @History:
 */
public class RoundTextureView extends TextureView {
    private static final String TAG = "CustomTextureView";
    private int radius = 0;

    public RoundTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                Rect rect = new Rect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
                outline.setOval(rect);
                setClipToOutline(true);
            }
        });
    }

    public void turnRound() {
        invalidateOutline();
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getRadius() {
        return radius;
    }
}