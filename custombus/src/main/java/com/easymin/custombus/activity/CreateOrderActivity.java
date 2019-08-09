package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.easymi.component.Config;
import com.easymi.component.base.RxPayActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymin.custombus.DZBusApiService;
import com.easymin.custombus.R;
import com.easymin.custombus.dialog.StationDialog;
import com.easymin.custombus.entity.DZBusLine;
import com.easymin.custombus.entity.StationBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Route(path = "/custombus/CreateOrderActivity")
public class CreateOrderActivity extends RxPayActivity {

    TextView banci_select;
    TextView start_place;
    TextView end_place;
    EditText edit_phone;
    TextView sub;
    TextView add;
    TextView num;
    TextView money;
    Button create_order;
    LinearLayout money_con;

    LinearLayout create_suc_con;
    TextView hint_1;
    Button btn;

    private DZBusLine dzBusLine;
    private List<StationBean> stationList;
    private StationBean startBean;
    private StationBean endBean;
    private int currentNum;
    private double prise;
    private DecimalFormat decimalFormat;
    private boolean isNumberSuccess;
    private long orderId;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_dzbus_create_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        currentNum = 1;
        banci_select = findViewById(R.id.banci_select);
        start_place = findViewById(R.id.start_place);
        end_place = findViewById(R.id.end_place);
        edit_phone = findViewById(R.id.edit_phone);
        sub = findViewById(R.id.sub);
        add = findViewById(R.id.add);
        num = findViewById(R.id.num);
        money = findViewById(R.id.money);
        create_order = findViewById(R.id.create_order);
        money_con = findViewById(R.id.money_con);

        create_suc_con = findViewById(R.id.create_suc);
        hint_1 = findViewById(R.id.hint_1);
        hint_1.setText("代付成功");
        TextView count_down = findViewById(R.id.count_down);
        count_down.setText("请提醒乘客查看订单信息");
        btn = findViewById(R.id.btn);
        btn.setText("返回我的订单");
        btn.setOnClickListener(view -> finish());
        stationList = new ArrayList<>();
        decimalFormat = new DecimalFormat("0.00");
        banci_select.setOnClickListener(view -> {
            Intent intent = new Intent(CreateOrderActivity.this, BanciSelectActivity.class);
            startActivityForResult(intent, 0);
        });

        start_place.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            checkList(true);
        });
        end_place.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            checkList(false);
        });

        edit_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 11) {
                    isNumberSuccess = true;
                    checkMoney();
                } else {
                    isNumberSuccess = false;
                }
            }
        });

        sub.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            currentNum--;
            num.setText(currentNum + "");
            add.setEnabled(true);
            if (currentNum == 1) {
                sub.setEnabled(false);
            }
            checkMoney();
        });

        add.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            currentNum++;
            num.setText(currentNum + "");
            sub.setEnabled(true);
            if (dzBusLine.restrict != 1 && dzBusLine.seats <= currentNum) {
                add.setEnabled(false);
            }
            checkMoney();
        });

        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
    }

    private void getStation(boolean isStart) {
        ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .queryStation(dzBusLine.lineId, dzBusLine.id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<List<StationBean>>(this, true, false, new NoErrSubscriberListener<List<StationBean>>() {
                    @Override
                    public void onNext(List<StationBean> dzBusStaion) {
                        stationList = dzBusStaion;
                        showDialog(isStart);
                    }
                }));
    }

    private void checkList(boolean isStart) {
        if (stationList == null || stationList.isEmpty()) {
            getStation(isStart);
        } else {
            showDialog(isStart);
        }
    }

    private void showDialog(boolean isStart) {
        StationDialog bottomSheetDialog = new StationDialog(this, stationList, isStart, startBean, endBean);
        bottomSheetDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (StationBean stationBean : stationList) {
                    if (stationBean.chooseStatus == 1) {
                        startBean = stationBean;
                        endBean = null;
                        end_place.setText("");
                        start_place.setText(startBean.name);
                    } else if (stationBean.chooseStatus == 2) {
                        endBean = stationBean;
                        end_place.setText(endBean.name);
                    }
                }
                checkMoney();
            }
        });
        bottomSheetDialog.show();
    }

    private void checkMoney() {
        if (startBean != null && endBean != null && dzBusLine != null) {
            //一口价
            if (dzBusLine.throughTicket == 1) {
                prise = dzBusLine.throughMoney;
            } else {
                //计算价钱
                int startIndex = startBean.sequence - 1;
                int endIndex = endBean.sequence - 1;
                prise = 0;
                for (int i = 0; i < stationList.size(); i++) {
                    if (i >= startIndex && i < endIndex) {
                        prise += stationList.get(i).ticket;
                    }
                }
            }
            money.setText(decimalFormat.format(prise * currentNum));
        } else {
            money.setText(decimalFormat.format(0));
        }

        if (isNumberSuccess) {
            create_order.setEnabled(true);
            create_order.setBackgroundResource(R.drawable.corners_button_selector);
        } else {
            create_order.setEnabled(false);
            create_order.setBackgroundResource(R.drawable.corners_button_press_bg);
        }
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.cus_toolbar);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle("司机补单");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                DZBusLine newDzBusLine = (DZBusLine) data.getSerializableExtra("data");
                if (dzBusLine == null || dzBusLine.id != newDzBusLine.id) {
                    dzBusLine = newDzBusLine;
                    stationList.clear();
                    banci_select.setText(dzBusLine.lineName);
                    start_place.setText("");
                    end_place.setText("");
                    startBean = null;
                    endBean = null;
                    currentNum = 1;
                    num.setText("" + currentNum);
                    sub.setEnabled(false);
                    if (dzBusLine.restrict == 1) {
                        add.setEnabled(true);
                    } else {
                        if (dzBusLine.seats > 1) {
                            add.setEnabled(true);
                        } else {
                            add.setEnabled(false);
                        }
                    }
                    checkMoney();
                }
            }
        }
    }

    /**
     * 创建订单
     */
    private void createOrder() {
        if (edit_phone.getText().length() != 11) {
            ToastUtil.showMessage(this, "请输入正确的电话号码");
            return;
        }

        if (orderId == 0) {
            Observable<Long> observable = ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                    .createOrder(
                            startBean.stationId,
                            endBean.stationId,
                            dzBusLine.id,
                            currentNum,
                            edit_phone.getText().toString(),
                            "driver"
                    )
                    .map(new HttpResultFunc2<>())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());

            mRxManager.add(observable.subscribe(new MySubscriber<Long>(this,
                    true,
                    false, new NoErrSubscriberListener<Long>() {
                @Override
                public void onNext(Long aLong) {
                    orderId = aLong;
                    showDialog(orderId);
                }
            })));
        } else {
            showDialog(orderId);
        }
    }


    @Override
    public void onPaySuc() {
        create_suc_con.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPayFail() {
        ToastUtil.showMessage(this, "支付失败,请重试");
    }
}