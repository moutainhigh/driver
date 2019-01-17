package com.easymi.common.register;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.easymi.component.decoration.Sticky;
import com.easymi.component.decoration.StickyDecoration;

/**
 * @author hufeng
 * 已废弃
 */
public class BrandDecoration extends StickyDecoration {

    private final float descent;
    private final float ascent;
    private Paint txPaint;
    private int leftX;

    public BrandDecoration(Context context,int mStickyHeight, int mDividerHeight) {
        super(mStickyHeight, mDividerHeight);
        int txSize = dp2px(context, 13);
        leftX = dp2px(context, 13);
        txPaint = new Paint();
        txPaint.setAntiAlias(true);
        txPaint.setTextSize(txSize);
        txPaint.setStyle(Paint.Style.FILL);
        txPaint.setColor(Color.parseColor("#aeaeae"));
        Paint.FontMetrics mFontMetrics = txPaint.getFontMetrics();
        descent = mFontMetrics.descent;
        ascent = mFontMetrics.ascent;
    }

    @Override
    protected void onDrawSticky(Canvas canvas, Sticky data, float left, float top, float right, float bottom) {
        float h = (bottom - top - (descent - ascent)) / 2;
        float titleX = left + leftX;
        float titleY = bottom - h - descent;
        txPaint.setColor(Color.parseColor("#f3f4f5"));
        canvas.drawRect(left,top,right,bottom,txPaint);
        txPaint.setColor(Color.parseColor("#aeaeae"));
        canvas.drawText(data.groupTag(), titleX, titleY, txPaint);
    }

    @Override
    protected void onDrawDivider(Canvas canvas, Sticky data, float left, float top, float right, float bottom) {
        txPaint.setColor(Color.parseColor("#dddddd"));
        canvas.drawRect(left + leftX,top,right,bottom,txPaint);
    }

    private static int dp2px(Context context, float dpValue){
        float scale=context.getResources().getDisplayMetrics().density;
        return (int)(dpValue*scale+0.5f);
    }



}
