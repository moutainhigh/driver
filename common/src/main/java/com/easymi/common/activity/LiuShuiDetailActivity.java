package com.easymi.common.activity;

import android.os.Bundle;
import android.widget.TextView;

import com.easymi.common.R;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;

public class LiuShuiDetailActivity extends RxBaseActivity {

    private CusToolbar liuShuiDetailCtb;
    private TextView liuShuiDetailTvPrise;
    private TextView liuShuiDetailTvCount;
    private TextView liuShuiDetailTvId;
    private TextView liuShuiDetailTvTime;
    private TextView liuShuiDetailTvName;
    private TextView liuShuiDetailTvOnPlace;
    private TextView liuShuiDetailTvOffPlace;

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
        liuShuiDetailCtb = findViewById(R.id.liuShuiDetailCtb);
        liuShuiDetailTvPrise = findViewById(R.id.liuShuiDetailTvPrise);
        liuShuiDetailTvCount = findViewById(R.id.liuShuiDetailTvCount);
        liuShuiDetailTvId = findViewById(R.id.liuShuiDetailTvId);
        liuShuiDetailTvTime = findViewById(R.id.liuShuiDetailTvTime);
        liuShuiDetailTvName = findViewById(R.id.liuShuiDetailTvName);
        liuShuiDetailTvOnPlace = findViewById(R.id.liuShuiDetailTvOnPlace);
        liuShuiDetailTvOffPlace = findViewById(R.id.liuShuiDetailTvOffPlace);
    }

}
