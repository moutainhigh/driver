package com.easymin.carpooling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.easymi.common.CommApiService;
import com.easymi.common.activity.PassengerSelectActivity;
import com.easymi.common.activity.SeatSelectActivity;
import com.easymi.common.adapter.PassengerAdapter;
import com.easymi.common.entity.PassengerBean;
import com.easymi.component.Config;
import com.easymi.component.base.RxPayActivity;
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
import com.easymin.carpooling.entity.StationResult;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CarpoolCreateOrderBySeatActivity extends RxPayActivity {
    TextView carPoolCreateOrderBySeatTvLineSelect;
    TextView carPoolCreateOrderBySeatTvStartPlace;
    TextView carPoolCreateOrderBySeatTvEndPlace;
    TextView carPoolCreateOrderBySeatTvMoney;
    Button carPoolCreateOrderBySeatBtCreate;
    LinearLayout carPoolCreateOrderBySeatLlMoney;

    LinearLayout carPoolCreateOrderBySeatLlCreateSuc;
    TextView carPoolCreateOrderBySeatTvCreateSucTitle;

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
     * 生成的未支付订单id
     */
    private long orderId;
    private Handler handler;
    private int time;
    private TextView carPoolCreateOrderBySeatTvCreateSucDesc;
    private TextView carPoolCreateOrderBySeatTvSeatSelect;
    private EditText carPoolCreateOrderBySeatEtPhone;
    private TextView carPoolCreateOrderBySeatBtCreateSuc;
    private RecyclerView carPoolCreateOrderBySeatRv;
    private PassengerAdapter adapter;
    private View passengerFooterView;
    private long passengerId;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_carpool_create_order_by_seat;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        carPoolCreateOrderBySeatTvLineSelect = findViewById(R.id.carPoolCreateOrderBySeatTvLineSelect);
        carPoolCreateOrderBySeatTvStartPlace = findViewById(R.id.carPoolCreateOrderBySeatTvStartPlace);
        carPoolCreateOrderBySeatTvEndPlace = findViewById(R.id.carPoolCreateOrderBySeatTvEndPlace);
        carPoolCreateOrderBySeatTvSeatSelect = findViewById(R.id.carPoolCreateOrderBySeatTvSeatSelect);
        carPoolCreateOrderBySeatTvSeatSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CarpoolCreateOrderBySeatActivity.this, SeatSelectActivity.class);
                startActivity(intent);
            }
        });
        carPoolCreateOrderBySeatEtPhone = findViewById(R.id.carPoolCreateOrderBySeatEtPhone);
        carPoolCreateOrderBySeatTvMoney = findViewById(R.id.carPoolCreateOrderBySeatTvMoney);
        carPoolCreateOrderBySeatBtCreate = findViewById(R.id.carPoolCreateOrderBySeatBtCreate);
        carPoolCreateOrderBySeatLlMoney = findViewById(R.id.carPoolCreateOrderBySeatLlMoney);

        carPoolCreateOrderBySeatLlCreateSuc = findViewById(R.id.create_suc);
        carPoolCreateOrderBySeatTvCreateSucTitle = findViewById(R.id.hint_1);
        carPoolCreateOrderBySeatTvCreateSucTitle.setText("代付成功");

        carPoolCreateOrderBySeatTvCreateSucDesc = findViewById(R.id.count_down);

        carPoolCreateOrderBySeatBtCreateSuc = findViewById(R.id.btn);
        carPoolCreateOrderBySeatBtCreateSuc.setText("返回我的订单");
        carPoolCreateOrderBySeatBtCreateSuc.setOnClickListener(view -> finish());

        carPoolCreateOrderBySeatTvLineSelect.setOnClickListener(view -> {
            Intent intent = new Intent(CarpoolCreateOrderBySeatActivity.this, BanciSelectActivity.class);
            startActivityForResult(intent, 0);
        });
        carPoolCreateOrderBySeatTvStartPlace.setOnClickListener(view -> {

            if (pcOrder == null) {
                ToastUtil.showMessage(CarpoolCreateOrderBySeatActivity.this, "请先选择班次");
                return;
            }
            if (stationResult == null || stationResult.data == null || stationResult.data.size() < 2) {
                ToastUtil.showMessage(CarpoolCreateOrderBySeatActivity.this, "未查询到站点信息");
                return;
            }


            Intent intent = new Intent(CarpoolCreateOrderBySeatActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 1);
//            if (stationResult.data.size() == 0) {
//                List<MapPositionModel> list = new ArrayList<>();
//                MapPositionModel model = new MapPositionModel();
//                model.setLatitude(stationResult.data.get(0).latitude);
//                model.setLongitude(stationResult.data.get(0).longitude);
//                list.add(model);


            intent.putParcelableArrayListExtra("pos_list",
                    (ArrayList<? extends Parcelable>) stationResult.data);
//            }
//            else {
//                intent.putParcelableArrayListExtra("pos_list",
//                        (ArrayList<? extends Parcelable>) stationResult.data.get(0).coordinate);
//            }
            startActivityForResult(intent, 1);
        });
        carPoolCreateOrderBySeatTvEndPlace.setOnClickListener(view -> {

            if (pcOrder == null) {
                ToastUtil.showMessage(CarpoolCreateOrderBySeatActivity.this, "请先选择班次");
                return;
            }
            if (stationResult == null || stationResult.data.get(1) == null) {
                ToastUtil.showMessage(CarpoolCreateOrderBySeatActivity.this, "未查询到站点信息");
                return;
            }
            Intent intent = new Intent(CarpoolCreateOrderBySeatActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 3);
//            if (stationResult.data.size() == 0) {
//                List<MapPositionModel> list = new ArrayList<>();
//                MapPositionModel model = new MapPositionModel();
//                model.setLatitude(stationResult.data.get(1).latitude);
//                model.setLongitude(stationResult.data.get(1).longitude);
//                list.add(model);
//
//                intent.putParcelableArrayListExtra("pos_list",
//                        (ArrayList<? extends Parcelable>) list);
//            } else {
            intent.putParcelableArrayListExtra("pos_list",
                    (ArrayList<? extends Parcelable>) stationResult.data);
//            }

            startActivityForResult(intent, 3);
        });

        carPoolCreateOrderBySeatEtPhone.addTextChangedListener(new TextWatcher() {
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
                    carPoolCreateOrderBySeatEtPhone.clearFocus();
                    PhoneUtil.hideKeyboard(CarpoolCreateOrderBySeatActivity.this);
                    getPassengerId();
                } else {
                    passengerId = 0;
                }
                setBtnEnable();
            }
        });

        carPoolCreateOrderBySeatBtCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
        setRecyclerView();
    }

    private void getPassengerId() {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getPassengerId(carPoolCreateOrderBySeatEtPhone.getText().toString(), EmUtil.getEmployInfo().companyId)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<Long>(this, true, false, new HaveErrSubscriberListener<Long>() {
                    @Override
                    public void onNext(Long aLong) {
                        if (aLong != null) {
                            passengerId = aLong.longValue();
                            setBtnEnable();
                        } else {
                            carPoolCreateOrderBySeatEtPhone.setText("");
                        }
                    }

                    @Override
                    public void onError(int code) {
                        carPoolCreateOrderBySeatEtPhone.setText("");
                    }
                }));
    }

    private void setRecyclerView() {
        carPoolCreateOrderBySeatRv = findViewById(R.id.carPoolCreateOrderBySeatRv);
        carPoolCreateOrderBySeatRv.setLayoutManager(new LinearLayoutManager(this));

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
                            adapter.getViewByPosition(carPoolCreateOrderBySeatRv, position + adapter.getHeaderLayoutCount(), com.easymi.common.R.id.itemPassengerSelectContentSml);
                    itemPassengerSelectContentSml.quickClose();
                    adapter.notifyDataSetChanged();
                }
            }
        });

        adapter.setEmptyView(getPassengerView(true));
        adapter.setHeaderFooterEmpty(true, true);
        carPoolCreateOrderBySeatRv.setAdapter(adapter);
    }


    public View getPassengerView(boolean isEmpty) {
        View emptyView = getLayoutInflater().inflate(com.easymi.common.R.layout.item_passenger_select_header, carPoolCreateOrderBySeatRv, false);
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
                if (passengerId != 0) {
                    Intent intent = new Intent(CarpoolCreateOrderBySeatActivity.this, PassengerSelectActivity.class);
                    startActivityForResult(intent, 0x10);
                } else {
                    ToastUtil.showMessage(CarpoolCreateOrderBySeatActivity.this, "请先输入乘客电话号码");
                }
            }
        });
        return emptyView;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        CusToolbar cusToolbar = findViewById(R.id.carPoolCreateOrderBySeatCtb);
        cusToolbar.setLeftBack(view -> finish());
        cusToolbar.setTitle("司机补单");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) {
                //班次
                PincheOrder newPcOrder = (PincheOrder) data.getSerializableExtra("pincheOrder");
                if (null != pcOrder) {
                    if (pcOrder.id != newPcOrder.id) {
                        //班次变化后  重新键入
                        pcOrder = newPcOrder;
                        initViewByPcOrder();
                    }
                } else {
                    pcOrder = newPcOrder;
                    initViewByPcOrder();
                }
                carPoolCreateOrderBySeatTvLineSelect.setText(pcOrder.lineName);
                setBtnEnable();
            } else if (requestCode == 1) {
                //起点
                startSite = data.getParcelableExtra("pos_model");
                carPoolCreateOrderBySeatTvStartPlace.setText(startSite.getAddress());
                setBtnEnable();
                if (endSite != null) {
                    if (startSite.getId() == endSite.getId()) {
                        ToastUtil.showMessage(this, "上车点和下车点是同一站点");
                    } else if (startSite.getSequence() > endSite.getSequence()) {
                        ToastUtil.showMessage(this, "下车点在上车点之前");
                    }
                }
            } else if (requestCode == 3) {
                //终点
                orderId = 0;
                carPoolCreateOrderBySeatLlMoney.setVisibility(View.GONE);
                endSite = data.getParcelableExtra("pos_model");
                if (startSite != null) {
                    if (startSite.getId() == endSite.getId()) {
                        endSite = null;
                        carPoolCreateOrderBySeatTvEndPlace.setText("");
                        ToastUtil.showMessage(this, "上车点和下车点是同一站点");
                    } else if (startSite.getSequence() > endSite.getSequence()) {
                        endSite = null;
                        carPoolCreateOrderBySeatTvEndPlace.setText("");
                        ToastUtil.showMessage(this, "下车点在上车点之前");
                    } else {
                        carPoolCreateOrderBySeatTvEndPlace.setText(endSite.getAddress());
                        setBtnEnable();
                    }
                } else {
                    carPoolCreateOrderBySeatTvEndPlace.setText(endSite.getAddress());
                    setBtnEnable();
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
        }
    }

    /**
     * 加载专线数据
     */
    private void initViewByPcOrder() {
        stationResult = null;

        carPoolCreateOrderBySeatTvStartPlace.setText(null);
        startSite = null;
        carPoolCreateOrderBySeatTvEndPlace.setText(null);
        endSite = null;

        carPoolCreateOrderBySeatLlMoney.setVisibility(View.GONE);

        queryStation(pcOrder.id);
    }

    /**
     * 设置按钮能否点击
     */
    private void setBtnEnable() {
        if (pcOrder != null
                && stationResult != null
                && stationResult.data != null
                && stationResult.data.size() >= 2
                && startSite != null
                && endSite != null
                && passengerId != 0) {
            carPoolCreateOrderBySeatBtCreate.setEnabled(true);
            carPoolCreateOrderBySeatBtCreate.setBackgroundResource(R.drawable.corners_button_selector);
        } else {
            carPoolCreateOrderBySeatBtCreate.setEnabled(false);
            carPoolCreateOrderBySeatBtCreate.setBackgroundResource(R.drawable.pc_btn_unpress_999999_bg);
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
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        mRxManager.add(observable.subscribe(new MySubscriber<>(CarpoolCreateOrderBySeatActivity.this,
                true,
                false,
                new HaveErrSubscriberListener<StationResult>() {
                    @Override
                    public void onNext(StationResult stationResult) {
                        CarpoolCreateOrderBySeatActivity.this.stationResult = stationResult;
                    }

                    @Override
                    public void onError(int code) {
                        stationResult = null;
                        setBtnEnable();
                    }
                })));
    }

    /**
     * 创建订单
     */
    private void createOrder() {
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
                            1,
                            carPoolCreateOrderBySeatEtPhone.getText().toString(),
                            "driver",
                            pcOrder.timeSlotId
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
                    showDialog(orderId);
                }

                @Override
                public void onError(int code) {

                }
            })));
        } else {
            showDialog(orderId);
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
        carPoolCreateOrderBySeatLlCreateSuc.setVisibility(View.VISIBLE);
        if (handler == null) {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    time--;
                    if (time == 0) {
                        finish();
                    } else {
                        carPoolCreateOrderBySeatTvCreateSucDesc.setText(time + "秒后自动返回订单列表");
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
