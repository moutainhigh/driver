package com.easymin.carpooling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.base.RxPayActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
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
    EditText edit_phone;
    TextView money;
    Button create_order;
    LinearLayout money_con;

    LinearLayout create_suc_con;
    TextView hint_1;
    Button btn;

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
    private TextView count_down;
    private TextView carPoolCreateOrderBySeatTvSeatSelect;
    private EditText carPoolCreateOrderBySeatEtPhone;

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
        carPoolCreateOrderBySeatEtPhone = findViewById(R.id.carPoolCreateOrderBySeatEtPhone);
        money = findViewById(R.id.money);
        create_order = findViewById(R.id.create_order);
        money_con = findViewById(R.id.money_con);

        create_suc_con = findViewById(R.id.create_suc);
        hint_1 = findViewById(R.id.hint_1);
        hint_1.setText("代付成功");
        count_down = findViewById(R.id.count_down);

        btn = findViewById(R.id.btn);
        btn.setText("返回我的订单");
        btn.setOnClickListener(view -> finish());

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
                }
                setBtnEnable();
            }
        });

        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
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
                endSite = data.getParcelableExtra("pos_model");
                carPoolCreateOrderBySeatTvEndPlace.setText(endSite.getAddress());
                setBtnEnable();
                if (startSite != null) {
                    if (startSite.getId() == endSite.getId()) {
                        ToastUtil.showMessage(this, "上车点和下车点是同一站点");
                    } else if (startSite.getSequence() > endSite.getSequence()) {
                        ToastUtil.showMessage(this, "下车点在上车点之前");
                    }
                }
            }
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

        money_con.setVisibility(View.GONE);

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
                && StringUtils.isNotBlank(carPoolCreateOrderBySeatEtPhone.getText().toString())
                && carPoolCreateOrderBySeatEtPhone.getText().toString().length() == 11) {
            create_order.setEnabled(true);
            create_order.setBackgroundResource(R.drawable.corners_button_selector);
        } else {
            create_order.setEnabled(false);
            create_order.setBackgroundResource(R.drawable.pc_btn_unpress_999999_bg);
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
        create_suc_con.setVisibility(View.VISIBLE);
        if (handler == null) {
            handler = new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    time--;
                    if (time == 0) {
                        finish();
                    } else {
                        count_down.setText(time + "秒后自动返回订单列表");
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
