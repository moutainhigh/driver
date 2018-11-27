package cn.projcet.hf.securitycenter.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import cn.projcet.hf.securitycenter.R;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MainDialog
 * Author: shine
 * Date: 2018/11/26 下午7:16
 * Description:
 * History:
 */
public class MainDialog {

    ImageView iv_colse;
    LinearLayout lin_line_share;
    LinearLayout lin_baojin;
    LinearLayout lin_luyin;
    TextView tv_luyin_status;
    LinearLayout lin_lianxiren;
    TextView tv_lianxiren_status;
    LinearLayout lin_decript;
    TextView tv_descript_status;

    private Context context;
    private CusBottomSheetDialog dialog;

    public MainDialog(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_main, null, false);

        iv_colse = view.findViewById(R.id.iv_colse);
        lin_line_share = view.findViewById(R.id.lin_line_share);
        lin_baojin = view.findViewById(R.id.lin_baojin);
        lin_luyin = view.findViewById(R.id.lin_luyin);
        tv_luyin_status = view.findViewById(R.id.tv_luyin_status);
        lin_lianxiren = view.findViewById(R.id.lin_lianxiren);
        tv_lianxiren_status = view.findViewById(R.id.tv_lianxiren_status);
        lin_decript = view.findViewById(R.id.lin_decript);
        tv_descript_status = view.findViewById(R.id.tv_descript_status);

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        getData();
        initView();
    }

    public void getData(){
        //todo 需要的数据的获取
    }

    public void initView(){
        iv_colse.setOnClickListener(v -> {
            dialog.dismiss();
        });
        lin_line_share.setOnClickListener(v -> {

        });
        lin_baojin.setOnClickListener(v -> {

        });
        lin_luyin.setOnClickListener(v -> {

        });
        lin_lianxiren.setOnClickListener(v -> {

        });
        lin_decript.setOnClickListener(v -> {

        });
    }

    public void show(){
        dialog.show();
    }
}
