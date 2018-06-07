package com.easymi.component.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.easymi.component.R;
import com.easymi.component.utils.TimeUtil;
import com.easymi.component.widget.wheelView.adapters.AbstractWheelTextAdapter;
import com.easymi.component.widget.wheelView.config.PickerConfig;
import com.easymi.component.widget.wheelView.wheel.OnWheelChangedListener;
import com.easymi.component.widget.wheelView.wheel.WheelView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by xyin on 2017/3/30.
 * 租车时间弹出框.
 */

public class RentDateDialog implements OnWheelChangedListener, View.OnClickListener {

    private static final int PASS_DAY = 60; //默认多少天

    private BottomSheetDialog bottomDialog;
    private Context mContext;
    private OnTimeClickListener listener;
    private WheelTextAdapter hourAdapter;

    private final View contentView;
    private final WheelView dayWheel;
    private final WheelView hourWheel;

    private List<String> dayList = new ArrayList<>(); //显示天的文本
    private List<String> hourList = new ArrayList<>(); //显示小时的文本
    private List<String> SaveDays = new ArrayList<>(); //保存天的时间字符串

    private String startStr;  //开始营业时间字符串"08:30"
    private String endStr;  //终止营业时间

    //开始和结束营业时间转化后的数字,08:30转化成8.5
    private float startTime;
    private float endTime;

    //保存选择到的天和时间字符串
    private String mDay;
    private String mHour;
    private long mTime;

    @SuppressLint("InflateParams")
    public RentDateDialog(Context context, long time, String startStr, String endStr, String title) {

        this.mContext = context;
        this.startStr = startStr;
        this.endStr = endStr;
        this.mTime = time;

        contentView = LayoutInflater.from(context).inflate(R.layout.rent_dialog_date, null);

        dayWheel = (WheelView) contentView.findViewById(R.id.day);
        hourWheel = (WheelView) contentView.findViewById(R.id.hour);

        View view = contentView.findViewById(R.id.btn_confirm);
        View view2 = contentView.findViewById(R.id.btn_cancel);
        view.setOnClickListener(this);
        view2.setOnClickListener(this);

        TextView tvTitle = (TextView) contentView.findViewById(R.id.tv_title);
        tvTitle.setText(title);

        dayWheel.addChangingListener(this);
        hourWheel.addChangingListener(this);

        initDayWheel(time);
        initHourWheel(startStr, endStr);

        bottomDialog = new BottomSheetDialog(context);
        bottomDialog.setContentView(contentView);
        bottomDialog.setCanceledOnTouchOutside(true);

    }

    /**
     * 初始化wheelView.
     *
     * @param wheelView wheelView
     * @param adapter   adapter
     */
    private void initWheelView(WheelView wheelView, WheelTextAdapter adapter) {
        PickerConfig config = new PickerConfig();
        config.mWheelTVSize = 18;
        adapter.setConfig(config);
        wheelView.setViewAdapter(adapter);
        wheelView.addChangingListener(this);
        wheelView.setCyclic(false); //设置不可循环
        wheelView.setCurrentItem(1);
    }

    /**
     * 初始化天.
     *
     * @param time 起始时间
     */
    private void initDayWheel(long time) {
        setDayTime(time); //初始化天数据
        WheelTextAdapter adapter = new WheelTextAdapter(mContext, dayList);
        initWheelView(dayWheel, adapter);
    }

    /**
     * 初始化小时.
     *
     * @param s    起点小时
     * @param endS 终点小时
     */
    private void initHourWheel(String s, String endS) {
        setHourTime(s, endS);
        hourAdapter = new WheelTextAdapter(mContext, hourList);
        initWheelView(hourWheel, hourAdapter);
        hourWheel.setCurrentItem(0);
    }

    /**
     * 设置天的时间数组.
     *
     * @param time 开始时间戳
     */
    private void setDayTime(long time) {
        dayList.clear();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time); //设置起始时间

        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.setTimeInMillis(System.currentTimeMillis());
        int today = todayCalendar.get(Calendar.DAY_OF_MONTH);

