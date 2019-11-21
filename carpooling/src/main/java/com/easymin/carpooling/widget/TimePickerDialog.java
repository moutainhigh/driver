package com.easymin.carpooling.widget;


import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easymi.component.widget.wheelview.OnWheelChangedListener;
import com.easymi.component.widget.wheelview.OnWheelScrollListener;
import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymin.carpooling.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerDialog extends BottomSheetDialog {

    private View mView;
    private WheelView dateWheelView;
    private WheelView HourWheelView;
    private WheelView MinuteWheelView;
    private BottomListDialog.OnSelectListener onSelectListener;
    private int offset = 5;

    String title;

    TextView tv_title;
    private Calendar calendar;
    private long currentTime;
    private boolean containsToday;

    public TimePickerDialog(Context context) {
        super(context);
        initViews(context);
    }

    @Override
    public void show() {
        View v = (View) mView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        //禁止下滑手势
        behavior.setHideable(false);
        super.show();
    }

    public void setTitle(String title) {
        this.title = title;
        tv_title.setText(this.title);
    }

    private void initViews(Context context) {
        currentTime = System.currentTimeMillis();
        calendar = Calendar.getInstance();

        mView = LayoutInflater.from(context).inflate(R.layout.carpool_layout_time_picker, null);
        mView.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.tv_sure).setOnClickListener(v -> ensure());

        tv_title = mView.findViewById(R.id.tv_title);

        dateWheelView = mView.findViewById(R.id.date);
        HourWheelView = mView.findViewById(R.id.hour);
        MinuteWheelView = mView.findViewById(R.id.minute);
        dateWheelView.addChangingListener((wheel, oldValue, newValue) -> {
            if (containsToday) {
                if (oldValue != 0 && newValue == 0) {
                    changeData(initHour(true), HourWheelView);
                    changeData(initMinutes(true), MinuteWheelView);
                } else if (oldValue == 0 && newValue != 0) {
                    changeData(initHour(false), HourWheelView);
                    changeData(initMinutes(false), MinuteWheelView);
                }
            }
        });
        HourWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                if (containsToday) {
                    if (oldValue != 0 && newValue == 0 && dateWheelView.getCurrentItem() == 0) {
                        changeData(initMinutes(true), MinuteWheelView);
                    } else if (oldValue == 0 && newValue != 0 && dateWheelView.getCurrentItem() == 0) {
                        changeData(initMinutes(false), MinuteWheelView);
                    }
                }
            }
        });

        initAdapter(initDate(), dateWheelView);
        initAdapter(initHour(containsToday), HourWheelView);
        initAdapter(initMinutes(containsToday), MinuteWheelView);
        setCancelable(true);
        setContentView(mView);
    }

    private void changeData(List<String> data, WheelView wheelView) {
        String lastData = ((Adapter) wheelView.getViewAdapter()).datas.get(wheelView.getCurrentItem());
        int position = data.indexOf(lastData);
        if (position < 0) {
            position = 0;
        }
        ((Adapter) wheelView.getViewAdapter()).datas = data;
        ((Adapter) wheelView.getViewAdapter()).notifyDataInvalidatedEvent();
        wheelView.setCurrentItem(position);
    }

    private List<String> initMinutes(boolean isToday) {
        List<String> minutes = new ArrayList<>();
        calendar.setTimeInMillis(currentTime);
        int minute = calendar.get(Calendar.MINUTE);
        for (int i = 0; i < 60; i++) {
            if (i % offset == 0) {
                if (isToday) {
                    if (minute <= i) {
                        minutes.add(i + "分");
                    }
                } else {
                    minutes.add(i + "分");
                }
            }
        }
        return minutes;
    }

    private List<String> initHour(boolean isToday) {
        List<String> hours = new ArrayList<>();
        calendar.setTimeInMillis(currentTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        for (int i = 0; i < 24; i++) {
            if (isToday) {
                if (hour <= i) {
                    hours.add(i + "点");
                }
            } else {
                hours.add(i + "点");
            }
        }
        return hours;
    }

    private List<String> initDate() {
        containsToday = true;
        List<String> dates = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            calendar.setTimeInMillis(currentTime);
            calendar.add(Calendar.DAY_OF_YEAR, i);
            builder.delete(0, builder.length());
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            builder.append(calendar.get(Calendar.YEAR)).append("-").append(month > 9 ? month : "0" + month).append("-").append(day > 9 ? day : "0" + day);
            if (i == 0) {
                builder.append("(今天)");
            }
            dates.add(builder.toString());
        }

        calendar.setTimeInMillis(currentTime);
        if (calendar.get(Calendar.HOUR_OF_DAY) == 23 && calendar.get(Calendar.MINUTE) >= 60 - offset) {
            containsToday = false;
            dates.remove(0);
        }
        return dates;
    }

    private void initAdapter(List<String> datas, WheelView wheelView) {
        TimePickerDialog.Adapter adapter = new TimePickerDialog.Adapter(datas, getContext());
        adapter.setItemResource(R.layout.com_item_picker);
        adapter.setItemTextResource(R.id.tvContent);
        wheelView.setCyclic(false);
        wheelView.setCurrentItem(0);
        wheelView.setViewAdapter(adapter);
        wheelView.setShadowColor(255, 255, 255);
    }


    private void ensure() {
        dismiss();
        int index = dateWheelView.getCurrentItem();
        if (onSelectListener != null) {
            onSelectListener.onSelect(index);
        }
    }

    public interface OnSelectListener {
        void onSelect(int index);
    }

    public TimePickerDialog setOnSelectListener(BottomListDialog.OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    private class Adapter extends AbstractWheelTextAdapter {

        private List<String> datas;

        Adapter(List<String> datas, Context context) {
            super(context);
            this.datas = datas;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return datas.get(index);
        }

        @Override
        public int getItemsCount() {
            return datas != null ? datas.size() : 0;
        }
    }
}
