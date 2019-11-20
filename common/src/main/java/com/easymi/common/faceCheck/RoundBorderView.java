package com.easymi.common.faceCheck;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.easymi.component.utils.DensityUtil;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: RoundBorderView
 * @Author: hufeng
 * @Date: 2019/11/15 下午2:15
 * @Description:
 * @History:
 */
public class RoundBorderView  extends View {
    private Paint paint;
    private int radius = 0;


    public RoundBorderView(Context context) {
        this(context, null);
    }

    public RoundBorderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (paint == null) {
            paint = new Paint();
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            RadialGradient sweepGradient = new RadialGradient(
                    ((float) getWidth() / 2),
                    ((float) getHeight() / 2),
                    ((float) getHeight() / 2),
                    Color.parseColor("#D3EAFF"),
                    Color.parseColor("#E9F5FF"),
                    Shader.TileMode.CLAMP);
            paint.setShader(sweepGradient);
        }
        drawBorder(canvas, DensityUtil.dp2px(getContext(),20));
    }


    private void drawBorder(Canvas canvas, int rectThickness) {
        if (canvas == null) {
            return;
        }
        paint.setStrokeWidth(rectThickness);
        Path drawPath = new Path();
        drawPath.addCircle(getWidth()/2, getWidth()/2, getWidth()/2,Path.Direction.CW);
        canvas.drawPath(drawPath, paint);
    }

    public void turnRound() {
        invalidate();
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }
}