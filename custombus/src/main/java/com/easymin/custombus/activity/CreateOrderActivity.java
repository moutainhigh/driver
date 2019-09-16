package com.easymin.custombus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.easymi.common.CommApiService;
import com.easymi.common.activity.PassengerSelectActivity;
import com.easymi.common.activity.SeatSelectActivity;
import com.easymi.common.adapter.PassengerAdapter;
import com.easymi.common.entity.PassengerBean;
import com.easymi.common.entity.SeatBean;
import com.easymi.component.Config;
import com.easymi.component.base.RxPayActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeMenuLayout;
import com.easymin.custombus.DZBusApiService;
import com.easymin.custombus.R;
import com.easymin.custombus.dialog.StationDialog;
import com.easymin.custombus.entity.DZBusLine;
import com.easymin.custombus.entity.StationBean;
import com.easymin.custombus.entity.StationMainBean;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Route(path = "/custombus/CreateOrderActivity")
public class CreateOrderActivity extends RxPayActivity {

    TextView customBusCreateOrderTvLine;
    TextView customBusCreateOrderTvStart;
    TextView customBusCreateOrderTvEnd;
    EditText customBusCreateOrderEtPhone;
    TextView customBusCreateOrderTvSub;
    TextView customBusCreateOrderTvAdd;
    TextView customBusCreateOrderTvNum;
    TextView customBusCreateOrderTvMoney;
    Button customBusCreateOrderBtCreate;
    LinearLayout customBusCreateOrderLlMoney;

    LinearLayout customBusCreateOrderLlCreateSuc;
    TextView customBusCreateOrderTvCreateSucDesc;
    TextView customBusCreateOrderTvCreateSucCount;
    Button customBusCreateOrderBtCreateSuc;

