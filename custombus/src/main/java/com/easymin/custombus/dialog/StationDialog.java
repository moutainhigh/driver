package com.easymin.custombus.dialog;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.easymi.component.utils.UIDisplayHelper;
import com.easymin.custombus.R;
import com.easymin.custombus.adapter.DialogQueryStationAdapter;
import com.easymin.custombus.entity.StationBean;

import java.util.List;

public class StationDialog extends BottomSheetDialog implements View.OnClickListener {

    private DialogQueryStationAdapter adapter;
    private Button dialogQueryStationBt;
    private ImageView dialogQueryStationIv;
    private View.OnClickListener onClickListener;
    private TextView dialogQueryStationTv;
    private RecyclerView dialogQueryStationRv;
    private List<StationBean> data;
    private BottomSheetBehavior<View> behavior;

    public StationDialog(@NonNull Context context, List<StationBean> data, boolean isStart, StationBean startBean, StationBean endBean) {
        super(context);
        this.data = data;
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_query_station, null);
        dialogQueryStationBt = view.findViewById(R.id.dialog_query_station_bt);
        dialogQueryStationIv = view.findViewById(R.id.dialog_query_station_iv);
        dialogQueryStationTv = view.findViewById(R.id.dialog_query_station_tv);
        dialogQueryStationTv.setText(isStart ? "选择上车点" : "选择下车点");
        dialogQueryStationRv = view.findViewById(R.id.dialog_query_station_rv);
        dialogQueryStationIv.setOnClickListener(this);
        dialogQueryStationBt.setOnClickListener(this);

        setContentView(view);
//        if (getWindow() != null) {
//            getWindow().findViewById(com.easymi.common.R.id.design_bottom_sheet).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        }
        adapter = new DialogQueryStationAdapter(context, isStart);
        adapter.setBean(startBean, endBean);
        adapter.setData(data);
        dialogQueryStationRv.setLayoutManager(new LinearLayoutManager(context));
        dialogQueryStationRv.setAdapter(adapter);
    }

    private int getHeight() {
        int height = Resources.getSystem().getDisplayMetrics().heightPixels;
        if (getContext() != null) {
            WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            if (wm != null) {
                wm.getDefaultDisplay().getSize(point);
                height = point.y;
            }
        }
        return height;
    }

    @Override
    public void onStart() {
        super.onStart();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        View bottomSheet = getDelegate().findViewById(R.id.design_bottom_sheet);
        if (bottomSheet != null) {
            bottomSheet.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) bottomSheet.getLayoutParams();

            int fragmentHeight = 0;
            //最多6个 虽少2个
            int totalCount = data.size() > 6 ? 6 : data.size();
            //24是iv 30是tablayout 56是bt 12是recyclerView底部padding
            int totalHeight = UIDisplayHelper.dpToPx(totalCount * 68 + 24 + 30 + 56 + 12);

            //getHeight是除了statusbar的高度
            if (totalHeight > getHeight()) {
                fragmentHeight = (int) (Resources.getSystem().getDisplayMetrics().heightPixels * 0.7);
            } else {
                fragmentHeight = totalHeight;
            }
            layoutParams.height = fragmentHeight;
            bottomSheet.setLayoutParams(layoutParams);
            behavior = BottomSheetBehavior.from(bottomSheet);
            //默认有peekHeight 可能进来时候看不全
            behavior.setPeekHeight(fragmentHeight);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_query_station_iv) {
            dismiss();
        } else if (v.getId() == R.id.dialog_query_station_bt) {
            if (onClickListener != null) {
                onClickListener.onClick(v);
            }
            dismiss();
        }
    }
}
