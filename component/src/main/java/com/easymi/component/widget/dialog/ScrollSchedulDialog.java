package com.easymi.component.widget.dialog;

import android.content.Context;
import android.os.Bundle;

import com.easymi.component.R;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: ScrollSchedulDialog
 * @Author: hufeng
 * @Date: 2019/11/20 下午11:08
 * @Description:
 * @History:
 */
public class ScrollSchedulDialog extends BaseCenterDialog{

    public ScrollSchedulDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_scroll_schedul);
    }
}
