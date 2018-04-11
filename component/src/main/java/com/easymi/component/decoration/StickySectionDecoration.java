package com.easymi.component.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Administrator on 2017/11/29 0029.
 * 粘性头部分割线.
 */

public class StickySectionDecoration extends RecyclerView.ItemDecoration {

    private final float mTextOffsetX;
    private final TextPaint mTextPaint;
    private int mHeaderHeight;
    private Paint mPaint;

    private float descent;
    private float ascent;


    public StickySectionDecoration(Context context, int headerHeight) {
        mHeaderHeight = headerHeight <= 0 ? 20 : headerHeight;
        mHeaderHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, headerHeight,
                context.getResources().getDisplayMetrics());

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.parseColor("#f7f7f7"));

        mTextOffsetX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, context.getResources().getDisplayMetrics());

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.parseColor("#999999"));
        float sp = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, context.getResources().getDisplayMetrics());
        mTextPaint.setTextSize(sp);
        Paint.FontMetrics mFontMetrics = mTextPaint.getFontMetrics();
        descent = mFontMetrics.descent;
        ascent = mFontMetrics.ascent;

    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter originalAdapter = parent.getAdapter();
        int position = parent.getChildAdapterPosition(view); //原始adapter中的位置,
        if (originalAdapter != null && originalAdapter instanceof StickyAdapter
                && position != RecyclerView.NO_POSITION) {
            StickyAdapter adapter = (StickyAdapter) originalAdapter;
            if (adapter.isFirstInGroup(position)) {
                outRect.top = mHeaderHeight;
            }
        }
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.Adapter originalAdapter = parent.getAdapter();
        if (originalAdapter == null || !(originalAdapter instanceof StickyAdapter)) {
            return;
        }
        StickyAdapter adapter = (StickyAdapter) originalAdapter;
        int childCount = parent.getChildCount();    //当前屏幕上显示的view
        for (int i = 0; i < childCount; i++) {

            View view = parent.getChildAt(i);
            int index = parent.getChildAdapterPosition(view);

            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();

            if (i == 0) {
                //第一个可见view,强制绘制StickyHeader
                int top = parent.getPaddingTop();
                if (/*index != 0 && */adapter.isLasInGroup(index)) {
                    //如果是组里最后一个,绘制推出屏幕效果
                    int realTop = view.getBottom() - mHeaderHeight;
                    top = Math.min(realTop, top);
                }
                int bottom = top + mHeaderHeight;
                c.drawRect(left, top, right, bottom, mPaint);

                String content = adapter.getContent(index);

                float h = ((bottom - top) - (descent - ascent)) / 2;
                float titleX = left + mTextOffsetX;
                float titleY = bottom - h - descent;

                c.drawText(content, titleX, titleY, mTextPaint);
            } else {
                if (adapter.isFirstInGroup(index)) {
                    int top = view.getTop() - mHeaderHeight;
                    int bottom = view.getTop();
                    c.drawRect(left, top, right, bottom, mPaint);

                    float h = ((bottom - top) - (descent - ascent)) / 2;
                    float titleX = left + mTextOffsetX;
                    float titleY = bottom - h - descent;

                    String content = adapter.getContent(index);
                    c.drawText(content, titleX, titleY, mTextPaint);

                }
            }
        }


    }

}
