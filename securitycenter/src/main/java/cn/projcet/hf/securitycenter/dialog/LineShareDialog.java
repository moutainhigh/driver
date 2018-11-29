package cn.projcet.hf.securitycenter.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.projcet.hf.securitycenter.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: LineShareDialog
 * Author: shine
 * Date: 2018/11/27 下午1:21
 * Description:
 * History:
 */
public class LineShareDialog {

    LinearLayout lin_no_content;
    Button btn_call_car;
    LinearLayout lin_have_content;
    LinearLayout lin_weixin;
    LinearLayout lin_duanxin;
    LinearLayout lin_QQ;
    TextView tv_content;

    private Context context;
    private CusBottomSheetDialog dialog;

    public LineShareDialog(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_line_share, null, false);

        lin_no_content = view.findViewById(R.id.lin_no_content);
        btn_call_car = view.findViewById(R.id.btn_call_car);
        lin_have_content = view.findViewById(R.id.lin_have_content);
        lin_weixin = view.findViewById(R.id.lin_weixin);
        lin_duanxin = view.findViewById(R.id.lin_duanxin);
        lin_QQ = view.findViewById(R.id.lin_QQ);
        tv_content = view.findViewById(R.id.tv_content);

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);

        getData();
        initView();
    }

    public void getData(){
        //todo 需要的数据的获取  是否获取到分享的行程
        if (true){
            lin_have_content.setVisibility(View.VISIBLE);
            lin_no_content.setVisibility(View.GONE);
        }else {
            lin_have_content.setVisibility(View.GONE);
            lin_no_content.setVisibility(View.VISIBLE);
        }
    }

    public void initView(){
        btn_call_car.setOnClickListener(v -> {
            dialog.dismiss();
        });
        lin_weixin.setOnClickListener(v -> {
            dialog.dismiss();
        });
        lin_duanxin.setOnClickListener(v -> {
            dialog.dismiss();
        });
        lin_QQ.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public void show(){
        dialog.show();
    }
}
