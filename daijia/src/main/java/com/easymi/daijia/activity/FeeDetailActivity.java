package com.easymi.daijia.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.daijia.R;

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

    @Override
    public int getLayoutId() {
        return R.layout.activity_fee_detail;
    }

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
    }
}
