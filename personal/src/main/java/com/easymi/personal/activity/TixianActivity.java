package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.widget.CusToolbar;
import com.easymi.personal.R;

/**
 * Created by developerLzh on 2017/11/11 0011.
 */

public class TixianActivity extends RxBaseActivity {

    CusToolbar cusToolbar;

    TextView balanceText;
    EditText editMoney;
    TextView maxTixian;
    EditText bankName;
    EditText bankNo;
    EditText bankOwner;
    Button apply;
    TextView tixianRule;
    TextView tixianRecord;

    @Override
    public int getLayoutId() {
        return R.layout.activity_tixian;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        balanceText = findViewById(R.id.balance_text);
        editMoney = findViewById(R.id.edit_money);
        maxTixian = findViewById(R.id.max_tixian);
        bankName = findViewById(R.id.edit_bank_name);
        bankNo = findViewById(R.id.edit_bank_no);
        bankOwner = findViewById(R.id.edit_bank_owner);
        apply = findViewById(R.id.apply);
        tixianRule = findViewById(R.id.tixian_rule);
        tixianRecord = findViewById(R.id.tixian_record);

        tixianRule.setOnClickListener(view -> startActivity(new Intent(TixianActivity.this, TixianRuleActivity.class)));
        tixianRecord.setOnClickListener(view -> startActivity(new Intent(TixianActivity.this, TixianRecordActivity.class)));
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.tixian_title);
    }
}
