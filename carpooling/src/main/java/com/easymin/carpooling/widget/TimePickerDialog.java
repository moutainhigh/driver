package com.easymin.carpooling.widget;


import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.easymi.component.widget.wheelview.OnWheelChangedListener;
import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymin.carpooling.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TimePickerDialog extends BottomSheetDialog {

    private View mView;
    private WheelView dateWheelView;
    private WheelView hourWheelView;
    private WheelView minuteWheelView;
    private TimePickerDialog.OnSelectListener onSelectListener;
    private long offsetInMills;
    String title;
    TextView tv_title;

    private Calendar calendar;
    private String startTime;
    private String endTime;
    private boolean containsToday;

    private long currentTime;
    private long endTimeStamp;
    private long startTimeStamp;

    public TimePickerDialog(Context context, String startTime, String endTime, int offset) {
        super(context);
        this.startTime = startTime;
        this.endTime = endTime;
        this.offsetInMills = offset * 60 * 1000;
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

    private void getTime() {
        currentTime = System.currentTimeMillis();

//        calendar.setTimeInMillis(currentTime);
//        calendar.set(Calendar.HOUR_OF_DAY, 0);
//        calendar.set(Calendar.MINUTE, 1);
//        currentTime = calendar.getTimeInMillis();

        int startHour = Integer.parseInt(startTime.substring(0, 2));
        int endHour = Integer.parseInt(endTime.substring(0, 2));

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, startHour);
        calendar.set(Calendar.MINUTE, Integer.parseInt(startTime.substring(startTime.length() - 2)));
        startTimeStamp = calendar.getTimeInMillis();

        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, endHour);
        calendar.set(Calendar.MINUTE, Integer.parseInt(endTime.substring(endTime.length() - 2)));
        endTimeStamp = calendar.getTimeInMillis();

        if (currentTime < endTimeStamp) {
            containsToday = true;
        } else {
            containsToday = false;
        }
    }

    private void initViews(Context context) {
        calendar = Calendar.getInstance();
        getTime();
        mView = LayoutInflater.from(context).inflate(R.layout.carpool_layout_time_picker, null);
        mView.findViewById(R.id.iv_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.tv_sure).setOnClickListener(v -> ensure());

        tv_title = mView.findViewById(R.id.tv_title);

        dateWheelView = mView.findViewById(R.id.date);
        hourWheelView = mView.findViewById(R.id.hour);
        minuteWheelView = mView.findViewById(R.id.minute);
        dateWheelView.addChangingListener((wheel, oldValue, newValue) -> {
            if (containsToday) {
                if (oldValue != 0 && newValue == 0) {
                    changeData(initHour(true), hourWheelView);
                } else if (oldValue == 0 && newValue != 0) {
                    changeData(initHour(false), hourWheelView);
                }
            }
        });
        hourWheelView.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                changeData(
                        initMinutes(containsToday && dateWheelView.getCurrentItem() == 0),
                        minuteWheelView);
            }
        });

        initAdapter(initDate(), dateWheelView);
        initAdapter(initHour(containsToday), hourWheelView);
        initAdapter(initMinutes(containsToday), minuteWheelView);
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
        int hour = Integer.parseInt(((Adapter) hourWheelView.getViewAdapter()).datas.get(hourWheelView.getCurrentItem()).replace("点", ""));
        String content;
        for (long i = startTimeStamp; i <= endTimeStamp; i += offsetInMills) {
            if (!isToday || i >= currentTime) {
                calendar.setTimeInMillis(i);
                if (calendar.get(Calendar.HOUR_OF_DAY) == hour) {
                    content = calendar.get(Calendar.MINUTE) + "分";
                    if (!minutes.contains(content)) {
                        minutes.add(content);
                    }
                }
            }
        }
        return minutes;
    }

    private List<String> initHour(boolean isToday) {
        List<String> hours = new ArrayList<>();
        String content;
        for (long i = startTimeStamp; i <= endTimeStamp; i += offsetInMills) {
            if (!isToday || i >= currentTime) {
                calendar.setTimeInMillis(i);
                content = calendar.get(Calendar.HOUR_OF_DAY) + "点";
                if (!hours.contains(content)) {
                    hours.add(content);
                }
            }
        }
        return hours;
    }

    private List<String> initDate() {
        List<String> dates = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 30; i++) {
            if (containsToday) {
                calendar.setTimeInMillis(currentTime);
            } else {
                calendar.setTimeInMillis(currentTime + 24 * 60 * 60 * 1000);
            }
            calendar.add(Calendar.DAY_OF_YEAR, i);
            builder.delete(0, builder.length());
            int month = calendar.get(Calendar.MONTH) + 1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            builder.append(calendar.get(Calendar.YEAR)).append("-").append(month > 9 ? month : "0" + month).append("-").append(day > 9 ? day : "0" + day);

            if (i == 0 && containsToday) {
                builder.append("(今天)");
            }
            dates.add(builder.toString());
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
        String date = ((Adapter) dateWheelView.getViewAdapter()).datas.get(dateWheelView.getCurrentItem()).replace("(今天)", "");
        String hour = ((Adapter) hourWheelView.getViewAdapter()).datas.get(hourWheelView.getCurrentItem());
        String minute = ((Adapter) minuteWheelView.getViewAdapter()).datas.get(minuteWheelView.getCurrentItem());

        calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));
        calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(date.length() - 2)));
        calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(hour.replace("点", "")));
        calendar.set(Calendar.MINUTE, Integer.parseInt(minute.replace("分", "")));

        if (onSelectListener != null) {
            onSelectListener.onSelect(calendar.getTimeInMillis());
        }
    }

    public interface OnSelectListener {
        void onSelect(long timeStamp);
    }

    public TimePickerDialog setOnSelectListener(TimePickerDialog.OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    private class Adapter extends AbstractWheelTextAdapter {

        private List<String> datas = new ArrayList<>();

        Adapter(List<String> datas, Context context) {
            super(context);
            this.datas.clear();
            this.datas.addAll(datas);
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
