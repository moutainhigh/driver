package com.easymi.daijia.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.entity.DymOrder;
import com.easymi.component.widget.CusToolbar;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJOrder;

import java.text.DecimalFormat;

/**
 * Created by developerLzh on 2017/11/28 0028.
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
    private DJOrder djOrder;

    @Override
    public int getLayoutId() {
        return R.layout.activity_fee_detail;
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

        dymOrder = (DymOrder) getIntent().getSerializableExtra("dymOrder");
        djOrder = (DJOrder) getIntent().getSerializableExtra("djOrder");
        if (dymOrder == null || djOrder == null) {
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

        if (djOrder.coupon != null && (djOrder.coupon.couponType == 1 || djOrder.coupon.couponType == 2)) {
            if (djOrder.coupon.couponType == 2) {
                couponType.setText("（" + djOrder.coupon.deductible + getString(R.string.xianjin_coupon) + "）");
            } else {
                DecimalFormat df = new DecimalFormat("#0.0");
                couponType.setText("（" + df.format(djOrder.coupon.discount / 10) + getString(R.string.zhekou_coupon) + "）");
            }
            couponFee.setText(dymOrder.couponFee + getString(R.string.yuan));
        }

        if (dymOrder.minestMoney != 0) {
            minestFeeCon.setVisibility(View.VISIBLE);
            minestFeeText.setText(String.valueOf(dymOrder.minestMoney) + getString(R.string.yuan));
        }

        totalFee.setText(getString(R.string.money_sign) + dymOrder.orderShouldPay);

    }

    @Override
    public boolean isEnableSwipe() {
        return true;
    }
}
