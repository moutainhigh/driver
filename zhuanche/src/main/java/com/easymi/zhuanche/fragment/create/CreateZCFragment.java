package com.easymi.zhuanche.fragment.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.base.RxLazyFragment;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.Log;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.TimeDialog;
import com.easymi.zhuanche.R;
import com.easymi.zhuanche.entity.Budget;
import com.easymi.zhuanche.entity.ZCType;
import com.easymi.zhuanche.entity.Passenger;
import com.easymi.zhuanche.flowMvp.FlowActivity;
import com.easymi.zhuanche.result.BudgetResult;
import com.easymi.zhuanche.result.ZCOrderResult;
import com.easymi.zhuanche.result.ZCTypeResult;
import com.easymi.zhuanche.result.PassengerResult;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by liuzihao on 2017/11/16.
 * 反射调用
 */

public class CreateZCFragment extends RxLazyFragment implements CreateZCContract.View {

    TextView timeText;
    EditText nameText;
    EditText phoneText;
    TextView startPlace;
    TextView endPlace;
    TextView esMoney;
    TextView about;
    TextView unit;

    Button createOrder;
    TabLayout tabLayout;

    LinearLayout esMoneyCon;

    private ZCType selectedZCType = null;
    private Passenger passenger = null;

    private PoiItem startPoi = null;
    private PoiItem endPoi = null;

    private Double distance;//单位千米
    private Integer duration;//单位分钟

    private Budget budget;//预估价格

    private CreateZCPresenter presenter;

    private static final int START_CODE = 0X00;
    private static final int END_CODE = 0X01;

    @Override
    public int getLayoutResId() {
        return R.layout.zhuanche_create_fragment;
    }

    @Override
    public void finishCreateView(Bundle state) {
        isPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || !isVisible) {
            return;
        }

        presenter = new CreateZCPresenter(this, getActivity());
        isPrepared = false;
        findById();

        init();

