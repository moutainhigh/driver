package com.easymin.carpooling.activity;

import android.content.Context;
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
import android.widget.Toast;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.alipay.sdk.app.PayTask;
import com.easymi.common.result.PayResult;
import com.easymi.common.widget.ComPayDialog;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
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
import com.google.gson.JsonElement;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.json.JSONException;
import org.json.JSONObject;

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
public class CreateOrderActivity extends RxBaseActivity {
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
    private int seatNo = 0;

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
            if (stationResult == null || stationResult.data == null || stationResult.data.size() != 2) {
                ToastUtil.showMessage(CreateOrderActivity.this, "未查询到站点信息");
                return;
            }
            Intent intent = new Intent(CreateOrderActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 1);
            if (stationResult.data.get(0).coordinate.size() == 0) {
                List<MapPositionModel> list = new ArrayList<>();
                MapPositionModel model = new MapPositionModel();
                model.setLatitude(stationResult.data.get(0).latitude);
                model.setLongitude(stationResult.data.get(0).longitude);
                list.add(model);

                intent.putParcelableArrayListExtra("pos_list",
                        (ArrayList<? extends Parcelable>) list);
            } else {
                intent.putParcelableArrayListExtra("pos_list",
                        (ArrayList<? extends Parcelable>) stationResult.data.get(0).coordinate);
            }
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
            if (stationResult.data.get(1).coordinate.size() == 0) {
                List<MapPositionModel> list = new ArrayList<>();
                MapPositionModel model = new MapPositionModel();
                model.setLatitude(stationResult.data.get(1).latitude);
                model.setLongitude(stationResult.data.get(1).longitude);
                list.add(model);

                intent.putParcelableArrayListExtra("pos_list",
                        (ArrayList<? extends Parcelable>) list);
            } else {
                intent.putParcelableArrayListExtra("pos_list",
                        (ArrayList<? extends Parcelable>) stationResult.data.get(1).coordinate);
            }

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
        this.money.setText("" +new DecimalFormat("######0.00").format(money));
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
            } else if (requestCode == 3) {
                //终点
                endSite = data.getParcelableExtra("pos_model");
                end_place.setText(endSite.getAddress());
                setBtnEnable();
            } else {
                if (data == null) {
                    return;
                }
                String str = data.getExtras().getString("pay_result");
                if (str.equalsIgnoreCase("success")) {

                    ToastUtil.showMessage(CreateOrderActivity.this, "支付成功");
                    //todo  指派订单
                    // 结果result_data为成功时，去商户后台查询一下再展示成功
                } else if (str.equalsIgnoreCase("fail")) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "支付失败！");
                } else if (str.equalsIgnoreCase("cancel")) {
                    ToastUtil.showMessage(CreateOrderActivity.this, "你已取消了本次订单的支付！");
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

        seatNo = 0;
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
                && stationResult.data.get(0) != null
                && stationResult.data.get(1) != null
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
        if (seatNo == 0) {
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
                        queryPrice(pcOrder.lineId, stationResult.data.get(0).id, stationResult.data.get(1).id);
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
        List<MapPositionModel> models = new ArrayList<>();
        models.add(startSite);
        models.add(endSite);
        String orderAddress = new Gson().toJson(models);
        Observable<Long> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .createOrder(
                        EmUtil.getEmployInfo().companyId,
                        stationResult.data.get(0).id,
                        stationResult.data.get(1).id,
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
            public void onNext(Long orderId) {
                //todo
                Log.e("hufeng/orderId",orderId+"");

                ComPayDialog comPayDialog = new ComPayDialog(CreateOrderActivity.this);
                comPayDialog.setOnMyClickListener(view -> {
                    comPayDialog.dismiss();
                    if (view.getId() == R.id.pay_wenXin) {
                        toPay("CHANNEL_APP_WECHAT", orderId);
                    } else if (view.getId() == R.id.pay_zfb) {
                        toPay("CHANNEL_APP_ALI", orderId);
                    } else if (view.getId() == R.id.pay_balance) {
                        toPay("PAY_DRIVER_BALANCE", orderId);
                    }
                });
                comPayDialog.show();
            }

            @Override
            public void onError(int code) {

            }
        })));
    }


    /**
     * 支付
     *
     * @param payType
     */
    private void toPay(String payType,long orderId) {
        Observable<JsonElement> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .payOrder(orderId, payType)
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, jsonElement -> {

            if (payType.equals("CHANNEL_APP_WECHAT")) {
                launchWeixin(jsonElement);
            } else if (payType.equals("CHANNEL_APP_ALI")) {
                String url = null;
                try {
                    url = new JSONObject(jsonElement.toString()).getString("ali_app_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                launchZfb(url);
            }else if (payType.equals("PAY_DRIVER_BALANCE")){
                //todo 司机代付

            }
        })));
    }


    /**
     * 加载充值微信配置信息
     *
     * @param data
     */
    private void launchWeixin(JsonElement data) {
        JSONObject json;
        try {
            json = new JSONObject(data.toString());
            PayReq req = new PayReq();
            req.appId = json.getString("wx_app_id");
            req.partnerId = json.getString("wx_mch_id");
            req.prepayId = json.getString("wx_pre_id");
            req.nonceStr = json.getString("wx_app_nonce");
            req.timeStamp = json.getString("wx_app_ts");
            req.packageValue = json.getString("wx_app_pkg");
            req.sign = json.getString("wx_app_sign");
            req.extData = "app data"; // optional
            Log.e("wxPay", "正常调起支付");

            IWXAPI api = WXAPIFactory.createWXAPI(CreateOrderActivity.this, req.appId);
            // 在支付之前，如果应用没有注册到微信，应该先调用IWXMsg.registerApp将应用注册到微信

            api.sendReq(req);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 调用支付包充值
     *
     * @param data
     */
    private void launchZfb(String data) {
        new Thread() {
            public void run() {

                PayTask alipay = new PayTask(CreateOrderActivity.this);
                String result = alipay
                        .pay(data, true);

                Message msg = new Message();
                msg.what = 0;
                msg.obj = result;
                handler.sendMessage(msg);
            }
        }.start();
    }


    /**
     * 各种支付回调处理
     */
    Handler handler = new Handler(msg -> {
        switch (msg.what) {
            case 0:
                Context context = CreateOrderActivity.this;
                PayResult result = new PayResult((String) msg.obj);
                if (result.resultStatus.equals("9000")) {
                    ToastUtil.showMessage(context, getString(R.string.com_alipay_success),
                            Toast.LENGTH_SHORT);
                    //todo  指派订单
                } else {
                    ToastUtil.showMessage(context, getString(R.string.com_alipay_failed),
                            Toast.LENGTH_SHORT);
                }
                break;
        }
        return true;
    });



    public void AssginOrder(long orderId){
        Observable<Object> observable = ApiManager.getInstance().createApi(Config.HOST, CarPoolApiService.class)
                .assginOrder(orderId, EmUtil.getEmployId())
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(this, true, true, object -> {
            finish();
        })));
    }

}
