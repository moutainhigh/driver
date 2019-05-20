package com.easymin.driver.securitycenter.dialog;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easymin.driver.securitycenter.CenterConfig;
import com.easymin.driver.securitycenter.ComService;
import com.easymin.driver.securitycenter.R;
import com.easymin.driver.securitycenter.activity.CallPoliceActivity;
import com.easymin.driver.securitycenter.activity.EmergeActivity;
import com.easymin.driver.securitycenter.activity.LuyinActivity;
import com.easymin.driver.securitycenter.activity.WebActivity;
import com.easymin.driver.securitycenter.network.ApiManager;
import com.easymin.driver.securitycenter.network.HttpResultFunc;
import com.easymin.driver.securitycenter.network.MySubscriber;
import com.easymin.driver.securitycenter.result.EmResult;
import com.easymin.driver.securitycenter.rxmvp.RxManager;
import com.easymin.driver.securitycenter.utils.ToastUtil;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: MainDialog
 *@Author: shine
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

    private Context mContext;
    private CusBottomSheetDialog dialog;
    private int soundRecordCheck;
    private int emergeContackCheck;


    public MainDialog(Context context, long passengerId, String appKey,String aeskey,String token,int orderId,String driver,String passengerPhone) {
        this.mContext = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_main, null, false);

        iv_colse = view.findViewById(R.id.iv_colse);
        lin_line_share = view.findViewById(R.id.lin_line_share);
        lin_baojin = view.findViewById(R.id.lin_baojin);
        lin_luyin = view.findViewById(R.id.lin_luyin);
        tv_luyin_status = view.findViewById(R.id.tv_luyin_status);
        lin_lianxiren = view.findViewById(R.id.lin_lianxiren);
        tv_lianxiren_status = view.findViewById(R.id.tv_lianxiren_status);
        lin_decript = view.findViewById(R.id.lin_decript);

        CenterConfig.PASSENGERID = passengerId;
        CenterConfig.APPKEY = appKey;
        CenterConfig.AES_KEY = aeskey;
        CenterConfig.TOKEN = token;
        CenterConfig.ORDERID = orderId;
        CenterConfig.DRIVER = driver;
        CenterConfig.PASSENGERPHONE = passengerPhone;

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        initData(passengerId,appKey);
        initView();
    }

    public void initView() {
        iv_colse.setOnClickListener(v -> {
            dialog.dismiss();
        });
        //行程分享
        lin_line_share.setOnClickListener(v -> {
            LineShareDialog shareDialog = new LineShareDialog(mContext);
            shareDialog.show();
            dialog.dismiss();
        });
        //一键报警
        lin_baojin.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, CallPoliceActivity.class);
            mContext.startActivity(intent);
            dialog.dismiss();
        });

        //录音授权
        lin_luyin.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, LuyinActivity.class);
            intent.putExtra("soundRecordCheck",soundRecordCheck);
            mContext.startActivity(intent);
            dialog.dismiss();
        });

        //联系人设置
        lin_lianxiren.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, EmergeActivity.class);
            intent.putExtra("emergeContackCheck",emergeContackCheck);
            mContext.startActivity(intent);
            dialog.dismiss();
        });

        //安全中心描述
        lin_decript.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, WebActivity.class);
            intent.putExtra("url", "http://h5.xiaokakj.com/#/protocol?articleName=passengerSafetyCenter&appKey="+CenterConfig.APPKEY);
            intent.putExtra("title", "安全中心说明");
            mContext.startActivity(intent);
            dialog.dismiss();
        });
    }

    public void show() {
        dialog.show();
    }

    public void initData(long passengerId,String appKey) {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .checkingAuth(passengerId,appKey)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(mContext, true,
                true, emResult -> {
            if (emResult.getCode() == 1){
                soundRecordCheck = emResult.soundRecordCheck;
                emergeContackCheck = emResult.emergeContackCheck;

                if (emResult.soundRecordCheck != 0){
                    tv_luyin_status.setText("已授权");
                    tv_luyin_status.setTextColor(mContext.getResources().getColor(R.color.color_666666));
                }else {
                    tv_luyin_status.setText("未授权");
                    tv_luyin_status.setTextColor(mContext.getResources().getColor(R.color.color_red));
                }
                if (emResult.emergeContackCheck != 0){
                    tv_lianxiren_status.setText("已绑定");
                    tv_lianxiren_status.setTextColor(mContext.getResources().getColor(R.color.color_666666));
                }else {
                    tv_lianxiren_status.setText("未绑定");
                    tv_lianxiren_status.setTextColor(mContext.getResources().getColor(R.color.color_red));
                }
                dialog.show();
            }else {
                ToastUtil.showMessage(mContext,emResult.getMessage(),Toast.LENGTH_LONG);
            }
        })));
    }
}
