package com.easymin.custombus.dialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelAdapter;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymin.custombus.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:TimePickerDialog
 *
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 时间选择器
 * History:
 */
public class TimePickerDialog extends BottomSheetDialog {

    /**
     * 开始时间
     */
    private long startMillis;
    /**
     * 日历控件
     */
    private Calendar mCalendar;

    /**
     * 选择控件
     */
    private View mView;
    private WheelView hourWheelView;
    private WheelView minuteWheelView;

    //保存时间天的部分
    private List<String> hourList = new ArrayList<>();
    private List<String> minuteList = new ArrayList<>();

    /**
     * 选择接口
     */
    private OnSelectListener onSelectListener;
    private long chooseHourTimeStamp;
    private boolean isToday;
    private long chooseDayTimeStamp;


    public TimePickerDialog(@NonNull Context context, long chooseDayTimeStamp, long chooseHourTimeStamp, boolean isToday) {
        super(context);
        this.chooseHourTimeStamp = chooseHourTimeStamp;
        this.chooseDayTimeStamp = chooseDayTimeStamp;
        this.isToday = isToday;
        initView(context);
    }

    /**
     * 初始化布局
     *
     * @param context
     */
    @SuppressLint("InflateParams")
    private void initView(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.dzbus_layout_time_picker, null);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.btn_confirm).setOnClickListener(v -> ensure());
        hourWheelView = mView.findViewById(R.id.hour);
        minuteWheelView = mView.findViewById(R.id.minute);

        initTimeData();

        initWheelView(hourWheelView, hourList, 0);
        initWheelView(minuteWheelView, minuteList, 1);

        hourWheelView.addChangingListener((wheel, oldValue, newValue) -> setMinuteView());

        setCancelable(true);
        setContentView(mView);
    }

    @Override
    public void show() {
        View v = (View) mView.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(v);
        //禁止下滑手势
        behavior.setHideable(false);
        super.show();
    }


    /**
     * 设置分钟布局
     */
    private void setMinuteView() {
        int hourIndex = hourWheelView.getCurrentItem();
        String lastMinute = minuteList.get(minuteWheelView.getCurrentItem());
        if (hourIndex == 0) {
            updateMinute(!isToday);
        } else {
            updateMinute(true);
        }
        AbstractWheelAdapter adapter = (AbstractWheelAdapter) minuteWheelView.getViewAdapter();
        adapter.notifyDataInvalidatedEvent();
        int index = minuteList.indexOf(lastMinute);
        if (index < 0) {
            index = 0;
        }
        minuteWheelView.setCurrentItem(index);
    }

    /**
     * 确定
     */
    private void ensure() {
        dismiss();
        int hourIndex = hourWheelView.getCurrentItem();
        int minuteIndex = minuteWheelView.getCurrentItem();
        String hour = hourList.get(hourIndex).replace("点", "");
        if (hour.length() == 1) {
            hour = "0" + hour;
        }
        hour += ":";
        String minute = minuteList.get(minuteIndex).replace("分", "");
        if (minute.length() == 1) {
            minute = "0" + minute;
        }
        String time = hour + minute;
        long t = parseData(time);
        if (onSelectListener != null) {
            onSelectListener.onSelect(t, hour + minute);
        }
    }

    private long parseData(String source) {
        Calendar dayCalendar = Calendar.getInstance();
        dayCalendar.setTimeInMillis(chooseDayTimeStamp);
        SimpleDateFormat mFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            Calendar timeCalendar = Calendar.getInstance();
            timeCalendar.setTime(mFormat.parse(source));
            timeCalendar.set(Calendar.YEAR, dayCalendar.get(Calendar.YEAR));
            timeCalendar.set(Calendar.MONTH, dayCalendar.get(Calendar.MONTH));
            timeCalendar.set(Calendar.DAY_OF_YEAR, dayCalendar.get(Calendar.DAY_OF_YEAR));
            return timeCalendar.getTimeInMillis();
        } catch (ParseException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    /**
     * 初始化时间数据
     */
    private void initTimeData() {
        //初始天时间
        mCalendar = Calendar.getInstance();
        if (chooseHourTimeStamp != 0) {
            mCalendar.setTimeInMillis(chooseHourTimeStamp);
        } else {
            if (isToday) {
                mCalendar.setTimeInMillis(System.currentTimeMillis());
            } else {
                mCalendar.setTimeInMillis(0);
            }
        }
        startMillis = mCalendar.getTimeInMillis();

        updateHour(!isToday);
        updateMinute(!isToday);
    }

    /**
     * 更新分钟数据
     *
     * @param all
     */
    private void updateMinute(boolean all) {
        minuteList.clear();
        if (all) {
            for (int i = 0; i < 12; i++) {
                minuteList.add((i * 5) + "分");
            }
        } else {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            int minute = mCalendar.get(Calendar.MINUTE);
            int extra = 55 - minute;
            if (extra > 0) {
                int times = extra / 5;
                int offset = extra % 5;
                if (offset > 0) {
                    minute += offset;
                    times++;
                } else {
                    minute += 5;
                }
                for (int i = 0; i < times; i++) {
                    minuteList.add((minute + i * 5) + "分");
                }
            } else {
                for (int i = 0; i < 12; i++) {
                    minuteList.add(i * 5 + "分");
                }
            }
        }
    }

    /**
     * 更新小时数据
     *
     * @param all
     */
    private void updateHour(boolean all) {
        hourList.clear();
        if (all) {
            for (int i = 0; i < 24; i++) {
                hourList.add(i + "点");
            }
        } else {
            mCalendar.setTimeInMillis(System.currentTimeMillis());
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            for (int i = hour; i < 24; i++) {
                hourList.add(i + "点");
            }
            int minute = mCalendar.get(Calendar.MINUTE);
            if (minute >= 55) {
                hourList.remove(0);
            }
        }
    }

    /**
     * 加载适配器
     *
     * @param wheelView
     * @param datas
     */
    private void initWheelView(WheelView wheelView, List<String> datas, int type) {
        TimeWheelAdapter adapter = new TimeWheelAdapter(datas, wheelView.getContext());
        adapter.setItemResource(R.layout.dzbus_item_picker);
        adapter.setItemTextResource(R.id.tvContent);
        wheelView.setCyclic(false);
        wheelView.setViewAdapter(adapter);
        if (type == 0 && chooseHourTimeStamp != 0) {
            mCalendar.setTimeInMillis(chooseHourTimeStamp);
            int current = 0;
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            for (int i = 0; i < hourList.size(); i++) {
                if (hourList.get(i).contains(String.valueOf(hour))) {
                    current = i;
                    break;
                }
            }
            wheelView.setCurrentItem(current);
        } else if (type == 1 && chooseHourTimeStamp != 0) {
            mCalendar.setTimeInMillis(chooseHourTimeStamp);
            int current = 0;
            int minute = mCalendar.get(Calendar.MINUTE);
            for (int i = 0; i < minuteList.size(); i++) {
                if (minuteList.get(i).contains(String.valueOf(minute))) {
                    current = i;
                    break;
                }
            }
            wheelView.setCurrentItem(current);
        } else {
            wheelView.setCurrentItem(0);
        }
    }

    public TimePickerDialog setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    /**
     * 选择接口
     */
    public interface OnSelectListener {
        /**
         * 确定选择监听
         *
         * @param time
         * @param timeStr
         */
        void onSelect(long time, String timeStr);
    }

    /**
     * 时间适配器
     */
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


}
