package com.easymi.personal.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
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
        apply.setOnClickListener(view -> apply());
    }

    private void apply() {
        double money = 0.0;
        try {
            money = Double.parseDouble(editMoney.getText().toString());
        } catch (Exception e) {
            money = 0.0;
        }
        String name = bankName.getText().toString();
        String no = bankNo.getText().toString();
        String owner = bankOwner.getText().toString();

        if(money == 0.0){
            ToastUtil.showMessage(TixianActivity.this,getString(R.string.please_money));
            return;
        } //TODO 提现最小金额 提现金额 整数倍
        if (StringUtils.isBlank(name)){
            ToastUtil.showMessage(TixianActivity.this,getString(R.string.please_bank_name));
            return;
        }
        if(StringUtils.isBlank(no)){
            ToastUtil.showMessage(TixianActivity.this,getString(R.string.please_bank_number));
            return;
        }
        if(StringUtils.isBlank(owner)){
            ToastUtil.showMessage(TixianActivity.this,getString(R.string.please_bank_owner));
            return;
        }
    }

    @Override
    public void initToolBar() {
        cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle(R.string.tixian_title);
    }
}
