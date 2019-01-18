package com.easymin.driver.securitycenter.dialog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.easymin.driver.securitycenter.R;
import com.easymin.driver.securitycenter.widget.wheelview.WheelView;
import com.easymin.driver.securitycenter.widget.wheelview.adapter.AbstractWheelTextAdapter;

/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName: TimeDialog
 *@Author: shine
 * Date: 2018/11/28 下午4:17
 * Description:
 * History:
 */
public class TimeDialog {

    WheelView start;
    WheelView end;
    WheelView center_hint;
    TextView tv_cancel;
    TextView tv_sure;

    private Context context;
    private CusBottomSheetDialog dialog;

    private ArrayList<String> startTime = new ArrayList<>();
    private ArrayList<String> endTime = new ArrayList<>();
    private ArrayList<String> center = new ArrayList<>();

    private OnSelectListener onSelectListener;

    public TimeDialog(Context context) {
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.layout_time, null, false);

        start = view.findViewById(R.id.start);
        end = view.findViewById(R.id.end);
        tv_cancel = view.findViewById(R.id.tv_cancel);
        tv_sure = view.findViewById(R.id.tv_sure);
        center_hint = view.findViewById(R.id.center_hint);

        dialog = new CusBottomSheetDialog(context);
        dialog.setCancelable(false);
        dialog.setContentView(view);
        getData();
        initView();
    }

    public void getData() {
        startTime.clear();
        endTime.clear();
        for (int i = 0; i <= 24; i++) {
            StringBuilder sb = new StringBuilder("");
            if (i < 10) {
                sb.append("0").append(i + "").append(":00");
            } else {
                sb.append(i + "").append(":00");
            }
            if (i == 0) {
                startTime.add(sb.toString());
            } else if (i == 24) {
                endTime.add(sb.toString());
            } else {
                startTime.add(sb.toString());
                endTime.add(sb.toString());
            }
        }
        center.add("到");

    }

    public void initView() {
        tv_cancel.setOnClickListener(v -> {
            dialog.dismiss();
        });
        tv_sure.setOnClickListener(v -> {
            int startIndex = start.getCurrentItem();
            int endIndex = end.getCurrentItem();
            String startStr = startTime.get(startIndex);
            String endStr = startTime.get(endIndex);
            if (onSelectListener != null) {
                onSelectListener.onSelect(startStr + "-" + endStr);
            }
            dialog.dismiss();
        });
        initWheelView(start, startTime);
        initWheelView(end, endTime);
        initWheelView(center_hint, center);
    }

    public void show() {
        dialog.show();
    }

    private void initWheelView(WheelView wheelView, List<String> datas) {
        TimeWheelAdapter adapter = new TimeWheelAdapter(datas, wheelView.getContext());
        adapter.setItemResource(R.layout.item_picker);
        adapter.setItemTextResource(R.id.tvContent);
        wheelView.setCyclic(false);
        wheelView.setCurrentItem(0);
        wheelView.setViewAdapter(adapter);
    }

    private class TimeWheelAdapter extends AbstractWheelTextAdapter {

        private List<String> datas;

        TimeWheelAdapter(List<String> datas, Context context) {
            super(context);
            this.datas = datas;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return datas != null ? datas.get(index) : "";
        }

        @Override
        public int getItemsCount() {
            return datas != null ? datas.size() : 0;
        }
    }

    public TimeDialog setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    public interface OnSelectListener {
        void onSelect(String timeStr);
    }
}
