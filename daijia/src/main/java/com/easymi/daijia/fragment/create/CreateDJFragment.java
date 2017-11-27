package com.easymi.daijia.fragment.create;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.easymi.component.Config;
import com.easymi.component.app.XApp;
import com.easymi.component.base.RxLazyFragment;
import com.easymi.component.entity.Employ;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.utils.StringUtils;
import com.easymi.component.widget.CusBottomSheetDialog;
import com.easymi.component.widget.TimePickerView;
import com.easymi.daijia.DJApiService;
import com.easymi.daijia.R;
import com.easymi.daijia.entity.DJType;
import com.easymi.daijia.result.DJOrderResult;
import com.easymi.daijia.result.DJTypeResult;
import com.easymi.daijia.result.PassengerResult;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by liuzihao on 2017/11/16.
 * 反射调用
 */

public class CreateDJFragment extends RxLazyFragment {

    TextView timeText;
    EditText nameText;
    EditText phoneText;
    TextView startPlace;
    TextView endPlace;
    TextView esMoney;

    Button createOrder;
    TabLayout tabLayout;

    private Employ employ;

    private long pid = -1;

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
        initView();
        isPrepared = false;
    }

    private void initView() {
        timeText = getActivity().findViewById(R.id.time_text);
        nameText = getActivity().findViewById(R.id.name_text);
        phoneText = getActivity().findViewById(R.id.phone_text);
        startPlace = getActivity().findViewById(R.id.start_place);
        endPlace = getActivity().findViewById(R.id.end_place);
        esMoney = getActivity().findViewById(R.id.es_money_text);
        createOrder = getActivity().findViewById(R.id.create_order);
        tabLayout = getActivity().findViewById(R.id.sub_tab_layout);
        initData();
        initClick();
    }

    private void initData() {
        employ = Employ.findByID(XApp.getMyPreferences().getLong(Config.SP_DRIVERID, -1));
        querySubType(employ.company_id);
    }

    private void initClick() {
        timeText.setOnClickListener(view -> showTimeDialog(timeText));
        startPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        endPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
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
                    queryPassengerInfo(str);
                }
            }
        });
    }

    CusBottomSheetDialog bottomSheetDialog;

    private void showTimeDialog(TextView tv) {
        bottomSheetDialog = new CusBottomSheetDialog(getActivity());
        TimePickerView timePickerView = new TimePickerView(getActivity());

        timePickerView.setPositiveButton(getString(R.string.confirm), (View v) -> {
            String day = timePickerView.getDayStr();
            String hourStr = timePickerView.getHourStr();
            String minStr = timePickerView.getMinStr();
            tv.setText(day + hourStr + minStr);
            bottomSheetDialog.dismiss();
        }).setNegativeButton(getString(R.string.cancel), v -> {
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(timePickerView);
        bottomSheetDialog.show();
    }

    /**
     * 查询代驾子类型
     */
    private void querySubType(Long companyId) {
        Observable<DJTypeResult> observable = ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .getBusiness(companyId, "daijia", Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, false, new HaveErrSubscriberListener<DJTypeResult>() {
            @Override
            public void onNext(DJTypeResult djTypeResult) {
                tabLayout.removeAllTabs();
                List<DJType> djTypes = djTypeResult.categories;
                for (int i = 0; i < djTypes.size(); i++) {
                    DJType djType = djTypes.get(i);
                    TabLayout.Tab tab = tabLayout.newTab().setText(djType.name);
                    tab.setTag(djType.pid);
                    tabLayout.addTab(tab);
                }
                tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        pid = (long) tab.getTag();
                    }

                    @Override
                    public void onTabUnselected(TabLayout.Tab tab) {

                    }

                    @Override
                    public void onTabReselected(TabLayout.Tab tab) {

                    }
                });
            }

            @Override
            public void onError(int code) {

            }
        })));
    }

    /**
     * 查询客户信息
     * @param phone
     */
    private void queryPassengerInfo(String phone) {
        Observable<PassengerResult> observable = ApiManager.getInstance().createApi(Config.HOST, DJApiService.class)
                .queryPassenger(employ.company_id, "小咖科技", phone, Config.APP_KEY)
                .filter(new HttpResultFunc<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

        mRxManager.add(observable.subscribe(new MySubscriber<>(getActivity(), true, true, new NoErrSubscriberListener<PassengerResult>() {
            @Override
            public void onNext(PassengerResult passengerResult) {

            }
        })));
    }

}
