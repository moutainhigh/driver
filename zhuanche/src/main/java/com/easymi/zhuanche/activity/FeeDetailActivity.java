package com.easymin.official.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.network.GsonUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.official.R;
import com.easymin.official.entity.GovOrder;
import com.easymin.official.entity.StageArrays;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FeeDetailActivity
 * @Author: hufeng
 * @Date: 2019/3/29 上午11:25
 * @Description:
 * @History:
 */
public class FeeDetailActivity extends RxBaseActivity {

    TextView startFee;
    TextView distance;
    TextView distanceFee;
    TextView time;
    TextView timeFee;
    TextView waitTime;
    TextView waitFee;
    TextView couponFee;

    TextView couponType;

    TextView totalFee;

    TextView prepayFee;
    RelativeLayout couponFeeCon;

    RelativeLayout minestFeeCon;
    TextView minestFeeText;

    TextView extraFee;
    TextView paymentFee;

    CusToolbar cusToolbar;

    private DymOrder dymOrder;
    private GovOrder govOrder;

    LinearLayout lin_jieti;

    List<StageArrays> stages = new ArrayList<>();

    @Override
    public int getLayoutId() {
        return R.layout.gw_activity_fee_detail;
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.fee_detail);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void initViews(Bundle savedInstanceState) {
        startFee = findViewById(R.id.start_fee);
        distance = findViewById(R.id.distance);
        distanceFee = findViewById(R.id.distance_fee);
        time = findViewById(R.id.time);
        timeFee = findViewById(R.id.time_fee);
        waitTime = findViewById(R.id.wait_time);
        waitFee = findViewById(R.id.wait_fee);
        couponFee = findViewById(R.id.coupon_fee);
        totalFee = findViewById(R.id.total_fee);
        prepayFee = findViewById(R.id.prepay_fee);
        couponType = findViewById(R.id.coupon_type);

        couponFeeCon = findViewById(R.id.coupon_con);

        extraFee = findViewById(R.id.extra_fee);
        paymentFee = findViewById(R.id.help_pay_fee);

        minestFeeCon = findViewById(R.id.minest_fee_con);
        minestFeeText = findViewById(R.id.minest_fee);

        TextView height_pay_fee = findViewById(R.id.height_pay_fee);
        TextView height_fee_title = findViewById(R.id.height_fee_title);

        TextView night_fee_title = findViewById(R.id.night_fee_title);
        TextView night_pay_fee = findViewById(R.id.night_pay_fee);

        TextView night_time_fee_title = findViewById(R.id.night_time_fee_title);
        TextView night_time_fee = findViewById(R.id.night_time_fee);


        TextView low_fee_title = findViewById(R.id.low_fee_title);
        TextView low_pay_fee = findViewById(R.id.low_pay_fee);

        lin_jieti = findViewById(R.id.lin_jieti);


        dymOrder = (DymOrder) getIntent().getSerializableExtra("dymOrder");
        govOrder = (GovOrder) getIntent().getSerializableExtra("govOrder");
        if (dymOrder == null || govOrder == null) {
            finish();
            return;
        }

        prepayFee.setText(dymOrder.prepay + getString(R.string.yuan));
        startFee.setText(dymOrder.startFee + getString(R.string.yuan));
        distance.setText("（" + dymOrder.distance + getString(R.string.k_k_meter) + "）");
        distanceFee.setText(dymOrder.disFee + getString(R.string.yuan));
        time.setText("（" + dymOrder.travelTime + getString(R.string.minutes) + "）");
        timeFee.setText(dymOrder.travelFee + getString(R.string.yuan));
        waitTime.setText("（" + dymOrder.waitTime + getString(R.string.minutes) + "）");
        waitFee.setText(dymOrder.waitTimeFee + getString(R.string.yuan));

        extraFee.setText(dymOrder.extraFee + getString(R.string.yuan));
        paymentFee.setText(dymOrder.paymentFee + getString(R.string.yuan));

        if (govOrder.coupon != null && (govOrder.coupon.couponType == 1 || govOrder.coupon.couponType == 2)) {
            if (govOrder.coupon.couponType == 2) {
                couponType.setText("（" + govOrder.coupon.deductible + getString(R.string.xianjin_coupon) + "）");
            } else {
                DecimalFormat df = new DecimalFormat("#0.0");
                couponType.setText("（" + df.format(govOrder.coupon.discount / 10) + getString(R.string.zhekou_coupon) + "）");
            }
            couponFee.setText(dymOrder.couponFee + getString(R.string.yuan));
        }

        if (dymOrder.minestMoney != 0) {
            minestFeeCon.setVisibility(View.VISIBLE);
            minestFeeText.setText(String.valueOf(dymOrder.minestMoney) + getString(R.string.yuan));
        }

        height_fee_title.setText("高峰费(" + new DecimalFormat("#0.00").format(dymOrder.peakMile / 1000) + "公里)");
        height_pay_fee.setText(dymOrder.peakCost + getString(R.string.yuan));

        night_fee_title.setText("夜间里程费(" + new DecimalFormat("#0.00").format(dymOrder.nightMile / 1000) + "公里)");
        night_pay_fee.setText(dymOrder.nightMileFee + getString(R.string.yuan));

        night_time_fee_title.setText("夜间时间费(" + dymOrder.nightTime + "分钟)");
        night_time_fee.setText(dymOrder.nightTimePrice + "元");

        low_fee_title.setText("低速费(" + dymOrder.lowSpeedTime + "分钟)");
        low_pay_fee.setText(dymOrder.lowSpeedCost + getString(R.string.yuan));

        totalFee.setText(getString(R.string.money_sign) + dymOrder.totalFee);

        stages = GsonUtil.parseToArrayList(dymOrder.stageArrays, StageArrays.class);

        if (stages != null && stages.size() != 0){
            for (StageArrays arrays : stages){
                View view = LayoutInflater.from(this).inflate(R.layout.gw_item_stages,null);
                TextView distance = view.findViewById(R.id.tv_distance);
                TextView money = view.findViewById(R.id.tv_money);

                distance.setText(getResources().getString(R.string.zc_jietifei)+arrays.num+"("+arrays.longRange+")"+getResources().getString(R.string.km));
                money.setText(new DecimalFormat("#0.00").format(arrays.rangePrice)+getResources().getString(R.string.yuan));
                lin_jieti.addView(view);
            }
        }
    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