    private DZBusLine dzBusLine;
    private List<StationBean> startList;
    private List<StationBean> endList;
    private StationBean startBean;
    private StationBean endBean;
    private int currentNum;
    private DecimalFormat decimalFormat;
    private long orderId;
    private boolean request;
    private double currentMoney;
    private Handler handler;
    private int time;
    private RecyclerView customBusCreateOrderRv;
    private PassengerAdapter adapter;
    private View passengerFooterView;
    private int currentModeType;
    private LinearLayout customBusCreateOrderLlCount;
    private long passengerId;
    private LinearLayout customBusCreateOrderLlSeatSelect;
    private TextView customBusCreateOrderTvSeatSelect;
    private ArrayList<SeatBean> chooseSeatList;

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
        currentMoney = -1;
        currentNum = 1;
        currentModeType = 1;
        customBusCreateOrderTvLine = findViewById(R.id.customBusCreateOrderTvLine);
        customBusCreateOrderTvStart = findViewById(R.id.customBusCreateOrderTvStart);
        customBusCreateOrderTvEnd = findViewById(R.id.customBusCreateOrderTvEnd);
        customBusCreateOrderEtPhone = findViewById(R.id.customBusCreateOrderEtPhone);
        customBusCreateOrderTvSub = findViewById(R.id.customBusCreateOrderTvSub);
        customBusCreateOrderTvAdd = findViewById(R.id.customBusCreateOrderTvAdd);
        customBusCreateOrderTvNum = findViewById(R.id.customBusCreateOrderTvNum);
        customBusCreateOrderTvMoney = findViewById(R.id.customBusCreateOrderTvMoney);
        customBusCreateOrderBtCreate = findViewById(R.id.customBusCreateOrderBtCreate);
        customBusCreateOrderLlMoney = findViewById(R.id.customBusCreateOrderLlMoney);
        customBusCreateOrderLlCount = findViewById(R.id.customBusCreateOrderLlCount);
        customBusCreateOrderLlCreateSuc = findViewById(R.id.create_suc);
        customBusCreateOrderTvCreateSucDesc = findViewById(R.id.hint_1);
        customBusCreateOrderTvCreateSucDesc.setText("代付成功");
        customBusCreateOrderLlSeatSelect = findViewById(R.id.customBusCreateOrderLvSeatSelect);
        customBusCreateOrderTvSeatSelect = findViewById(R.id.customBusCreateOrderTvSeatSelect);
        customBusCreateOrderTvSeatSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateOrderActivity.this, SeatSelectActivity.class);
                startActivityForResult(intent, 0x20);
            }
        });
        customBusCreateOrderTvCreateSucCount = findViewById(R.id.count_down);
        customBusCreateOrderBtCreateSuc = findViewById(R.id.btn);
        customBusCreateOrderBtCreateSuc.setText("返回我的订单");
        customBusCreateOrderBtCreateSuc.setOnClickListener(view -> finish());

        startList = new ArrayList<>();
        endList = new ArrayList<>();

        decimalFormat = new DecimalFormat("0.00");
        customBusCreateOrderTvLine.setOnClickListener(view -> {
            Intent intent = new Intent(CreateOrderActivity.this, BanciSelectActivity.class);
            startActivityForResult(intent, 0);
        });

        customBusCreateOrderTvStart.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            checkList(true);
        });
        customBusCreateOrderTvEnd.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            checkList(false);
        });

        customBusCreateOrderEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (currentModeType == 2) {
                    if (editable.length() == 11) {
                        getPassengerId();
                    } else {
                        passengerId = 0;
                    }
                } else {
                    passengerId = 0;
                }
                buttonAction();
            }
        });

        customBusCreateOrderTvSub.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            currentNum--;
            customBusCreateOrderTvNum.setText(currentNum + "");
            customBusCreateOrderTvAdd.setEnabled(true);
            if (currentNum == 1) {
                customBusCreateOrderTvSub.setEnabled(false);
            }
            checkMoney();
        });

        customBusCreateOrderTvAdd.setOnClickListener(view -> {
            if (dzBusLine == null) {
                ToastUtil.showMessage(this, "请先选择班次");
                return;
            }
            currentNum++;
            customBusCreateOrderTvNum.setText(currentNum + "");
            customBusCreateOrderTvSub.setEnabled(true);
            if (dzBusLine.restrict != 1 && dzBusLine.seats <= currentNum) {
                customBusCreateOrderTvAdd.setEnabled(false);
            }
            checkMoney();
        });

        customBusCreateOrderBtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
        setRecyclerView();
    }

    private void getPassengerId() {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getPassengerId(customBusCreateOrderEtPhone.getText().toString(), EmUtil.getEmployInfo().companyId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<Long>(this, true, false, new HaveErrSubscriberListener<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        if (aLong != null) {
                            passengerId = aLong.longValue();
                            buttonAction();
                        } else {
                            customBusCreateOrderEtPhone.setText("");
                        }
                    }

                    @Override
                    public void onError(int code) {
                        passengerId = 0;
                        customBusCreateOrderEtPhone.setText("");
                    }
                }));
    }

    private void setRecyclerView() {
        customBusCreateOrderRv = findViewById(R.id.customBusCreateOrderRv);
        customBusCreateOrderRv.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return adapter.getData().size() > 0;
            }
        });

        adapter = new PassengerAdapter(false);

        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == com.easymi.common.R.id.itemPassengerSelectContentTvDelete) {
                    adapter.getData().remove(adapter.getData().get(position));
                    if (adapter.getData().size() == 0 && passengerFooterView != null) {
                        adapter.removeFooterView(passengerFooterView);
                        passengerFooterView = null;
                    }
                    SwipeMenuLayout itemPassengerSelectContentSml = (SwipeMenuLayout)
                            adapter.getViewByPosition(customBusCreateOrderRv, position + adapter.getHeaderLayoutCount(), com.easymi.common.R.id.itemPassengerSelectContentSml);
                    itemPassengerSelectContentSml.quickClose();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter.setEmptyView(getPassengerView(true));
        adapter.setHeaderFooterEmpty(true, true);
        customBusCreateOrderRv.setAdapter(adapter);
    }

    public View getPassengerView(boolean isEmpty) {
        View emptyView = getLayoutInflater().inflate(com.easymi.common.R.layout.item_passenger_select_header, customBusCreateOrderRv, false);
        View itemPassengerSelectHeaderV = emptyView.findViewById(com.easymi.common.R.id.itemPassengerSelectHeaderV);
        if (!isEmpty) {
            itemPassengerSelectHeaderV.setVisibility(View.VISIBLE);
        }
        ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
        if (isEmpty) {
            layoutParams.height = DensityUtil.dp2px(this, 100);
        }
        emptyView.setLayoutParams(layoutParams);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (passengerId == 0) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先输入乘客联系电话");
                } else if (chooseSeatList == null || chooseSeatList.isEmpty()) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先选择乘客座位");
                } else {
                    Intent intent = new Intent(CreateOrderActivity.this, PassengerSelectActivity.class);
                    intent.putExtra("passengerId", 170964L);
                    intent.putExtra("data", chooseSeatList);
                    intent.putExtra("chooseList", new ArrayList<>(adapter.getData()));
                    startActivityForResult(intent, 0x10);
                }
            }
        });
        return emptyView;
    }

    private void getStation(boolean isStart) {
        ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                .queryStation(dzBusLine.lineId, dzBusLine.id)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<StationMainBean>(this, true, false, new NoErrSubscriberListener<StationMainBean>() {
                    @Override
                    public void onNext(StationMainBean dzBusStaion) {
                        request = true;
                        startList = dzBusStaion.onStation;
                        endList = dzBusStaion.offStation;
                        showDialog(isStart);
                    }
                }));
    }

    private void checkList(boolean isStart) {
        if (!request) {
            getStation(isStart);
        } else {
            showDialog(isStart);
        }
    }

    private void showDialog(boolean isStart) {
        StationDialog bottomSheetDialog = new StationDialog(this, isStart ? startList : endList, isStart, startBean, endBean);
        bottomSheetDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (StationBean stationBean : isStart ? startList : endList) {
                    if (stationBean.chooseStatus == 1) {
                        startBean = stationBean;
                        endBean = null;
                        customBusCreateOrderTvEnd.setText("");
                        customBusCreateOrderTvStart.setText(startBean.name);
                        currentMoney = -1;
                        customBusCreateOrderTvMoney.setText("0.00");
                    } else if (stationBean.chooseStatus == 2) {
                        endBean = stationBean;
                        customBusCreateOrderTvEnd.setText(endBean.name);
                        currentMoney = -1;
                    }
                    buttonAction();
                }
                checkMoney();
            }
        });
        bottomSheetDialog.show();
    }

    private void setMoney() {
        customBusCreateOrderTvMoney.setText(decimalFormat.format(currentMoney * currentNum));
    }

    private void checkMoney() {
        if (currentModeType == 2) {
            customBusCreateOrderTvMoney.setText("0.00");
        } else {
            if (startBean != null && endBean != null && dzBusLine != null && currentMoney == -1) {
                ApiManager.getInstance().createApi(Config.HOST, DZBusApiService.class)
                        .priceOrder(startBean.stationId, endBean.stationId, dzBusLine.id)
                        .map(new HttpResultFunc2<>())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new MySubscriber<Double>(this, true, false, new HaveErrSubscriberListener<Double>() {
                            @Override
                            public void onNext(Double aDouble) {
                                currentMoney = aDouble;
                                setMoney();
                            }

                            @Override
                            public void onError(int code) {
                                endBean = null;
                                customBusCreateOrderTvEnd.setText("");
                            }
                        }));
            } else if (currentMoney != -1) {
                setMoney();
            }
        }
    }

    private void buttonAction() {
        if (currentModeType == 1) {
            if (!TextUtils.isEmpty(customBusCreateOrderTvStart.getText()) && !TextUtils.isEmpty(customBusCreateOrderTvEnd.getText()) &&
                    !TextUtils.isEmpty(customBusCreateOrderTvLine.getText()) && customBusCreateOrderEtPhone.getText().length() == 11) {
                customBusCreateOrderBtCreate.setEnabled(true);
                customBusCreateOrderBtCreate.setBackgroundResource(R.drawable.corners_button_bg);
            } else {
                customBusCreateOrderBtCreate.setEnabled(false);
                customBusCreateOrderBtCreate.setBackgroundResource(R.drawable.pc_btn_unpress_999999_bg);
            }
        } else {
            if (!TextUtils.isEmpty(customBusCreateOrderTvStart.getText()) && !TextUtils.isEmpty(customBusCreateOrderTvEnd.getText()) &&
                    !TextUtils.isEmpty(customBusCreateOrderTvLine.getText()) && customBusCreateOrderEtPhone.getText().length() == 11
                    && passengerId != 0 && !TextUtils.isEmpty(customBusCreateOrderTvSeatSelect.getText())) {
                customBusCreateOrderBtCreate.setEnabled(true);
                customBusCreateOrderBtCreate.setBackgroundResource(R.drawable.corners_button_bg);
            } else {
                customBusCreateOrderBtCreate.setEnabled(false);
                customBusCreateOrderBtCreate.setBackgroundResource(R.drawable.pc_btn_unpress_999999_bg);
            }
        }
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.customBusCreateOrderCtb);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle("司机补单");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                DZBusLine newDzBusLine = (DZBusLine) data.getSerializableExtra("data");
                if (dzBusLine == null || dzBusLine.id != newDzBusLine.id) {
                    if (dzBusLine != null && dzBusLine.model != newDzBusLine.model) {
                        customBusCreateOrderEtPhone.setText("");
                    }
                    dzBusLine = newDzBusLine;
                    currentModeType = dzBusLine.model;
                    if (currentModeType == 1) {
                        customBusCreateOrderLlSeatSelect.setVisibility(View.GONE);
                        customBusCreateOrderLlCount.setVisibility(View.VISIBLE);
                        customBusCreateOrderRv.setVisibility(View.INVISIBLE);
                    } else {
                        customBusCreateOrderLlSeatSelect.setVisibility(View.VISIBLE);
                        customBusCreateOrderLlCount.setVisibility(View.GONE);
                        customBusCreateOrderRv.setVisibility(View.VISIBLE);
                    }

                    adapter.setNewData(null);
                    if (passengerFooterView != null) {
                        adapter.removeFooterView(passengerFooterView);
                        passengerFooterView = null;
                    }
                    request = false;
                    startList.clear();
                    endList.clear();
                    currentMoney = -1;
                    customBusCreateOrderTvLine.setText(dzBusLine.lineName);
                    customBusCreateOrderTvStart.setText("");
                    customBusCreateOrderTvEnd.setText("");
                    customBusCreateOrderTvMoney.setText("0.00");
                    customBusCreateOrderTvSeatSelect.setText("");
                    startBean = null;
                    endBean = null;
                    currentNum = 1;
                    customBusCreateOrderTvNum.setText("" + currentNum);
                    customBusCreateOrderTvSub.setEnabled(false);
                    buttonAction();
                    if (dzBusLine.restrict == 1) {
                        customBusCreateOrderTvAdd.setEnabled(true);
                    } else {
                        if (dzBusLine.seats > 1) {
                            customBusCreateOrderTvAdd.setEnabled(true);
                        } else {
                            customBusCreateOrderTvAdd.setEnabled(false);
                        }
                    }
                }
            }
        } else if (resultCode == 0x11) {
            List<PassengerBean> newData = (List<PassengerBean>) data.getSerializableExtra("data");
            List<PassengerBean> currentData = adapter.getData();
            for (PassengerBean newDatum : newData) {
                if (!currentData.contains(newDatum)) {
                    currentData.add(newDatum);
                }
            }
            if (passengerFooterView == null) {
                passengerFooterView = getPassengerView(false);
                adapter.addFooterView(passengerFooterView, 0);
            }
            adapter.notifyDataSetChanged();
        } else if (requestCode == 0x20) {
            double prise = data.getDoubleExtra("prise", 0);
            customBusCreateOrderTvMoney.setText(String.valueOf(prise));
            chooseSeatList = (ArrayList<SeatBean>) data.getSerializableExtra("data");
        }
    }

    /**
     * 创建订单
     */
    private void createOrder() {
        if (customBusCreateOrderEtPhone.getText().length() != 11) {
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
                            customBusCreateOrderEtPhone.getText().toString(),
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
                    showDialog(orderId, Double.parseDouble(customBusCreateOrderTvMoney.getText().toString()));
                }
            })));
        } else {
            showDialog(orderId, Double.parseDouble(customBusCreateOrderTvMoney.getText().toString()));
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
        handler = null;
    }

    @Override
    public void onPaySuc() {
        customBusCreateOrderLlCreateSuc.setVisibility(View.VISIBLE);
        if (handler == null) {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    time--;
                    if (time == 0) {
                        finish();
                    } else {
                        customBusCreateOrderTvCreateSucCount.setText(time + "秒后自动返回订单列表");
                        handler.sendEmptyMessageDelayed(0, 1000);
                    }
                    return true;
                }
            });
        }
        time = 5;
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    @Override
    public void onPayFail() {
        ToastUtil.showMessage(this, "支付失败,请重试");
    }
}
