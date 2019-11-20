package com.easymi.common.faceCheck;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.dinuscxj.progressbar.CircleProgressBar;
import com.easymi.common.R;
import com.easymi.component.widget.BaseCenterDialog;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: UpLoadProgressDialog
 * @Author: hufeng
 * @Date: 2019/11/15 下午4:12
 * @Description:
 * @History:
 */
public class UpLoadProgressDialog extends BaseCenterDialog {

    CircleProgressBar progressBar;
    TextView tv_hint;

    public UpLoadProgressDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_upload_progress);

        progressBar = findViewById(R.id.progress_bar);
        tv_hint = findViewById(R.id.tv_hint);

    }

    public void setProgress(String hint,int progress){
        progressBar.setProgress(progress);
        tv_hint.setText(hint);
    }
}
