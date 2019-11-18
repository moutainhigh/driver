package com.easymi.personal.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.personal.R;
import com.easymi.personal.entity.MyPopularizeRecordBean;

public class MyPopularizeProcessActivity extends RxBaseActivity {

    private com.easymi.component.widget.CusToolbar myPopularizeProcessCtb;
    private android.widget.TextView myPopularizeProcessTvTime;
    private android.widget.TextView myPopularizeProcessTvPrize;
    private android.widget.TextView myPopularizeProcessTvStatus;
    private android.widget.TextView myPopularizeProcessTvDoingTime;
    private LinearLayout myPopularizeProcessLlReply;
    private android.widget.TextView myPopularizeProcessTvReply;
    private LinearLayout myPopularizeProcessLlDoingTime;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_my_popularize_process;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        myPopularizeProcessTvTime = findViewById(R.id.myPopularizeProcessTvTime);
        myPopularizeProcessTvPrize = findViewById(R.id.myPopularizeProcessTvPrize);
        myPopularizeProcessTvStatus = findViewById(R.id.myPopularizeProcessTvStatus);
        myPopularizeProcessTvDoingTime = findViewById(R.id.myPopularizeProcessTvDoingTime);
        myPopularizeProcessLlDoingTime = findViewById(R.id.myPopularizeProcessLlDoingTime);
        myPopularizeProcessLlReply = findViewById(R.id.myPopularizeProcessLlReply);
        myPopularizeProcessTvReply = findViewById(R.id.myPopularizeProcessTvReply);
        MyPopularizeRecordBean myPopularizeRecordBean = (MyPopularizeRecordBean) getIntent().getSerializableExtra("data");

        if (myPopularizeRecordBean == null) {
            ToastUtil.showMessage(this, "数据出现错误,请重试...");
            finish();
        } else {
            myPopularizeProcessTvTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", myPopularizeRecordBean.created * 1000));
            myPopularizeProcessTvPrize.setText("¥" + myPopularizeRecordBean.settlementTotal);
            myPopularizeProcessTvStatus.setText(myPopularizeRecordBean.status == 1 ? "审核中" : "已通过");
            myPopularizeProcessLlDoingTime.setVisibility(myPopularizeRecordBean.auditTime > 0 ? View.VISIBLE : View.GONE);
            myPopularizeProcessTvDoingTime.setText(TimeUtil.getTime("yyyy-MM-dd HH:mm", myPopularizeRecordBean.auditTime * 1000));
            myPopularizeProcessLlReply.setVisibility(TextUtils.isEmpty(myPopularizeRecordBean.remark) ? View.GONE : View.VISIBLE);
            myPopularizeProcessTvReply.setText(myPopularizeRecordBean.remark);
        }
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        myPopularizeProcessCtb = findViewById(R.id.myPopularizeProcessCtb);
        myPopularizeProcessCtb.setTitle("进度详情").setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