        //处理第一条数据是否为今天
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH); //获取日期在本月的第几天
        if (today == dayOfMonth) {  //是今天
            dayList.add("\r\r\r\r\r\r\r\r今天\r" + TimeUtil.getWeekDay(calendar));
        } else {  //不是今天
            String week = TimeUtil.getWeekDay(calendar);
            dayList.add(TimeUtil.getTime( "MM月dd日 ",calendar.getTimeInMillis()) + week);
        }
        SaveDays.add(TimeUtil.getTime("yyyy-MM-dd",calendar.getTimeInMillis())); //增加保存时间队列

        //获取后面天
        for (int i = 1; i < PASS_DAY; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth + 1);
            String week = TimeUtil.getWeekDay(calendar);
            dayList.add(TimeUtil.getTime( "MM月dd日 ",calendar.getTimeInMillis()) + week);
            SaveDays.add(TimeUtil.getTime("yyyy-MM-dd", calendar.getTimeInMillis()));
            dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);   //重新获取当前日期
        }

    }

    /**
     * 获取起点和终点的时间段(时间如: 08:30).
     *
     * @param start 起点时间段
     * @param end   结束时间段
     * @return 可选择时间区域
     */
    private void setHourTime(String start, String end) {

        hourList.clear();

        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            hourList.add("--:--");
            return;
        }

        if (!start.contains(":") || !end.contains(":")) {
            hourList.add("--:--");
            return;    //时间格式有问题
        }

        //开始营业时间
        String[] strs = start.split(":");
        int startH = Integer.parseInt(strs[0]); //小时
        int startM = Integer.parseInt(strs[1]); //分钟
        if (startM != 0) {   //说明起始时间为x:30分
            startTime = startH + 0.5F;
        } else {
            startTime = startH;
        }

        //结束营业时间
        String[] strs2 = end.split(":");
        int endH = Integer.parseInt(strs2[0]);
        int endM = Integer.parseInt(strs2[1]);
        if (endM != 0) {
            endTime = endH + 0.5F;
        } else {
            endTime = endH;
        }

        for (float i = startTime; i <= endTime; i += 0.5F) {
            if (i == (int) i) { //说明为整点
                hourList.add(hourFormat(i, ":00"));
            } else {
                hourList.add(hourFormat(i, ":30"));
            }
        }

        if (hourList.isEmpty()) {
            hourList.add("--:--");
        }
    }

    /**
     * 格式化分钟小时部分.
     *
     * @param i       小时
     * @param pattern 添加的部分
     * @return 格式化后的字符串
     */
    private String hourFormat(float i, String pattern) {
        if ((int) i < 10) {
            return "0" + (int) i + pattern;
        } else {
            return (int) i + pattern;
        }
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        int id = wheel.getId();
        if (id == R.id.day) {
            if (newValue == 0 && oldValue != 0) {
                updateHour(true);
            } else if (oldValue == 0 && newValue != 0) {
                updateHour(false);
            } else {
                mDay = SaveDays.get(newValue);
                return;
            }

            if (hourAdapter != null) {
                mHour = hourList.get(0);
                hourAdapter.notifyDataInvalidatedEvent();
                hourWheel.setCurrentItem(0);
            }
            mDay = SaveDays.get(newValue);
        } else if (id == R.id.hour) {
            mHour = hourList.get(newValue);
        }
    }

    /**
     * 更新小时数据.
     */
    private void updateHour(boolean isLimit) {
        if (!isLimit) {
            setHourTime(startStr, endStr);
            return;
        }

        //---日子滑到了第一个---------

        long t = mTime;

        String str = TimeUtil.getTime("HH:mm", t);
        String[] strs = str.split(":");
        int startH = Integer.parseInt(strs[0]);
        int startM = Integer.parseInt(strs[1]);

        float ss = startH + startM / 60;    //当前时间
        if (ss < startTime) { //早于营业时间
            setHourTime(startStr, endStr);
            return;
        } else if (ss >= endTime) { //晚于营业时间
            hourList.clear();
            hourList.add("--:--");
            return;
        }

        //在营业时间段中
        float s;
        if (startM > 30) {   //延迟到下一小时
            s = startH + 1;
        } else {    //到x:30
            s = startH + 0.5F;
        }

        hourList.clear();

        for (float i = s; i <= endTime; i += 0.5F) {
            if (i == (int) i) { //说明为整点
                hourList.add(hourFormat(i, ":00"));
            } else {
                hourList.add(hourFormat(i, ":30"));
            }
        }

        if (hourList.isEmpty()) {
            hourList.add("--:--");
        }
    }

    /**
     * 返回选中的事件戳.
     */
    private long getTimeMillis() {
        String time = mDay + " " + mHour;
        long t = 0;
        SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        try {
            Date data = mFormat.parse(time);
            t = data.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(mContext, "请选择有效的时间", Toast.LENGTH_SHORT).show();
        }

        return t;

    }

    /**
     * 显示对话框.
     */
    public void show() {
        if (bottomDialog != null) {
            View parent = (View) contentView.getParent();
            BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//      behavior.setHideable(false);    //禁止下滑手势
            bottomDialog.show();
        }
    }


    /**
     * 设置时间点击事件.
     *
     * @param listener 时间监听器
     */
    public void setOnTimeClickListener(OnTimeClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btn_confirm) {
            long t = getTimeMillis();
            if (t == 0) {
                return;
            }
            if (listener != null) {
                listener.onTimeClick(t);
            }
        }

        if (bottomDialog != null) {
            bottomDialog.dismiss();
        }

    }

    /**
     * 点击确定后时间接口.
     */
    public interface OnTimeClickListener {
        void onTimeClick(long time);
    }

    /**
     * 滚轮显示文本adapter.
     */
    private class WheelTextAdapter extends AbstractWheelTextAdapter {

        private List<String> textList;

        WheelTextAdapter(Context context, List<String> textList) {
            super(context);
            this.textList = textList;
        }

        @Override
        public int getItemsCount() {
            return textList == null ? 0 : textList.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            if (index >= 0 && index < getItemsCount() && textList != null) {
                return textList.get(index);
            }
            return null;
        }
    }


}
