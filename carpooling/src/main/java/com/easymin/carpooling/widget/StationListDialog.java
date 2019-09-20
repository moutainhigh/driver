package com.easymin.carpooling.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.MyStation;

import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: StationListDialog
 * @Author: hufeng
 * @Date: 2019/9/17 下午7:15
 * @Description:
 * @History:
 */
public class StationListDialog extends BottomSheetDialog {


    private View mView;
    private WheelView wheelView;
    private OnSelectListener onSelectListener;
    private List<MyStation> stations;

    public StationListDialog(@NonNull Context context, @NonNull List<MyStation> stations) {
        super(context);
        this.stations = stations;
        initViews(context, stations);
    }

    @Override
    public void show() {
        View v = (View) mView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        //禁止下滑手势
        behavior.setHideable(false);
        super.show();
    }

    private void initViews(Context context, List<MyStation> companies) {
        mView = LayoutInflater.from(context).inflate(R.layout.pc_dialog_station_list, null);
        mView.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.tv_sure).setOnClickListener(v -> ensure());
        wheelView = mView.findViewById(R.id.wheelView);

        wheelView.setShadowColor(255,255,255);
        TimeWheelAdapter adapter = new TimeWheelAdapter(companies, wheelView.getContext());
        adapter.setItemResource(R.layout.com_item_picker);
        adapter.setItemTextResource(R.id.tvContent);
        wheelView.setCyclic(false);
        wheelView.setCurrentItem(0);
        wheelView.setViewAdapter(adapter);

        setCancelable(true);
        setContentView(mView);
    }

    private void ensure() {
        dismiss();
        int index = wheelView.getCurrentItem();
        MyStation station = null;
        if (index >= 0 && index < stations.size()) {
            station = stations.get(index);
        }
        if (onSelectListener != null && station != null) {
            onSelectListener.onSelect(index);
        }
    }

    public interface OnSelectListener {
        void onSelect(int index);
    }

    public StationListDialog setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    private class TimeWheelAdapter extends AbstractWheelTextAdapter {

        private List<MyStation> datas;

        TimeWheelAdapter(List<MyStation> datas, Context context) {
            super(context);
            this.datas = datas;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return datas != null ? datas.get(index).stationName : "";
        }

        @Override
        public int getItemsCount() {
            return datas != null ? datas.size() : 0;
        }
    }

}
