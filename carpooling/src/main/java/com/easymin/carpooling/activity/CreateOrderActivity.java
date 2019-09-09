package com.easymin.carpooling.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
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
        hint_1.setText("支付成功");

        btn = findViewById(R.id.btn);
        btn.setText("返回我的订单");
        btn.setOnClickListener(view -> finish());

        banci_select.setOnClickListener(view -> {
            Intent intent = new Intent(CreateOrderActivity.this, BanciSelectActivity.class);
            startActivityForResult(intent, 0);
        });
        start_place.setOnClickListener(view -> {

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
        end_place.setOnClickListener(view -> {

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

        edit_phone.addTextChangedListener(new TextWatcher() {
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
                    edit_phone.clearFocus();
                    PhoneUtil.hideKeyboard(CreateOrderActivity.this);
                }
                setBtnEnable();
            }
        });
        sub.setOnClickListener(view -> {

            seatNo--;
            num.setText("" + seatNo);

            setBtnEnable();
            calcPrice();
        });
        add.setOnClickListener(view -> {

            seatNo++;
            num.setText("" + seatNo);

            setBtnEnable();
            calcPrice();
        });
        create_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createOrder();
            }
        });
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
        this.money.setText("" + new DecimalFormat("######0.00").format(money));
        if (money != 0) {
            money_con.setVisibility(View.VISIBLE);
        } else {
            money_con.setVisibility(View.GONE);
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
                banci_select.setText(pcOrder.lineName);
                setBtnEnable();
            } else if (requestCode == 1) {
                //起点
                startSite = data.getParcelableExtra("pos_model");
                start_place.setText(startSite.getAddress());
                setBtnEnable();
                if (endSite != null) {
                    if (startSite.getId() == endSite.getId()) {
                        ToastUtil.showMessage(this, "上车点和下车点是同一站点");
                    } else if (startSite.getSequence() > endSite.getSequence()) {
                        ToastUtil.showMessage(this, "下车点在上车点之前");
                    } else {
                        queryPrice(pcOrder.lineId, startSite.getId(), endSite.getId());
                    }
                }
            } else if (requestCode == 3) {
                //终点
                endSite = data.getParcelableExtra("pos_model");
                end_place.setText(endSite.getAddress());
                setBtnEnable();
                if (startSite != null) {
                    if (startSite.getId() == endSite.getId()) {
                        ToastUtil.showMessage(this, "上车点和下车点是同一站点");
                    } else if (startSite.getSequence() > endSite.getSequence()) {
                        ToastUtil.showMessage(this, "下车点在上车点之前");
                    } else {
                        queryPrice(pcOrder.lineId, startSite.getId(), endSite.getId());
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

        start_place.setText(null);
        startSite = null;
        end_place.setText(null);
        endSite = null;

        seatNo = 1;
        num.setText("" + seatNo);
        sub.setEnabled(false);
        if (pcOrder.seats > seatNo) {
            add.setEnabled(true);
        }

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
                && StringUtils.isNotBlank(edit_phone.getText().toString())
                && edit_phone.getText().toString().length() == 11
                && seatNo > 0) {
            create_order.setEnabled(true);
            create_order.setBackgroundResource(R.drawable.corners_button_selector);
        } else {
            create_order.setEnabled(false);
            create_order.setBackgroundResource(R.drawable.pc_btn_unpress_999999_bg);
        }
        if (seatNo == 1) {
            sub.setEnabled(false);
        } else {
            sub.setEnabled(true);
        }

        if (pcOrder != null && seatNo < pcOrder.seats) {
            add.setEnabled(true);
        } else {
            add.setEnabled(false);
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
                        stationResult = null;
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
                            seatNo,
                            edit_phone.getText().toString(),
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

    public void assginOrder() {
        Observable<EmResult> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .assginOrder(orderId, pcOrder.timeSlotId, EmUtil.getEmployId(), pcOrder.seats, priceResult.data.money * seatNo, pcOrder.saleSeat, 1, EmUtil.getAppKey())
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, emResult -> {
            if (emResult.getCode() == 1) {
                ToastUtil.showMessage(this, "补单成功");
            } else {
                ToastUtil.showMessage(this, "指派订单失败，请联系客服重新指派");
            }
            finish();
        })));
    }


    @Override
    public void onPaySuc() {
        assginOrder();
//        ToastUtil.showMessage(this, "支付成功");
//        finish();
    }

    @Override
    public void onPayFail() {
        ToastUtil.showMessage(this, "支付失败,请重试");
    }
}
