package com.easymi.common.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Space;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.adapter.PassengerAdapter;
import com.easymi.common.entity.PassengerBean;
import com.easymi.common.entity.SeatBean;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.network.NoErrSubscriberListener;
import com.easymi.component.result.EmResult;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.ToastUtil;
import com.easymi.component.widget.CusToolbar;
import com.easymi.component.widget.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class PassengerSelectActivity extends RxBaseActivity {

    private CusToolbar passengerSelectCtb;
    private SwipeRecyclerView passengerSelectRv;
    private BaseQuickAdapter<PassengerBean, BaseViewHolder> adapter;
    private long passengerId;
    private ArrayList<SeatBean> chooseSeatList;
    private ArrayList<PassengerBean> chooseList;
    private int adultSeatCount;

    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_passenger_select;
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        passengerSelectCtb = findViewById(R.id.passengerSelectCtb);
        passengerSelectCtb.setTitle("选择乘车人")
                .setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        passengerId = getIntent().getLongExtra("passengerId", 0);
        passengerSelectRv = findViewById(R.id.passengerSelectRv);
        passengerSelectRv.setLoadMoreEnable(false);
        passengerSelectRv.setLayoutManager(new LinearLayoutManager(this));
        ((DefaultItemAnimator) passengerSelectRv.getRecyclerView().getItemAnimator()).setSupportsChangeAnimations(false);
        TextView passengerSelectTvDesc = findViewById(R.id.passengerSelectTvDesc);

        chooseSeatList = (ArrayList<SeatBean>) getIntent().getSerializableExtra("data");
        chooseList = (ArrayList<PassengerBean>) getIntent().getSerializableExtra("chooseList");
        int childSeatCount = 0;
        adultSeatCount = 0;
        for (SeatBean seatBean : chooseSeatList) {
            if (seatBean.isChild == 1) {
                childSeatCount++;
            } else {
                adultSeatCount++;
            }
        }
        StringBuilder stringBuilder = new StringBuilder("请选择");
        if (adultSeatCount > 0) {
            stringBuilder.append(adultSeatCount + "个成人");
        }
        if (childSeatCount > 0) {
            if (adultSeatCount > 0) {
                stringBuilder.append(",");
            }
            stringBuilder.append(childSeatCount + "个儿童");
        }
        passengerSelectTvDesc.setText(stringBuilder.toString());
        TextView passengerSelectTvAction = findViewById(R.id.passengerSelectTvAction);

        passengerSelectTvAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goAction();
            }
        });

        adapter = new PassengerAdapter(true);
        adapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.itemPassengerSelectContentTvDelete) {
                    deletePassenger(((PassengerBean) adapter.getData().get(position)).id);
                } else if (view.getId() == R.id.itemPassengerSelectContentRl) {
                    PassengerBean passengerBean = (PassengerBean) adapter.getData().get(position);
                    passengerBean.isSelect = !passengerBean.isSelect;
                    adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
                }
            }
        });

        addHeaderView();
        passengerSelectRv.getRecyclerView().setAdapter(adapter);
        passengerSelectRv.setOnLoadListener(new SwipeRecyclerView.OnLoadListener() {
            @Override
            public void onRefresh() {
                loadData();
            }

            @Override
            public void onLoadMore() {

            }
        });
    }

    private void goAction() {
        List<PassengerBean> listData = adapter.getData();
        ArrayList<PassengerBean> chooseData = new ArrayList<>();
        int chooseAdultCount = 0;
        for (PassengerBean listDatum : listData) {
            if (listDatum.isSelect) {
                if (listDatum.riderType == 1) {
                    chooseAdultCount++;
                }
                chooseData.add(listDatum);
            }
        }
        if (chooseData.isEmpty()) {
            ToastUtil.showMessage(PassengerSelectActivity.this, "请选择乘客");
        } else {
            if (chooseData.size() != chooseSeatList.size()) {
                ToastUtil.showMessage(PassengerSelectActivity.this, "仅能选择" + chooseSeatList.size() + "位乘客");
                return;
            }
            if (chooseAdultCount > adultSeatCount) {
                if (adultSeatCount == 0) {
                    ToastUtil.showMessage(PassengerSelectActivity.this, "仅能选择儿童");
                } else {
                    ToastUtil.showMessage(PassengerSelectActivity.this, "仅能选择" + adultSeatCount + "位成人");
                }
                return;
            }
            Intent intent = new Intent();
            intent.putExtra("data", chooseData);
            setResult(RESULT_OK, intent);
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        passengerSelectRv.setRefreshing(true);
    }

    public void addHeaderView() {
        Space headerSpaceView = new Space(this);
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(this, 10));
        headerSpaceView.setLayoutParams(layoutParams);
        adapter.addHeaderView(headerSpaceView);

        View headerView = getLayoutInflater().inflate(R.layout.item_passenger_select_header, passengerSelectRv, false);

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PassengerSelectActivity.this, PassengerAddActivity.class);
                intent.putExtra("passengerId", passengerId);
                startActivity(intent);
            }
        });
        adapter.addHeaderView(headerView);
    }

    @Override
    public void loadData() {
        super.loadData();
        mRxManager.add(ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .getPassengerList(passengerId)
                .subscribeOn(Schedulers.io())
                .map(new HttpResultFunc2<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<List<PassengerBean>>(this, false, false, new HaveErrSubscriberListener<List<PassengerBean>>() {
                    @Override
                    public void onNext(List<PassengerBean> passengerBean) {
                        passengerSelectRv.complete();
                        if (chooseList != null && !chooseList.isEmpty()) {
                            for (PassengerBean bean : passengerBean) {
                                for (PassengerBean passengerBean1 : chooseList) {
                                    if (bean.equals(passengerBean1)) {
                                        bean.isSelect = true;
                                    }
                                }
                            }
                            chooseList.clear();
                        }
                        adapter.setNewData(passengerBean);
                    }

                    @Override
                    public void onError(int code) {
                        passengerSelectRv.complete();
                    }
                })));
    }

    private void deletePassenger(long id) {
        ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                .deletePassenger(id)
                .subscribeOn(Schedulers.io())
                .filter(new HttpResultFunc<>())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<EmResult>(this, true, false, new NoErrSubscriberListener<EmResult>() {
                    @Override
                    public void onNext(EmResult emResult) {
                        List<PassengerBean> data = adapter.getData();
                        for (int i = 0; i < data.size(); i++) {
                            if (data.get(i).id == id) {
                                data.remove(i);
                                adapter.notifyItemRemoved(i + adapter.getHeaderLayoutCount());
                                break;
                            }
                        }
                    }
                }));
    }

}
