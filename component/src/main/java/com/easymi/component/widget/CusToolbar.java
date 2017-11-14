package com.easymi.component.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.component.R;

/**
 * Created by developerLzh on 2017/10/28 0028.
 */

public class CusToolbar extends RelativeLayout {

    private ImageView leftIcon;
    private TextView title;
    private ImageView rightIcon;
    private TextView rightText;

    public CusToolbar(Context context) {
        this(context, null);
    }

    public CusToolbar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusToolbar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bar_layout, this);
        leftIcon = view.findViewById(R.id.left_icon);
        title = view.findViewById(R.id.title);
        rightIcon = view.findViewById(R.id.right_icon);
        rightText = view.findViewById(R.id.right_text);
    }

    public CusToolbar setLeftIcon(int visible, int resId, OnClickListener listener) {
        leftIcon.setVisibility(visible);
        leftIcon.setImageResource(resId);
        leftIcon.setOnClickListener(listener);

        return this;
    }

    public CusToolbar setTitle(int textResId) {
        title.setText(textResId);
        return this;
    }

    public CusToolbar setRightIcon(int visible, int resId, OnClickListener listener) {
        rightIcon.setVisibility(visible);
        rightIcon.setImageResource(resId);
        rightIcon.setOnClickListener(listener);
        return this;
    }

    public CusToolbar setRightText(int visible, String textResId) {
        rightText.setVisibility(visible);
        rightText.setText(textResId);

        return this;
    }
}
