package com.easymi.zhuanche.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;

import com.easymi.component.utils.CommonUtil;
import com.easymi.component.widget.wheelview.WheelView;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelAdapter;
import com.easymi.component.widget.wheelview.adapter.AbstractWheelTextAdapter;
import com.easymi.zhuanche.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Copyright (C), 2012-2018, Sichuan Xiaoka Technology Co., Ltd.
 * FileName:TimePicker2
 * @Author: shine
 * Date: 2018/12/24 下午1:10
 * Description: 时间选择器
 * History:
 */
public class TimePicker2 extends BottomSheetDialog {

    //可选天数
    private static final int PASS_DAY = 7;
    /**
     * 预约提前时间
     */
    private int preMinute;
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
    private WheelView dayWheelView;
    private WheelView hourWheelView;
    private WheelView minuteWheelView;

    //保存时间天的部分
    private List<String> saveDays = new ArrayList<>();
    private List<String> dayList = new ArrayList<>();
    private List<String> hourList = new ArrayList<>();
    private List<String> minuteList = new ArrayList<>();

    /**
     * 选择接口
     */
    private OnSelectListener onSelectListener;

    /**
     *
     * @param context
     * @param preMinute 预约提前时间
     */
    public TimePicker2(@NonNull Context context, int preMinute) {
        super(context);
        this.preMinute = preMinute;
        initView(context);
    }

