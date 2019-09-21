package com.easymin.carpooling.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.easymi.common.entity.CarpoolOrder;
import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.MyStation;
import com.easymin.carpooling.entity.Sequence;
import com.easymin.carpooling.entity.Station;

import java.util.Iterator;
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
    private AllStation allStation;

    public StationListDialog(@NonNull Context context, @NonNull AllStation allStation) {
        super(context);
        this.allStation = allStation;
        stations = allStation.scheduleStationVoList;
        checkStations();
        initViews(context, stations);
    }



    public void checkStations(){
        int index = 0;
        for (int i = 0;i<stations.size();i++){
            if(allStation.scheduleStationVoList.get(i).stationId == allStation.currentStationId){
                index = allStation.scheduleStationVoList.get(i).stationSequence;
            }
        }

        Iterator iterator = stations.iterator();

        while (iterator.hasNext()) {
            MyStation station = (MyStation) iterator.next();
            if (station.stationSequence < index)  {
                iterator.remove();
            }
        }
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
