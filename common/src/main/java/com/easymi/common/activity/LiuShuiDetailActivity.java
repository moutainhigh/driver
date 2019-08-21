package com.easymi.common.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.BaseOrder;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LiuShuiDetailActivity extends RxBaseActivity {

    private CusToolbar liuShuiDetailCtb;
    private TextView liuShuiDetailTvPrise;
    private TextView liuShuiDetailTvCount;
    private TextView liuShuiDetailTvId;
    private TextView liuShuiDetailTvTime;
    private TextView liuShuiDetailTvName;
    private TextView liuShuiDetailTvOnPlace;
    private TextView liuShuiDetailTvOffPlace;
    private long orderId;
    private LinearLayout liuShuiDetailLlName;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_liu_shui_detail;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        liuShuiDetailLlName = findViewById(R.id.liuShuiDetailLlName);
        liuShuiDetailCtb = findViewById(R.id.liuShuiDetailCtb);
        liuShuiDetailTvPrise = findViewById(R.id.liuShuiDetailTvPrise);
        liuShuiDetailTvCount = findViewById(R.id.liuShuiDetailTvCount);
        liuShuiDetailTvId = findViewById(R.id.liuShuiDetailTvId);
        liuShuiDetailTvTime = findViewById(R.id.liuShuiDetailTvTime);
        liuShuiDetailTvName = findViewById(R.id.liuShuiDetailTvName);
        liuShuiDetailTvOnPlace = findViewById(R.id.liuShuiDetailTvOnPlace);
        liuShuiDetailTvOffPlace = findViewById(R.id.liuShuiDetailTvOffPlace);

        orderId = getIntent().getLongExtra("orderId", -1);
        if (orderId == -1) {
            ToastUtil.showMessage(this, "数据发生错误,请重试");
        }
        getData();
    }


    @Override
    public void initToolBar() {
        super.initToolBar();
        liuShuiDetailCtb.setTitle("流水详情")
                .setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    private void getData() {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getOrderInfo(orderId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<BaseOrder>(this, true, false, new HaveErrSubscriberListener<BaseOrder>() {
                    @Override
                    public void onNext(BaseOrder baseOrder) {
                        liuShuiDetailTvId.setText(baseOrder.orderNo);
                        liuShuiDetailTvTime.setText(String.valueOf(baseOrder.bookTime));
                        if (TextUtils.isEmpty(baseOrder.passengerName)) {
                            liuShuiDetailLlName.setVisibility(View.GONE);
                        } else {
                            liuShuiDetailLlName.setVisibility(View.VISIBLE);
                            liuShuiDetailTvName.setText(baseOrder.passengerName);
                        }
                        liuShuiDetailTvOnPlace.setText(baseOrder.bookAddress);
                        liuShuiDetailTvOffPlace.setText(baseOrder.destination);
                        if (TextUtils.isEmpty(baseOrder.ticketNumber)) {
                            liuShuiDetailTvCount.setVisibility(View.GONE);
                        } else {
                            liuShuiDetailTvCount.setText("乘车人数: " + baseOrder.ticketNumber);
                        }
                        liuShuiDetailTvPrise.setText("¥" + baseOrder.budgetFee);
                    }

                    @Override
                    public void onError(int code) {
                        finish();
                    }
                }));

    }


}
