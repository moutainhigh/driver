package com.easymi.common.activity;

import android.content.Intent;
import android.content.res.Resources;
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
import com.easymi.common.CommApiService;
import com.easymi.common.R;
import com.easymi.common.entity.SeatBean;
import com.easymi.common.entity.SeatQueryBean;
import com.easymi.component.Config;
import com.easymi.component.base.RxBaseActivity;
import com.easymi.component.network.ApiManager;
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
    private android.widget.RelativeLayout itemSeatSelectHeaderRlChildSelect;
    private TextView itemSeatSelectHeaderTvChildSelect;
    private SeatQueryBean seatQueryBean;
    private List<SeatBean> childSeats;
    private int size8;
    private DecimalFormat decimalFormat;
    private double total;
    private float itemMainWidth;
    private float itemSubWidth;
    private List<SeatBean> chooseSeatList;
    private boolean needRefresh;


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
                    intent.putExtra("data", chooseSeatList);
                    intent.putExtra("prise", total);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        seatSelectRv = findViewById(R.id.seatSelectRv);
        seatQueryBean = (SeatQueryBean) getIntent().getSerializableExtra("seatQueryBean");
        if (seatQueryBean == null) {
            ToastUtil.showMessage(this, "数据发生错误,请重试");
            finish();
        }
        decimalFormat = new DecimalFormat("0.00");
        chooseSeatList = (List<SeatBean>) getIntent().getSerializableExtra("chooseSeatList");
        initRecyclerView();
        getData();
    }

    private void getData() {
        Observable<EmResult2<List<SeatBean>>> observable;
        if (TextUtils.equals(seatQueryBean.type, Config.COUNTRY)) {
            observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                    .queryBusSeats(seatQueryBean.id, seatQueryBean.startId, seatQueryBean.endId);
        } else {
            observable = ApiManager.getInstance().createApi(Config.HOST, CommApiService.class)
                    .queryCarpoolSeats(seatQueryBean.timeSlotId, seatQueryBean.startId, seatQueryBean.endId);
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
            itemSeatSelectHeaderTvChildSelect.setHint("点击可选择儿童座");
        } else {
            itemSeatSelectHeaderTvChildSelect.setHint("暂无儿童座");
        }
        itemSeatSelectHeaderRlChildSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createDialog();
            }
        });

        if (needRefresh) {
            changeData();
        }
    }

    private void bindData(List<SeatBean> seatBeans) {
        needRefresh = false;
        if (chooseSeatList != null && !chooseSeatList.isEmpty()) {
            for (SeatBean seatBean : chooseSeatList) {
                for (SeatBean bean : seatBeans) {
                    if (seatBean.sort == bean.sort) {
                        bean.isChoose = seatBean.isChoose;
                        bean.isChild = seatBean.isChild;
                        if (bean.isChoose && bean.isChild == 1) {
                            bean.isDialogSelect = true;
                        }
                        needRefresh = true;
                    }
                }
            }
        }
        int size16 = DensityUtil.dp2px(this, 16);
        size8 = DensityUtil.dp2px(this, 8);
        itemMainWidth = ((float) Resources.getSystem().getDisplayMetrics().widthPixels) / 3 - size16 - size8;
        itemSubWidth = ((float) Resources.getSystem().getDisplayMetrics().widthPixels) / 3 - size16;

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
                seatBean.type = 2;
                if (seatBean.child == 1 && seatBean.status == 1) {
                    childSeats.add(seatBean);
                }
                if (i == 0 || i == 2 || i == 4) {
                    seatBean.paddingLeft = size16;
                    seatBean.paddingRight = size8;
                } else if (i == 1 || i == 6) {
                    seatBean.paddingRight = size16;
                    seatBean.paddingLeft = size8;
                } else {
                    seatBean.paddingLeft = size8;
                    seatBean.paddingRight = size8;
                }
                seatBean.paddingBottom = size16;
            }
            createData(seatBeans);
        } else if (seatBeans.size() == 5) {
            for (int i = 0; i < 5; i++) {
                seatBean = seatBeans.get(i);
                seatBean.type = 1;
                if (seatBean.child == 1 && seatBean.status == 1) {
                    childSeats.add(seatBean);
                }
                if (i == 0 || i == 2) {
                    seatBean.paddingLeft = size16;
                    seatBean.paddingRight = size8;
                } else if (i == 1 || i == 4) {
                    seatBean.paddingRight = size16;
                    seatBean.paddingLeft = size8;
                } else {
                    seatBean.paddingLeft = size8;
                    seatBean.paddingRight = size8;
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
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < childSeats.size(); i++) {
            SeatBean childSeat = childSeats.get(i);
            if (childSeat.isDialogSelect) {

                if (TextUtils.isEmpty(stringBuilder)) {
                    stringBuilder.append(childSeat.getDesc());
                } else {
                    stringBuilder.append("; ");
                    stringBuilder.append(childSeat.getDesc());
                }
                for (SeatBean seatBean : currentList) {
                    if (seatBean.sort == childSeat.sort) {
                        seatBean.isChild = 1;
                        seatBean.isChoose = true;
                    }
                }
            } else {
                for (SeatBean seatBean : currentList) {
                    if (seatBean.sort == childSeat.sort && seatBean.isChild == 1 && seatBean.isChoose) {
                        seatBean.isChild = 0;
                        seatBean.isChoose = false;
                    }
                }
            }
        }
        itemSeatSelectHeaderTvChildSelect.setText(stringBuilder.toString());
        calculateTotal();
        baseQuickAdapter.notifyDataSetChanged();
    }

    private void createDialog() {
        if (childSeats.size() > 0) {
            List<SeatBean> currentList = baseQuickAdapter.getData();

            for (SeatBean childSeat : childSeats) {
                childSeat.isDialogSelect = false;
            }
            for (SeatBean childSeat : childSeats) {
                for (SeatBean chooseChildSeat : currentList) {
                    if (chooseChildSeat.isChild == 1 && chooseChildSeat.isChoose && childSeat.sort == chooseChildSeat.sort) {
                        childSeat.isDialogSelect = true;
                    }
                }
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
                    FrameLayout itemBottomSheetDialogSeatSelectFl = helper.getView(R.id.itemBottomSheetDialogSeatSelectFl);
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) itemBottomSheetDialogSeatSelectFl.getLayoutParams();
                    if (helper.getLayoutPosition() % 2 == 0) {
                        layoutParams.rightMargin = size8;
                    } else {
                        layoutParams.leftMargin = size8;
                    }
                    itemBottomSheetDialogSeatSelectFl.setLayoutParams(layoutParams);
                    ImageView itemBottomSheetDialogSeatSelectIv = helper.getView(R.id.itemBottomSheetDialogSeatSelectIv);
                    setContent(item, itemBottomSheetDialogSeatSelectTv, itemBottomSheetDialogSeatSelectIv, true);
                    ImageView itemBottomSheetDialogSeatSelectIvIcon = helper.getView(R.id.itemBottomSheetDialogSeatSelectIvIcon);
                    itemBottomSheetDialogSeatSelectFl.getBackground().setLevel(item.isDialogSelect ? 2 : 1);
                    itemBottomSheetDialogSeatSelectIvIcon.setVisibility(item.isDialogSelect ? View.VISIBLE : View.GONE);
                    itemBottomSheetDialogSeatSelectTv.setTextColor(ContextCompat.getColor(SeatSelectActivity.this, R.color.colorBlue));
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
        seatSelectCtb.setTitle("选择座位")
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
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) itemSeatSelectContentFl.getLayoutParams();
                int position = helper.getLayoutPosition() - 1;
                if (baseQuickAdapter.getData().size() == 7) {
                    if (position == 0) {
                        layoutParams.width = (int) itemMainWidth;
                    } else if (position == 3) {
                        layoutParams.width = (int) itemSubWidth;
                    } else {
                        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
                    }
                } else {
                    if (position == 0) {
                        layoutParams.width = (int) itemMainWidth;
                    } else {
                        layoutParams.width = FrameLayout.LayoutParams.MATCH_PARENT;
                    }
                }
                itemSeatSelectContentFl.setLayoutParams(layoutParams);
                TextView itemSeatSelectContentTv = helper.getView(R.id.itemSeatSelectContentTv);
                ImageView itemSeatSelectContentIv = helper.getView(R.id.itemSeatSelectContentIv);
                ImageView itemSeatSelectContentIvIcon = helper.getView(R.id.itemSeatSelectContentIvIcon);
                setStatus(item, itemSeatSelectContentFl, itemSeatSelectContentTv, itemSeatSelectContentIv);
                setContent(item, itemSeatSelectContentTv, itemSeatSelectContentIvIcon, false);
            }
        };

        baseQuickAdapter.setOnItemClickListener((adapter, view, position) -> {
            SeatBean seatBean = ((SeatBean) adapter.getData().get(position));
            if (seatBean.status == 1) {
                seatBean.isChoose = !seatBean.isChoose;
                seatBean.isChild = 0;
                String currentText = itemSeatSelectHeaderTvChildSelect.getText().toString();

                if (TextUtils.isEmpty(currentText)){
                    itemSeatSelectHeaderTvChildSelect.setText(currentText);
                }else {
                    itemSeatSelectHeaderTvChildSelect.setText(currentText);
                }
//
                if (currentText.contains(seatBean.getDesc() + "; ")) {
                    itemSeatSelectHeaderTvChildSelect.setText(currentText.replace(seatBean.getDesc() + "; ", ""));
                } else if (currentText.contains(seatBean.getDesc())) {
                    itemSeatSelectHeaderTvChildSelect.setText(currentText.replace(seatBean.getDesc(), ""));
                }
//                changeData();
                calculateTotal();
                adapter.notifyItemChanged(position + adapter.getHeaderLayoutCount());
            }
        });

        baseQuickAdapter.addHeaderView(getHeaderView());
        baseQuickAdapter.addFooterView(getFooterView());

        seatSelectRv.setAdapter(baseQuickAdapter);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (position == 0 || position == baseQuickAdapter.getData().size() + 1) {
                    return 3;
                }
                if (baseQuickAdapter.getData().size() == 5) {
                    if (position == 1) {
                        return 2;
                    } else {
                        return 1;
                    }
                } else {
                    if (position == 1 || position == 4) {
                        return 2;
                    } else {
                        return 1;
                    }
                }
            }
        });
        seatSelectRv.setLayoutManager(gridLayoutManager);
        ((DefaultItemAnimator) seatSelectRv.getItemAnimator()).setSupportsChangeAnimations(false);
    }

    @NonNull
    public View getFooterView() {
        View footerView = getLayoutInflater().inflate(R.layout.item_seat_select_footer, seatSelectCtb, false);
        itemSeatSelectHeaderRlChildSelect = footerView.findViewById(R.id.itemSeatSelectHeaderRlChildSelect);
        itemSeatSelectHeaderTvChildSelect = footerView.findViewById(R.id.itemSeatSelectHeaderTvChildSelect);
        return footerView;
    }

    @NonNull
    public View getHeaderView() {
        return getLayoutInflater().inflate(R.layout.item_seat_select_header, seatSelectCtb, false);
    }

    private void setContent(SeatBean seatBean, TextView textView, ImageView itemSeatSelectContentIvIcon, boolean isDialog) {
        if (baseQuickAdapter.getData().size() == 7) {
            if (seatBean.sort == 0) {
                textView.setText("司机");
                itemSeatSelectContentIvIcon.setImageResource(R.drawable.icon_driver);
            } else if (seatBean.sort == 1) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_front_right, R.drawable.icon_disable_front_right);
            } else if (seatBean.sort == 2) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_middle_left, R.drawable.icon_disable_middle_left);
            } else if (seatBean.sort == 3) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_middle_right, R.drawable.icon_disable_middle_right);
            } else if (seatBean.sort == 4) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_behind_left, R.drawable.icon_disable_behind_left);
            } else if (seatBean.sort == 5) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_behind_middle, R.drawable.icon_disable_behind_middle);
            } else if (seatBean.sort == 6) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_behind_right, R.drawable.icon_disable_behind_right);
            }
        } else {
            if (seatBean.sort == 0) {
                textView.setText("司机");
                itemSeatSelectContentIvIcon.setImageResource(R.drawable.icon_driver);
            } else if (seatBean.sort == 1) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_front_right, R.drawable.icon_disable_front_right);
            } else if (seatBean.sort == 2) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_behind_left, R.drawable.icon_disable_behind_left);
            } else if (seatBean.sort == 3) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_behind_middle, R.drawable.icon_disable_behind_middle);
            } else if (seatBean.sort == 4) {
                setImageResource(seatBean, itemSeatSelectContentIvIcon, isDialog, R.drawable.icon_enable_behind_right, R.drawable.icon_disable_behind_right);
            }
        }
        if (!isDialog) {
            if (seatBean.status == 2) {
                textView.setText("禁售");
            } else if (seatBean.sort != 0) {
                if (seatBean.isChoose && seatBean.isChild == 1) {
                    textView.setText(" ¥" + seatBean.childPrice);
                } else {
                    textView.setText(" ¥" + seatBean.price);
                }
            }
        } else {
            textView.setText(" ¥" + seatBean.childPrice);
        }
    }

    private void setImageResource(SeatBean seatBean, ImageView itemSeatSelectContentIvIcon, boolean isDialog, int p, int p2) {
        if (isDialog) {
            itemSeatSelectContentIvIcon.setImageResource(p);
        } else {
            if (seatBean.status == 1) {
                itemSeatSelectContentIvIcon.setImageResource(p);
            } else {
                itemSeatSelectContentIvIcon.setImageResource(p2);
            }
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


    private void setStatus(SeatBean seatBean, FrameLayout frameLayout, TextView textView, ImageView imageView) {
        if (seatBean.status == 2) {
            frameLayout.getBackground().setLevel(3);
            imageView.setVisibility(View.GONE);
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorSub));
        } else if (seatBean.status == 3) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageResource(R.drawable.icon_seat_taken);
            frameLayout.getBackground().setLevel(3);
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorSub));
        } else {
            textView.setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
            if (seatBean.isChoose) {
                frameLayout.getBackground().setLevel(2);
                imageView.setVisibility(View.VISIBLE);
                imageView.setImageResource(R.drawable.icon_seat_choose);
            } else {
                frameLayout.getBackground().setLevel(1);
                imageView.setVisibility(View.GONE);
            }
        }
    }

}
