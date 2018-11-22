package com.easymi.cityline.activity;

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
import com.easymi.cityline.CLService;
import com.easymi.cityline.R;
import com.easymi.cityline.entity.MapPositionModel;
import com.easymi.cityline.entity.Site;
import com.easymi.cityline.entity.ZXOrder;
import com.easymi.cityline.result.PriceResult;
import com.easymi.cityline.result.StationResult;
import com.easymi.component.Config;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.PhoneUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2018/11/21.
 * <p>
 * 专线补单
 */
@Route(path = "/cityline/CreateOrderActivity")
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
    TextView count_down;
    Button btn;

    private ZXOrder zxOrder = null;
    private StationResult stationResult = null;

    private MapPositionModel startSite = null;
    private MapPositionModel endSite = null;

    private PriceResult priceResult = null;

    private int seatNo = 0;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_create_order;
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
        hint_1.setText("发送成功");

        count_down = findViewById(R.id.count_down);
        count_down.setText("请提醒乘客确认支付");

        btn = findViewById(R.id.btn);
        btn.setText("返回我的订单");
        btn.setOnClickListener(view -> finish());

        banci_select.setOnClickListener(view -> {

            Intent intent = new Intent(CreateOrderActivity.this, BanciSelectActivity.class);
            startActivityForResult(intent, 0);
        });
        start_place.setOnClickListener(view -> {

            if (zxOrder == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "请先选择班次");
                return;
            }
            if (stationResult == null || stationResult.startStationVo == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "未查询到站点信息");
                return;
            }
            Intent intent = new Intent(CreateOrderActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 1);
            intent.putParcelableArrayListExtra("pos_list",
                    (ArrayList<? extends Parcelable>) stationResult.startStationVo.coordinate);
            startActivityForResult(intent, 1);
        });
        end_place.setOnClickListener(view -> {

            if (zxOrder == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "请先选择班次");
                return;
            }
            if (stationResult == null || stationResult.endStationVo == null) {
                ToastUtil.showMessage(CreateOrderActivity.this, "未查询到站点信息");
                return;
            }
            Intent intent = new Intent(CreateOrderActivity.this, SelectPlaceOnMapActivity.class);
            intent.putExtra("select_place_type", 3);
            intent.putParcelableArrayListExtra("pos_list",
                    (ArrayList<? extends Parcelable>) stationResult.endStationVo.coordinate);
            startActivityForResult(intent, 3);
        });
//        edit_phone.setOnFocusChangeListener((view, b) -> {
//            String edit = edit_phone.getText().toString();
//            if(StringUtils.isNotBlank(edit)){
//                edit_phone.setSelection(edit.length() - 1);
//            } else {
//                edit_phone.setSelection(0);
//            }
//        });
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

    private void calcPrice() {
        double money = 0;
        if (null != priceResult) {
            money = priceResult.money * seatNo;
        } else {
            money = 0;
        }
        this.money.setText("" + money);
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
        cusToolbar.setTitle("班次补单");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 0) { //班次
                ZXOrder newZxOrder = (ZXOrder) data.getSerializableExtra("zxOrder");
                if (null != zxOrder) {
                    if (zxOrder.orderId != newZxOrder.orderId) { //班次变化后  重新键入
                        zxOrder = newZxOrder;
                        initViewByZxOrder();
                    }
                } else {
                    zxOrder = newZxOrder;
                    initViewByZxOrder();
                }
                banci_select.setText(zxOrder.lineName);
            } else if (requestCode == 1) { //起点
                startSite = data.getParcelableExtra("pos_model");
                start_place.setText(startSite.getAddress());
            } else if (requestCode == 3) { //终点
                endSite = data.getParcelableExtra("pos_model");
                end_place.setText(endSite.getAddress());
            }
            setBtnEnable();
        }
    }

    private void initViewByZxOrder() {
        stationResult = null;

        start_place.setText(null);
        startSite = null;
        end_place.setText(null);
        endSite = null;

        seatNo = 0;
        num.setText("" + seatNo);
        sub.setEnabled(false);
        if (zxOrder.seats > seatNo) {
            add.setEnabled(true);
        }

        money_con.setVisibility(View.GONE);

        queryStation(zxOrder.orderId);
    }

    private void setBtnEnable() {
        if (zxOrder != null
                && stationResult != null
                && stationResult.startStationVo != null
                && stationResult.endStationVo != null
                && startSite != null
                && endSite != null
                && StringUtils.isNotBlank(edit_phone.getText().toString())
                && edit_phone.getText().toString().length() == 11
                && seatNo > 0) {
            create_order.setEnabled(true);
            create_order.setBackgroundResource(R.drawable.corners_button_selector);
        } else {
            create_order.setEnabled(false);
            create_order.setBackgroundResource(R.drawable.corners_button_press_bg);
        }
        if (seatNo == 0) {
            sub.setEnabled(false);
        } else {
            sub.setEnabled(true);
        }

        if (zxOrder != null && seatNo < zxOrder.seats) {
            add.setEnabled(true);
        } else {
            add.setEnabled(false);
        }

//        

    }

    /**
     * 查询起终站点
     *
     * @param scheduleId
     */
    private void queryStation(long scheduleId) {
        Observable<StationResult> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
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
                        queryPrice(zxOrder.lineId, stationResult.startStationVo.id, stationResult.endStationVo.id);
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
        Observable<PriceResult> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .getPrice(endStationId, lineId, startStationId)
                .map(new HttpResultFunc2<>())
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

    private void createOrder() {
        List<MapPositionModel> models = new ArrayList<>();
        models.add(startSite);
        models.add(endSite);
        String orderAddress = new Gson().toJson(models);
        Observable<Object> observable = ApiManager.getInstance().createApi(Config.HOST, CLService.class)
                .createOrder(
                        System.currentTimeMillis(),
                        "driver",
                        stationResult.endStationVo.id,
                        orderAddress,
                        edit_phone.getText().toString(),
                        zxOrder.orderId,
                        stationResult.startStationVo.id,
                        seatNo)
                .map(new HttpResultFunc2<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io());

        mRxManager.add(observable.subscribe(new MySubscriber<Object>(this,
                true,
                true,
                o -> create_suc_con.setVisibility(View.VISIBLE))));
    }
}
