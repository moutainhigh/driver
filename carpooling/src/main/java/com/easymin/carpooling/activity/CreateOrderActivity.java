package com.easymin.carpooling.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputType;
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
import com.easymi.common.entity.SeatQueryBean;
import com.easymi.component.Config;
import com.easymi.component.base.RxPayActivity;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeMenuLayout;
import com.easymin.carpooling.CarPoolApiService;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.MapPositionModel;
import com.easymin.carpooling.entity.PincheOrder;
import com.easymin.carpooling.entity.PriceResult;
import com.easymin.carpooling.entity.StationResult;
import com.google.gson.Gson;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: CreateOrderActivity
 * @Author: hufeng
 * @Date: 2019/5/21 上午11:47
 * @Description:
 * @History:
 */
@Route(path = "/carpooling/CreateOrderActivity")
public class CreateOrderActivity extends RxPayActivity {
    TextView carPoolCreateOrderTvLine;
    TextView carPoolCreateOrderTvStart;
    TextView carPoolCreateOrderTvEnd;
    EditText carPoolCreateOrderEtPhone;
    TextView carPoolCreateOrderTvSub;
    TextView carPoolCreateOrderTvAdd;
    TextView carPoolCreateOrderTvNum;
    TextView carPoolCreateOrderTvMoney;
    Button carPoolCreateOrderBtCreate;
    LinearLayout carPoolCreateOrderLlMoney;

    LinearLayout carPoolCreateOrderLlPaySuc;
    TextView carPoolCreateOrderTvPaySucDesc;
    Button carPoolCreateOrderBtPaySuc;
    TextView carPoolCreateOrderTvPaySucCountDown;

    /**
     * 专线订单
     */
    private PincheOrder pcOrder = null;
    /**
     * 站点信息
     */
    private StationResult stationResult = null;
    /**
     * 起点信息
     */
    private MapPositionModel startSite = null;
    /**
     * 终点信息
     */
    private MapPositionModel endSite = null;
    /**
     * 费用信息
     */
    private PriceResult priceResult = null;

    /**
     * 座位数
     */
    private int seatNo = 1;

    /**
     * 生成的未支付订单id
     */
    private long orderId;
    private double calMoney;