    /**
     * 初始化布局
     * @param context
     */
    @SuppressLint("InflateParams")
    private void initView(Context context) {
        mView = LayoutInflater.from(context).inflate(R.layout.zc_layout_time_picker, null);
        mView.findViewById(R.id.btn_cancel).setOnClickListener(v -> dismiss());
        mView.findViewById(R.id.btn_confirm).setOnClickListener(v -> ensure());
        dayWheelView = mView.findViewById(R.id.day);
        hourWheelView = mView.findViewById(R.id.hour);
        minuteWheelView = mView.findViewById(R.id.minute);

        initTimeData();

        initWheelView(dayWheelView, dayList);
        initWheelView(hourWheelView, hourList);
        initWheelView(minuteWheelView, minuteList);

        hourWheelView.addChangingListener((wheel, oldValue, newValue) -> setMinuteView());
        dayWheelView.addChangingListener((wheel, oldValue, newValue) -> {
            setHourView(oldValue, newValue);
            setMinuteView();
        });

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
     * 设置小时布局
     * @param oldValue
     * @param newValue
     */
    private void setHourView(int oldValue, int newValue) {
        int hourIndex = hourWheelView.getCurrentItem();
        String lastHour = hourList.get(hourIndex);  //滑动前的值
        if (newValue == 0) {
            updateHour(false);
        } else if (oldValue == 0 && newValue >= 1) {
            updateHour(true);
        } else {
            return;
        }
        AbstractWheelAdapter adapter = (AbstractWheelAdapter) hourWheelView.getViewAdapter();
        adapter.notifyDataInvalidatedEvent();
        int index = hourList.indexOf(lastHour);
        if (index < 0) {
            index = 0;
        }
        hourWheelView.setCurrentItem(index);
    }

    /**
     * 设置分钟布局
     */
    private void setMinuteView() {
        int dayIndex = dayWheelView.getCurrentItem();
        int hourIndex = hourWheelView.getCurrentItem();
        String lastMinute = minuteList.get(minuteWheelView.getCurrentItem());
        if (dayIndex == 0 && hourIndex == 0) {
            updateMinute(false);
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
        int dayIndex = dayWheelView.getCurrentItem();
        int hourIndex = hourWheelView.getCurrentItem();
        int minuteIndex = minuteWheelView.getCurrentItem();
        String hour = hourList.get(hourIndex);
        String minute = minuteList.get(minuteIndex);
        String time = "" + saveDays.get(dayIndex) + " " + hour + minute;
        long t = parseData(time);
        if (onSelectListener != null) {
            onSelectListener.onSelect(t, dayList.get(dayIndex) + hour + minute);
        }
    }

    /**
     * 初始化时间数据
     */
    private void initTimeData() {
        //初始天时间
        mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        mCalendar.set(Calendar.MILLISECOND, 0);

        //真正的星期
        int wd1 = mCalendar.get(Calendar.DAY_OF_WEEK);
        int minute = mCalendar.get(Calendar.MINUTE);
        int afterMinute = minute + preMinute;
        int dev = afterMinute % 60;

        //预约后的时间落在(50,60)区间需要补到60位置
        int offsetMinute = 0;
        if (dev >= 51 && dev <= 59) {
            offsetMinute = 60 - dev;
        }
        mCalendar.setTimeInMillis(mCalendar.getTimeInMillis() + (offsetMinute + preMinute) * 60 * 1000);
        startMillis = mCalendar.getTimeInMillis();

        //调整后的星期
        int wd2 = mCalendar.get(Calendar.DAY_OF_WEEK);

        //初始化天数
        dayList.clear();

        //获取日期在本月的第几天
        int dayOfMonth;
        for (int i = 0; i < PASS_DAY; i++) {
            dayList.add(CommonUtil.dateFormat(mCalendar.getTimeInMillis(), "MM月dd日 "));
            saveDays.add(CommonUtil.dateFormat(mCalendar.getTimeInMillis(), "yyyy-MM-dd"));
            dayOfMonth = mCalendar.get(Calendar.DAY_OF_MONTH);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1);
        }

        if (wd1 == wd2) {
            dayList.set(0,"今天 ");
            dayList.set(1,"明天 ");
            dayList.set(2,"后天 ");
        } else {
            dayList.set(0,"明天 ");
            dayList.set(1,"后天 ");
        }

        //初始小时和分钟
        updateHour(false);
        updateMinute(false);

    }

    /**
     * 更新分钟数据
     * @param all
     */
    private void updateMinute(boolean all) {
        minuteList.clear();
        mCalendar.setTimeInMillis(startMillis);
        int minute = mCalendar.get(Calendar.MINUTE);
        if (minute == 0 || all) {
            for (int i = 0; i < 6; i++) {
                minuteList.add((i * 10) + "分");
            }
        } else {
            int start = (minute + 9) / 10;
            for (int i = start; i < 6; i++) {
                minuteList.add((i * 10) + "分");
            }
        }
    }

    /**
     * 更新小时数据
     * @param all
     */
    private void updateHour(boolean all) {
        hourList.clear();
        if (all) {
            for (int i = 0; i < 24; i++) {
                hourList.add(i + "点");
            }
        } else {
            mCalendar.setTimeInMillis(startMillis);
            int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
            for (int i = hour; i < 24; i++) {
                hourList.add(i + "点");
            }
        }
    }

    /**
     * 加载适配器
     * @param wheelView
     * @param datas
     */
    private void initWheelView(WheelView wheelView, List<String> datas) {
        TimeWheelAdapter adapter = new TimeWheelAdapter(datas, wheelView.getContext());
        adapter.setItemResource(R.layout.zc_item_picker);
        adapter.setItemTextResource(R.id.tvContent);
        wheelView.setCyclic(false);
        wheelView.setCurrentItem(0);
        wheelView.setViewAdapter(adapter);
    }

    /**
     * 解析时间字符串成时间戳,例如yyyy-MM-dd HH:mm格式的字符串2000-10-10 12:00解析成时间戳.
     */
    private long parseData(String source) {
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH点mm分", Locale.getDefault());
        try {
            Date data = mFormat.parse(source);
            return data.getTime();
        } catch (ParseException ex) {
            ex.printStackTrace();
            return -1;
        }
    }

    public TimePicker2 setOnSelectListener(OnSelectListener listener) {
        this.onSelectListener = listener;
        return this;
    }

    /**
     * 选择接口
     */
    public interface OnSelectListener {
        /**
         * 确定选择监听
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
