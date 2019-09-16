package com.easymi.common.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.easymi.common.R;
import com.easymi.common.entity.SeatBean;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.HaveErrSubscriberListener;
import com.easymi.component.network.HttpResultFunc2;
import com.easymi.component.network.MySubscriber;
import com.easymi.component.result.EmResult2;
import com.easymi.component.utils.DensityUtil;
import com.easymi.component.utils.ToastUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SeatSelectActivity extends RxBaseActivity {


    private com.easymi.component.widget.CusToolbar seatSelectCtb;
    private android.widget.TextView seatSelectTvPrice;
    private android.widget.Button seatSelectBt;
    private android.support.v7.widget.RecyclerView seatSelectRv;
    private BaseQuickAdapter<SeatBean, BaseViewHolder> baseQuickAdapter;
    private TextView itemSeatSelectHeaderStartStation;
    private TextView itemSeatSelectHeaderEndStation;
    private TextView itemSeatSelectHeaderStartTime;
    private TextView itemSeatSelectHeaderEndTime;
    private TextView itemSeatSelectHeaderDesc;
    private TextView itemSeatSelectFooterTvPhone;
    private TextView itemSeatSelectFooterTvInfo;
    private android.widget.RelativeLayout itemSeatSelectHeaderRlChildSelect;
    private android.widget.ImageView itemSeatSelectHeaderIvChildSelect;
    private TextView itemSeatSelectHeaderTvChildSelect;
//    private QueryLineListBean queryLineListBean;
    private List<SeatBean> childSeats;
    private int size3;
    private DecimalFormat decimalFormat;
    private String type;
    private double total;


    @Override
    public boolean isEnableSwipe() {
        return false;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_seat_select;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        seatSelectCtb = findViewById(R.id.seatSelectCtb);
        seatSelectTvPrice = findViewById(R.id.seatSelectTvPrice);
        seatSelectBt = findViewById(R.id.seatSelectBt);
        seatSelectBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<SeatBean> data = baseQuickAdapter.getData();
                ArrayList<SeatBean> chooseSeatList = new ArrayList<>();
                for (SeatBean datum : data) {
                    if (datum.isChoose) {
                        chooseSeatList.add(datum);
                    }
                }
                if (chooseSeatList.isEmpty()) {
                    ToastUtil.showMessage(SeatSelectActivity.this, "请选择座位");
                } else {
                    Intent intent = new Intent();
//                    intent.putExtra("queryLineListBean", queryLineListBean);
                    intent.putExtra("data", chooseSeatList);
                    intent.putExtra("size", baseQuickAdapter.getData().size());
                    intent.putExtra("prise", total);
                    intent.putExtra("type", type);
                    setResult(RESULT_OK,intent);
                }
            }
        });
        seatSelectRv = findViewById(R.id.seatSelectRv);
