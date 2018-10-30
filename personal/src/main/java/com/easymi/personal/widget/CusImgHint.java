package com.easymi.personal.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/10/28 0028.
 */

public class CusImgHint extends RelativeLayout {

    private ImageView hintImg;
    private TextView hintText;
    private Button knownBtn;

    public CusImgHint(Context context) {
        this(context, null);
    }

    public CusImgHint(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CusImgHint(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.img_hint_layout, this);
        hintImg = view.findViewById(R.id.hint_img);
        hintText = view.findViewById(R.id.hint_text);
        knownBtn = view.findViewById(R.id.confirm_btn);

        knownBtn.setOnClickListener(v -> CusImgHint.this.setVisibility(View.GONE));
    }

    public CusImgHint setImageResource(int resId) {
        hintImg.setImageResource(resId);

        return this;
    }

    public CusImgHint setText(int textResId) {
        hintText.setText(textResId);
        return this;
    }

}
