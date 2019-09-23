package com.easymin.custombus.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.common.entity.ManualConfigBean;
import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.switchButton.SwitchButton;
import com.easymin.custombus.DZBusApiService;
import com.easymin.custombus.R;
import com.easymin.custombus.dialog.DateSelectDialog;
import com.easymin.custombus.dialog.ManualCreateDialog;
import com.easymin.custombus.dialog.TimePickerDialog;
import com.easymin.custombus.entity.ManualCreateLineBean;
import com.easymin.custombus.entity.TimeBean;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Route(path = "/custombus/ManualCreateActivity")
public class ManualCreateActivity extends RxBaseActivity {

    private CusToolbar manualCreateCtb;
    private TextView manualCreateTvLineSelect;
    private TextView manualCreateTvDateSelect;
    private TextView manualCreateTvTimeSelect;
    private SwitchButton manualCreateSb;
    private TextView manualCreateTvCreate;
    private ManualCreateDialog dialog;
    private int day;
    private FrameLayout manualCreateFl;
    private int isShow;
    private ManualCreateLineBean manualCreateLineBean;
    private long ChooseDayTimeMillSeconds;
    private long chooseHourTimeMillSecond;
    private List<TimeBean> timeBeans;

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
        manualCreateSb.setChecked(true);
        manualCreateFl = findViewById(R.id.manualCreateFl);
        String content = XApp.getMyPreferences().getString(Config.SP_MANUAL_DATA, "");
        timeBeans = new ArrayList<>();
        if (!TextUtils.isEmpty(content)) {
            ManualConfigBean manualConfigBean = new Gson().fromJson(content, ManualConfigBean.class);
            day = manualConfigBean.day;
            day--;
            for (int i = 0; i < day; i++) {
                timeBeans.add(new TimeBean(i));
            }
            isShow = manualConfigBean.isShow;
            manualCreateFl.setVisibility(isShow == 1 ? View.VISIBLE : View.GONE);
        } else {
            ToastUtil.showMessage(this, "数据发生错误");
            finish();
        }
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
            getDataList();
        } else if (v.getId() == R.id.manualCreateTvDateSelect) {
            showDatePick();
        } else if (v.getId() == R.id.manualCreateTvTimeSelect) {
            if (ChooseDayTimeMillSeconds == 0) {
                ToastUtil.showMessage(this, "请先选择发车日期");
                return;
            }
            showTimePicker();
        } else if (v.getId() == R.id.manualCreateTvCreate) {
            createManualOrder();
        }
    }

    private void showDatePick() {
        DateSelectDialog dateSelectDialog = new DateSelectDialog(this, timeBeans, new DateSelectDialog.OnDateSelectDialogClickListener() {
            @Override
            public void onClick(TimeBean content) {
                ChooseDayTimeMillSeconds = content.getTimeStamp();
                manualCreateTvDateSelect.setText(TimeUtil.getTime("yyyy-MM-dd", content.getTimeStamp()));
                manualCreateTvTimeSelect.setText("");
                chooseHourTimeMillSecond = 0;
            }
        });
        dateSelectDialog.show();
    }

    private void createManualOrder() {
        if (manualCreateLineBean == null) {
            ToastUtil.showMessage(this, "请先选择线路");
            return;
        }
        if (TextUtils.isEmpty(manualCreateTvDateSelect.getText())) {
            ToastUtil.showMessage(this, "请先选择发车日期");
            return;
        }
        if (TextUtils.isEmpty(manualCreateTvTimeSelect.getText())) {
            ToastUtil.showMessage(this, "请先选择发车时间");
            return;
        }
        Map<String, String> data = new HashMap<>();

        data.put("lineId", String.valueOf(manualCreateLineBean.id));
        data.put("lineName", manualCreateLineBean.name);
        data.put("driverId", String.valueOf(EmUtil.getEmployId()));
        data.put("driverName", EmUtil.getEmployInfo().realName);
        data.put("driverPhone", EmUtil.getEmployInfo().phone);
        data.put("day", manualCreateTvDateSelect.getText().toString());
        data.put("hour", manualCreateTvTimeSelect.getText().toString());
        data.put("endTimeType", String.valueOf(1));
        data.put("isShow", String.valueOf(manualCreateSb.isChecked() ? 1 : 2));

        ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .manualOrderCreate(data)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<EmResult>(this, true, false, new NoErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        if (emResult.getCode() == 1) {
                            ToastUtil.showMessage(ManualCreateActivity.this, "报班成功");
                            finish();
                        }
                    }
                }));

    }

    private void getDataList() {
        ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .findLineListByDriverId(EmUtil.getEmployId())
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<List<ManualCreateLineBean>>
                        (this, true, false, new NoErrSubscriberListener<List<ManualCreateLineBean>>() {
                            @Override
                            public void onNext(List<ManualCreateLineBean> manualCreateLineBeans) {
                                if (!manualCreateLineBeans.isEmpty()) {
                                    showDialog(manualCreateLineBeans);
                                } else {
                                    ToastUtil.showMessage(ManualCreateActivity.this, "数据发生错误,请重试");
                                }
                            }
                        }));
    }

    private void showDialog(List<ManualCreateLineBean> manualCreateLineBeans) {
        if (dialog == null) {
            dialog = new ManualCreateDialog(this, manualCreateLineBeans, manualCreateLineBean -> {
                this.manualCreateLineBean = manualCreateLineBean;
                manualCreateTvLineSelect.setText(manualCreateLineBean.name);
            });
        }
        if (dialog.isShowing()) {
            dialog.dismiss();
        }
        dialog.show();
    }

    public void showTimePicker() {

        Calendar chooseCalendar = Calendar.getInstance();
        chooseCalendar.setTimeInMillis(ChooseDayTimeMillSeconds);
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.setTimeInMillis(System.currentTimeMillis());
        int chooseDayInYear = chooseCalendar.get(Calendar.DAY_OF_YEAR);
        int currentDayInYear = currentCalendar.get(Calendar.DAY_OF_YEAR);

        if (chooseDayInYear == currentDayInYear
                && currentCalendar.get(Calendar.HOUR_OF_DAY) == 23
                && currentCalendar.get(Calendar.MINUTE) >= 55) {
            ToastUtil.showMessage(this, "无可选时间,请选择其他日期");
            return;
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, ChooseDayTimeMillSeconds, chooseHourTimeMillSecond,
                chooseDayInYear == currentDayInYear);
        timePickerDialog.setOnSelectListener(new TimePickerDialog.OnSelectListener() {
            @Override
            public void onSelect(long time, String timeStr) {
                chooseHourTimeMillSecond = time;
                manualCreateTvTimeSelect.setText(timeStr.replace("点", ":").replace("分", ""));
            }
        });
        timePickerDialog.show();
    }
}
