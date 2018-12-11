package cn.projcet.hf.securitycenter.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.projcet.hf.securitycenter.CenterConfig;
import cn.projcet.hf.securitycenter.ComService;
import cn.projcet.hf.securitycenter.R;
import cn.projcet.hf.securitycenter.network.ApiManager;
import cn.projcet.hf.securitycenter.network.HttpResultFunc;
import cn.projcet.hf.securitycenter.network.MySubscriber;
import cn.projcet.hf.securitycenter.result.EmResult;
import cn.projcet.hf.securitycenter.rxmvp.RxManager;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    ImageView iv_close;

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
        iv_close = view.findViewById(R.id.iv_close);

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);

        getData();
        initView();
    }

    public void getData(){
        if (CenterConfig.ORDERID != 0){
            lin_have_content.setVisibility(View.VISIBLE);
            lin_no_content.setVisibility(View.GONE);
            //todo 如何展示司机相关的信息，以及点击相关分享如何返回到项目进行分享
            getContents();
        }else {
            lin_have_content.setVisibility(View.GONE);
            lin_no_content.setVisibility(View.VISIBLE);
        }
    }

    public void initView(){
        //我知道了
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
        iv_close.setOnClickListener(v -> {
            dialog.dismiss();
        });
    }

    public void show(){
        dialog.show();
    }


    public void getContents(){
        double lat = 0;
        double lng = 0;
        Observable<EmResult> observable = ApiManager.getInstance().createApi(CenterConfig.HOST, ComService.class)
                .shareContents(lat,lng,CenterConfig.ORDERID,CenterConfig.AES_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        new RxManager().add(observable.subscribe(new MySubscriber<>(context, true,
                true, emResult -> {
            if (emResult.getCode() == 1){
                //todo 分享的内容

            }else {
                Toast.makeText(context,emResult.getMessage(),Toast.LENGTH_LONG);
            }
        })));
    }
}
