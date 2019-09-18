package com.easymin.custombus.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.switchButton.SwitchButton;
import com.easymin.custombus.R;
import com.easymin.custombus.dialog.ManualCreateDialog;
import com.jzxiang.pickerview.TimePickerDialog;
import com.jzxiang.pickerview.data.Type;

import java.util.ArrayList;
import java.util.List;

public class ManualCreateActivity extends RxBaseActivity {

    private CusToolbar manualCreateCtb;
    private TextView manualCreateTvLineSelect;
    private TextView manualCreateTvDateSelect;
    private TextView manualCreateTvTimeSelect;
    private SwitchButton manualCreateSb;
    private TextView manualCreateTvCreate;
    private ManualCreateDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_manual_create;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        manualCreateTvLineSelect = findViewById(R.id.manualCreateTvLineSelect);
        manualCreateTvDateSelect = findViewById(R.id.manualCreateTvDateSelect);
        manualCreateTvTimeSelect = findViewById(R.id.manualCreateTvTimeSelect);
        manualCreateTvCreate = findViewById(R.id.manualCreateTvCreate);
        manualCreateSb = findViewById(R.id.manualCreateSb);
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        manualCreateCtb = findViewById(R.id.manualCreateCtb);
        manualCreateCtb.setTitle("手动报班")
                .setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    public void onManualCreateClick(View v) {
        if (v.getId() == R.id.manualCreateTvLineSelect) {
            showDialog();
        } else if (v.getId() == R.id.manualCreateTvDateSelect) {
            showTimePicker(Type.YEAR_MONTH_DAY);
        } else if (v.getId() == R.id.manualCreateTvTimeSelect) {
            showTimePicker(Type.HOURS_MINS);
        }
    }

    private void showDialog() {
        List<String> mList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            mList.add("position" + i);
        }

        if (dialog == null) {
            dialog = new ManualCreateDialog(this, mList, str -> {
                manualCreateTvLineSelect.setText(str);
            });
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.show();
    }

    public void showTimePicker(Type type) {
        long sevenDay = 6;
        new TimePickerDialog.Builder()
                .setCallBack((timePickerView, millseconds) -> {
                    if (type == Type.YEAR_MONTH_DAY) {
                        manualCreateTvDateSelect.setText(TimeUtil.getTime("MM月dd日", millseconds));
                    } else {
                        manualCreateTvTimeSelect.setText(TimeUtil.getTime("HH:mm", millseconds));
                    }
                })
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId(type == Type.YEAR_MONTH_DAY ? "发车日期" : "发车时间")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + sevenDay * 24 * 60 * 60 * 1000)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(ContextCompat.getColor(this, R.color.colorBlue))
                .setType(type)
                .setWheelItemTextSize(12)
                .build()
                .show(getSupportFragmentManager(), "");
    }
}
