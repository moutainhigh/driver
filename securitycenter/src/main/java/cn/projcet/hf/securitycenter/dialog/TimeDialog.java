package cn.projcet.hf.securitycenter.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.widget.wheelview.WheelView;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TimeDialog
 * Author: shine
 * Date: 2018/11/28 下午4:17
 * Description:
 * History:
 */
public class TimeDialog {

    WheelView start;
    WheelView end;
    TextView tv_cancel;
    TextView tv_sure;

    private Context context;
    private CusBottomSheetDialog dialog;

    public TimeDialog(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_time, null, false);

        start = view.findViewById(R.id.start);
        end = view.findViewById(R.id.end);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_sure = view.findViewById(R.id.tv_sure);

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        getData();
        initView();
    }

    public void getData(){
        //todo 需要的数据的获取  是否获取到分享的行程

    }

    public void initView(){
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        tv_sure.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public void show(){
        dialog.show();
    }

}
