package com.easymi.daijia.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.daijia.R;

/**
 * Created by developerLzh on 2017/11/22 0022.
 */

public class CancelActivity extends RxBaseActivity {

    CheckBox checkBox1;
    CheckBox checkBox2;
    CheckBox checkBox3;
    CheckBox checkBox4;
    CheckBox checkBox5;

    private int checkId = 1;

    @Override
    public int getLayoutId() {
        return R.layout.activity_cancel_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        checkBox1 = findViewById(R.id.check_1);
        checkBox2 = findViewById(R.id.check_2);
        checkBox3 = findViewById(R.id.check_3);
        checkBox4 = findViewById(R.id.check_4);
        checkBox5 = findViewById(R.id.check_5);

        checkBox1.setOnCheckedChangeListener(new MyCheckChange(1));
        checkBox2.setOnCheckedChangeListener(new MyCheckChange(2));
        checkBox3.setOnCheckedChangeListener(new MyCheckChange(3));
        checkBox4.setOnCheckedChangeListener(new MyCheckChange(4));
        checkBox5.setOnCheckedChangeListener(new MyCheckChange(5));
    }

    class MyCheckChange implements CheckBox.OnCheckedChangeListener {

        int id;

        public MyCheckChange(int id) {
            this.id = id;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                checkId = id;
                checkBox1.setChecked(false);
                checkBox2.setChecked(false);
                checkBox3.setChecked(false);
                checkBox4.setChecked(false);
                checkBox5.setChecked(false);

                buttonView.setChecked(true);
            }
        }
    }

    public void cancel_cancel(View view) {
        finish();
    }

    public void cancel_order(View view) {
        Intent intent = new Intent();
        intent.putExtra("reasonId", checkId);
        setResult(RESULT_OK, intent);
    }
}
