package com.easymin.carpooling.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymin.carpooling.R;
import com.easymin.carpooling.entity.AllStation;
import com.easymin.carpooling.entity.LineBean;
import com.easymin.carpooling.entity.MyStation;
import com.easymin.carpooling.entity.TimeSlotBean;

import java.util.Iterator;
import java.util.List;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: BottomListDialog
 * @Author: hufeng
 * @Date: 2019/10/29 下午5:16
 * @Description:
 * @History:
 */
public class BottomListDialog extends BottomSheetDialog {


    private View mView;
    private WheelView wheelView;
    private OnSelectListener onSelectListener;
    private List<?> datas;

    String title;

    TextView tv_title;

    public BottomListDialog(Context context,List<?> datas) {
        super(context);
        this.datas = datas;
        initViews(context, datas);
    }

    @Override
    public void show() {
        View v = (View) mView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        //禁止下滑手势
        behavior.setHideable(false);
        super.show();
    }

    public void setTitle(String title){
        this.title = title;
        tv_title.setText(this.title);
    }

    private void initViews(Context context, List<?> datas) {
        mView = LayoutInflater.from(context).inflate(R.layout.pc_dialog_station_list, null);
        mView.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.tv_sure).setOnClickListener(v -> ensure());

        tv_title = mView.findViewById(R.id.tv_title);

        tv_title.setText(this.title);

        wheelView = mView.findViewById(R.id.wheelView);

        wheelView.setShadowColor(255,255,255);
        TimeWheelAdapter adapter = new TimeWheelAdapter(datas, wheelView.getContext());
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
        if (onSelectListener != null) {
            onSelectListener.onSelect(index);
        }
    }

    public interface OnSelectListener {
        void onSelect(int index);
    }

    public BottomListDialog setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    private class TimeWheelAdapter extends AbstractWheelTextAdapter {

        private List<?> datas;

        TimeWheelAdapter(List<?> datas, Context context) {
            super(context);
            this.datas = datas;
        }

        @Override
        protected CharSequence getItemText(int index) {
            String item = "";
            if (datas != null){
                if (datas.get(index) instanceof  LineBean){
                    LineBean lineBean = (LineBean) datas.get(index);
                    item =  datas != null ? lineBean.lineName : "";
                }else if (datas.get(index) instanceof TimeSlotBean){
                    TimeSlotBean timeSlotBean = (TimeSlotBean) datas.get(index);
                    item =  timeSlotBean.day +" "+ timeSlotBean.timeSlot+" 余票："+(timeSlotBean.tickets == null ? "充足":timeSlotBean.tickets);
                }
            }
            return item;
        }

        @Override
        public int getItemsCount() {
            return datas != null ? datas.size() : 0;
        }
    }

}