        presenter.queryZCType();//查询代驾子类型
    }


    @Override
    public void findById() {
        timeText = getActivity().findViewById(R.id.time_text);
        nameText = getActivity().findViewById(R.id.name_text);
        phoneText = getActivity().findViewById(R.id.phone_text);
        startPlace = getActivity().findViewById(R.id.start_place);
        endPlace = getActivity().findViewById(R.id.end_place);
        esMoney = getActivity().findViewById(R.id.es_money_text);
        createOrder = getActivity().findViewById(R.id.create_order);
        tabLayout = getActivity().findViewById(R.id.sub_tab_layout);
        esMoneyCon = getActivity().findViewById(R.id.es_money_con);
        about = getActivity().findViewById(R.id.about);
        unit = getActivity().findViewById(R.id.unit);
    }

    @Override
    public void initPhoneEdit() {
        phoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (StringUtils.isNotBlank(str) && str.length() == 11) {
                    presenter.queryPassenger(str);
                }
            }
        });
    }

    @Override
    public void initPlace() {
        //起始地默认为当前地址
        EmLoc emLoc = EmUtil.getLastLoc();
        startPlace.setText(emLoc.poiName);
        startPlace.setTextColor(getResources().getColor(R.color.text_color_black));
        startPoi = new PoiItem("", new LatLonPoint(emLoc.latitude, emLoc.longitude), emLoc.poiName, emLoc.address);

        startPlace.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PlaceActivity.class);
            intent.putExtra("hint", getString(R.string.where_start));
            startActivityForResult(intent, START_CODE);
        });
        endPlace.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PlaceActivity.class);
            intent.putExtra("hint", getString(R.string.where_end));
            startActivityForResult(intent, END_CODE);
        });
    }

    @Override
    public void init() {
        timeText.setOnClickListener(view -> showTimePickDialog(timeText));
        initPhoneEdit();
        initPlace();
        createOrder.setOnClickListener(view -> {
            String phone = phoneText.getText().toString();
            if (StringUtils.isBlank(phone) || phone.length() != 11) {
                ToastUtil.showMessage(getActivity(), getString(R.string.leagel_phone));
                return;
            }
            if (selectedZCType == null) {
                ToastUtil.showMessage(getActivity(), getString(R.string.no_type));
                return;
            }
            if (passenger == null) {
                ToastUtil.showMessage(getActivity(), getString(R.string.no_passenger));
                return;
            }
            if (startPoi == null) {
                ToastUtil.showMessage(getActivity(), getString(R.string.no_start));
                return;
            }
//            if (budget == null) {
//                ToastUtil.showMessage(getActivity(), getString(R.string.no_budget));
//                return;
//            }
            presenter.createOrder(passenger.id, passenger.name, passenger.phone,
                    orderTime == null ? System.currentTimeMillis() / 1000 : orderTime / 1000, startPoi.getTitle(),
                    startPoi.getLatLonPoint().getLatitude(), startPoi.getLatLonPoint().getLongitude(),
                    endPoi == null ? "" : endPoi.getTitle(),
                    endPoi == null ? null : endPoi.getLatLonPoint().getLatitude(),
                    endPoi == null ? null : endPoi.getLatLonPoint().getLongitude(),
                    budget == null ? null : budget.total, selectedZCType.id);
        });
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void showTypeTab(ZCTypeResult result) {
        tabLayout.removeAllTabs();
        List<ZCType> zcTypes = result.categories;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedZCType = (ZCType) tab.getTag();
                getBudget();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < zcTypes.size(); i++) {
            ZCType zcType = zcTypes.get(i);
            TabLayout.Tab tab = tabLayout.newTab().setText(zcType.name);
            tab.setTag(zcType);
            if (i == 0) {
                tab.select();//设置选中
            }
            tabLayout.addTab(tab);
        }
    }

    @Override
    public void showPassenger(PassengerResult result) {
        passenger = result.passenger;
        nameText.setText(passenger.name);
        getBudget();
    }

    @Override
    public void showBudget(BudgetResult result) {
        budget = result.budgetFee;
        esMoneyCon.setVisibility(View.VISIBLE);
        if (distance == null || duration == null) {
            about.setVisibility(View.GONE);
            unit.setText(getString(R.string.yuan_qi));
        } else {
            about.setVisibility(View.VISIBLE);
            unit.setText(getString(R.string.yuan));
        }
        esMoney.setText(String.valueOf(result.budgetFee.total));
    }

    @Override
    public void showQueryTypeErr(int tag) {
        selectedZCType = null;
    }

    @Override
    public void showQueryPasErr(int tag) {
        passenger = null;
    }

    @Override
    public void showQueryBudgetErr(int tag) {
        budget = null;
        esMoneyCon.setVisibility(View.GONE);
    }

    private Long orderTime = null;

    @Override
    public void showTimePickDialog(TextView tv) {

        TimeDialog dialog = new TimeDialog(getActivity());
        dialog.setOnTimeSelectListener((time, timeStr) -> {
            if (timeStr.contains("现在")) {
                orderTime = null;
            } else {
                orderTime = time;
            }
            tv.setText(timeStr);
            dialog.dismiss();
            getBudget();
        });
        dialog.show();
    }

    @Override
    public void showDisAndTime(float mile, float sec) {
        distance = (double) (mile / 1000);
        duration = (int) sec / 60;
        getBudget();
    }

    @Override
    public void showDisAndTimeErr() {
        distance = null;
        duration = null;
    }

    @Override
    public void createSuc(ZCOrderResult zcOrderResult) {
        ToastUtil.showMessage(getActivity(), getString(R.string.create_suc));
        Intent intent = new Intent(getActivity(), FlowActivity.class);
        intent.putExtra("orderId", zcOrderResult.order.orderId);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == START_CODE) {
                startPoi = data.getParcelableExtra("poiItem");
                Log.e("poi", startPoi.toString());
                startPlace.setText(startPoi.getTitle());
                startPlace.setTextColor(getResources().getColor(R.color.text_color_black));
            } else if (requestCode == END_CODE) {
                endPoi = data.getParcelableExtra("poiItem");
                endPlace.setText(endPoi.getTitle());
                endPlace.setTextColor(getResources().getColor(R.color.text_color_black));
            }
            if (null != startPoi && null != endPoi) {
                presenter.routePlan(startPoi.getLatLonPoint(), endPoi.getLatLonPoint());
            }
        }
    }

    private void getBudget() {
        if (null == passenger || selectedZCType == null) {
            return;
        }

        presenter.queryBudget(passenger.id, distance, duration,
                orderTime == null ? System.currentTimeMillis() / 1000 : orderTime / 1000, selectedZCType.id);
    }
}