    private Handler handler;
    private int time;
    private int currentModel;
    private LinearLayout carPoolCreateOrderLvSeatSelect;
    private LinearLayout carPoolCreateOrderLlCount;
    private RecyclerView carPoolCreateOrderRv;
    private PassengerAdapter adapter;
    private ArrayList<SeatBean> chooseSeatList;
    private View passengerFooterView;
    private TextView carPoolCreateOrderTvSeatSelect;
    private long passengerId;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.pc_activity_create_order;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        currentModel = 1;
        passengerId = 0;
        carPoolCreateOrderTvLine = findViewById(R.id.carPoolCreateOrderTvLine);
        carPoolCreateOrderTvStart = findViewById(R.id.carPoolCreateOrderTvStart);
        carPoolCreateOrderTvEnd = findViewById(R.id.carPoolCreateOrderTvEnd);
        carPoolCreateOrderEtPhone = findViewById(R.id.carPoolCreateOrderEtPhone);
        carPoolCreateOrderTvSub = findViewById(R.id.carPoolCreateOrderTvSub);
        carPoolCreateOrderTvAdd = findViewById(R.id.carPoolCreateOrderTvAdd);
        carPoolCreateOrderTvNum = findViewById(R.id.carPoolCreateOrderTvNum);
        carPoolCreateOrderTvMoney = findViewById(R.id.carPoolCreateOrderTvMoney);
        carPoolCreateOrderBtCreate = findViewById(R.id.carPoolCreateOrderBtCreate);
        carPoolCreateOrderLlMoney = findViewById(R.id.carPoolCreateOrderLlMoney);
        carPoolCreateOrderRv = findViewById(R.id.carPoolCreateOrderRv);
        carPoolCreateOrderLlPaySuc = findViewById(R.id.create_suc);
        carPoolCreateOrderTvPaySucDesc = findViewById(R.id.hint_1);
        carPoolCreateOrderTvPaySucDesc.setText("支付成功");
        carPoolCreateOrderTvPaySucCountDown = findViewById(R.id.count_down);
        carPoolCreateOrderBtPaySuc = findViewById(R.id.btn);
        carPoolCreateOrderBtPaySuc.setText("返回我的订单");
        carPoolCreateOrderBtPaySuc.setOnClickListener(view -> finish());
        carPoolCreateOrderLvSeatSelect = findViewById(R.id.carPoolCreateOrderLvSeatSelect);
        carPoolCreateOrderTvSeatSelect = findViewById(R.id.carPoolCreateOrderTvSeatSelect);
        carPoolCreateOrderTvSeatSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goSeatSelect();
            }
        });
        carPoolCreateOrderLlCount = findViewById(R.id.carPoolCreateOrderLlCount);
        carPoolCreateOrderTvLine.setOnClickListener(view -> {
            Intent intent = new Intent(CreateOrderActivity.this, BanciSelectActivity.class);
            startActivityForResult(intent, 0);
        });

        carPoolCreateOrderTvStart.setOnClickListener(view -> {
            if (pcOrder == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "请先选择班次");
                return;
            }
            if (stationResult == null || stationResult.data == null || stationResult.data.size() < 2) {
                ToastUtil.showMessage(CreateOrderActivity.this, "未查询到站点信息");
                return;
            }
            Intent intent = new Intent(CreateOrderActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 1);
            intent.putParcelableArrayListExtra("pos_list",
                    (ArrayList<? extends Parcelable>) stationResult.data);
            startActivityForResult(intent, 1);
        });

        carPoolCreateOrderTvEnd.setOnClickListener(view -> {
            if (pcOrder == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "请先选择班次");
                return;
            }
            if (stationResult == null || stationResult.data.get(1) == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "未查询到站点信息");
                return;
            }
            Intent intent = new Intent(CreateOrderActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 3);
            intent.putParcelableArrayListExtra("pos_list",
                    (ArrayList<? extends Parcelable>) stationResult.data);
            startActivityForResult(intent, 3);
        });
        carPoolCreateOrderEtPhone.setInputType(InputType.TYPE_NULL);
        carPoolCreateOrderEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (StringUtils.isNotBlank(editable.toString())
                        && editable.toString().length() == 11) {
                    carPoolCreateOrderEtPhone.clearFocus();
                    PhoneUtil.hideKeyboard(CreateOrderActivity.this);
                    if (currentModel == 2) {
                        getPassengerId();
                    }
                } else {
                    passengerId = 0;
                }
                setBtnEnable();
            }
        });
        carPoolCreateOrderTvSub.setOnClickListener(view -> {
            seatNo--;
            carPoolCreateOrderTvNum.setText("" + seatNo);

            setBtnEnable();
            calcPrice();
        });
        carPoolCreateOrderTvAdd.setOnClickListener(view -> {

            seatNo++;
            carPoolCreateOrderTvNum.setText("" + seatNo);

            setBtnEnable();
            calcPrice();
        });
        carPoolCreateOrderBtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
        setRecyclerView();
    }

    private void getPassengerId() {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getPassengerId(carPoolCreateOrderEtPhone.getText().toString(), EmUtil.getEmployInfo().companyId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<Employ>(this, true, false, new HaveErrSubscriberListener<Employ>() {
                    @Override
                    public void onNext(Employ employ) {
                        if (employ != null) {
                            passengerId = employ.id;
                            setBtnEnable();
                        } else {
                            passengerId = 0;
                            carPoolCreateOrderEtPhone.setText("");
                        }
                    }

                    @Override
                    public void onError(int code) {
                        passengerId = 0;
                        carPoolCreateOrderEtPhone.setText("");
                    }
                }));
    }

    public void goSeatSelect() {
        if (pcOrder == null) {
            ToastUtil.showMessage(CreateOrderActivity.this, "请先选择班次");
            return;
        }
        if (startSite == null) {
            ToastUtil.showMessage(CreateOrderActivity.this, "请先选择上车点");
            return;
        }
        if (endSite == null) {
            ToastUtil.showMessage(CreateOrderActivity.this, "请先选择下车点");
            return;
        }
        Intent intent = new Intent(CreateOrderActivity.this, SeatSelectActivity.class);
        SeatQueryBean seatQueryBean = new SeatQueryBean();
        seatQueryBean.type = Config.CARPOOL;
        seatQueryBean.startId = startSite.getId();
        seatQueryBean.endId = endSite.getId();
        seatQueryBean.timeSlotId = pcOrder.timeSlotId;
        intent.putExtra("seatQueryBean", seatQueryBean);
        startActivityForResult(intent, 0x20);
    }

    private void setRecyclerView() {
        carPoolCreateOrderRv = findViewById(R.id.carPoolCreateOrderRv);
        carPoolCreateOrderRv.setLayoutManager(new LinearLayoutManager(this));

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
                            adapter.getViewByPosition(carPoolCreateOrderRv, position + adapter.getHeaderLayoutCount(), com.easymi.common.R.id.itemPassengerSelectContentSml);
                    itemPassengerSelectContentSml.quickClose();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter.setEmptyView(getPassengerView(true));
        adapter.setHeaderFooterEmpty(true, true);
        carPoolCreateOrderRv.setAdapter(adapter);
    }

    public View getPassengerView(boolean isEmpty) {
        View emptyView = getLayoutInflater().inflate(R.layout.item_passenger_select_header, carPoolCreateOrderRv, false);
        View itemPassengerSelectHeaderV = emptyView.findViewById(R.id.itemPassengerSelectHeaderV);
        TextView itemPassengerSelectHeaderTv = emptyView.findViewById(R.id.itemPassengerSelectHeaderTv);
        if (!isEmpty) {
            itemPassengerSelectHeaderV.setVisibility(View.VISIBLE);
        }
        ViewGroup.LayoutParams layoutParams = emptyView.getLayoutParams();
        Drawable drawable = ContextCompat.getDrawable(this, isEmpty ? R.mipmap.ic_add_passenger_sub : R.mipmap.ic_add_passenger);
        if (isEmpty) {
            itemPassengerSelectHeaderTv.setTextColor(ContextCompat.getColor(this, R.color.colorSub));
            layoutParams.height = DensityUtil.dp2px(this, 100);
        } else {
            itemPassengerSelectHeaderTv.setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        itemPassengerSelectHeaderTv.setCompoundDrawables(drawable, null, null, null);
        emptyView.setLayoutParams(layoutParams);
        emptyView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pcOrder == null) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先选择班次");
                } else if (startSite == null) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先选择上车点");
                } else if (endSite == null) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先选择下车点");
                } else if (chooseSeatList == null || chooseSeatList.isEmpty()) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先选择乘客座位");
                } else if (passengerId == 0) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "请先输入乘客联系电话");
                } else {
                    Intent intent = new Intent(CreateOrderActivity.this, PassengerSelectActivity.class);
                    intent.putExtra("passengerId", passengerId);
                    intent.putExtra("data", chooseSeatList);
                    intent.putExtra("chooseList", new ArrayList<>(adapter.getData()));
                    startActivityForResult(intent, 0x10);
                }
            }
        });
        return emptyView;
    }

    /**
     * 计算价格
     */
    private void calcPrice() {
        double money = 0;
        if (null != priceResult) {
            money = priceResult.data.money * seatNo;
        } else {
            money = 0;
        }
        this.carPoolCreateOrderTvMoney.setText(new DecimalFormat("######0.00").format(money));
        if (money != 0) {
            carPoolCreateOrderLlMoney.setVisibility(View.VISIBLE);
        } else {
            carPoolCreateOrderLlMoney.setVisibility(View.GONE);
        }
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.carPoolCreateOrderCtb);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle("司机补单");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                PincheOrder newPcOrder = (PincheOrder) data.getSerializableExtra("pincheOrder");
                if (null == pcOrder || pcOrder.id != newPcOrder.id) {
//                    currentModel = newPcOrder.model;
                    currentModel = 1;
                    pcOrder = newPcOrder;
                    initViewByPcOrder();
                    queryStation(pcOrder.id);
                }
                carPoolCreateOrderTvLine.setText(pcOrder.startStation + " 到 " + pcOrder.endStation);
                setBtnEnable();
            } else if (requestCode == 1) {
                //起点
                startSite = data.getParcelableExtra("pos_model");
                carPoolCreateOrderTvStart.setText(startSite.getAddress());
                endSite = null;
                carPoolCreateOrderTvEnd.setText("");
                setBtnEnable();
                carPoolCreateOrderLlMoney.setVisibility(View.GONE);
                orderId = 0;
            } else if (requestCode == 3) {
                //终点
                orderId = 0;
                carPoolCreateOrderLlMoney.setVisibility(View.GONE);
                endSite = data.getParcelableExtra("pos_model");
                if (startSite != null) {
                    if (startSite.getId() == endSite.getId()) {
                        endSite = null;
                        carPoolCreateOrderTvEnd.setText("");
                        ToastUtil.showMessage(this, "上车点和下车点是同一站点");
                    } else if (startSite.getSequence() > endSite.getSequence()) {
                        endSite = null;
                        carPoolCreateOrderTvEnd.setText("");
                        ToastUtil.showMessage(this, "下车点在上车点之前");
                    } else {
                        carPoolCreateOrderTvEnd.setText(endSite.getAddress());
                        setBtnEnable();
                        if (currentModel == 1) {
                            queryPrice(pcOrder.lineId, startSite.getId(), endSite.getId());
                        }
                    }
                } else {
                    carPoolCreateOrderTvEnd.setText(endSite.getAddress());
                    setBtnEnable();
                }
            } else if (requestCode == 0x20) {
                if (data != null) {
                    double prise = data.getDoubleExtra("prise", 0);
                    carPoolCreateOrderTvMoney.setText(new DecimalFormat("######0.00").format(prise));
                    carPoolCreateOrderLlMoney.setVisibility(View.VISIBLE);
                    chooseSeatList = (ArrayList<SeatBean>) data.getSerializableExtra("data");
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < chooseSeatList.size(); i++) {
                        SeatBean seatBean = chooseSeatList.get(i);
                        stringBuilder.append(seatBean.getDescAndSeatInfo());
                        if (i != chooseSeatList.size() - 1) {
                            stringBuilder.append("; ");
                        }
                    }
                    carPoolCreateOrderTvSeatSelect.setText(stringBuilder.toString());
                    carPoolCreateOrderTvSeatSelect.setSelected(true);
                    setBtnEnable();
                }
            } else if (requestCode == 0x10) {
                if (data != null) {
                    List<PassengerBean> newData = (List<PassengerBean>) data.getSerializableExtra("data");
                    adapter.setNewData(newData);
                    if (passengerFooterView == null) {
                        passengerFooterView = getPassengerView(false);
                        adapter.addFooterView(passengerFooterView, 0);
                    }
                    setBtnEnable();
                }
            }
        }
    }

    /**
     * 加载专线数据
     */
    private void initViewByPcOrder() {
        carPoolCreateOrderEtPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        stationResult = null;
        orderId = 0;
        carPoolCreateOrderTvStart.setText(null);
        startSite = null;
        carPoolCreateOrderTvEnd.setText(null);
        endSite = null;
        seatNo = 1;
        carPoolCreateOrderLlMoney.setVisibility(View.GONE);
        if (currentModel == 2) {
            carPoolCreateOrderLvSeatSelect.setVisibility(View.VISIBLE);
            carPoolCreateOrderLlCount.setVisibility(View.GONE);
            carPoolCreateOrderRv.setVisibility(View.VISIBLE);
        } else {
            carPoolCreateOrderRv.setVisibility(View.GONE);
            carPoolCreateOrderTvNum.setText("" + seatNo);
            carPoolCreateOrderTvSub.setEnabled(false);
            if (pcOrder.seats > seatNo) {
                carPoolCreateOrderTvAdd.setEnabled(true);
            }
            carPoolCreateOrderLvSeatSelect.setVisibility(View.GONE);
            carPoolCreateOrderLlCount.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 设置按钮能否点击
     */
    private void setBtnEnable() {
        if (currentModel == 1) {
            if (pcOrder != null
                    && stationResult != null
                    && stationResult.data != null
                    && stationResult.data.size() >= 2
                    && startSite != null
                    && endSite != null
                    && StringUtils.isNotBlank(carPoolCreateOrderEtPhone.getText().toString())
                    && carPoolCreateOrderEtPhone.getText().toString().length() == 11
                    && seatNo > 0) {
                carPoolCreateOrderBtCreate.setEnabled(true);
                carPoolCreateOrderBtCreate.setBackgroundResource(R.drawable.cor4_solid_blue);
            } else {
                carPoolCreateOrderBtCreate.setEnabled(false);
                carPoolCreateOrderBtCreate.setBackgroundResource(R.drawable.cor4_solid_sub);
            }
            if (seatNo == 1) {
                carPoolCreateOrderTvSub.setEnabled(false);
            } else {
                carPoolCreateOrderTvSub.setEnabled(true);
            }

            if (pcOrder != null && seatNo < pcOrder.seats) {
                carPoolCreateOrderTvAdd.setEnabled(true);
            } else {
                carPoolCreateOrderTvAdd.setEnabled(false);
            }
        } else {
            if (pcOrder != null
                    && stationResult != null
                    && stationResult.data != null
                    && stationResult.data.size() >= 2
                    && startSite != null
                    && endSite != null
                    && passengerId != 0
                    && !TextUtils.isEmpty(carPoolCreateOrderTvSeatSelect.getText())
                    && adapter.getData().size() > 0) {
                carPoolCreateOrderBtCreate.setEnabled(true);
                carPoolCreateOrderBtCreate.setBackgroundResource(R.drawable.cor4_solid_blue);
            } else {
                carPoolCreateOrderBtCreate.setEnabled(false);
                carPoolCreateOrderBtCreate.setBackgroundResource(R.drawable.cor4_solid_sub);
            }
        }
    }

    /**
     * 查询起终站点
     *
     * @param scheduleId
     */
    private void queryStation(long scheduleId) {
        Observable<StationResult> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .getStationResult(scheduleId)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(CreateOrderActivity.this,
                true,
                false,
                new HaveErrSubscriberListener<StationResult>() {
                    @Override
                    public void onNext(StationResult stationResult) {
                        CreateOrderActivity.this.stationResult = stationResult;
                    }

                    @Override
                    public void onError(int code) {
                        pcOrder = null;
                        carPoolCreateOrderTvLine.setText("");
                        initViewByPcOrder();
                        setBtnEnable();
                    }
                })));
    }

    /**
     * 查询单价
     *
     * @param lineId
     * @param startStationId
     * @param endStationId
     */
    private void queryPrice(long lineId, long startStationId, long endStationId) {
        Observable<PriceResult> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .getPrice(endStationId, lineId, startStationId)
                .filter(new HttpResultFunc<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this,
                false,
                false,
                new HaveErrSubscriberListener<PriceResult>() {
                    @Override
                    public void onNext(PriceResult priceResult) {
                        CreateOrderActivity.this.priceResult = priceResult;
                        calcPrice();
                    }

                    @Override
                    public void onError(int code) {
                        priceResult = null;
                        endSite = null;
                        carPoolCreateOrderTvEnd.setText("");
                        setBtnEnable();
                    }
                })));
    }

    /**
     * 创建订单
     */
    private void createOrder() {
        calMoney = 0;
        try {
            calMoney = Double.parseDouble(carPoolCreateOrderTvMoney.getText().toString());
        } catch (NumberFormatException e) {
            e.fillInStackTrace();
        }
        if (orderId == 0) {
            List<MapPositionModel> models = new ArrayList<>();
            models.add(startSite);
            models.add(endSite);
            Observable<Long> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                    .createOrder(
                            EmUtil.getEmployInfo().companyId,
                            new Gson().toJson(models),
                            startSite.getId(),
                            endSite.getId(),
                            pcOrder.id,
                            currentModel == 1 ? seatNo : 1,
                            carPoolCreateOrderEtPhone.getText().toString(),
                            "driver",
                            pcOrder.timeSlotId,
                            currentModel == 1 ? "" : new Gson().toJson(adapter.getData()),
                            currentModel == 1 ? "" : new Gson().toJson(chooseSeatList)
                    )
                    .map(new HttpResultFunc2<>())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io());

            mRxManager.add(observable.subscribe(new MySubscriber<Long>(this,
                    true,
                    true, new HaveErrSubscriberListener<Long>() {
                @Override
                public void onNext(Long id) {
                    orderId = id;
                    showDialog(orderId, calMoney);
                }

                @Override
                public void onError(int code) {

                }
            })));
        } else {
            showDialog(orderId, calMoney);
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
        carPoolCreateOrderLlPaySuc.setVisibility(View.VISIBLE);
        if (handler == null) {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    time--;
                    if (time == 0) {
                        finish();
                    } else {
                        carPoolCreateOrderTvPaySucCountDown.setText(time + "秒后自动返回订单列表");
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
