package com.easymi.daijia.fragment.create;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.easymi.component.Config;
import com.easymi.component.activity.PlaceActivity;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxLazyFragment;
import com.easymi.component.entity.EmLoc;
import com.easymi.component.rxmvp.RxManager;
import com.easymi.component.utils.EmUtil;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.TimePickerView;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.Budget;
import com.easymi.daijia.entity.DJType;
import com.easymi.daijia.entity.Passenger;
import com.easymi.daijia.flowMvp.FlowActivity;
import com.easymi.daijia.result.BudgetResult;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.DJTypeResult;
import com.easymi.daijia.result.PassengerResult;
import com.google.gson.Gson;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by liuzihao on 2017/11/16.
 * 反射调用
 */

public class CreateDJFragment extends RxLazyFragment implements CreateDJContract.View {

    TextView timeText;
    TextView nameText;
    EditText phoneText;
    TextView startPlace;
    TextView endPlace;
    TextView esMoney;
    TextView about;
    TextView unit;

    Button createOrder;
    TabLayout tabLayout;

    LinearLayout esMoneyCon;

    private DJType selectedDJType = null;
    private Passenger passenger = null;

    private PoiItem startPoi = null;
    private PoiItem endPoi = null;

    private Double distance;//单位千米
    private Integer duration;//单位分钟

    private Budget budget;//预估价格

    private CreateDJPresenter presenter;

    private static final int START_CODE = 0X00;
    private static final int END_CODE = 0X01;

    @Override
    public int getLayoutResId() {
        return R.layout.daijia_create_fragment;
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

        presenter = new CreateDJPresenter(this, getActivity());
        isPrepared = false;
        findById();

        init();

        presenter.queryDJType();//查询代驾子类型
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
        day = getString(R.string.today);
        hourStr = getString(R.string.now);
        timeText.setOnClickListener(view -> showTimePickDialog(timeText));
        initPhoneEdit();
        initPlace();
        createOrder.setOnClickListener(view -> {
            if (selectedDJType == null) {
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
                    TimePickerView.getTime(day, hourStr, minStr) / 1000, startPoi.getTitle(),
                    startPoi.getLatLonPoint().getLatitude(), startPoi.getLatLonPoint().getLongitude(),
                    endPoi == null ? "" : endPoi.getTitle(),
                    endPoi == null ? null : endPoi.getLatLonPoint().getLatitude(),
                    endPoi == null ? null : endPoi.getLatLonPoint().getLongitude(),
                    budget == null ? null : budget.total, selectedDJType.id);
        });
    }

    @Override
    public RxManager getManager() {
        return mRxManager;
    }

    @Override
    public void showTypeTab(DJTypeResult result) {
        tabLayout.removeAllTabs();
        List<DJType> djTypes = result.categories;
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                selectedDJType = (DJType) tab.getTag();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        for (int i = 0; i < djTypes.size(); i++) {
            DJType djType = djTypes.get(i);
            TabLayout.Tab tab = tabLayout.newTab().setText(djType.name);
            tab.setTag(djType);
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
        selectedDJType = null;
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

    private String day;
    private String hourStr;
    private String minStr;

    @Override
    public void showTimePickDialog(TextView tv) {
        CusBottomSheetDialog bottomSheetDialog;
        bottomSheetDialog = new CusBottomSheetDialog(getActivity());
        TimePickerView timePickerView = new TimePickerView(getActivity());

        timePickerView.setPositiveButton(getString(R.string.confirm), (View v) -> {
            day = timePickerView.getDayStr();
            hourStr = timePickerView.getHourStr();
            minStr = timePickerView.getMinStr();
            tv.setText(day + hourStr + minStr);
            bottomSheetDialog.dismiss();
            getBudget();
        }).setNegativeButton(getString(R.string.cancel), v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(timePickerView);
        bottomSheetDialog.show();
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
    public void createSuc(DJOrderResult djOrderResult) {
        ToastUtil.showMessage(getActivity(), getString(R.string.create_suc));
        Intent intent = new Intent(getActivity(), FlowActivity.class);
        intent.putExtra("orderId", djOrderResult.order.orderId);
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
        if (null == passenger || selectedDJType == null) {
            return;
        }

        presenter.queryBudget(passenger.id, distance, duration,
                TimePickerView.getTime(day, hourStr, minStr), selectedDJType.id);
    }
}