//        queryLineListBean = (QueryLineListBean) getIntent().getSerializableExtra("queryLineListBean");
//        if (queryLineListBean == null) {
//            ToastUtil.showMessage(this, "数据发生错误,请重试");
//            finish();
//        }
        type = getIntent().getStringExtra("type");
        decimalFormat = new DecimalFormat("0.00");
        initRecyclerView();
        getData();
    }

    private void getData() {
        Observable<EmResult2<List<SeatBean>>> observable = null;
        if (TextUtils.equals(type, Config.COUNTRY)) {
//            observable = ApiManager.getInstance().createApi(Config.HOST, CommonService.class)
//                    .queryBusSeats(queryLineListBean.id, queryLineListBean.startId, queryLineListBean.endId);
        } else {
//            observable = ApiManager.getInstance().createApi(Config.HOST, CommonService.class)
//                    .queryCarpoolSeats(queryLineListBean.timeSlotId, queryLineListBean.startId, queryLineListBean.endId);
        }

        mRxManager.add(observable
                .map(new HttpResultFunc2<>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new MySubscriber<List<SeatBean>>(this, true, false, new HaveErrSubscriberListener<List<SeatBean>>() {
                    @Override
                    public void onNext(List<SeatBean> seatBeans) {
                        bindData(seatBeans);
                    }

                    @Override
                    public void onError(int code) {
                        ToastUtil.showMessage(SeatSelectActivity.this, "数据发生错误,请重试");
                        finish();
                    }
                })));
    }

    private void createData(List<SeatBean> seatBeans) {
        baseQuickAdapter.setNewData(seatBeans);
        if (childSeats.size() > 0) {
            itemSeatSelectHeaderTvChildSelect.setVisibility(View.GONE);
            itemSeatSelectHeaderIvChildSelect.setVisibility(View.VISIBLE);
        } else {
            itemSeatSelectHeaderTvChildSelect.setVisibility(View.VISIBLE);
            itemSeatSelectHeaderIvChildSelect.setVisibility(View.GONE);
        }
        itemSeatSelectHeaderRlChildSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });
    }

    private void bindData(List<SeatBean> seatBeans) {
        int size16 = DensityUtil.dp2px(this, 16);
        size3 = DensityUtil.dp2px(this, 3);
        SeatBean seatBean = new SeatBean();
        seatBean.status = 2;
        seatBeans.add(0, seatBean);

        if (childSeats == null) {
            childSeats = new ArrayList<>();
        } else {
            childSeats.clear();
        }

        if (seatBeans.size() == 7) {
            for (int i = 0; i < 7; i++) {
                seatBean = seatBeans.get(i);
                if (seatBean.child == 1 && seatBean.status == 1) {
                    childSeats.add(seatBean);
                }
                if (i == 0 || i == 2 || i == 4) {
                    seatBean.paddingLeft = size16;
                    seatBean.paddingRight = size3;
                } else if (i == 1 || i == 3 || i == 6) {
                    seatBean.paddingRight = size16;
                    seatBean.paddingLeft = size3;
                } else {
                    seatBean.paddingLeft = size3;
                    seatBean.paddingRight = size3;
                }
                seatBean.paddingBottom = size16;
            }
            createData(seatBeans);
        } else if (seatBeans.size() == 5) {
            for (int i = 0; i < 5; i++) {
                seatBean = seatBeans.get(i);
                if (seatBean.child == 1 && seatBean.status == 1) {
                    childSeats.add(seatBean);
                }
                if (i == 0 || i == 2) {
                    seatBean.paddingLeft = size16;
                    seatBean.paddingRight = size3;
                } else if (i == 1 || i == 4) {
                    seatBean.paddingRight = size16;
                    seatBean.paddingLeft = size3;
                } else {
                    seatBean.paddingLeft = size3;
                    seatBean.paddingRight = size3;
                }
                seatBean.paddingBottom = size16;
            }
            createData(seatBeans);
        } else {
            ToastUtil.showMessage(this, "数据发生错误,请重试");
            finish();
        }
    }

    private void changeData() {
        List<SeatBean> currentList = baseQuickAdapter.getData();
        for (SeatBean childSeat : childSeats) {
            if (childSeat.isDialogSelect) {
                for (SeatBean seatBean : currentList) {
                    if (seatBean.sort == childSeat.sort) {
                        seatBean.isChild = 1;
                        seatBean.isChoose = true;
                    }
                }
            }
        }
        calculateTotal();
        baseQuickAdapter.notifyDataSetChanged();
    }

    private void createDialog() {
        if (itemSeatSelectHeaderIvChildSelect.isShown()) {
            for (SeatBean childSeat : childSeats) {
                childSeat.isDialogSelect = false;
            }

            BottomSheetDialog dialog = new BottomSheetDialog(this);

            View view = getLayoutInflater().inflate(R.layout.bottom_sheet_dialog_seat_select, seatSelectCtb, false);
            ImageView bottomSheetDialogSeatSelectIv = view.findViewById(R.id.bottomSheetDialogSeatSelectIv);
            RecyclerView bottomSheetDialogSeatSelectRv = view.findViewById(R.id.bottomSheetDialogSeatSelectRv);
            TextView bottomSheetDialogSeatSelectTv = view.findViewById(R.id.bottomSheetDialogSeatSelectTv);

            ((DefaultItemAnimator) bottomSheetDialogSeatSelectRv.getItemAnimator()).setSupportsChangeAnimations(false);

            View.OnClickListener disMissOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.bottomSheetDialogSeatSelectTv) {
                        changeData();
                    }
                    dialog.dismiss();
                }
            };
            bottomSheetDialogSeatSelectIv.setOnClickListener(disMissOnClickListener);
            bottomSheetDialogSeatSelectTv.setOnClickListener(disMissOnClickListener);

            bottomSheetDialogSeatSelectRv.setLayoutManager(new GridLayoutManager(this, 2));

            BaseQuickAdapter<SeatBean, BaseViewHolder> adapter = new BaseQuickAdapter<SeatBean, BaseViewHolder>
                    (R.layout.item_bottom_sheet_dialog_seat_select, childSeats) {
                @Override
                protected void convert(BaseViewHolder helper, SeatBean item) {
                    TextView itemBottomSheetDialogSeatSelectTv = helper.getView(R.id.itemBottomSheetDialogSeatSelectTv);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemBottomSheetDialogSeatSelectTv.getLayoutParams();
                    if (helper.getLayoutPosition() % 2 == 0) {
                        layoutParams.rightMargin = size3;
                    } else {
                        layoutParams.leftMargin = size3;
                    }
                    itemBottomSheetDialogSeatSelectTv.setLayoutParams(layoutParams);
                    setText(item, itemBottomSheetDialogSeatSelectTv, true);
                    itemBottomSheetDialogSeatSelectTv.getBackground().setLevel(item.isDialogSelect ? 3 : 4);
                    itemBottomSheetDialogSeatSelectTv.setTextColor(
                            ContextCompat.getColor(SeatSelectActivity.this, item.isDialogSelect ? R.color.white : R.color.colorBlackLight));
                }
            };
            bottomSheetDialogSeatSelectRv.setAdapter(adapter);
            adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    SeatBean seatBean = (SeatBean) adapter.getData().get(position);
                    seatBean.isDialogSelect = !seatBean.isDialogSelect;
                    adapter.notifyItemChanged(position);
                }
            });
            dialog.setContentView(view);
            Window window = dialog.getWindow();
            if (window != null) {
                dialog.getWindow().findViewById(R.id.design_bottom_sheet)
                        .setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            }
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }
    }

    @Override
    public void initToolBar() {
        super.initToolBar();
        seatSelectCtb.setTitle("司机补单")
                .setLeftIcon(R.drawable.ic_arrow_back, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
    }

    @Override
    public void initRecyclerView() {
        super.initRecyclerView();

        baseQuickAdapter = new BaseQuickAdapter<SeatBean, BaseViewHolder>(R.layout.item_seat_select_content) {
            @Override
            protected void convert(BaseViewHolder helper, SeatBean item) {
                helper.itemView.setPadding(item.paddingLeft, 0, item.paddingRight, item.paddingBottom);
                FrameLayout itemSeatSelectContentFl = helper.getView(R.id.itemSeatSelectContentFl);
                TextView itemSeatSelectContentTv = helper.getView(R.id.itemSeatSelectContentTv);
                setStatus(item, itemSeatSelectContentFl, itemSeatSelectContentTv);
                setText(item, itemSeatSelectContentTv, false);
            }
        };

        baseQuickAdapter.setOnItemClickListener((adapter, view, position) -> {
            SeatBean seatBean = ((SeatBean) adapter.getData().get(position));
            if (seatBean.status == 1) {
                seatBean.isChoose = !seatBean.isChoose;
                seatBean.isChild = 0;
                calculateTotal();
                adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
            }
        });

        baseQuickAdapter.addHeaderView(getHeaderView());

        seatSelectRv.setAdapter(baseQuickAdapter);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 6);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == baseQuickAdapter.getData().size() + 1) {
                    return 6;
                }
                if (baseQuickAdapter.getData().size() == 5) {
                    if (position < 3) {
                        return 3;
                    } else {
                        return 2;
                    }
                } else {
                    if (position < 5) {
                        return 3;
                    } else {
                        return 2;
                    }
                }
            }
        });
        seatSelectRv.setLayoutManager(gridLayoutManager);
        ((DefaultItemAnimator) seatSelectRv.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @NonNull
    public View getHeaderView() {
        View headerView = getLayoutInflater().inflate(R.layout.item_seat_select_header, seatSelectCtb, false);
        itemSeatSelectHeaderRlChildSelect = headerView.findViewById(R.id.itemSeatSelectHeaderRlChildSelect);
        itemSeatSelectHeaderIvChildSelect = headerView.findViewById(R.id.itemSeatSelectHeaderIvChildSelect);
        itemSeatSelectHeaderTvChildSelect = headerView.findViewById(R.id.itemSeatSelectHeaderTvChildSelect);
        itemSeatSelectHeaderStartStation = headerView.findViewById(R.id.itemSeatSelectHeaderStartStation);
        itemSeatSelectHeaderEndStation = headerView.findViewById(R.id.itemSeatSelectHeaderEndStation);
        itemSeatSelectHeaderStartTime = headerView.findViewById(R.id.itemSeatSelectHeaderStartTime);
        itemSeatSelectHeaderEndTime = headerView.findViewById(R.id.itemSeatSelectHeaderEndTime);
        itemSeatSelectHeaderDesc = headerView.findViewById(R.id.itemSeatSelectHeaderDesc);

//        itemSeatSelectHeaderStartStation.setText(queryLineListBean.startStationName);
//        itemSeatSelectHeaderEndStation.setText(queryLineListBean.endStationName);

        return headerView;
    }

    private void setText(SeatBean seatBean, TextView textView, boolean isDialog) {
        if (baseQuickAdapter.getData().size() == 7) {
            if (seatBean.sort == 0) {
                textView.setText("司机");
            } else if (seatBean.sort == 1) {
                textView.setText("前右");
            } else if (seatBean.sort == 2) {
                textView.setText("中左");
            } else if (seatBean.sort == 3) {
                textView.setText("中右");
            } else if (seatBean.sort == 4) {
                textView.setText("后左");
            } else if (seatBean.sort == 5) {
                textView.setText("后中");
            } else if (seatBean.sort == 6) {
                textView.setText("后右");
            }
        } else {
            if (seatBean.sort == 0) {
                textView.setText("司机");
            } else if (seatBean.sort == 1) {
                textView.setText("前右");
            } else if (seatBean.sort == 2) {
                textView.setText("后左");
            } else if (seatBean.sort == 3) {
                textView.setText("后中");
            } else if (seatBean.sort == 4) {
                textView.setText("后右");
            }
        }
        if (!isDialog) {
            if (seatBean.isChoose && seatBean.isChild == 1) {
                textView.setText(textView.getText() + "(儿童) ¥" + decimalFormat.format(seatBean.childPrice));
            } else {
                if (seatBean.price != 0) {
                    textView.setText(textView.getText() + " ¥" + decimalFormat.format(seatBean.price));
                }
            }
        } else {
            textView.setText(textView.getText() + " ¥" + decimalFormat.format(seatBean.childPrice));
        }
    }

    private void calculateTotal() {
        total = 0;
        for (SeatBean datum : baseQuickAdapter.getData()) {
            if (datum.isChoose) {
                if (datum.isChild == 1) {
                    total += datum.childPrice;
                } else {
                    total += datum.price;
                }
            }
        }
        seatSelectTvPrice.setText("¥" + decimalFormat.format(total));
    }


    private void setStatus(SeatBean seatBean, FrameLayout frameLayout, TextView textView) {
        if (seatBean.status == 2) {
            frameLayout.getBackground().setLevel(1);
            textView.getCompoundDrawables()[1].setLevel(2);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if (seatBean.status == 3) {
            frameLayout.getBackground().setLevel(2);
            textView.getCompoundDrawables()[1].setLevel(2);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else {
            if (seatBean.isChoose) {
                frameLayout.getBackground().setLevel(3);
                if (seatBean.isChild == 1) {
                    textView.getCompoundDrawables()[1].setLevel(1);
                } else {
                    textView.getCompoundDrawables()[1].setLevel(2);
                }
                textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            } else {
                frameLayout.getBackground().setLevel(4);
                textView.getCompoundDrawables()[1].setLevel(3);
                textView.setTextColor(ContextCompat.getColor(this, R.color.colorBlackLight));
            }
        }
    }

}
